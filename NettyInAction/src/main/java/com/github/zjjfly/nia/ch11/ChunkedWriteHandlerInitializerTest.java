package com.github.zjjfly.nia.ch11;

import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

/**
 * @author zjjfly
 */
@Log4j2
public class ChunkedWriteHandlerInitializerTest {

    private EmbeddedChannel channel;

    File file;

    @Before
    public void setUp() throws Exception {
        file = new File("build.gradle");
        ChunkedWriteHandlerInitializer chunkedWriteHandlerInitializer = new ChunkedWriteHandlerInitializer(
                file);
        channel = new EmbeddedChannel(chunkedWriteHandlerInitializer);
    }

    @Test
    public void test() throws Exception {
        ByteBuf buf = channel.readOutbound();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        byte[] expectedBytes = Files.readAllBytes(file.toPath());
        Assert.assertArrayEquals(expectedBytes, bytes);
    }

}
