package com.github.zjjfly.ndg.ch04;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author zjjfly
 */
public class ImprovedServerHandler extends ChannelHandlerAdapter {
    private static final Logger logger = Logger.getLogger(ImprovedServerHandler.class.getName());

    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //因为使用的是StringDecoder,所以msg已经是String了,不需要之前的
        String body = (String) msg;
        logger.info("the times server receive order:" + body + ",the count is:" + ++counter);
        String time = "Query Time Order".equalsIgnoreCase(body) ? new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                .format(new Date()) : "Bad order";
        time = time + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(time.getBytes());
        ctx.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
