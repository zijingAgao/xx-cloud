package com.xx.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class WsConnectionChild implements Serializable {

  private static final long serialVersionUID = 1L;

  /** 目的地id（这里可以是ws连接ID） */
  private String connectionId;

  /** 业务模块ID */
  private String businessId;

  /** 创建连接时间 */
  private String createTime;
}
