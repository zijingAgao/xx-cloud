package com.xx.idempotent.handler.token;

/**
 * @author Agao
 * @date 2025/11/4 14:32
 */
public interface IdempotentTokenService {
    /**
     * 创建幂等验证Token
     */
    String applyToken();
}
