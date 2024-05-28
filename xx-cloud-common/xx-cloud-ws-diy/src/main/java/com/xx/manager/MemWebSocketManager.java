package com.xx.manager;

import com.xx.event.TodoAtRemoved;
import com.xx.pojo.WebSocketSession;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author Agao
 * @date 2024/5/28 13:49
 */
@Slf4j
public class MemWebSocketManager implements WebSocketManager {
  /** websocket的session不能被序列化，所以只能保存在内存中，不能存在redis中。 通过redis的发布订阅功能实现集群部署情况下的websocket能够被正确消费 */
  private final Map<String, WebSocketSession> connectionMap = new ConcurrentHashMap<>(100);

  /**
   * 在websocket过期时间内发送心跳检测消息 定时检测websocket的心跳时间跟现在的间隔，超过设定的值说明失去了心跳
   *
   * @param expired 心跳检测过期间隔时间
   * @param todoAtRemoved 在删除的时候额外需要做的事情
   */
  @Override
  public void check(long expired, TodoAtRemoved todoAtRemoved) {
    LocalDateTime now = LocalDateTime.now();
    connectionMap.entrySet().parallelStream()
        .forEach(
            entry -> {
              WebSocketSession webSocketSession = entry.getValue();
              String connectionId = webSocketSession.getConnectionId();
              String connectionType = webSocketSession.getConnectionType();
              try {
                long interval =
                    Duration.between(webSocketSession.getLastHeartTime(), now).toMillis();
                if (interval < expired) {
                  // 在websocket过期时间内发送心跳检测消息
                  ping(connectionType, connectionId);
                } else {

                  log.warn("WebSocket连接已失效{}", webSocketSession);
                  // 删除连接
                  this.disconnect(connectionType, connectionId, "ws service heart expired", null);
                  // 删除连接额外操作
                  todoAtRemoved.todoWith(webSocketSession);
                }
              } catch (Exception e) {
                this.disconnect(connectionType, connectionId, "ws service heart expired", null);
                log.error("心跳检测发生异常，当前连接ID = {}", connectionId, e);
              }
            });
  }

  @Override
  public WebSocketSession get(String connectionType, String connectionId) {
    return connectionMap.get(buildConnectionKey(connectionType, connectionId));
  }

  @Override
  public void connect(WebSocketSession webSocketSession) {
    String connectionId = webSocketSession.getConnectionId();
    String connectionType = webSocketSession.getConnectionType();
    String key = buildConnectionKey(connectionType, connectionId);

    if (connectionMap.containsKey(key)) {
      WebSocketSession wss = connectionMap.get(key);
      Map<String, Session> shareSessionMap = wss.getShareSessionMap();
      // 限制最多9个共享session
      if (shareSessionMap.size() <= 10) {
        shareSessionMap.putAll(webSocketSession.getShareSessionMap());
        log.info("创建共享websocket session, 当前共享session数量：{}", shareSessionMap.size());
      }
      return;
    }
    // 后面的覆盖前面的
    connectionMap.put(key, webSocketSession);
  }

  @Override
  public void disconnect(
      String connectionType, String connectionId, String disconnectBy, String httpSessionId) {
    String key = buildConnectionKey(connectionType, connectionId);
    if (connectionMap.containsKey(key)) {
      WebSocketSession webSocketSession = connectionMap.get(key);
      Map<String, Session> shareSessionMap = webSocketSession.getShareSessionMap();
      if (StringUtils.hasText(httpSessionId)
          && shareSessionMap != null
          && shareSessionMap.size() > 1) {
        shareSessionMap.remove(httpSessionId);
        return;
      }
      connectionMap.remove(key);
    }
  }

  @Override
  public boolean validateToken(String connectionId) {
    return true;
  }

  @Override
  public void handleMessage(String message) {
    // todo: 内存模式的消息处理
  }

  @Override
  public int size() {
    return connectionMap.size();
  }
}
