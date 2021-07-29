package com.ht.lock;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

import androidx.annotation.RequiresApi;

import static com.ht.lock.ArrayUtil.arrayCheckNull;

/**
 * 本地通讯器线程
 */
class LockDatagramSocketThread extends Thread {
    private static final String TAG = "LockDatagramSocketThread";
    public static final String MESSAGE_KEY = "message";
    /**
     * 本地通讯器
     */
    private DatagramSocket ds;
    private Handler messageCallback;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void start (DatagramSocket ds, Handler messageCallback) {
        this.ds = ds;
        this.messageCallback = messageCallback;
        this.start();
    }

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        while (true){
            byte[] buf = new byte[3072];
            DatagramPacket dp = new DatagramPacket(buf,buf.length);
            try {
                ds.receive(dp);
                byte[] value =  arrayCheckNull(dp.getData());
                String valueString = new String(value, StandardCharsets.UTF_8);
                Log.d(TAG, "收到消息: " + valueString);
                Message message = new Message();
                message.getData().putString(MESSAGE_KEY,valueString);
                this.messageCallback.handleMessage(message);
            } catch (IOException e) {
                Log.e(TAG, "处理接收数据异常",e);
            }
        }
    }
}
