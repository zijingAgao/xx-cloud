package com.xx.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xx.config.RabbitMQConfig;
import com.xx.entity.Order;
import com.xx.enums.OrderStatus;
import com.xx.repo.OrderRepository;
import com.xx.ro.OrderRo;
import com.xx.ro.PreOrderRo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @author Agao
 * @date 2024/3/2 14:30
 */
@Slf4j
@Service
public class OrderService {
  @Autowired private AmqpTemplate amqpTemplate;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private OrderRepository orderRepository;

  // 条件查询
  public List<Order> findCondition(OrderRo ro) {
    return orderRepository.findByCondition(ro);
  }

  // 分页条件查询
  public Page<Order> pageFind(OrderRo ro) {
    return orderRepository.pageFindByCondition(ro);
  }

  // 查单个
  public Order findOne(String id) {
    return orderRepository.findById(id).orElse(null);
  }

  // 订单生产者
  public void preOrder(PreOrderRo ro) throws JsonProcessingException {
    log.info("生成预订单----orderId: {}", ro.getId());
    Order order = new Order();
    order.setId(UUID.randomUUID().toString());
    order.setPrice(ro.getPrice());
    order.setStatus(OrderStatus.WAIT_PAY.getCode());
    order.setCreateDate(System.currentTimeMillis());

    // 投递消息
    amqpTemplate.convertAndSend(
        RabbitMQConfig.ORDER_EXCHANGE,
        RabbitMQConfig.ROUTING_KEY_ORDER,
        objectMapper.writeValueAsString(order),
        message -> {
          // 也可以直接设置queue 的过期时间
          message.getMessageProperties().setExpiration(1000 * 10 + "");
          return message;
        });
    log.info("订单生产者结束----------");
  }

  // 订单生产者,投递延迟队列
  public void delayPreOrder(PreOrderRo ro) throws JsonProcessingException {
    log.info("生成延迟预订单----orderId: {}", ro.getId());
    Order order = new Order();
    order.setId(ro.getId());
    order.setPrice(ro.getPrice());
    order.setStatus(OrderStatus.WAIT_PAY.getCode());
    order.setCreateDate(System.currentTimeMillis());

    // 投递消息
    amqpTemplate.convertAndSend(
        RabbitMQConfig.DELAY_EXCHANGE,
        RabbitMQConfig.DELAY_ROUTING_KEY,
        objectMapper.writeValueAsString(order),
        message -> {
          // 延迟10s后投递到队列
          message.getMessageProperties().setDelay(1000 * 10);
          return message;
        });
    log.info("订单生产者结束----------");
  }
}
