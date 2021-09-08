package com.github.zjjfly.nia.ch5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author zjjfly
 */
@Log4j2
public class ByteBufOperation {
    @Test
    public void randomAccess() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("test", StandardCharsets.UTF_8);
        for (int i = 0; i < byteBuf.capacity(); i++) {
            log.info(byteBuf.getByte(i));
        }
    }

    @Test
    public void readAllReadableBytes() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("test", StandardCharsets.UTF_8);
        while (byteBuf.isReadable()) {
            log.info(byteBuf.readByte());
        }
    }

    @Test
    public void writeAllWritableBytes() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("test", StandardCharsets.UTF_8);
        Random random = new Random();
        while (byteBuf.writableBytes() >= 4) {
            int value = random.nextInt();
            byteBuf.writeInt(value);
        }
    }

    @Test
    public void search() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("test\n", StandardCharsets.UTF_8);
        assertEquals(byteBuf.indexOf(0, 4, (byte) 116), 0);
        //使用ByteProcessor查找unix的换行符
        assertEquals(byteBuf.forEachByte(ByteProcessor.FIND_LF), 4);
    }

    /**
     * 派生buffer,有自己的读写索引,但这种buffer和最初的buffer的内部存储是共享的,所以在对其操作的时候需要特别小心
     */
    @Test
    public void derivedBuffer() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("test", StandardCharsets.UTF_8);
        //创建和原始buffer的副本
        ByteBuf duplicate = byteBuf.duplicate();
        duplicate.setByte(0, 122);
        //下面的断言就说明派生的buffer修改了也会修改原始的buffer
        assertEquals(byteBuf.getByte(0), 122);
        //创建一个原始buffer的readable bytes的切片,所以它的capacity有原始buffer的readable bytes长度决定
        ByteBuf slice = byteBuf.slice();
        assertEquals(slice.capacity(), 4);
        //创建一个原始buffer的指定起始点和长度的切片
        ByteBuf partSlice = byteBuf.slice(0, 3);
        assertEquals(partSlice.toString(StandardCharsets.UTF_8), (char) 122 + "es");
        //创建一个原始buffer的只读副本
        ByteBuf readOnlyBuffer = Unpooled.unmodifiableBuffer(byteBuf);
        //创建一个从原始buffer的readerIndex开始的指定长度的切片,一般用于解码时读取某一段bytes
        ByteBuf readSlice = byteBuf.readSlice(4);
        log.info(readSlice.toString(StandardCharsets.UTF_8));
    }

    /**
     * 复制buffer,和派生不同的是,复制出的buffer和原始buffer不共享底层数据
     */
    @Test
    public void copyBuffer() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("test", StandardCharsets.UTF_8);
        ByteBuf copy = byteBuf.copy(0, 4);
        copy.setByte(0, 122);
        assertNotEquals(copy.getByte(0), byteBuf.getByte(0));
    }

    /**
     * 写操作是以set或write开头的函数,读操作是get或read开头的函数
     * set,get不改变索引,而read,write会改变索引
     */
    @Test
    public void writeRead() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("test", StandardCharsets.UTF_8);
        byteBuf.capacity(100);
        byteBuf.writeBoolean(true);
        byteBuf.setBoolean(4, false);
        byteBuf.writeInt(10);
        byteBuf.setInt(5, 100);
        byteBuf.setByte(0, (byte) 122);
        assertEquals(byteBuf.readBytes(4).toString(StandardCharsets.UTF_8), "zest");
        assertEquals(byteBuf.readBoolean(), false);
        assertEquals(byteBuf.readInt(), 100);
    }

    @Test
    public void otherOperation() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("test", StandardCharsets.UTF_8);
        //是否有可写的byte
        assertEquals(byteBuf.isWritable(), true);
        //是否有可读的byte
        assertEquals(byteBuf.isReadable(), true);
        //可读的byte数目
        assertEquals(byteBuf.readableBytes(), 4);
        //可写的byte数目
        assertEquals(byteBuf.writableBytes(), 8);
        //buffer的容量
        assertEquals(byteBuf.capacity(), 12);
        //buffer的最大容量
        assertEquals(byteBuf.maxCapacity(), Integer.MAX_VALUE);
        //buffer的底层是否是byte数组
        assertEquals(byteBuf.hasArray(), true);
        //如果buffer的底层是byte数组则返回这个数组,否则抛出异常
        assertEquals(new String(byteBuf.array()), new String(new byte[]{116, 101, 115, 116, 0, 0, 0, 0, 0, 0, 0, 0}));
    }
}
