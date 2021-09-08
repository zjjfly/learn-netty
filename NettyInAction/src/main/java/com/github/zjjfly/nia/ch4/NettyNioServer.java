package com.github.zjjfly.nia.ch4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;

/**
 * @author zjjfly
 */
@Log4j2
public class NettyNioServer {

    public static void main(String[] args) {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        log.info("start netty NIO server");
        new NettyNioServer().serve(port);
    }

    private void serve(int port) {
        final ByteBuf buf = Unpooled.copiedBuffer("Hi\n", StandardCharsets.UTF_8);
        EventLoopGroup group = new EpollEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                     .localAddress(port)
                     .channel(EpollServerSocketChannel.class)
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         protected void initChannel(SocketChannel ch) throws Exception {
                             ch.pipeline().addLast(new ChannelHandlerAdapter() {
                                 @Override
                                 public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                     ctx.writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE);
                                 }
                             });
                         }
                     });

            ChannelFuture f = bootstrap.bind().sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e);
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                log.error(e);
            }
        }

    }
}
