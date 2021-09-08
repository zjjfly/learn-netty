package com.github.zjjfly.nia.ch13;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

/**
 * @author zjjfly
 */
@Log4j2
public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause,cause);
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, LogEvent msg) throws Exception {
        log.info(msg);
    }
}
