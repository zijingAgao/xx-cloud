package com.xx.filter;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理websocket连接子协议
 *
 * @author jugheadzhou
 */
@WebFilter(filterName = "WebsocketFilter", urlPatterns = "/websocket/*")
public class WebsocketFilter implements Filter {
  /** websocket连接子协议的默认名称 */
  private static final String WebSocketProtocol = "Sec-WebSocket-Protocol";

  /** 处理websocket连接子协议 */
  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    // 如果客户端创建websocket链接时规定了子协议，则需要在响应头设置对应的子协议
    response.setHeader(WebSocketProtocol, request.getHeader(WebSocketProtocol));
    filterChain.doFilter(servletRequest, servletResponse);
  }
}
