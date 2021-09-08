package com.github.zjjfly.ndg.ch02;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zjjfly
 */
@Log4j2
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel asc;

    public ReadCompletionHandler(AsynchronousSocketChannel asc) {
        if (this.asc == null) {
            this.asc = asc;
        }
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] bytes = new byte[attachment.remaining()];
        attachment.get(bytes);
        String body = new String(bytes, StandardCharsets.UTF_8);
        log.info("the times server receive order:" + body);
        String response = "q".equalsIgnoreCase(body) ? new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                .format(new Date()) : "Bad order";
        doWrite(response);
    }

    private void doWrite(String resp) {
        if (StringUtils.isNoneBlank(resp)) {
            byte[] bytes = resp.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.put(bytes);
            buffer.flip();
            asc.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    if (buffer.hasRemaining()) {
                        asc.write(buffer, buffer, this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        asc.close();
                    } catch (IOException e) {
                        log.error(e);
                    }
                }
            });
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            asc.close();
        } catch (IOException e) {
            log.error(e);
        }
    }
}
