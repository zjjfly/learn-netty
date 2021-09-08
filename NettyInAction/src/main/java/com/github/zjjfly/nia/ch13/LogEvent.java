package com.github.zjjfly.nia.ch13;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetSocketAddress;

/**
 * @author zjjfly
 */
@Data
@AllArgsConstructor
public class LogEvent {

    public static final byte SEPARATOR = (byte) ':';

    private final InetSocketAddress source;

    private final long received;

    private final String logfile;

    private final String msg;

    public LogEvent(String logfile, String msg) {
        this(null, -1, logfile, msg);
    }
}
