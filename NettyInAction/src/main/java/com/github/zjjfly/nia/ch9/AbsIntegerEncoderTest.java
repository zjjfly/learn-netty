package com.github.zjjfly.nia.ch9;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 测试出站信息
 *
 * @author zjjfly
 */
public class AbsIntegerEncoderTest {
    @Test
    public void testEncode() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buffer.writeInt(i * -1);
        }
        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        assertTrue(channel.writeOutbound(buffer));
        assertTrue(channel.finish());
        for (Integer i = 1; i < 10; i++) {
            assertEquals(i, channel.readOutbound());
        }
    }
}
