package com.github.zjjfly.nia.ch2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @author zjjfly
 */
@Log4j2
public class EchoClient {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
        new EchoClient().connect("127.0.0.1", port);
    }

    public void connect(String host, int port) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        EchoClientHandler echoClientHandler = new EchoClientHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                     .channel(NioSocketChannel.class)
                     .remoteAddress(new InetSocketAddress(host, port))
                     .handler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         protected void initChannel(SocketChannel ch) {
                             ch.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8))
                               .addLast(echoClientHandler);
                         }
                     });
            ChannelFuture f = bootstrap.connect().sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
