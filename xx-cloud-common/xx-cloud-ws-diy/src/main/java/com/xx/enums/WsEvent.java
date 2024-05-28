package com.xx.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * websocket 默认事件
 *
 * @author jugheadzhou
 */

@Getter
@AllArgsConstructor
public enum WsEvent {
    ON_OPEN_CHILD_WS("onOpenChildWs", "创建子连接"),
    ON_CLOSE_CHILD_WS("onCloseChildWs", "关闭子连接"),
    ON_OPEN("onOpen", "连接创建"),
    ON_MESSAGE("onMessage", "连接通信"),
    ON_CLOSE("onClose", "连接关闭"),
    ON_ERROR("onError", "连接异常");
    private final String name;
    private final String desc;

    public static WsEvent getWsEvent(String name) {
        for (WsEvent enums : WsEvent.values()) {
            if (enums.getName().equals(name)) {
                return enums;
            }
        }
        return null;
    }

}