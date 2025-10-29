package com.xx.chain.context;

import com.xx.chain.handler.AbstractChainHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 责任链上下文
 *
 * @author Agao
 * @date 2025/10/29 15:31
 */
@Slf4j
@Component
public class AbstractChainContext<T> implements ApplicationContextAware {

    private static final Map<String, List<AbstractChainHandler>> abstractChainHandlerContainer = new HashMap<>();

    /**
     * 责任链组件执行
     *
     * @param reqParam 请求参数
     */
    public void handler(String mark, T reqParam) {
        log.info("start execute mark:{} chain...", mark);
        abstractChainHandlerContainer.get(mark)
                .stream()
                .sorted(Comparator.comparing(Ordered::getOrder))
                .forEach(chainHandler -> chainHandler.handler(reqParam));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("AbstractChainHandler init start...");
        Map<String, AbstractChainHandler> chainHandlerMap = applicationContext.getBeansOfType(AbstractChainHandler.class);
        chainHandlerMap.forEach((beanName, chainHandler) -> {
            String bizChainMark = chainHandler.mark();
            List<AbstractChainHandler> abstractChainHandlerList = abstractChainHandlerContainer.get(bizChainMark);
            if (CollectionUtils.isEmpty(abstractChainHandlerList)) {
                abstractChainHandlerList = new ArrayList<>();
            }
            abstractChainHandlerList.add(chainHandler);
            abstractChainHandlerContainer.put(bizChainMark, abstractChainHandlerList);

        });
        log.info("AbstractChainHandler init success...");
        if (log.isDebugEnabled()) {
            printContainer();
        }
    }

    /**
     * debug日志级下打印加载的所有handler
     */
    private void printContainer() {
        abstractChainHandlerContainer.forEach((mark, chainHandlerList) -> {
            log.info("mark:{}", mark);
            chainHandlerList.forEach(chainHandler ->
                    log.info("order:{} chainHandler: {}", chainHandler.getOrder(), chainHandler.getClass().getName())
            );
        });
    }
}
