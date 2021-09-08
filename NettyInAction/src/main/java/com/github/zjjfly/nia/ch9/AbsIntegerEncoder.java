package com.github.zjjfly.nia.ch9;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author zjjfly
 */
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List out) throws Exception {
        while (msg.readableBytes() >= 4) {
            int i = Math.abs(msg.readInt());
            out.add(i);
        }
    }
}
