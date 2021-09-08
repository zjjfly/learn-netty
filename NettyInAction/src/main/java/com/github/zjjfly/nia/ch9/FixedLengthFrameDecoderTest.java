package com.github.zjjfly.nia.ch9;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCountUtil;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 测试入站消息
 *
 * @author zjjfly
 */
public class FixedLengthFrameDecoderTest {
    @Test
    public void testFrameDecode() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf input = buffer.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        //解码之后会自动调用release方法,所以如果需要保留这个ByteBuf以便之后使用,需要调用ReferenceCountUtil.retain,保证refCnt不会变为0
        ReferenceCountUtil.retain(buffer);
        assertTrue(channel.writeInbound(input));
        assertTrue(channel.finish());

        //第一帧
        ByteBuf read = channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();

        //第二帧
        read = channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();

        //第三帧
        read = channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();

        //没有数据可读取了
        assertNull(channel.readInbound());
        input.release();
    }

    @Test
    public void testFrameDecode2() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf input = buffer.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        assertFalse(channel.writeInbound(input.readBytes(2)));
        assertTrue(channel.writeInbound(input.readBytes(7)));
        assertTrue(channel.finish());

        //第一帧
        ByteBuf read = channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();

        //第二帧
        read = channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();

        //第三帧
        read = channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();

        //没有数据可读取了
        assertNull(channel.readInbound());
        input.release();
    }
}
