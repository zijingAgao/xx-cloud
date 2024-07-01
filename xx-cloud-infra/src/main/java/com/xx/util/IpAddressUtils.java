package com.xx.util;

import io.micrometer.core.instrument.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class IpAddressUtils {
  /**
   * 获取Ip地址
   *
   * @param request
   * @return
   */
  public static String getIpAddress(HttpServletRequest request) {
    String Xip = request.getHeader("X-Real-IP");
    String XFor = request.getHeader("X-Forwarded-For");
    if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
      // 多次反向代理后会有多个ip值，第一个ip才是真实ip
      int index = XFor.indexOf(",");
      if (index != -1) {
        return XFor.substring(0, index);
      } else {
        return XFor;
      }
    }
    XFor = Xip;
    if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
      return XFor;
    }
    if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
      XFor = request.getHeader("Proxy-Client-IP");
    }
    if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
      XFor = request.getHeader("WL-Proxy-Client-IP");
    }
    if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
      XFor = request.getHeader("HTTP_CLIENT_IP");
    }
    if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
      XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
      XFor = request.getRemoteAddr();
    }
    return XFor;
  }
}
