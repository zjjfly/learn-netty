package com.github.zjjfly.nia.ch11;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zjjfly
 */
public class HttpChannelInitializerTest {

    private EmbeddedChannel channel;

    @Before
    public void setUp() throws Exception {
        HttpChannelInitializer httpChannelInitializer = new HttpChannelInitializer(null, false);
        channel = new EmbeddedChannel(httpChannelInitializer);
    }


    @Test
    public void test() throws Exception {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(("GET /favicon.ico HTTP/1.1\n").getBytes());
        buffer.writeBytes("Host: www.baidu.com\n".getBytes());
        //send end tag
        buffer.writeByte(HttpConstants.LF);
        channel.writeInbound(buffer);
        HttpMethod method = channel.readOutbound();
        Assert.assertEquals(HttpMethod.GET,method);
        HttpVersion version = channel.readOutbound();
        Assert.assertEquals(HttpVersion.HTTP_1_1,version);
    }
}
