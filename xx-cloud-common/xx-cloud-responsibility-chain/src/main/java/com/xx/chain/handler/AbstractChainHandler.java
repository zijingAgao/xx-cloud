package com.xx.chain.handler;


import org.springframework.core.Ordered;

/**
 * 责任链抽象处理器
 *
 * @author Agao
 * @date 2025/10/29 15:32
 */
public interface AbstractChainHandler<T> extends Ordered {

    String mark();

    void handler(T reqParam);

}
