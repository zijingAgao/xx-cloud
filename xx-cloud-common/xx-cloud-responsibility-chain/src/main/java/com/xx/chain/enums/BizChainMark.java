package com.xx.chain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 责任链-业务标识
 *
 * @author Agao
 * @date 2025/10/29 15:40
 */
@Getter
@AllArgsConstructor
public enum BizChainMark {

    BIZ_TEXT("BIZ_TEXT"),

    ;

    private final String mark;
}
