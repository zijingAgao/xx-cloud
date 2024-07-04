package com.xx.config;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * guava event bus 配置
 *
 * @author Agao
 * @date 2024/7/4 22:44
 */
@Configuration
public class EventBusConfig {
  @Bean
  public EventBus eventBus() {
    return new EventBus();
  }

  @Bean
  public AsyncEventBus asyncEventBus() {
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
    return new AsyncEventBus(scheduledExecutorService);
  }
}
