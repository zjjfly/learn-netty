package com.github.zjjfly.nia.ch2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;

/**
 * @author zjjfly
 */
@Log4j2
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        log.info("start echo server");
        new EchoServer(port).run();
    }

    public void run() throws Exception {
        final EchoServerHandler echoServerHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
             .channel(NioServerSocketChannel.class)
             .localAddress(new InetSocketAddress(port))
             .childHandler(echoServerHandler);
            ChannelFuture f = b.bind().sync();
            //下面这一行的作用是让程序不要结束,因为调用sync的时候会等待,直到这个channel被close,而channel一般不会自己close,需要手动调用close方法
            f.channel()
             .closeFuture()
             .sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
