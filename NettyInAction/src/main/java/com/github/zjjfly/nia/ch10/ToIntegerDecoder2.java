package com.github.zjjfly.nia.ch10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 另一种实现自定义字节解码器,继承ReplayingDecoder,它不需要判断是否有足够的字节
 * 缺点:1.不支持所有的ByteBuf中定义的操作;2.速度没有ByteToMessageDecoder快
 *
 * @author zjjfly
 */
public class ToIntegerDecoder2 extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //ByteBuf的类型实际是ReplayingDecoderByteBuf,调用它的读取方法如果没有足够的字节会抛出一个Error,会被ReplayingDecoder捕获并处理
        //当有更多字节可读时,会再次调用decode方法
        out.add(in.readInt());
    }
}
