package com.xx.controller;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.xx.pojo.EventMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Agao
 * @date 2024/7/4 23:10
 */
@RestController
@RequiredArgsConstructor
public class EventBusController {

  private final EventBus eventBus;
  private final AsyncEventBus asyncEventBus;

  @GetMapping("/bus")
  public void bus() {
    EventMsg msg = new EventMsg("bus");
    eventBus.post(msg);
  }

  @GetMapping("/async/bus")
  public void asyncBus() {
    EventMsg msg = new EventMsg("asyncBus");
    asyncEventBus.post(msg);
  }
}
