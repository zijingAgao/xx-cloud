package com.xx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xx.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Agao
 * @date 2024/5/24 10:28
 */
@RestController
@RequiredArgsConstructor
public class KafkaSendMsgController {
  private final ProducerService producerService;

  @GetMapping("/api/send/msg")
  public void sendMsg() throws JsonProcessingException {
    producerService.producer();
  }
}
