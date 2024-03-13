package com.xx.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.xx.config.RabbitMQConfig;
import com.xx.entity.Order;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author Agao
 * @date 2024/3/2 14:26
 */
@Slf4j
@Component
public class OrderExpireListener {
  @Autowired private ObjectMapper objectMapper;

  /**
   * 死信队列
   *
   * @param orderMsg
   * @param message
   * @param headers
   * @param channel
   * @throws IOException
   */
  @RabbitListener(queues = RabbitMQConfig.DEAL_QUEUE_ORDER)
  public void orderExpireDeadQueue(
      @Payload String orderMsg,
      Message message,
      @Headers Map<String, Object> headers,
      Channel channel)
      throws IOException {
    Order order = objectMapper.readValue(orderMsg, Order.class);
    log.info("order pay expire :{}", order);
    // 判断订单是否支付，做未支付的业务处理

    // 手动ack
    Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
    // 手动签收
    channel.basicAck(deliveryTag, false);
  }

  /**
   * 延时队列
   *
   * @param orderMsg
   * @param message
   * @param headers
   * @param channel
   * @throws IOException
   */
  @RabbitListener(queues = RabbitMQConfig.DELAY_QUEUE)
  public void orderExpireDelayQueue(
      @Payload String orderMsg,
      Message message,
      @Headers Map<String, Object> headers,
      Channel channel)
      throws IOException {
    Order order = objectMapper.readValue(orderMsg, Order.class);
    log.info("order pay expire [delay] :{}", order);
    // 判断订单是否支付，做未支付的业务处理

    // 手动ack
    Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
    // 手动签收
    channel.basicAck(deliveryTag, false);
  }
}
