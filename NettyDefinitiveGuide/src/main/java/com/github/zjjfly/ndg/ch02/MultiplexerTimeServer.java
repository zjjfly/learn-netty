package com.github.zjjfly.ndg.ch02;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

@Log4j2
public class MultiplexerTimeServer implements Runnable {
    private Selector selector;
    private ServerSocketChannel servChannel;
    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {
        try {
            selector = Selector.open();
            servChannel = ServerSocketChannel.open();
            servChannel.configureBlocking(false);
            //1024是backlog数,backlog实际上是一个连接队列
            servChannel.socket().bind(new InetSocketAddress(port), 1024);
            servChannel.register(selector, SelectionKey.OP_ACCEPT);
            log.info("The time server is at port:" + port);
        } catch (IOException e) {
            log.error(e);
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                //1000是超时时间,毫秒
                //select方法会阻塞直到channel就绪或者到了超时时间
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey key = null;
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    handleInput(key);
                }
            } catch (IOException e) {
                log.error(e);
            }
        }
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int read = sc.read(buffer);
                if (read > 0) {
                    buffer.flip();//flip的作用是去除buffer中为空的部分
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String body = new String(bytes, StandardCharsets.UTF_8);
                    log.info("the times server receive order:" + body);
                    String response = "q".equalsIgnoreCase(body) ? new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                            .format(new Date()) : "Bad order";
                    doWrite(sc, response);
                } else if (read < 0) {
                    key.cancel();
                    sc.close();
                } else {

                }
            }
        }
    }

    private void doWrite(SocketChannel sc, String resp) throws IOException {
        if (StringUtils.isNoneBlank(resp)) {
            byte[] bytes = resp.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.put(bytes);
            buffer.flip();
            sc.write(buffer);
        }
    }
}
