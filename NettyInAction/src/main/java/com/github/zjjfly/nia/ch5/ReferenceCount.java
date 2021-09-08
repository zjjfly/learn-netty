package com.github.zjjfly.nia.ch5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Assert;
import org.junit.Test;

/**
 * 引用计数是一种通过在某个对象所持有的资源不再被其他对象引用的时候释放该对象所持有的资源的一种优化内存使用和性能的计数
 * Netty中提供了ReferenceCounted接口来实现这一技术
 *
 * @author zjjfly
 */
public class ReferenceCount {
    @Test
    public void referenceCounted() {
        NioSocketChannel nioSocketChannel = new NioSocketChannel();
        ByteBufAllocator alloc = nioSocketChannel.alloc();
        ByteBuf directBuffer = alloc.directBuffer();
        Assert.assertEquals(directBuffer.refCnt(), 1);
        directBuffer.release();
        Assert.assertEquals(directBuffer.refCnt(), 0);
    }
}
