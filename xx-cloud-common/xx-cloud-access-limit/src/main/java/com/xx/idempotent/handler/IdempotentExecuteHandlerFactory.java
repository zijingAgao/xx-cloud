package com.xx.idempotent.handler;

import com.xx.context.ApplicationContextHolder;
import com.xx.idempotent.enums.IdempotentType;
import com.xx.idempotent.handler.param.IdempotentParamExecuteHandler;
import com.xx.idempotent.handler.token.IdempotentTokenExecuteHandler;

/**
 * 幂等性执行处理器工厂
 *
 * @author Agao
 * @date 2025/11/4 10:32
 */
public final class IdempotentExecuteHandlerFactory {

    public static IdempotentExecuteHandler getInstance( IdempotentType type) {
        switch (type) {
            case PARAM:
                return ApplicationContextHolder.getBean(IdempotentParamExecuteHandler.class);
            case TOKEN:
                return ApplicationContextHolder.getBean(IdempotentTokenExecuteHandler.class);
            default:
                return null;
        }
    }
}
