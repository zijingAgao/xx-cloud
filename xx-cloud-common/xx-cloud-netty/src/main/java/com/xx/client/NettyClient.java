package com.xx.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Agao
 * @date 2024/9/29 14:01
 */
@Slf4j
public class NettyClient {

  private static final String HOST = "192.168.2.236";
  private static final Integer PORT = 8299;

  public static void main(String[] args) {
    Bootstrap bootstrap =
        new Bootstrap()
            .group(new NioEventLoopGroup())
            .channel(NioSocketChannel.class)
            .handler(
                new ChannelInitializer<SocketChannel>() {
                  @Override
                  protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new StringEncoder());
                  }
                });

    Channel channel = bootstrap.connect(HOST, PORT).channel();

    while (true) {
      channel.writeAndFlush("hello world");
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        log.warn("sleep error: ", e);
      }
    }
  }
}
