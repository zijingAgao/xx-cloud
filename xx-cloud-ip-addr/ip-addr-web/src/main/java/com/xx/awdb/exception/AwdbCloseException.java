package com.xx.awdb.exception;

import java.io.IOException;

/**
 * awdb文件关闭异常类
 */
public class AwdbCloseException extends IOException {
    /**
     * 告知 AWDB 文件已关闭。
     */
    public AwdbCloseException() {
        super("The AWDB file has been closed.");
    }
}
