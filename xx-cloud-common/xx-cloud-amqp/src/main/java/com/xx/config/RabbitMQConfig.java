package com.xx.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * rabbitMQ 提供了四种 路由策略 1.Direct 2.Fanout 3.Topic 4.Header
 *
 * @author Agao
 * @date 2024/3/2 14:06
 */
@Configuration
public class RabbitMQConfig {
  // -------------------------------------------1.Direct---------------------------------------------------------

  /** 队列名称 */
  public static final String ORDER_QUEUE = "order_queue";

  /** 交换机名称 */
  public static final String ORDER_EXCHANGE = "order_exchange";

  /** 订单路由key */
  public static final String ROUTING_KEY_ORDER = "routing_key_order";

  /** 死信消息队列名称 */
  public static final String DEAL_QUEUE_ORDER = "deal_queue_order";

  /** 死信交换机名称 */
  public static final String DEAL_EXCHANGE_ORDER = "deal_exchange_order";

  /** 死信 routingKey */
  public static final String DEAD_ROUTING_KEY_ORDER = "dead_routing_key_order";

  @Bean
  public Queue orderQueue() {
    // 将普通队列绑定到死信队列交换机上
    return QueueBuilder.durable(ORDER_QUEUE)
        .deadLetterExchange(DEAL_EXCHANGE_ORDER)
        .deadLetterRoutingKey(DEAD_ROUTING_KEY_ORDER)
        .build();
  }

  // 声明一个direct类型的交换机
  @Bean
  public DirectExchange orderExchange() {
    return new DirectExchange(RabbitMQConfig.ORDER_EXCHANGE);
  }

  // 绑定Queue队列到交换机,并且指定routingKey
  @Bean
  public Binding bindingDirectExchange() {
    return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(ROUTING_KEY_ORDER);
  }

  // 创建配置死信队列
  @Bean
  public Queue deadQueueOrder() {
    return QueueBuilder.durable(DEAL_QUEUE_ORDER).build();
  }

  // 创建死信交换机
  @Bean
  public DirectExchange deadExchangeOrder() {
    return new DirectExchange(DEAL_EXCHANGE_ORDER);
  }

  // 死信队列与死信交换机绑定
  @Bean
  public Binding bindingDeadExchange() {
    return BindingBuilder.bind(deadQueueOrder())
        .to(deadExchangeOrder())
        .with(DEAD_ROUTING_KEY_ORDER);
  }

  // --------------------延迟队列-----------------------
  public static final String DELAY_QUEUE = "delay_queue_order";
  public static final String DELAY_EXCHANGE = "delay_exchange_order";
  public static final String DELAY_ROUTING_KEY = "delay_routing_key_order";

  @Bean
  public Queue delayQueue() {
    return QueueBuilder.durable(DELAY_QUEUE).build();
  }

  @Bean
  public Exchange orderDelayExchange() {
    Map<String, Object> args = new HashMap<>(1);
    args.put("x-delayed-type", "direct");
    return new CustomExchange(DELAY_EXCHANGE, "x-delayed-message", true, false, args);
  }

  @Bean
  public Binding bindingDelayExchange() {
    return BindingBuilder.bind(delayQueue())
        .to(orderDelayExchange())
        .with(DELAY_ROUTING_KEY)
        .noargs();
  }

  // -------------------------------------------2.Fanout---------------------------------------------------------

  // -------------------------------------------3.Topic----------------------------------------------------------

  // -------------------------------------------4.Header---------------------------------------------------------

}
