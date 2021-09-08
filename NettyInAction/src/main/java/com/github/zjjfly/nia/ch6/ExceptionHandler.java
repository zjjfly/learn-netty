package com.github.zjjfly.nia.ch6;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * @author zjjfly
 */
public class ExceptionHandler extends ChannelHandlerAdapter {
    /**
     * 对于入站异常,重写Handler的exceptionCaught方法即可.
     * exceptionCaught的默认实现是把错误传递到下一个handler
     * 所以重写exceptionCaught的handler最好位于pipeline的最后,这样可以处理在任何环节抛出的错误
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //处理出站异常,使用ChannelFutureListener
        Channel channel = ctx.channel();
        ChannelFuture hello = channel.writeAndFlush("hello");
        hello.addListener(future -> {
            if (!future.isSuccess()) {
                future.cause().printStackTrace();
                channel.close();
            }
        });
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        promise.addListener(f -> {
            if (!f.isSuccess()) {
                f.cause().printStackTrace();
                ctx.channel().close();
            }
        });
    }
}
