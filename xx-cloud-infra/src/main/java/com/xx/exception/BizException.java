package com.xx.exception;

/**
 * 业务异常
 * @author Agao
 * @date 2024/7/1 23:53
 */
public class BizException extends RuntimeException{

    public BizException(String message) {
        super(message);
    }
}
