package com.github.zjjfly.nia.ch5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * ByteBuf使用模式
 *
 * @author zjjfly
 */
@Log4j2
public class ByteBufPattern {
    /**
     * ByteBuf作为堆缓冲区
     */
    @Test
    public void heapBuffer() {
        ByteBuf heapBuffer = Unpooled.copiedBuffer("test", StandardCharsets.UTF_8);
        if (heapBuffer.hasArray()) {
            byte[] array = heapBuffer.array();
            int offset = heapBuffer.arrayOffset() + heapBuffer.readerIndex();
            int length = heapBuffer.readableBytes();
            byte[] bytes = Arrays.copyOfRange(array, offset, length);
            log.info(new String(bytes));
        }
    }

    /**
     * ByteBuf作为直接缓冲器.这个缓冲区是在堆之外的,是通过本地方法调用实现的.
     * 如果要把它作为数组使用,需要进行一次赋值,所以这种情况使用堆缓冲区更好
     */
    @Test
    public void direstBuffer() {
        ByteBuf directBuffer = Unpooled.directBuffer(10);
        directBuffer.writeBytes("test".getBytes());
        //直接缓冲器调用hasArray返回的是false
        if (!directBuffer.hasArray()) {
            int length = directBuffer.readableBytes();
            byte[] array = new byte[length];
            directBuffer.getBytes(directBuffer.readerIndex(), array);
            log.info(new String(array));
        }
    }

    /**
     * 复合缓冲区.可以为多个缓冲区提供一个聚合视图,可以根据需要添加和删除缓冲区
     */
    @Test
    public void compositeBuffer() {
        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();
        ByteBuf directBuffer = Unpooled.directBuffer(10);
        directBuffer.writeBytes("hello,".getBytes());
        ByteBuf heapBuffer = Unpooled.copiedBuffer("world", StandardCharsets.UTF_8);
        //添加
        compositeByteBuf.addComponents(directBuffer, heapBuffer);
        //需要手动的设置writerIndex和readerIndex
        compositeByteBuf.writerIndex(directBuffer.writerIndex() + heapBuffer.writerIndex());
        log.info(compositeByteBuf.toString(StandardCharsets.UTF_8));
        //删除
        compositeByteBuf.removeComponent(0);
        //遍历
        for (ByteBuf byteBuf : compositeByteBuf) {
            log.info(byteBuf.toString(StandardCharsets.UTF_8));
        }
    }
}
