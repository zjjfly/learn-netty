package com.github.zjjfly.nia.ch5;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * Netty通过接口ByteBufAllocator来实现buffer的池化
 *
 * @author zjjfly
 */
public class Allocator extends ChannelHandlerAdapter {
    /**
     * 一种获取ByteBufAllocator的方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBufAllocator allocator = ctx.alloc();
    }

    /**
     * 另一种获取ByteBufAllocator的方法
     */
    @Test
    public void getAllocator() {
        NioSocketChannel nioSocketChannel = new NioSocketChannel();
        ByteBufAllocator allocator = nioSocketChannel.alloc();
    }

    /**
     * 创建非池化的ByteBuf的工具类Unpooled
     */
    @Test
    public void unpooledBuf() {
        Unpooled.copiedBuffer("test", StandardCharsets.UTF_8);
        Unpooled.buffer();
        Unpooled.directBuffer();
        Unpooled.wrappedBuffer("test".getBytes());
    }
}
