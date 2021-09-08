package com.github.zjjfly.nia.ch11;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * 一个自定义的默认把SslHandler作为pipeline第一个handler的ChannelInitializer
 *
 * @author zjjfly
 */
public class SslChannelInitializer extends ChannelInitializer<Channel> {
    private final SslContext sslContext;
    private final boolean startTls;

    public SslChannelInitializer(SslContext sslContext, boolean startTls) {
        this.sslContext = sslContext;
        this.startTls = startTls;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
        ch.pipeline().addFirst(new SslHandler(sslEngine, startTls));
    }
}
