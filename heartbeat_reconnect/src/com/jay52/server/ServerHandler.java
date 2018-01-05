package com.jay52.server;

import io.netty.channel.*;

import com.jay52.code.entity.CustomProtocol;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        // 用于获取客户端发来的数据信息  
    	CustomProtocol body = (CustomProtocol) msg;
        System.out.println("Server接收的客户端的信息 :" + body.toString());
        byte[] content = body.getContent();
        String str = new String(content);
        System.out.println("内容： " + str);

        if (!"Pong".equals(str)){
            // 回写数据给客户端
            String resp_msg = "Hi Client,welcome...";
            CustomProtocol response = new CustomProtocol(resp_msg.getBytes().length,
                    resp_msg.getBytes());
            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 当发生读写空闲的时候触发
        if (evt instanceof IdleStateEvent){
            // 发送 Ping 心跳给客户端
            String ping_msg = "Ping";
            CustomProtocol response = new CustomProtocol(ping_msg.getBytes().length,
                    ping_msg.getBytes());
            // 当发生错误的时候关闭 future
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    }
}
