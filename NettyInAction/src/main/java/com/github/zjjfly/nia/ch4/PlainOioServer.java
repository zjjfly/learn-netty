package com.github.zjjfly.nia.ch4;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 最经典的JDK网络编程,它无法处理大量的并发连入连接
 *
 * @author zjjfly
 */
@Log4j2
public class PlainOioServer {
    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        log.info("start plain OIO server");
        new PlainOioServer().serve(port);
    }

    private void serve(int port) throws IOException {
        ServerSocket socket = new ServerSocket(port);
        try {
            for (; ; ) {
                final Socket clientSocket = socket.accept();
                log.info("Accepted connection from " + clientSocket);
                new Thread(() -> {
                    try (OutputStream out = clientSocket.getOutputStream()) {
                        out.write("Hi!\n".getBytes(StandardCharsets.UTF_8));
                        out.flush();
                        clientSocket.close();
                    } catch (IOException e) {
                        log.error(e);
                    }
                }).start();
            }
        } catch (IOException e) {
            log.error(e);
        }

    }
}
