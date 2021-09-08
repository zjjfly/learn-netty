package com.github.zjjfly.ndg.ch02;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @author zjjfly
 */
@Log4j2
public class AsyncTimeServerHandler implements Runnable {
    CountDownLatch latch;
    AsynchronousServerSocketChannel assc;
    private int port;

    public AsyncTimeServerHandler(int port) {
        this.port = port;
        try {
            assc = AsynchronousServerSocketChannel.open();
            assc.bind(new InetSocketAddress(port));
            log.info("The time server is at port:" + port);
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        doAccept();
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error(e);
        }
    }

    private void doAccept() {
        assc.accept(this, new AcceptCompletionHandler());
    }
}
