package com.github.zjjfly.nia.ch12;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.extern.log4j.Log4j2;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

/**
 * @author zjjfly
 */
@Log4j2
public class SecureChatServer extends ChatServer {

    private final SslContext context;

    public SecureChatServer(SslContext context) {
        this.context = context;
    }

    @Override
    protected ChannelInitializer createInitializer(
            ChannelGroup group) {
        return new SecureChatServerInitializer(group, context);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            log.error("Please give port as argument");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        SelfSignedCertificate cert;
        try {
            cert = new SelfSignedCertificate();
            SslContext context = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).build();
            final SecureChatServer endpoint = new SecureChatServer(context);
            ChannelFuture future = endpoint.start(
                    new InetSocketAddress(port));
            Runtime.getRuntime().addShutdownHook(new Thread(endpoint::destroy));
            future.channel().closeFuture().syncUninterruptibly();
        } catch (CertificateException | SSLException e) {
            log.error(e);
        }
    }

}
