package com.xx.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Agao
 * @date 2024/7/4 22:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMsg {
  private Integer code;
  private String msg;
  private String content;

  public EventMsg(String content) {
    this.content = content;
  }
}
