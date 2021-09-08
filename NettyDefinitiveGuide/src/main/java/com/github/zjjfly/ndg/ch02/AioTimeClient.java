package com.github.zjjfly.ndg.ch02;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class AioTimeClient {
    public static void main(String[] args) {
        int port = 8088;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                log.error(e);
            }
        }
        new Thread(new AsyncTimeClientHandler("127.0.0.1", port)).start();
    }
}
