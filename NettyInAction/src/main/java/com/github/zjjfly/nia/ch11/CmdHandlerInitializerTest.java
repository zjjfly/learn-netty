package com.github.zjjfly.nia.ch11;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zjjfly
 */
public class CmdHandlerInitializerTest {

    private EmbeddedChannel channel;

    @Before
    public void setUp() throws Exception {
        CmdHandlerInitializer cmdHandlerInitializer = new CmdHandlerInitializer();
        channel = new EmbeddedChannel(cmdHandlerInitializer);
    }

    @Test
    public void test() throws Exception {
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes("echo".getBytes());
        buf.writeByte(CmdHandlerInitializer.SPACE);
        buf.writeBytes("hello\n".getBytes());
        channel.writeInbound(buf);
        CmdHandlerInitializer.Cmd cmd = channel.readOutbound();
        Assert.assertEquals(new CmdHandlerInitializer.Cmd("echo", "hello"), cmd);
    }

}
