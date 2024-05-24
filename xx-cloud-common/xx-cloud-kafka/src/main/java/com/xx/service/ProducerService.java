package com.xx.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xx.constant.KafkaTopicConstance;
import com.xx.pojo.KafkaMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Agao
 * @date 2024/5/24 10:01
 */
@Service
@RequiredArgsConstructor
public class ProducerService {
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public void producer() throws JsonProcessingException {
    KafkaMsg kafkaMsg = new KafkaMsg(200, "this is msg");
    kafkaTemplate.send(KafkaTopicConstance.TEST_TOPIC, objectMapper.writeValueAsString(kafkaMsg));
  }
}
