package com.xx.endpoint;

import com.xx.config.CustomServerEndpointConfig;
import com.xx.exception.WebSocketException;
import com.xx.manager.WebSocketManager;
import com.xx.pojo.WebSocketSession;
import com.xx.util.ApplicationContextUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.WsSession;

/**
 * webSocket 全局端点
 *
 * @author Agao
 * @date 2024/5/28 10:37
 */
@Slf4j
@ServerEndpoint(
    value = "/ws/{connectionType}/{connectionId}",
    configurator = CustomServerEndpointConfig.class)
public class WsGlobalEndpoint {
  private static final String CONNECTION_ID = "connectionId";
  private static final String CONNECTION_TYPE = "connectionType";

  private final WebSocketManager webSocketManager =
      ApplicationContextUtil.getBean(WebSocketManager.class);

  @OnOpen
  public void onOpen(
      Session session,
      @PathParam(CONNECTION_TYPE) String connectionType,
      @PathParam(CONNECTION_ID) String connectionId) {
    String httpSessionId = ((WsSession) session).getHttpSessionId();
    log.info(
        "WebSocket onOpen from connectionType={}, connectionId={}, sessionId={}",
        connectionType,
        connectionId,
        httpSessionId);
    // 鉴权
    boolean exitsEffectiveToken = webSocketManager.validateToken(connectionId);
    if (!exitsEffectiveToken) {
      throw new WebSocketException("创建连接失败，原因是连接ID不合法");
    }
    Map<String, Object> userProperties = session.getUserProperties();
    WebSocketSession connection = new WebSocketSession();
    connection.setConnectionId(connectionId);
    connection.setConnectionType(connectionType);
    connection.setIpAddr((String) userProperties.get("ipAddr"));
    connection.setBrowser((String) userProperties.get("browser"));
    connection.setDeviceType((String) userProperties.get("deviceType"));
    connection.setOsName((String) userProperties.get("osName"));
    connection.setLastHeartTime(LocalDateTime.now());
    connection.setConnectTime(LocalDateTime.now());
    Map<String, Session> shareSessionMap = new HashMap<>(1);
    shareSessionMap.put(httpSessionId, session);
    connection.setShareSessionMap(shareSessionMap);
    log.info("WebSocket opened from {} ", connection);
    webSocketManager.connect(connection);
  }

  @OnMessage
  public void onMessage(
      String message,
      Session session,
      @PathParam(CONNECTION_TYPE) String connectionType,
      @PathParam(CONNECTION_ID) String connectionId) {
    String httpSessionId = ((WsSession) session).getHttpSessionId();
    // 判断是否心跳检测，并且每次接收到消息都会更新心跳
    boolean pong = webSocketManager.pong(connectionType, connectionId, message);
    if (pong) {
      log.debug(
          "WebSocket onMessage {} from connectionType={} , connectionId={}, httpSessionId={}",
          message,
          connectionType,
          connectionId,
          httpSessionId);
      return;
    }
    log.info(
        "WebSocket onMessage {} from connectionType={} , connectionId={}, httpSessionId={}",
        message,
        connectionType,
        connectionId,
        httpSessionId);
    // 正常接收消息处理
    webSocketManager.handleMessage(message);
  }

  @OnClose
  public void onClose(
      Session session,
      @PathParam(CONNECTION_TYPE) String connectionType,
      @PathParam(CONNECTION_ID) String connectionId) {
    String httpSessionId = ((WsSession) session).getHttpSessionId();
    log.info(
        "WebSocket closed from connectionType={}, sessionId={}, connectionId={}",
        connectionType,
        httpSessionId,
        connectionId);
    webSocketManager.disconnect(connectionType, connectionId, "onClose", httpSessionId);
  }

  @OnError
  public void onError(
      Session session,
      Throwable t,
      @PathParam(CONNECTION_TYPE) String connectionType,
      @PathParam(CONNECTION_ID) String connectionId) {
    String httpSessionId = ((WsSession) session).getHttpSessionId();
    log.error(
        "WebSocket happen error from connectionType={}, sessionId={}, connectionId={}",
        connectionType,
        httpSessionId,
        connectionId,
        t);
    // TODO 连接错误后是否有业务处理
  }
}
