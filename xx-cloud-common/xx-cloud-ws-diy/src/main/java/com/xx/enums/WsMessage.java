package com.xx.enums;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jugheadzhou
 * @date 2021-12-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WsMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 接收人ID
     */
    private String toUserId;

    /**
     * websocket默认事件
     * 1. onOpen -> 连接创建
     * 2. onMessage -> 连接通信
     * 3. onClose -> 连接关闭
     * 4. onError -> 连接异常
     *
     * @see WsEvent
     */
    private String wsEvent;

    /**
     * websocket连接类型
     * <p>
     * eg.
     * 1. global -> 全局
     *
     * @see WsConnectionType
     */
    private String connectionType;

    /**
     * 连接ID
     */
    private String connectionId;

    /**
     * websocket通信业务事件
     * <p>
     * eg.
     * 1. notification -> 通知
     *
     * @see DefaultMsgTypeEnum
     */
    private String businessEvent;

    /**
     * 业务ID
     * <p>
     * eg.
     * 1. notification -> 通知
     *
     * @see DefaultMsgTypeEnum
     */
    private String businessId;


    /**
     * 消息内容 (默认是json字符串)
     */
    private String content;


}
