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
public class R<T> {
  private Integer code;
  private String msg;
  private T data;
  private Pagination pagination;

  public R(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public static <T> R<T> success() {
    return new R<>(200, "success", null, null);
  }

  public static <T> R<T> success(T data) {
    return new R<>(200, "success", data, null);
  }

  public static <T> R<T> success(T data, Pagination pagination) {
    return new R<>(200, "success", data, pagination);
  }

  public static <T> R<T> error() {
    return new R<>(400, "error", null, null);
  }

  public static <T> R<T> error(Integer code) {
    return new R<>(code, "error", null, null);
  }

  public static <T> R<T> error(String msg) {
    return new R<>(400, msg, null, null);
  }

  public static <T> R<T> error(T e) {
    return new R<>(400, "", e, null);
  }

  public static <T> R<T> error(Integer code, String msg) {
    return new R<>(code, msg, null, null);
  }

  public static <T> R<T> error(Exception e) {
    return R.error(500, e.getMessage());
  }
}
