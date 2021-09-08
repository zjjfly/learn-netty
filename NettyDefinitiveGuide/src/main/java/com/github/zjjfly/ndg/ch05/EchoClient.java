package com.github.zjjfly.ndg.ch05;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.nio.charset.Charset;

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
                log.error(e);
            }
        }
        new EchoClient().connect("127.0.0.1", port);
    }

    public void connect(String host, int port) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                     .channel(NioSocketChannel.class)
                     .option(ChannelOption.TCP_NODELAY, true)
                     .handler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         protected void initChannel(SocketChannel ch) throws Exception {
                             val delimiter = Unpooled.copiedBuffer("$_", Charset.forName("utf-8"));
                             ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                             ch.pipeline().addLast(new StringDecoder());
                             ch.pipeline().addLast(new EchoClientHandler());
                         }
                     });
            ChannelFuture f = bootstrap.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
