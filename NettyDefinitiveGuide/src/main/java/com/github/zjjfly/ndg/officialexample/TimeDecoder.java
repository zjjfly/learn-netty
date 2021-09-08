package com.github.zjjfly.ndg.officialexample;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @author zjjfly
 */
//public class TimeDecoder extends ByteToMessageDecoder {
//    @Override
//    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        //in是一个堆积的buffer
//        if(in.readableBytes()<4){
//            return;
//        }
//        out.add(in.readBytes(4));
//    }
//}
//使用ReplayingDecoder更简单,因为这个解码器假设你需要的buffer已经全部接收了
public class TimeDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        out.add(new UnixTime(in.readUnsignedInt()));
    }

}

