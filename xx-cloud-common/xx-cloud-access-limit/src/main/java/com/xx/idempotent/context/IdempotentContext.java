package com.xx.idempotent.context;

import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 幂等性上下文
 *
 * @author Agao
 * @date 2025/11/4 15:28
 */
public class IdempotentContext {

    /**
     * 线程上下文
     */
    private static final ThreadLocal<Map<String, Object>> THREAD_CONTEXT = new ThreadLocal<>();

    public static Map<String, Object> get() {
        return THREAD_CONTEXT.get();
    }

    public static Object getKey(String key) {
        Map<String, Object> threadContext = THREAD_CONTEXT.get();
        if (CollectionUtils.isEmpty(threadContext)) {
            return null;
        }
        return threadContext.get(key);
    }

    public static void put(String key, Object value) {
        Map<String, Object> threadContext = get();
        if (CollectionUtils.isEmpty(threadContext)) {
            threadContext = new HashMap<>();
        }
        threadContext.put(key, value);
        putContext(threadContext);
    }

    public static void putContext(Map<String, Object> context) {
        Map<String, Object> threadContext = THREAD_CONTEXT.get();
        if (CollectionUtils.isEmpty(threadContext)) {
            THREAD_CONTEXT.set(context);
            return;
        }
        threadContext.putAll(context);
    }

    public static void remove() {
        THREAD_CONTEXT.remove();
    }
}
