package com.jay52.client;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;

/**
 * 连接检测
 */
public class ConnectionListener implements ChannelFutureListener {
    private Client client;

    public ConnectionListener(Client client) {
        this.client = client;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {

        if (!future.isSuccess()) {
            System.out.println("Try to reconnect ");
            final EventLoop loop = future.channel().eventLoop();
            loop.schedule(new Runnable() {
                @Override
                public void run() {
                    client.createBootstrap(new Bootstrap(), loop);
                }
            }, 10L, TimeUnit.SECONDS);// 设置重连间隔为10秒。防止频繁连接造成服务器卡顿(合理设置该值)

        }
    }

}
