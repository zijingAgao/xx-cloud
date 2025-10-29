package com.xx.anno;

import cn.hutool.crypto.SecureUtil;
import com.xx.constant.Const;
import com.xx.exception.BizException;
import com.xx.util.IpAddressUtils;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 防重复提交切面
 *
 * @author Agao
 * @date 2024/7/1 23:27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RepeatSubmitAspect {
  private final RedissonClient redissonClient;

  @Pointcut("@annotation(repeatSubmit)")
  public void pointCut(RepeatSubmit repeatSubmit) {}

  /**
   * 环绕切入
   *
   * @param point
   * @param repeatSubmit
   * @return
   * @throws Throwable
   */
  @Around("pointCut(repeatSubmit)")
  public Object around(ProceedingJoinPoint point, RepeatSubmit repeatSubmit) throws Throwable {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) return null;
    HttpServletRequest req = attributes.getRequest();
    HttpServletResponse resp = attributes.getResponse();

    String businessId = repeatSubmit.businessId();
    RepeatSubmit.Type type = repeatSubmit.type();

    boolean result = false;

    if (RepeatSubmit.Type.PARAM.equals(type)) {
      // 通过 redissonClient 获取分布式锁，基于IP地址、类名、方法名和服务ID生成唯一key
      long expire = repeatSubmit.lockExpire();
      String ip = IpAddressUtils.getIpAddress(req);
      MethodSignature signature = (MethodSignature) point.getSignature();
      String methodName = signature.getMethod().getName();
      String clazzName = signature.getMethod().getDeclaringClass().getName();
      // 重复提交缓存key
      String key =
          businessId
              + Const.REPEAT_SUBMIT_KEY_PARAM
              + SecureUtil.md5().digestHex(String.format("%s-%s-%s-%s", ip, clazzName, methodName, businessId));

      RLock lock = redissonClient.getLock(key);
      // 尝试获取锁，等待0s，超时后自动过期释放，不手动释放，以达到短时间内防止重复提交的目的
      result = lock.tryLock(0, expire, TimeUnit.SECONDS);
    }

    if (RepeatSubmit.Type.TOKEN.equals(type)) {
      String requestToken = req.getHeader("Authorization");
      if (StringUtils.isBlank(requestToken)) {
        throw new BizException("请求未包含令牌");
      }
      //TODO: 构造Redis的key，尝试从Redis中删除这个键。如果删除成功，说明是首次提交；否则认为是重复提交
      String key = businessId + Const.REPEAT_SUBMIT_KEY_TOKEN + requestToken;
      result = redissonClient.getBucket(key).delete();
    }

    if (result) {
      resp.setStatus(429);
      resp.getWriter().write("重复的提交，已被忽略");
      return null;
    }

    return point.proceed();
  }
}
