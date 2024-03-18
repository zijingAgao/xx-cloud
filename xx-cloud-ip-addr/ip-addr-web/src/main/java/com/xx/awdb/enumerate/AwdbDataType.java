package com.xx.awdb.enumerate;

/**
 * awdb数据类型
 */
public enum AwdbDataType {
    /**
     * 列表类型，即数组
     */
    ARRAY,
    /**
     * 指针类型
     */
    POINTER,
    /**
     * 字符串类型
     */
    STRING,
    /**
     * 长文本类型
     */
    TEXT,
    /**
     * 无符号int类型
     */
    UINT,
    /**
     * 有符号int类型
     */
    INT,
    /**
     * float类型
     */
    FLOAT,
    /**
     * double类型
     */
    DOUBLE;

    private final static AwdbDataType[] VALUES = AwdbDataType.values();

    public static AwdbDataType getDataType(int b) {
        return AwdbDataType.VALUES[b - 1];
    }
}
