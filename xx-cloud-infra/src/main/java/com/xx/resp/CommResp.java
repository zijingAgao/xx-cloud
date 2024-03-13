package com.xx.resp;

import javafx.scene.control.Pagination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公共返回对象
 *
 * @author Agao
 * @date 2024/2/6 10:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommResp<T> {
  private Integer code;
  private String msg;
  private T data;
  private Pagination pagination;

  public CommResp(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public static <T> CommResp<T> success() {
    return new CommResp<>(200, "success", null, null);
  }

  public static <T> CommResp<T> success(T data) {
    return new CommResp<>(200, "success", data, null);
  }

  public static <T> CommResp<T> success(T data, Pagination pagination) {
    return new CommResp<>(200, "success", data, pagination);
  }

  public static <T> CommResp<T> error() {
    return new CommResp<>(400, "error", null, null);
  }

  public static <T> CommResp<T> error(Integer code) {
    return new CommResp<>(code, "error", null, null);
  }

  public static <T> CommResp<T> error(String msg) {
    return new CommResp<>(400, msg, null, null);
  }

  public static <T> CommResp<T> error(T e) {
    return new CommResp<>(400, "", e, null);
  }

  public static <T> CommResp<T> error(Integer code, String msg) {
    return new CommResp<>(code, msg, null, null);
  }

  public static <T> CommResp<T> error(Exception e) {
    return CommResp.error(500, e.getMessage());
  }
}
