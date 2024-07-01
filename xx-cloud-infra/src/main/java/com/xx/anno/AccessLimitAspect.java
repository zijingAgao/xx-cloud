package com.xx.anno;

import com.xx.constant.Const;
import com.xx.resp.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 重复提交注解切面
 *
 * @author Agao
 * @date 2024/7/1 22:51
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AccessLimitAspect {

  private final RedissonClient redissonClient;

  @Around("@annotation(accessLimit)")
  public Object around(ProceedingJoinPoint point, AccessLimit accessLimit) throws Throwable {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletResponse resp = attributes.getResponse();
    if (resp == null) {
      return null;
    }

    MethodSignature methodSignature = (MethodSignature) point.getSignature();
    // 方法名
    String methodName = methodSignature.getMethod().getName();
    // 类名
    String clazzName = point.getTarget().getClass().getName();
    // 限流key： 类名+方法名
    String key = clazzName + "." + methodName;

    int second = accessLimit.second();
    int maxCount = accessLimit.maxCount();

    RMap<String, Integer> accessLimitMap = redissonClient.getMap(Const.ACCESS_LIMIT_KEY);
    if (accessLimitMap.get(key) == null) {
      accessLimitMap.put(key, 1);
      accessLimitMap.expire(Duration.ofMinutes(second));
    } else {
      int count = accessLimitMap.get(key);
      if (count >= maxCount) {
        resp.setStatus(429);
        resp.getWriter().write(accessLimit.msg());
        return null;
      }
      accessLimitMap.put(key, count + 1);
    }

    // 放行
    return point.proceed();
  }
}
