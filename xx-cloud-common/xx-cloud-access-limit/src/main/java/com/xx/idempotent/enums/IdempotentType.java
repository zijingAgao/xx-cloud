package com.xx.idempotent.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Agao
 * @date 2025/11/4 10:30
 */
@Getter
@AllArgsConstructor
public enum IdempotentType {
    /**
     * 生成和验证token的机制
     */
    TOKEN,
    /**
     * PARAM 表示基于方法参数来防止重复
     */
    PARAM
}
