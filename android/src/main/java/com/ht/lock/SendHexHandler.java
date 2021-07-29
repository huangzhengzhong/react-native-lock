package com.ht.lock;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

class SendHexHandler extends Handler {

    private static final String TAG = "HexSendHandler";
    private static final long TIMEOUT = 2;//响应超时时间 单位（秒）
    private BaseSerialPort baseSerialPort;

    public SendHexHandler(BaseSerialPort baseSerialPort) {
        this.baseSerialPort = baseSerialPort;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        String hex = msg.getData().getString("hex");
        baseSerialPort.sendHex(hex.replace(" ",""));

        Log.d(TAG, "向串口发送指令: " + hex);
        //如果串口响应超时 则释放接收指令线程
        HexResponseTimeoutRunnable runnable = new HexResponseTimeoutRunnable(baseSerialPort);
        baseSerialPort.setRunnable(runnable);;
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        scheduledThreadPool.schedule(runnable,TIMEOUT, TimeUnit.SECONDS);
    }
}
