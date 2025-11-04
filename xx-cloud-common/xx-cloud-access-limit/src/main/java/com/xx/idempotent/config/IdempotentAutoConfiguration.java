package com.xx.idempotent.config;

import com.xx.idempotent.aspect.IdempotentAspect;
import com.xx.idempotent.handler.param.IdempotentParamExecuteHandler;
import com.xx.idempotent.handler.param.IdempotentParamService;
import com.xx.idempotent.handler.token.IdempotentTokenController;
import com.xx.idempotent.handler.token.IdempotentTokenExecuteHandler;
import com.xx.idempotent.handler.token.IdempotentTokenService;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Agao
 * @date 2025/11/4 17:05
 */
@Configuration
public class IdempotentAutoConfiguration {

    @Bean
    public IdempotentAspect idempotentAspect(){
        return new IdempotentAspect();
    }
    @Bean
    @ConditionalOnMissingBean
    public IdempotentParamService idempotentParamExecuteHandler(RedissonClient redissonClient) {
        return new IdempotentParamExecuteHandler(redissonClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotentTokenService idempotentTokenExecuteHandler(RedissonClient redissonClient) {
        return new IdempotentTokenExecuteHandler(redissonClient);
    }

    @Bean
    public IdempotentTokenController idempotentTokenController(IdempotentTokenService idempotentTokenService) {
        return new IdempotentTokenController(idempotentTokenService);
    }


}
