package com.github.zjjfly.ndg.ch02;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zjjfly
 */
@Log4j2
public class TimeServerHandler implements Runnable {

    private Socket socket;

    TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream())
        ) {
            String body;
            while (true) {
                body = in.readLine();
                if (body == null) {
                    break;
                }
                log.info("the times server receive order:" + body);
                String response = "q".equalsIgnoreCase(body) ? new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        .format(new Date()) : "Bad order";
                out.println(response);
                out.flush();
            }
        } catch (IOException e) {
            log.error(e, e);
        }
    }
}
