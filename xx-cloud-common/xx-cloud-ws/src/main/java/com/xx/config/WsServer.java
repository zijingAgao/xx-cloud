package com.xx.config;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @author Agao
 * @date 2024/5/28 9:13
 */
@Slf4j
@Configuration
@ServerEndpoint(value = "/ws")
public class WsServer {
  /** 在线连接数 */
  private static volatile int onlineCount = 0;

  /** concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识 */
  public static CopyOnWriteArraySet<WsServer> webSocketSet = new CopyOnWriteArraySet<>();

  /** 与某个客户端的连接会话，需要通过它来给客户端发送数据 */
  private Session session;

  /**
   * 连接建立成功调用的方法
   *
   * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
   */
  @OnOpen
  public void onOpen(Session session) {
    this.session = session;
    webSocketSet.add(this); // 加入set中
    addOnlineCount(); // 在线数加1
    log.info("有新连接加入！当前在线人数为: {}", getOnlineCount());
  }

  /** 连接关闭调用的方法 */
  @OnClose
  public void onClose() {
    webSocketSet.remove(this); // 从set中删除
    subOnlineCount(); // 在线数减1
    log.info("有一连接关闭！当前在线人数为: {}", getOnlineCount());
  }

  /**
   * 收到客户端消息后调用的方法
   *
   * @param message 客户端发送过来的消息
   * @param session 可选的参数
   */
  @OnMessage
  public void onMessage(String message, Session session) {
    log.info("来自客户端的消息: {}", message);
    // 群发消息
    for (WsServer item : webSocketSet) {
      try {
        item.sendMessage(message);
      } catch (IOException e) {
        log.warn("sessionId :{} ,发送消息出现异常: ", session.getId(), e);
      }
    }
  }

  /**
   * 发生错误时调用
   *
   * @param session
   * @param error
   */
  @OnError
  public void onError(Session session, Throwable error) {
    log.error("sessionId:{} ,发生错误: ", session.getId(), error);
  }

  /**
   * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
   *
   * @param message
   * @throws IOException
   */
  public void sendMessage(String message) throws IOException {
    this.session.getBasicRemote().sendText(message);
  }

  public static synchronized int getOnlineCount() {
    return onlineCount;
  }

  public static synchronized void addOnlineCount() {
    WsServer.onlineCount++;
  }

  public static synchronized void subOnlineCount() {
    WsServer.onlineCount--;
  }
}
