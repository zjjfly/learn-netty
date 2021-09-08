package com.github.zjjfly.ndg.ch02;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author zjjfly
 */
@Log4j2
public class TimeClient {

    public static void main(String[] args) throws IOException {
        int port = 8088;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                log.error(e);
            }
        }
        @Cleanup Socket socket = new Socket("127.0.0.1", port);
        @Cleanup BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        @Cleanup PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.println("q");
        out.flush();
        log.info("send order to server succeed.");
        String body = in.readLine();
        log.info("Now is " + body);
    }
}
