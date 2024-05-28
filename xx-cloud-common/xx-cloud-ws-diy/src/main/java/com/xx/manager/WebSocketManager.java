package com.xx.manager;

import com.xx.event.TodoAtRemoved;
import com.xx.exception.WebSocketException;
import com.xx.pojo.WebSocketSession;
import com.xx.util.WebSocketUtil;
import io.micrometer.core.instrument.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 封装WebSocket管理器，在原始处理器外层包裹一层
 *
 * @author Agao
 * @date 2024/5/28 11:15
 */
public interface WebSocketManager {

  /**
   * 在websocket过期时间内发送心跳检测消息
   * 定时检测websocket的心跳时间跟现在的间隔，超过设定的值说明失去了心跳
   *
   * @param expired       心跳检测过期间隔时间
   * @param todoAtRemoved 在删除的时候额外需要做的事情
   */
  void check(long expired, TodoAtRemoved todoAtRemoved);
  /**
   * 根据标识获取websocket session
   *
   * @param connectionId 标识
   * @return WebSocket
   */
  WebSocketSession get(String connectionType, String connectionId);

  /**
   * 放入一个 websocket session
   *
   * @param webSocketSession websocket
   */
  void connect(WebSocketSession webSocketSession);

  /**
   * 断开连接
   *
   * @param connectionType 连接类型
   * @param connectionId 标识
   * @param disconnectBy 断开原因
   */
  void disconnect(
      String connectionType, String connectionId, String disconnectBy, String httpSessionId);

  /**
   * 验证token
   *
   * @param connectionId
   * @return
   */
  boolean validateToken(String connectionId);

  /**
   * 处理消息
   *
   * @param message
   */
  void handleMessage(String message);

  /**
   * 构建连接map key
   *
   * @param connectionType
   * @param connectionId
   * @return
   */
  default String buildConnectionKey(String connectionType, String connectionId) {
    if (StringUtils.isBlank(connectionId)) {
      throw new WebSocketException("WebSocket连接id不能为空");
    }
    if (StringUtils.isBlank(connectionType)) {
      throw new WebSocketException("WebSocket连接Type不能为空");
    }
    return connectionType + "_" + connectionId;
  }

  /**
   * 心跳检测发起
   *
   * @param connectionId
   */
  default void ping(String connectionType, String connectionId) {
    if (StringUtils.isNotBlank(connectionId)) {
      WebSocketSession webSocketSession = get(connectionType, connectionId);
      webSocketSession.getShareSessionMap().values().forEach(session -> {
        WebSocketUtil.sendMessageAsync(session, "ping");
      });
    }
  }

  default boolean pong(String connectionType, String connectionId, String message) {
    WebSocketSession webSocketSession = get(connectionType, connectionId);
    if (webSocketSession != null) {
      webSocketSession.setLastHeartTime(LocalDateTime.now());
    }
    return "pong".equals(message);
  }

  /**
   * 统计所有在线人数
   *
   * @return 所有在线人数
   */
   int size();

}
