package com.xx.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Agao
 * @date 2024/3/2 14:32
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {
    WAIT_PAY(0,"待支付"),
    PAYED(1,"已支付"),
    ;

    private final int code;
    private final String msg;
}
