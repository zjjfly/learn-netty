package com.github.zjjfly.nia.ch11;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import lombok.extern.log4j.Log4j2;


/**
 * @author zjjfly
 */
@Log4j2
public class HttpChannelInitializer extends ChannelInitializer<Channel> {
    private final SslContext context;
    private final boolean client;

    public HttpChannelInitializer(SslContext context, boolean client) {
        this.context = context;
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //使用Https则需要下面两行代码
//        SSLEngine sslEngine = context.newEngine(ch.alloc());
//        pipeline.addFirst(new SslHandler(sslEngine));
        if (client) {
            pipeline.addLast(new HttpClientCodec());
            //如果响应进行了压缩,那么需要解压缩器
            pipeline.addLast("decompressor", new HttpContentDecompressor());
        } else {
            pipeline.addLast(new HttpServerCodec());
            //对响应进行了压缩
            pipeline.addLast("compressor", new HttpContentCompressor());
        }
        //需要添加聚合器,把多个HttpObject聚合成一个完整的Http请求或相应,最大长度512KB
        pipeline.addLast("aggregator", new HttpObjectAggregator(512 * 1024));
        pipeline.addLast("handler", new HttpHandler());
    }

    public static final class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
            HttpMethod method = msg.method();
            HttpVersion protocolVersion = msg.protocolVersion();
            ctx.write(method);
            ctx.write(protocolVersion);
            ctx.flush();
        }
    }
}
