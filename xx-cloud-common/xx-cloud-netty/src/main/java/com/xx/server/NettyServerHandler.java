package com.xx.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * netty 消息处理器
 *
 * @author Agao
 * @date 2024/9/29 13:51
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

  /** 管理一个全局map，保存连接进服务端的通道数量 */
  private static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> CHANNEL_MAP = new ConcurrentHashMap<>();

  /**
   * 有客户端连接服务器会触发此函数
   *
   * @param ctx
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    try {
      InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
      String clientIp = inSocket.getAddress().getHostAddress();
      int clientPort = inSocket.getPort();
      // 获取连接通道唯一标识
      ChannelId channelId = ctx.channel().id();
      // 如果map中不包含此连接，就保存连接
      if (CHANNEL_MAP.containsKey(channelId)) {
        log.info("netty-socket-客户端【{}】是连接状态，此时的连接通道数量: {}", channelId, CHANNEL_MAP.size());
      } else {
        // 保存连接
        CHANNEL_MAP.put(channelId, ctx);
        log.info(
            "netty-socket-客户端【{}】连接netty服务器[IP:{}--->PORT:{}]，此时的连接通道数量: {}",
            channelId,
            clientIp,
            clientPort,
            CHANNEL_MAP.size());
      }
    } catch (Exception e) {
      log.error("channelActive error: ", e);
    }
  }

  /**
   * 有客户端终止连接服务器会触发此函数
   *
   * @param ctx
   */
  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    try {
      InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
      String clientIp = inSocket.getAddress().getHostAddress();
      ChannelId channelId = ctx.channel().id();
      // 包含此客户端才去删除
      if (CHANNEL_MAP.containsKey(channelId)) {
        // 删除连接
        CHANNEL_MAP.remove(channelId);
        log.info(
            "netty客户端【{}】退出netty服务器[IP:{}--->PORT:{}]，此时的连接通道数量: {}",
            channelId,
            clientIp,
            inSocket.getPort(),
            CHANNEL_MAP.size());
      }
    } catch (Exception e) {
      log.error("channelInactive error:", e);
    }
  }

  /**
   * 有客户端发消息会触发此函数
   *
   * @param ctx
   * @param msg
   * @throws Exception
   */
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg == null) {
      throw new Exception("netty-socket加载客户端报文为空!");
    }
    log.info("netty-socket-接收客户端报文【{}】 :{}", ctx.channel().id(), msg);
    // 给客户端发送消息
    this.channelWrite(ctx.channel().id(), msg);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
    log.info("netty-socket-【{}】 数据接收完毕", ctx.channel().id());
  }

  /**
   * 服务端给客户端发送消息
   *
   * @param channelId 连接通道唯一id
   * @param msg 需要发送的消息内容
   * @throws Exception
   */
  public void channelWrite(ChannelId channelId, Object msg) {
    try {
      ChannelHandlerContext ctx = CHANNEL_MAP.get(channelId);
      if (ctx == null) {
        log.info("netty-socket-通道【{}】不存在", channelId);
        return;
      }
      if (msg == null || msg == "") {
        log.info("netty-socket-服务端响应空的消息");
        return;
      }
      // 将客户端的信息直接返回写入ctx,同时进行刷新
      ctx.writeAndFlush(msg);
    } catch (Exception e) {
      log.error("channelWrite error: ", e);
    }
  }

  /**
   * 事件触发（读写超时、总超时）
   *
   * @param ctx
   * @param evt
   */
  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
    try {
      String socketString = ctx.channel().remoteAddress().toString();
      if (evt instanceof IdleStateEvent) {
        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state() == IdleState.READER_IDLE) {
          log.info("userEventTriggered-netty-client: {} READER_IDLE 读超时", socketString);
          ctx.disconnect();
        } else if (event.state() == IdleState.WRITER_IDLE) {
          log.info("userEventTriggered-netty-client: {} WRITER_IDLE 写超时", socketString);
          ctx.disconnect();
        } else if (event.state() == IdleState.ALL_IDLE) {
          log.info("userEventTriggered-netty-client: {} ALL_IDLE 总超时", socketString);
          ctx.disconnect();
        }
      }
    } catch (Exception e) {
      log.error("userEventTriggered error: ", e);
    }
  }

  /**
   * 发生异常会触发此函数
   *
   * @param ctx
   * @param cause
   * @throws Exception
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error("exceptionCaught error:", cause);
    ctx.close();
    log.error("netty-Socket：{} 发生了错误,此连接被关闭此时连通数量: {}", ctx.channel().id(), CHANNEL_MAP.size());
  }
}
