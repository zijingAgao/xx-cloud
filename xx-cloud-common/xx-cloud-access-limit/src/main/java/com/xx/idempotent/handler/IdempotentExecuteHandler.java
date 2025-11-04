package com.xx.idempotent.handler;

import com.xx.idempotent.IdempotentParamWrapper;
import com.xx.idempotent.aspect.Idempotent;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 幂等性处理器
 *
 * @author Agao
 * @date 2025/11/4 10:32
 */
public interface IdempotentExecuteHandler {

    /**
     * 幂等处理逻辑(真正的执行)
     *
     * @param wrapper 幂等参数包装器
     */
    void handler(IdempotentParamWrapper wrapper);

    /**
     * 执行幂等处理逻辑(包装获取IdempotentParamWrapper)
     *
     * @param joinPoint  AOP 方法处理
     * @param idempotent 幂等注解
     */
    void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent);

    /**
     * 异常流程处理
     */
    default void exceptionProcessing() {

    }

    /**
     * 后置处理
     */
    default void postProcessing() {

    }
}
