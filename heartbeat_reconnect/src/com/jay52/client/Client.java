package com.jay52.client;

import com.jay52.code.decoder.CustomDecoder;
import com.jay52.code.encoder.CustomEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 采用netty实现的客户端
 */
public final class Client {

     static final String HOST = "127.0.0.1";
     static final int PORT = 9999;

    private static final EventLoopGroup loop = new NioEventLoopGroup();

    public Bootstrap createBootstrap(Bootstrap bootstrap,
                                     EventLoopGroup eventLoop) {
        if (bootstrap != null) {
            final ClientHandler handler = new ClientHandler(this);
            bootstrap.group(eventLoop);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch)
                        throws Exception {
                    ch.pipeline().addLast(new CustomDecoder());// 添加自定义解码器
                    ch.pipeline().addLast(new CustomEncoder());// 添加自定义编码器
                    ch.pipeline().addLast(handler);
                }
            });
            bootstrap.remoteAddress(HOST,PORT);
            bootstrap.connect().addListener(new ConnectionListener(this));
        }
        return bootstrap;
    }

    public void run() {
        createBootstrap(new Bootstrap(), loop);
    }

    public static void main(String[] args) {
        new Client().run();
    }
}  