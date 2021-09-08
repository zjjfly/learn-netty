package com.github.zjjfly.nia.ch8;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.net.InetSocketAddress;

/**
 * @author zjjfly
 */
@Log4j2
public class BootstrapTest {
    /**
     * Bootstrap一般被用于客户端或使用了无连接的协议的应用程序
     */
    @Test
    public void bootstrap() throws InterruptedException {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                 .channel(NioSocketChannel.class)
                 .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                     @Override
                     protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) {
                         log.info("Receive data");
                     }
                 });
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("www.manning.com", 80));
        channelFuture.addListener(clientChannelFutureHandler(channelFuture)).sync();
    }

    /**
     * ServerBootstrap一般用于服务器处理请求
     *
     * @throws InterruptedException
     */
    @Test
    public void serverBootstrap() throws InterruptedException {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(nioEventLoopGroup)
                 .channel(NioServerSocketChannel.class)
                 .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
                     @Override
                     protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) {
                         log.info("Receive data");
                     }
                 });
        ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(8080));
        channelFuture.addListener(serverChannelFutureHandler()).sync();
    }

    /**
     * 不同类型的组件不能混用,例如:NioEventLoopGroup只能和Nio开头的Channel一起使用
     */
    @Test(expected = IllegalStateException.class)
    public void unMatchComponent() throws InterruptedException {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                 .channel(OioSocketChannel.class)
                 .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                     @Override
                     protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) {
                         log.info("Receive data");
                     }
                 });
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("www.manning.com", 80));
        channelFuture.addListener(clientChannelFutureHandler(channelFuture)).sync();
    }

    /**
     * 有些请求需要服务器去调用第三方的服务,这种时候可以复用当前的EventLoop
     *
     * @throws InterruptedException
     */
    @Test
    public void serverClient() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                 .channel(NioServerSocketChannel.class)
                 .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
                     ChannelFuture channelFuture;

                     @Override
                     public void channelActive(ChannelHandlerContext ctx) {
                         Bootstrap bs = new Bootstrap();
                         bs.channel(NioSocketChannel.class)
                           .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                               @Override
                               protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) {
                                   log.info("Receive data");
                               }
                           });
                         //复用当前channel的event loop
                         bs.group(ctx.channel().eventLoop());
                         channelFuture = bs.connect(new InetSocketAddress("www.manning.com", 80));
                     }

                     @Override
                     protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) {
                         if (channelFuture.isDone()) {
                             //TODO
                         }
                     }
                 });
        ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(8080));
        channelFuture.addListener(serverChannelFutureHandler()).sync();
    }

    /**
     * 使用ChannelInitializer可以实现在引导的时候加入多个ChannelHandler
     *
     * @throws InterruptedException
     */
    @Test
    public void channelInitializer() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                 .channel(NioServerSocketChannel.class)
                 .childHandler(new ChannelInitializer() {
                     @Override
                     protected void initChannel(Channel ch) {
                         ch.pipeline()
                           .addLast(new HttpClientCodec())
                           .addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
                     }
                 });
        ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(8080));
        channelFuture.sync();
    }

    /**
     * 设置Channel的option和attribute
     *
     * @throws InterruptedException
     */
    @Test
    public void optionAndAttribute() throws InterruptedException {
        AttributeKey<Integer> id = AttributeKey.newInstance("ID");
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                 .channel(NioSocketChannel.class)
                 .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                     @Override
                     public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                         Integer i = ctx.channel().attr(id).get();
                         log.info(i);
                     }

                     @Override
                     protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) {
                         log.info("Receive data");
                     }
                 });
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        bootstrap.attr(id, 123456);
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("www.manning.com", 80));
        channelFuture.addListener(clientChannelFutureHandler(channelFuture)).syncUninterruptibly();
    }

    /**
     * 无连接的协议(如UDP)的程序
     */
    @Test
    public void datagramChannel() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioDatagramChannel.class)
                 .group(new NioEventLoopGroup())
                 .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
                     @Override
                     protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) {
                         //TODO
                     }
                 });
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(0));
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                log.info("Channel bound");
            } else {
                System.err.println("Bind attempt failed");
                channelFuture.cause().printStackTrace();
            }
        }).syncUninterruptibly();
    }

    /**
     * 关闭程序使用
     */
    @Test
    public void shutdown() {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(nioEventLoopGroup)
                 .channel(NioServerSocketChannel.class)
                 .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
                     @Override
                     protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) {
                         log.info("Receive data");
                     }
                 });
        ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(8080));
        channelFuture.addListener(serverChannelFutureHandler()).syncUninterruptibly();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                nioEventLoopGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                log.error(e);
            }
        }));
    }

    private GenericFutureListener<Future<? super Void>> serverChannelFutureHandler() {
        return future -> {
            if (future.isSuccess()) {
                log.info("Server bound");
            } else {
                log.info("Bound attempt failed");
                future.cause().printStackTrace();
            }
        };
    }


    private GenericFutureListener<Future<? super Void>> clientChannelFutureHandler(ChannelFuture channelFuture) {
        return future -> {
            if (future.isSuccess()) {
                log.info("Connection established");
            } else {
                log.info("Connection attempt failed");
                channelFuture.cause().printStackTrace();
            }
        };
    }

}
