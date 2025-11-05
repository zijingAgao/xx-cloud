package com.xx.idempotent.aspect;

import com.xx.idempotent.context.IdempotentContext;
import com.xx.idempotent.enums.IdempotentType;
import com.xx.idempotent.exception.IdempotentException;
import com.xx.idempotent.handler.IdempotentExecuteHandler;
import com.xx.idempotent.handler.IdempotentExecuteHandlerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 防重复提交切面
 *
 * @author Agao
 * @date 2024/7/1 23:27
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class IdempotentAspect {

    @Pointcut("@annotation(idempotent)")
    public void pointCut(Idempotent idempotent) {
    }

    /**
     * 环绕切入
     *
     * @param point
     * @param idempotent
     * @return
     * @throws Throwable
     */
    @Around(value = "pointCut(idempotent)", argNames = "point,idempotent")
    public Object around(ProceedingJoinPoint point, Idempotent idempotent) throws Throwable {
        IdempotentType type = idempotent.type();
        IdempotentExecuteHandler executeHandler = IdempotentExecuteHandlerFactory.getInstance(idempotent.type());
        if (executeHandler == null) {
            log.error("Idempotent type not found {}", type);
            throw new IdempotentException("Idempotent type not found");
        }

        Object proceed;
        try {
            // 执行切面
            executeHandler.execute(point, idempotent);
            // 执行业务
            proceed = point.proceed();
            // 后置增强
            executeHandler.postProcessing();
        } catch (IdempotentException e) {
            executeHandler.exceptionProcessing();
            throw e;
        } finally {
            IdempotentContext.remove();
        }

        return proceed;
    }
}
