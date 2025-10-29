package com.xx.resp;

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
public class XR<T> {
  private Integer code;
  private String msg;
  private T data;
  private XPage xPage;

  public XR(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public static <T> XR<T> success() {
    return new XR<>(200, "success", null, null);
  }

  public static <T> XR<T> success(T data) {
    return new XR<>(200, "success", data, null);
  }

  public static <T> XR<T> success(T data, XPage xPage) {
    return new XR<>(200, "success", data, xPage);
  }

  public static <T> XR<T> error() {
    return new XR<>(400, "error", null, null);
  }

  public static <T> XR<T> error(Integer code) {
    return new XR<>(code, "error", null, null);
  }

  public static <T> XR<T> error(String msg) {
    return new XR<>(400, msg, null, null);
  }

  public static <T> XR<T> error(T e) {
    return new XR<>(400, "", e, null);
  }

  public static <T> XR<T> error(Integer code, String msg) {
    return new XR<>(code, msg, null, null);
  }

  public static <T> XR<T> error(Exception e) {
    return XR.error(500, e.getMessage());
  }
}
