package com.xx.awdb.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.xx.awdb.AwdbNodeCache;

import java.io.IOException;

/**
 * 使用静态内部类方式实现的无缓存单例
 */
public class AwdbNoCacheImpl implements AwdbNodeCache {

    private AwdbNoCacheImpl() {
    }

    @Override
    public JsonNode get(Loader loader, int key) throws IOException {
        return loader.load(key);
    }

    private static class SingletonHolder {
        private static final AwdbNoCacheImpl INSTANCE = new AwdbNoCacheImpl();
    }

    public static AwdbNoCacheImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
