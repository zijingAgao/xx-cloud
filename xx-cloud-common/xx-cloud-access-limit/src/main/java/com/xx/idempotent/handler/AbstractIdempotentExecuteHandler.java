package com.xx.idempotent.handler;

import com.xx.idempotent.IdempotentParamWrapper;
import com.xx.idempotent.aspect.Idempotent;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author Agao
 * @date 2025/11/4 10:40
 */
public abstract class AbstractIdempotentExecuteHandler implements IdempotentExecuteHandler {

    protected abstract IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint);

    @Override
    public void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        IdempotentParamWrapper idempotentParamWrapper = buildWrapper(joinPoint);
        idempotentParamWrapper.setIdempotent(idempotent);
        handler(idempotentParamWrapper);
    }
}
