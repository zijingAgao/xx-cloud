package com.xx.idempotent.handler.token;

import com.xx.exception.BizException;
import com.xx.idempotent.IdempotentParamWrapper;
import com.xx.idempotent.handler.AbstractIdempotentExecuteHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.UUID;

/**
 * @author Agao
 * @date 2025/11/4 14:32
 */
@Slf4j
@RequiredArgsConstructor
public class IdempotentTokenExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentTokenService {
    private final RedissonClient redissonClient;

    /**
     * 请求header中幂等性的token key
     */
    private static final String TOKEN_KEY = "idempotent-token";
    /**
     * 幂等性token缓存key的前缀
     */
    private static final String TOKEN_PREFIX_KEY = "idempotent:token:";
    /**
     * 幂等性token的过期时间 6s内请求
     */
    private static final long TOKEN_EXPIRED_TIME = 6000L;

    @Override
    protected IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint) {
        return new IdempotentParamWrapper();
    }

    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        ServletRequestAttributes reqAttr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = reqAttr.getRequest();
        String token = request.getHeader(TOKEN_KEY);
        if (!StringUtils.hasText(token)) {
            throw new BizException("幂等Token为空");
        }
        RBucket<Object> bucket = redissonClient.getBucket(token);
        if (!bucket.delete()) {
            throw new BizException("幂等Token已被使用或失效");
        }
    }

    @Override
    public String applyToken() {
        String token = TOKEN_PREFIX_KEY + UUID.randomUUID();
        RBucket<Object> bucket = redissonClient.getBucket(token);
        bucket.set("");
        bucket.expire(Duration.ofMillis(TOKEN_EXPIRED_TIME));
        log.debug("申请幂等Token:{}", token);
        return token;
    }
}
