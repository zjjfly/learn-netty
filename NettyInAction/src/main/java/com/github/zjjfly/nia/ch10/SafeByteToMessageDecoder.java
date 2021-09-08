package com.github.zjjfly.nia.ch10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * netty把所以读取到的字节放入内存中,所以为了避免内存耗光,最好设置一个最大字节数
 * 当可读字节数超过这个最大字节数的时候,抛出TooLongFrameException
 *
 * @author zjjfly
 */
public class SafeByteToMessageDecoder extends ByteToMessageDecoder {

    private static final int MAX_FRAME_SIZE = 1024;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();
        if (readableBytes > MAX_FRAME_SIZE) {
            in.skipBytes(MAX_FRAME_SIZE);
            throw new TooLongFrameException("Frame too big!");
        }
        //TODO
    }
}
