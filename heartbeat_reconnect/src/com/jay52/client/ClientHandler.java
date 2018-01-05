package com.jay52.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.util.ReferenceCountUtil;

import com.jay52.code.entity.CustomProtocol;

/**
 * 用于读取服务端发来的数据
 * @author Administrator
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private Client client;

    public ClientHandler(Client client) {
        this.client = client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 发送自定义协议的消息
        String data = "Hello Server,I am client ...";
        // 获得要发送信息的字节数组
        byte[] content = data.getBytes();
        // 要发送信息的长度
        int contentLength = content.length;
        CustomProtocol protocol = new CustomProtocol(contentLength, content);
        ctx.writeAndFlush(protocol);
    }

    // 用于获取服务端发来的数据信息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        CustomProtocol body = (CustomProtocol) msg;
        System.out.println("Client 接收的服务端的信息 :" + body.toString());

        byte[] content = body.getContent();
        String str = new String(content);
        System.out.println("内容： " + str);

        // 当客户端收到服务端的 Ping心跳时发送 Pong心跳来响应
        if ("Ping".equals(str)){
            String pong_msg = "Pong";
            CustomProtocol response = new CustomProtocol(pong_msg.getBytes().length,
                    pong_msg.getBytes());
            ctx.writeAndFlush(response);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        // 当出现异常时打印异常并关闭ChannelHandlerContext
        cause.printStackTrace();;
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 当客户端 Channel 处于 inactive 时进行重连
        final EventLoop eventLoop = ctx.channel().eventLoop();
        client.createBootstrap(new Bootstrap(), eventLoop);

    }
}
