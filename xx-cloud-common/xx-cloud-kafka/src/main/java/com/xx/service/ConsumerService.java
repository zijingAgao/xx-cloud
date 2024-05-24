package com.xx.service;

import com.xx.constant.KafkaTopicConstance;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author Agao
 * @date 2024/5/24 10:01
 */
@Service
public class ConsumerService {

    @KafkaListener(topics = KafkaTopicConstance.TEST_TOPIC, groupId = "consumer-group")
    public void consume(String message) {
        System.out.println("Received message: " + message);
    }


}
