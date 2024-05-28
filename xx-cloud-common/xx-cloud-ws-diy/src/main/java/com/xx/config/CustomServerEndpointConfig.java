package com.xx.config;

import com.xx.util.IpUtil;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 自定义 ServerEndpointConfig
 *
 * @author Agao
 * @date 2024/5/28 10:41
 */
@Configuration
public class CustomServerEndpointConfig extends ServerEndpointConfig.Configurator {
  /**
   * 重写 modifyHandshake 方法，添加所需的参数
   *
   * @param sec
   * @param request
   * @param response
   */
  @Override
  public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
    // session
    HttpSession httpSession = (HttpSession) request.getHttpSession();
    Map<String, Object> userProperties = sec.getUserProperties();
    userProperties.put(HttpSession.class.getName(), httpSession);
    // ip
    HttpServletRequest req = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    String ip = IpUtil.getHttpServletRequestIpAddress(req);
    userProperties.put("ipAddr", ip);
    // 浏览器信息
    UserAgent userAgent = UserAgent.parseUserAgentString(req.getHeader("User-Agent"));
    String browserName = userAgent.getBrowser().getName();
    OperatingSystem operatingSystem = userAgent.getOperatingSystem();
    userProperties.put("browser", browserName);
    userProperties.put("deviceType", operatingSystem.getDeviceType().getName());
    userProperties.put("osName", operatingSystem.getName());
  }
}
