package com.xx.idempotent.aspect;

import com.xx.exception.BizException;
import com.xx.idempotent.context.IdempotentContext;
import com.xx.idempotent.enums.IdempotentType;
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
    public void pointCut(Idempotent idempotent) {}

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
            log.error("幂等类型：{} 处理器不存在", type);
            throw new BizException("幂等类型不存在");
        }

        Object proceed;
        try {
            // 执行切面
            executeHandler.execute(point, idempotent);
            // 执行业务
            proceed = point.proceed();
            // 后置增强
            executeHandler.postProcessing();
        } catch (Throwable e) {
            executeHandler.exceptionProcessing();
            log.error("幂等执行异常", e);
            throw new BizException("幂等执行异常");
        } finally {
            IdempotentContext.remove();
        }

        return proceed;
    }
}
