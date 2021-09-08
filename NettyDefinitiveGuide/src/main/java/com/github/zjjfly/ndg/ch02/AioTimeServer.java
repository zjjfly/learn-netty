package com.github.zjjfly.ndg.ch02;

import lombok.extern.log4j.Log4j2;

/**
 * @author zjjfly
 */
@Log4j2
public class AioTimeServer {
    public static void main(String[] args) {
        int port = 8088;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                log.error(e);
            }
        }
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
        new Thread(timeServer, "AIO-Thread-001").start();
    }
}
