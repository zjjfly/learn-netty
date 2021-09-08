package com.github.zjjfly.ndg.ch03;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zjjfly
 */
@Log4j2
public class TimeServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        String body = new String(bytes, StandardCharsets.UTF_8);
        log.info("the times server receive order:" + body);
        String time = "q".equalsIgnoreCase(body) ? new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                .format(new Date()) : "Bad order";
        ByteBuf resp = Unpooled.copiedBuffer(time.getBytes());
        ctx.write(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
