package com.xx.config;

import com.xx.server.NettyServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author Agao
 * @date 2024/9/29 13:50
 */
public class NettyServerChannelInitializer  extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast("decoder",new StringDecoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast("encoder",new StringEncoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast(new IdleStateHandler(600, 600, 600, TimeUnit.SECONDS));
        socketChannel.pipeline().addLast(new NettyServerHandler());
    }
}
