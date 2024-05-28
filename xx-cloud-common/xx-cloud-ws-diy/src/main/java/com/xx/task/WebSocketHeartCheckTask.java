package com.xx.task;

import com.xx.manager.WebSocketManager;
import com.xx.service.WsConnectionService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Agao
 * @date 2024/5/28 15:34
 */
@Slf4j
@Component
public class WebSocketHeartCheckTask {
  /** 心跳检测过期间隔时间 */
  @Value("${websocket.heartCheck.expired:90000}")
  private long expired;

  @Resource private WebSocketManager webSocketManager;
  @Resource private WsConnectionService wsConnectionService;

  /** 任务1 检测websocket连接的心跳 */
  @Scheduled(fixedRate = 1000 * 30)
  public void webSocketHeartCheckJob() {
    log.info("开始检测websocket连接的心跳");
    webSocketManager.check(
        expired, (webSocketSession) -> log.info("删除失去心跳的WebSocket: {}", webSocketSession));
    log.info("websocket连接数量：{}", webSocketManager.size());
  }
}
