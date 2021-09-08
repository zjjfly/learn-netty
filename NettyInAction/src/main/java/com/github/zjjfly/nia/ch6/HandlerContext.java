package com.github.zjjfly.nia.ch6;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import com.github.zjjfly.nia.ch2.EchoClientHandler;
import org.junit.Test;

/**
 * @author zjjfly
 */
public class HandlerContext {

    /**
     * 每个handler都对应一个ChannelHandlerContext
     * ChannelHandlerContext也提供了一些调用入站和出站操作的方法,它们不像ChannelPipeline的同名方法会在整个pipeline中传播,而是只会把事件传播到下一个handler
     */
    @Test(expected = Exception.class)
    public void api() {
        NioSocketChannel nioSocketChannel = new NioSocketChannel();
        ChannelPipeline pipeline = nioSocketChannel.pipeline();
        pipeline.addFirst(new EchoClientHandler());
        ChannelHandlerContext channelHandlerContext = pipeline.firstContext();
        channelHandlerContext.read();
        channelHandlerContext.write("test");
    }
}
