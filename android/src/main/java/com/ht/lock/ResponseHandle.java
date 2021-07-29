package com.ht.lock;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.app.serial.HexHandler;

import androidx.annotation.NonNull;

public class ResponseHandle extends HexHandler {
    private static final String TAG = "ResponseHandle";
    private HexResponseTimeoutRunnable mHexResponseTimeoutRunnable;
    private Handler callback;
    public ResponseHandle(HexResponseTimeoutRunnable hexResponseTimeoutRunnable) {
        mHexResponseTimeoutRunnable = hexResponseTimeoutRunnable;
    }

    public void setCallback(Handler callback) {
        this.callback = callback;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        Log.d(TAG, "handleMessage: 收到串口相应："+msg);
        if(null != mHexResponseTimeoutRunnable) {
            mHexResponseTimeoutRunnable.stop();
        }

        if(null != callback){
            callback.handleMessage(msg);
        }
    }

    public void setRunnable (HexResponseTimeoutRunnable runnable) {
        mHexResponseTimeoutRunnable = runnable;
    }
};
