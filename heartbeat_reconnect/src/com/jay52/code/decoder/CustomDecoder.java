package com.jay52.code.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import com.jay52.code.entity.Constant;
import com.jay52.code.entity.CustomProtocol;

/**
 * 自定义协议的解码器
 * @author Administrator
 * 
 */
public class CustomDecoder extends ByteToMessageDecoder {

	/**
	 * 协议开始的标志head_data,int类型，占据4个字节 
	 * 表示数据的长度contentLength,int类型，占据4个字节
	 */
	public final int BASE_LENGTH = 4 + 4;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer,
			List<Object> out) throws Exception {
		// 可读长度必须大于基本长度
		if (buffer.readableBytes() >= BASE_LENGTH) {
			// 防止Socket字节流攻击
			// 防止客户端传来的数据过大
			if (buffer.readableBytes() > 2048) {
				// 将当前读取器索引在此缓冲区中增加指定长度。
				buffer.skipBytes(buffer.readableBytes());
			}

			// 记录包头开始的index
			int beginReader;

			while (true) {
				// 获取包头开始的index
				beginReader = buffer.readerIndex();
				// 标记包头开始的index
				buffer.markReaderIndex();
				// 读到了协议的开始标志，结束while循环
				if (buffer.readInt() == Constant.HEAD_DATA) {
					break;
				}

				// 未读到包头，略过一个字节
				// 每次略过一个字节去读取包头信息的开始标记
				buffer.resetReaderIndex();
				buffer.readByte();

				// 当略过一个字节后，数据包的长度又变得不满足，此时应该结束，等待后面的数据包到来
				if (buffer.readableBytes() < BASE_LENGTH) {
					return;
				}

			}
			// 消息的长度
			int length = buffer.readInt();
			// 判断请求数据包数据是否到齐
			if (buffer.readableBytes() < length) {
				// 还原读指针
				buffer.readerIndex(beginReader);
				return;
			}

			// 读取data数据
			byte[] data = new byte[length];
			buffer.readBytes(data);

			CustomProtocol customProtocol = new CustomProtocol(
					data.length, data);
			out.add(customProtocol);
		}

	}

}
