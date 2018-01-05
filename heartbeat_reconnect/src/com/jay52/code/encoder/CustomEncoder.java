package com.jay52.code.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.jay52.code.entity.CustomProtocol;

/**
 * 自定义协议的编码器
 * @author Administrator
 *
 */
public class CustomEncoder extends MessageToByteEncoder<CustomProtocol>{

	@Override
	protected void encode(ChannelHandlerContext ctx, CustomProtocol msg,
			ByteBuf out) throws Exception {
		// 写入消息的具体内容
		// 1、写入消息的开头的信息标志
		out.writeInt(msg.getHead_data());
		// 2、写入消息的长度
		out.writeInt(msg.getContentLength());
		// 3、写入消息的内容
		out.writeBytes(msg.getContent());
	}

}
