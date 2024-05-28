package com.xx.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xx.enums.WsEvent;
import com.xx.enums.WsMessage;
import com.xx.pojo.WebSocketSession;
import com.xx.service.WsConnectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author Agao
 * @date 2024/5/28 13:49
 */
@Slf4j
public class MongoWebSocketManager extends MemWebSocketManager {

  private final WsConnectionService wsConnectionService;

  public MongoWebSocketManager(WsConnectionService wsConnectionService) {
    this.wsConnectionService = wsConnectionService;
  }

  @Override
  public void connect(WebSocketSession webSocketSession) {
    boolean connected = wsConnectionService.connect(webSocketSession);
    if (connected) {
      super.connect(webSocketSession);
    }
  }

  @Override
  public void disconnect(
      String connectionType, String connectionId, String disconnectBy, String httpSessionId) {
    WebSocketSession wsSession = get(connectionType, connectionId);
    if (wsSession != null) {
      super.disconnect(connectionType, connectionId, disconnectBy, httpSessionId);
      wsSession.setDisconnectBy(disconnectBy);
      wsConnectionService.disconnect(wsSession);
    } else {
      log.warn("关闭连接ID不存在：{}", connectionId);
    }
  }

  @Override
  public boolean pong(String connectionType, String connectionId, String message) {
    boolean pong = super.pong(connectionType, connectionId, message);
    if (pong) {
      wsConnectionService.pong(get(connectionType, connectionId));
    }
    return pong;


  }

  @Override
  public boolean validateToken(String connectionId) {
    return wsConnectionService.validateToken(connectionId);
  }

  @Override
  public void handleMessage(String message) {
    if (!StringUtils.hasText(message)){
      log.error("消息为空");
      return;
    }
    ObjectMapper objectMapper = new ObjectMapper();

    WsMessage wsMessage = null;
    try {
      wsMessage = objectMapper.readValue(message, WsMessage.class);
    } catch (JsonProcessingException e) {
      log.warn("消息解析失败：{}", message);
      return;
    }
    // 创建子连接（用于其他业务连接）
    if (wsMessage != null) {
      if (WsEvent.ON_OPEN_CHILD_WS.getName().equals(wsMessage.getWsEvent())) {
        wsConnectionService.openChildWs(wsMessage.getBusinessEvent(), wsMessage.getConnectionId(), wsMessage.getBusinessId());
      } else if (WsEvent.ON_CLOSE_CHILD_WS.getName().equals(wsMessage.getWsEvent())) {
        wsConnectionService.closeChildWs(wsMessage.getBusinessEvent(), wsMessage.getConnectionId());
      }

    }
  }
}
