package com.xx.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 连接类型
 *
 * @author Agao
 * @date 2024/5/28 11:06
 */
@Getter
@AllArgsConstructor
public enum WsConnectionType {
  GLOBAL("global", "全局的"),
  ;
  private final String name;
  private final String desc;

  public static WsConnectionType fromName(String msgType) {
    WsConnectionType[] connectionTypes = WsConnectionType.values();
    for (WsConnectionType connectionType : connectionTypes) {
      if (connectionType.getName().equals(msgType)) {
        return connectionType;
      }
    }
    return WsConnectionType.GLOBAL;
  }
}
