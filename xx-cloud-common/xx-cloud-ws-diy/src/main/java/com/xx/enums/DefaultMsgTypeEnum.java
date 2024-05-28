package com.xx.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultMsgTypeEnum {

    PC_NOTIFICATION("pc.notification", "PC通知业务"),
    MR_PRINT("mr.print", "电子病历打印"),
    SQUARED("squared", "九宫格"),
    USER_SCORE("user.score", "用户评分"),
    WS_ONCLOSE("ws.onclose", "WS接收关闭消息"),
    WS_ONCLOSE_OUT("ws.onclose.out", "WS发出关闭消息"),
    ERROR_NOTIFICATION("error.notification", "异常通知飞书"),
    PAD_FLESH_ANALYSE1("pad.flesh.analyse1", "实时推送手麻的血气分析1的数据");
    ;
    private final String name;
    private final String desc;

}