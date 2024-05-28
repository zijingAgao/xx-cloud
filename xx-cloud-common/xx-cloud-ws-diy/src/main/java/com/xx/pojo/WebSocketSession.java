package com.xx.pojo;

import com.xx.enums.WsConnectionType;
import lombok.Data;

import javax.websocket.Session;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author Agao
 * @date 2024/5/28 11:14
 */
@Data
public class WebSocketSession {
    /**
     * 连接ID 全局唯一
     */
    private String connectionId;

    /**
     * 连接类型
     *
     * @see WsConnectionType
     */
    private String connectionType;

    /**
     * 登录IP地址
     */
    private String ipAddr;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 操作系统名称
     */
    private String osName;
    /**
     * 同一个链接多个session
     */
    private Map<String, Session> shareSessionMap;

    /**
     * 创建连接时间
     */
    private LocalDateTime connectTime;

    /**
     * 最后心跳时间
     */
    private LocalDateTime lastHeartTime;

    /**
     * 连接断开操作对象
     */
    private String disconnectBy;
}
