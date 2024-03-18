package com.xx.awdb;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public interface AwdbNodeCache {

    /**
     * 加载器
     */
    interface Loader {
        /**
         * 加载数据
         *
         * @param key key
         * @return JsonNode
         * @exception IOException 文件打开或读取失败
         */
        JsonNode load(int key) throws IOException;
    }

    /**
     * 从cache获取数据
     *
     * @param loader 加载器
     * @param key    key
     * @return JsonNode
     * @exception IOException 文件打开或读取失败
     */
    JsonNode get(Loader loader, int key) throws IOException;
}
