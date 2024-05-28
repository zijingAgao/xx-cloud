package com.xx.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class IpUtil {

  private static final String UNKNOWN = "unknown";
  private static final String X_FORWARDED_FOR = "X-Forwarded-For";

  /**
   * 获取请求IP
   *
   * @return String IP
   */
  public static String getHttpServletRequestIpAddress(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
  }

  public static String getRealIp() {
    // 本地IP，如果没有配置外网IP则返回它
    String localip = null;
    // 外网IP
    String netip = null;
    Enumeration<NetworkInterface> netInterfaces = null;
    try {
      netInterfaces = NetworkInterface.getNetworkInterfaces();
    } catch (SocketException e) {
      log.warn("获取本机网络信息失败", e);
    }
    InetAddress ip = null;
    // 是否找到外网IP
    boolean finded = false;
    while (netInterfaces.hasMoreElements() && !finded) {
      NetworkInterface ni = netInterfaces.nextElement();
      Enumeration<InetAddress> address = ni.getInetAddresses();
      while (address.hasMoreElements()) {
        ip = address.nextElement();
        // 外网IP
        if (!ip.isSiteLocalAddress()
            && !ip.isLoopbackAddress()
            && !ip.getHostAddress().contains(":")) {
          netip = ip.getHostAddress();
          finded = true;
          break;
          // 内网IP
        } else if (ip.isSiteLocalAddress()
            && !ip.isLoopbackAddress()
            && !ip.getHostAddress().contains(":")) {
          localip = ip.getHostAddress();
        }
      }
    }

    if (StringUtils.hasText(netip)) {
      return netip;
    }
    return localip;
  }
}
