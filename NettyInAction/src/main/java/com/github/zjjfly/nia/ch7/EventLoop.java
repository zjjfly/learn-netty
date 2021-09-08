package com.github.zjjfly.nia.ch7;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author zjjfly
 */
public class EventLoop {
    @Test
    public void scheduleTask() throws InterruptedException {
        DefaultEventLoop defaultEventLoop = new DefaultEventLoop(new NioEventLoopGroup());
        int[] count = {0};
        defaultEventLoop.schedule(() -> count[0]++, 1, TimeUnit.SECONDS);
        Thread.sleep(2000L);
        Assert.assertEquals(1, count[0]);
        defaultEventLoop.scheduleAtFixedRate(() -> count[0]++, 1, 2, TimeUnit.SECONDS);
        Thread.sleep(3100L);
        Assert.assertEquals(3, count[0]);
    }
}
