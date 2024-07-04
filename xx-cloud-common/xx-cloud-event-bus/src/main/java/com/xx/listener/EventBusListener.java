package com.xx.listener;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.xx.pojo.EventMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author Agao
 * @date 2024/7/4 23:01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventBusListener implements ApplicationRunner {
  private final EventBus eventBus;
  private final AsyncEventBus asyncEventBus;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    eventBus.register(this);
    asyncEventBus.register(this);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handle(EventMsg event) {
    log.info("eventBus handle event: {}", event);
  }
}
