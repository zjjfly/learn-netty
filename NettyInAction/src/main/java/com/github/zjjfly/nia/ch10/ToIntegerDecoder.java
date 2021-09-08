package com.github.zjjfly.nia.ch10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 实现自定义字节解码器,继承ByteToMessageDecoder,缺点是需要判断是否有足够的字节
 * 如果使用它不会引入太多的复杂性,就使用它,否则使用ReplayingDecoder
 *
 * @author zjjfly
 */
public class ToIntegerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 4) {
            out.add(in.readInt());
        }
    }
}
