package com.xx.awdb.exception;

import java.io.IOException;

/**
 * 无效awdb文件异常类
 */
public class InvalidAwdbException extends IOException {
    /**
     * AWDB文件不正确，因为数据格式错误。
     *
     * @param message 异常原因
     */
    public InvalidAwdbException(String message) {
        super(message);
    }
}
