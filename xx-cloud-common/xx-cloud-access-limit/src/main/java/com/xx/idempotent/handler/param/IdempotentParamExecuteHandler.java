package com.xx.idempotent.handler.param;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.xx.exception.BizException;
import com.xx.idempotent.IdempotentParamWrapper;
import com.xx.idempotent.aspect.Idempotent;
import com.xx.idempotent.context.IdempotentContext;
import com.xx.idempotent.handler.AbstractIdempotentExecuteHandler;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * param类型的幂等性处理
 *
 * @author Agao
 * @date 2025/11/4 10:46
 */
@RequiredArgsConstructor
public class IdempotentParamExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentParamService {
    private final RedissonClient redissonClient;

    /**
     * threadLocal中当前线程 分布式锁的key
     */
    private final static String LOCK = "lock:param:restAPI";

    @Override
    protected IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint) {

        String lockKey = String.format("idempotent:path:%s:currentUserId:%s:md5:%s", getServletPath(), getCurrentUserId(), calcArgsMD5(joinPoint));
        return IdempotentParamWrapper.builder().lockKey(lockKey).joinPoint(joinPoint).build();
    }

    private String getCurrentUserId() {
        // TODO: 获取当前登录的用户id

        return "1001";
    }

    /**
     * @return joinPoint md5
     */
    private String calcArgsMD5(ProceedingJoinPoint joinPoint) {
        return DigestUtil.md5Hex(JSON.toJSONBytes(joinPoint.getArgs()));
    }

    /**
     * 路由path
     *
     * @return 路由
     */
    private String getServletPath() {
        ServletRequestAttributes reqAttr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest req = reqAttr.getRequest();

        return req.getServletPath();
    }

    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        Idempotent idempotent = wrapper.getIdempotent();
        String lockKey = wrapper.getLockKey();

        RLock lock = redissonClient.getLock(lockKey);
        if (!lock.tryLock()) {
            throw new BizException(idempotent.message());
        }

        IdempotentContext.put(LOCK, lock);
    }

    @Override
    public void postProcessing() {
        RLock lock = null;
        try {
            lock = (RLock) IdempotentContext.getKey(LOCK);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    @Override
    public void exceptionProcessing() {
        postProcessing();
    }
}
