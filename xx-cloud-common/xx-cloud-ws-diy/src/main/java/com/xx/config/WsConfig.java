package com.xx.config;

import com.xx.constant.Constants;
import com.xx.endpoint.WsGlobalEndpoint;
import com.xx.manager.MemWebSocketManager;
import com.xx.manager.MongoWebSocketManager;
import com.xx.manager.WebSocketManager;
import com.xx.service.WsConnectionService;
import com.xx.task.WebSocketHeartCheckTask;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Agao
 * @date 2024/5/28 15:35
 */
@Configuration
public class WsConfig {
  /**
   * 定义WebSocket的管理类 - 持久化连接信息进mysql
   *
   * @return
   */
  @Bean
  @ConditionalOnProperty(
      prefix = Constants.BASE_PROPERTY_PATH,
      value = "ws.datasource",
      havingValue = "mongo")
  @ConditionalOnBean(value = WsConnectionService.class)
  public WebSocketManager webSocketManager(WsConnectionService wsConnectionService) {
    return new MongoWebSocketManager(wsConnectionService);
  }

  /**
   * 定义WebSocket的管理类 - 存储在内存管理
   *
   * @return
   */
  @Bean
  @ConditionalOnMissingBean(WebSocketManager.class)
  public WebSocketManager webSocketManager() {
    return new MemWebSocketManager();
  }

  /**
   * 定义WebSocket的连接点
   *
   * @return
   */
  @Bean
  @ConditionalOnBean({WebSocketManager.class})
  public WsGlobalEndpoint wsGlobalEndpoint() {
    return new WsGlobalEndpoint();
  }

  /**
   * 定义WebSocket任务bean
   *
   * @return
   */
  @Bean
  @ConditionalOnBean(WebSocketManager.class)
  public WebSocketHeartCheckTask webSocketScheduled() {
    return new WebSocketHeartCheckTask();
  }
}
