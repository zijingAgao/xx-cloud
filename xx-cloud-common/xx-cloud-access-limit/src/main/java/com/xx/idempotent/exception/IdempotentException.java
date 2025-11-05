package com.xx.idempotent.exception;

/**
 * 定义幂等异常类.用于和业务异常区分开来
 *
 * @author Agao
 * @date 2025/11/5 17:08
 */
public class IdempotentException extends RuntimeException {

    public IdempotentException(String message) {
        super(message);
    }

    public IdempotentException(Throwable cause) {
        super(cause);
    }
}
