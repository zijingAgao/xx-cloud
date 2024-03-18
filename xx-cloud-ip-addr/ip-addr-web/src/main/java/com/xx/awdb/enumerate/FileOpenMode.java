package com.xx.awdb.enumerate;

/**
 * 打开awdb时使用的文件模式
 */
public enum FileOpenMode {
    /**
     * 默认文件模式。
     * 这会将数据库映射到虚拟内存。
     */
    MEMORY_MAPPED,
    /**
     * 构造读取器时将数据库加载到内存中。
     */
    MEMORY
}
