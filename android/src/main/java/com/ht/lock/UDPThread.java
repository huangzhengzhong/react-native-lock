package com.ht.lock;

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


class UDPThread extends Thread {
    private static final String TAG = "UDPThread";
    private final Object lock = new Object();
    //标志线程阻塞情况
    private boolean pause = false;
    DatagramSocket ds;
    private Handler mHandler;

    private UDPThread() {};

    public UDPThread(DatagramSocket datagramSocket, Handler handler) {
        this.ds = datagramSocket;
        this.mHandler = handler;
    }

    /**
     * 调用该方法实现恢复线程的运行
     */
    public void resumeThread() {
        this.pause = false;
        synchronized (lock) {
            //唤醒线程
            lock.notify();
            Log.d(TAG, "resumeThread: 接收线程已唤醒！");
        }
    }

    /**
     * 这个方法只能在run 方法中实现，不然会阻塞主线程，导致页面无响应
     */
    void onPause() {
        synchronized (lock) {
            try {
                //线程 等待/阻塞
                lock.wait();
                Log.d(TAG, "onPause: 接收线程已暂停！");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {

        while (true){
            byte[] buf = new byte[40];
            DatagramPacket dp = new DatagramPacket(buf,buf.length);
            try {
                ds.receive(dp);
                if(pause){
                    onPause();
                }
                pause = true;
                byte[] value =  arrayCheckNull(dp.getData());
                String text = new String(value, StandardCharsets.UTF_8);

                if(null != mHandler && text.length() > 0){
                    Message message = new Message();
                    message.getData().putString("hex",text);
                    mHandler.handleMessage(message);
                }
            } catch (IOException e) {
                Log.d(TAG, "消息处理失败！",e);
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }
}
