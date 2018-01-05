package com.jay52.server;

import com.jay52.code.decoder.CustomDecoder;
import com.jay52.code.encoder.CustomEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 采用netty实现的服务端
 */
public class Server {  
	  
    public Server() {  
    }  
  
    public void bind(int port) throws Exception {  
        // 配置NIO线程组  
        EventLoopGroup bossGroup = new NioEventLoopGroup();  
        EventLoopGroup workerGroup = new NioEventLoopGroup();  
        try {  
            // 服务器辅助启动类配置  
            ServerBootstrap b = new ServerBootstrap();  
            b.group(bossGroup, workerGroup)  
                    .channel(NioServerSocketChannel.class)  
                    .handler(new LoggingHandler(LogLevel.INFO))  
                    .childHandler(new ChildChannelHandler())//  
                    .option(ChannelOption.SO_BACKLOG, 1024) // 设置tcp缓冲区
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定端口 同步等待绑定成功  
            ChannelFuture f = b.bind(port).sync();
            // 等到服务端监听端口关闭  
            f.channel().closeFuture().sync();  
        } finally {  
            // 优雅释放线程资源  
            workerGroup.shutdownGracefully();  
            bossGroup.shutdownGracefully();  
        }  
    }  
  
    /** 
     * 网络事件处理器 
     */  
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {  
        @Override  
        protected void initChannel(SocketChannel ch) throws Exception {
            // 添加空闲时间处理器,设置读写超时10s
            ch.pipeline().addLast(new IdleStateHandler(0,0,10));
            // 添加自定义协议的编解码工具  
            ch.pipeline().addLast(new CustomEncoder());
            ch.pipeline().addLast(new CustomDecoder());
            // 处理网络IO  
            ch.pipeline().addLast(new ServerHandler());
        }  
    }  
  
    public static void main(String[] args) throws Exception {  
        new Server().bind(9999);  
    }  
}  