package com.github.zjjfly.nia.ch9;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author zjjfly
 */
@Log4j2
public class FrameChunkDecoderTest {
    @Test
    public void testFramesDecode() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf input = buffer.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));
        assertTrue(channel.writeInbound(input.readBytes(2)));
        try {
            channel.writeInbound(input.readBytes(4));
        } catch (TooLongFrameException e) {
            log.error(e);
        }
        assertTrue(channel.writeInbound(input.readBytes(3)));
        assertTrue(channel.finish());

        //第一帧
        ByteBuf read = channel.readInbound();
        assertEquals(buffer.readSlice(2), read);
        read.release();

        //第二帧
        read = channel.readInbound();
        assertEquals(buffer.skipBytes(4).readSlice(3), read);
        read.release();

        buffer.release();
    }
}
