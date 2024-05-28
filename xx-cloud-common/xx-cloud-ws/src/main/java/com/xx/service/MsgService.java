package com.xx.service;

import com.xx.config.WsServer;
import com.xx.constant.KafkaTopicConstance;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author Agao
 * @date 2024/5/28 9:32
 */
@Service
@RequiredArgsConstructor
public class MsgService {
    
    private final WsServer wsServer;
    @KafkaListener(topics = KafkaTopicConstance.TEST_TOPIC, groupId = "consumer-group")
    public void consume(String message) throws IOException {
        System.out.println("Received message: " + message);
        wsServer.sendMessage(message);
    }
}
