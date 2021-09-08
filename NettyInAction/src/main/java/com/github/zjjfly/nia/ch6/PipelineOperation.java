package com.github.zjjfly.nia.ch6;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import com.github.zjjfly.nia.ch2.EchoClientHandler;
import com.github.zjjfly.nia.ch2.EchoServerHandler;
import org.junit.Test;

import java.util.List;

/**
 * @author zjjfly
 */
public class PipelineOperation {
    @Test
    public void modifyHandler() {
        //增删改查Handler
        NioSocketChannel nioSocketChannel = new NioSocketChannel();
        ChannelPipeline pipeline = nioSocketChannel.pipeline();
        pipeline.addLast(new EchoServerHandler());
        pipeline.addFirst(new EchoClientHandler());
        pipeline.remove("EchoServerHandler#0");
        pipeline.replace("EchoClientHandler#0", "EchoServerHandler#0", new EchoServerHandler());
        ChannelHandler channelHandler = pipeline.get("EchoServerHandler#0");
        ChannelHandlerContext context = pipeline.context("EchoServerHandler#0");
        List<String> names = pipeline.names();
        names.forEach(System.out::println);
    }

    /**
     * ChannelPipeline提供了调用出站和入站操作的API.它们会沿着整个pipline,调用每个handler的对应的方法
     */
    @Test(expected = Exception.class)
    public void fireEvent() {
        NioSocketChannel nioSocketChannel = new NioSocketChannel();
        ChannelPipeline pipeline = nioSocketChannel.pipeline();
        pipeline.addFirst(new EchoClientHandler());
        pipeline.fireChannelRegistered();
        pipeline.fireChannelRead("read");
        pipeline.read();
        pipeline.write("write");
        pipeline.flush();
    }
}
