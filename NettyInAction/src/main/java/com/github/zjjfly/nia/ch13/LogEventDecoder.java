package com.github.zjjfly.nia.ch13;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author zjjfly
 */
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        ByteBuf content = msg.content();
        int index = content.indexOf(0, content.readableBytes(), LogEvent.SEPARATOR);
        String fileName = content.slice(0, index).toString(StandardCharsets.UTF_8);
        String logMsg = content.slice(index+1,content.readableBytes()).toString(StandardCharsets.UTF_8);
        LogEvent logEvent = new LogEvent(msg.sender(),System.currentTimeMillis(),fileName, logMsg);
        out.add(logEvent);
    }
}
