package com.xx.event;

import com.xx.pojo.WebSocketSession;

/**
 * @author Agao
 * @date 2024/5/28 15:15
 */
public interface TodoAtRemoved {
  /**
   * 心跳检测下线，额外的执行的事件
   *
   * @param websocket
   */
  void todoWith(WebSocketSession websocket);
}
