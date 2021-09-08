package com.github.zjjfly.nia.ch11;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LineBasedFrameDecoder;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 基于换行符的解码器的使用例子
 *
 * @author zjjfly
 */
public class CmdHandlerInitializer extends ChannelInitializer<Channel> {
    public static final byte SPACE = (byte) ' ';

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new CmdDecoder(64 * 1024));
        pipeline.addLast(new CmdHandler());
    }

    @Data
    @AllArgsConstructor
    public static class Cmd {
        private String name;
        private String args;
    }

    public static final class CmdDecoder extends LineBasedFrameDecoder {
        public CmdDecoder(int maxLength) {
            super(maxLength);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer)
                throws Exception {
            ByteBuf frame = (ByteBuf) super.decode(ctx, buffer);
            if (frame == null) {
                return null;
            }
            int endIndex = frame.writerIndex();
            int index = frame.indexOf(frame.readerIndex(), endIndex, SPACE);
            ByteBuf nameBuf = frame.slice(frame.readerIndex(), index);
            int argLength = endIndex - index - 1;
            ByteBuf argBuf = frame.slice(index + 1, argLength);
            byte[] name = new byte[index];
            byte[] arg = new byte[argLength];
            nameBuf.readBytes(name);
            argBuf.readBytes(arg);
            Cmd cmd = new Cmd(new String(name), new String(arg));
            return cmd;
        }
    }

    public static final class CmdHandler extends SimpleChannelInboundHandler<Cmd> {

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, Cmd msg) throws Exception {
            ctx.writeAndFlush(msg);
        }
    }
}
