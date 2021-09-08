package com.github.zjjfly.ndg.ch02;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author zjjfly
 */
@Log4j2
public class ThreadPoolTimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8088;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                log.error(e);
            }
        }
        @Cleanup ServerSocket serverSocket = new ServerSocket(port);
        log.info("the time server is start at " + port);
        ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(
                1000,
                new BasicThreadFactory.Builder()
                        .namingPattern(
                                "my-thread-%d")
                        .daemon(true)
                        .build());
        while (true) {
            @Cleanup Socket socket = serverSocket.accept();
            poolExecutor.submit(new TimeServerHandler(socket));
        }
    }
}
