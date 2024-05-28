package com.xx.entity;

import com.xx.enums.WsConnectionType;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * 持久化 ws连接信息
 *
 * @author Agao
 * @date 2024/5/28 11:03
 */
@Data
public class WsConnection {
  /** 主键ID */
  @Id private Integer id;

  /** 连接ID 全局唯一 */
  private String connectionId;

  /**
   * 连接类型
   *
   * @see WsConnectionType
   */
  private String connectionType;

  /** 登录IP地址 */
  private String ipAddr;

  /** 浏览器类型 */
  private String browser;

  /** 设备类型 */
  private String deviceType;

  /** 操作系统名称 */
  private String osName;

  /** 是否保持连接 */
  private Boolean connected;

  /** 创建连接时间 */
  private LocalDateTime connectTime;

  /** 最后心跳时间 */
  private LocalDateTime lastHeartTime;

  /** 连接断开时间 */
  private LocalDateTime disconnectTime;

  /** 连接断开操作对象 */
  private String disconnectBy;
}
