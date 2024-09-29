package com.xx.server;

import com.xx.config.NettyProperties;
import com.xx.config.NettyServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * netty 服务端
 *
 * @author Agao
 * @date 2024/9/29 13:37
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NettyServer {
  private final NettyProperties nettyProperties;

  /** 主线程组 */
  private final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);

  /** 工作线程组 */
  private final NioEventLoopGroup workerGroup = new NioEventLoopGroup(10);

  /** 开启Netty服务端 */
  @PostConstruct
  public void start() {
      try {
          ServerBootstrap bootstrap =
              new ServerBootstrap()
                  .channel(NioServerSocketChannel.class)
                  // 绑定线程池
                  .group(bossGroup, workerGroup)
                  // 三次握手中的A、B队列总和最大值（第二次握手加入A, 第三次握手从A移动到B, accept 后从B取出）
                  .option(ChannelOption.SO_BACKLOG, 1024 * 10)
                  // 解决端口占用问题, 可以共用服务器端口（即使该端口已被其他端口占用）
                  .option(ChannelOption.SO_REUSEADDR, true)
                  // 接收消息缓冲区大小
                  .option(ChannelOption.SO_RCVBUF, 1024 * 1024 * 10)
                  // 发送消息缓冲区大小
                  .option(ChannelOption.SO_SNDBUF, 1024 * 1024 * 10)
                  // 用于启用或关于Nagle算法。如果要求高实时性，有数据发送时就马上发送，就将该选项设置为true关闭Nagle算法；
                  // 如果要减少发送次数减少网络交互，就设置为false等累积一定大小后再发送
                  .option(ChannelOption.TCP_NODELAY, true)
                  // 用于检测长时间没有数据传输的连接状态，当设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文
                  .option(ChannelOption.SO_KEEPALIVE, true)
                  .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(1024 * 10))
                  .childHandler(new NettyServerChannelInitializer())
                  // 当用户调用close（）方法的时候，函数返回，在可能的情况下，尽量发送数据，不一定保证全部发送成功
                  // 使用SO_LINGER可以阻塞close()的调用时间，直到数据完全发送
                  .option(ChannelOption.SO_LINGER, 2000);

          log.info("start netty server--start listening port");
          ChannelFuture future = bootstrap.bind(new InetSocketAddress(nettyProperties.getHost(), nettyProperties.getPort())).sync();
          if (future.isSuccess()) {
              log.info("start netty server--success");
          }

      } catch (InterruptedException e) {
          log.warn("netty server start fail, ",e);
          bossGroup.shutdownGracefully();
          workerGroup.shutdownGracefully();
      }
  }


  @PreDestroy
  public void destroy() {
      bossGroup.shutdownGracefully().syncUninterruptibly();
      workerGroup.shutdownGracefully().syncUninterruptibly();
      log.info("service destroy, netty server stop");
  }
}
