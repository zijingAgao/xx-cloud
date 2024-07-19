package com.xx.controller;

import com.xx.constant.Const;
import com.xx.pojo.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Agao
 * @date 2024/7/19 15:33
 */
@Slf4j
@RestController
@RequestMapping("/api/producer")
public class ProducerController {
  @Autowired private StreamBridge streamBridge;

  @PostMapping("/email")
  public void sendEmail() {
    Person person = new Person(1, "agao-email");
    log.info("发送email消息：{}", person);
    streamBridge.send(Const.EMAIL_EXCHANGE, person);
  }

  @PostMapping("/short_msg")
  public void sendShotMsg() {
    Person person = new Person(1, "agao-short-msg");
    log.info("发送short-msg消息：{}", person);
    streamBridge.send(Const.SHORT_MSG_EXCHANGE, person);
  }
}
