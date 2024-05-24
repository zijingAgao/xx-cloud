package com.xx.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Agao
 * @date 2024/5/24 10:14
 */
@Data
@NoArgsConstructor
public class KafkaMsg<T> {
  private Integer code;
  private String msg;
  private T data;

  public KafkaMsg(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
    this.data = null;
  }
}
