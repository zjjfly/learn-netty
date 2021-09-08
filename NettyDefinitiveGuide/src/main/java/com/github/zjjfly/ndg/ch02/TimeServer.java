package com.github.zjjfly.ndg.ch02;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author zjjfly
 */
@Log4j2
public class TimeServer {

    public static void main(String[] args) throws IOException, InterruptedException {
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
        @Cleanup Socket socket = serverSocket.accept();
        new Thread(new TimeServerHandler(socket)).start();
        Thread.currentThread().join();
    }
}
