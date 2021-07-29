package com.ht.lock;

public class HexResponseTimeoutRunnable implements Runnable {
    private boolean isStop = false;
    public void stop () {
        isStop = true;
    }

    BaseSerialPort mBaseSerialPort;
    public HexResponseTimeoutRunnable(BaseSerialPort baseSerialPort) {
        mBaseSerialPort = baseSerialPort;
    }

    @Override
    public void run() {
        if(isStop || null == mBaseSerialPort) return;
        mBaseSerialPort.responseTimeout();
    }
}