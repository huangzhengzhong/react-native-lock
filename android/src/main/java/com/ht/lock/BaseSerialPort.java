package com.ht.lock;

public interface BaseSerialPort {
    void responseTimeout();

    void responseSuccess();

    void setRunnable(HexResponseTimeoutRunnable runnable);

    void sendHex(String hex);

    void close();
}
