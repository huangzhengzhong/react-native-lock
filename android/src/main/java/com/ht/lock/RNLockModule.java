
package com.ht.lock;

import android.os.Handler;
import android.util.Log;

import com.app.serial.SerialUtil;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;

import java.net.DatagramSocket;

public class RNLockModule extends ReactContextBaseJavaModule {
    private static final String TAG = "RNLockModule";
    private final ReactApplicationContext reactContext;
    private static LockSerialPortManager mLockSerialPortManager = new LockSerialPortManager();
    private static String serverIP;//数据推送服务地址
    private static String serverPort;//数据推送服务端口
    private final int HEX_RESOLVER_PORT = 9001;//指令接收 本地通讯器端口
    private static DatagramSocket lockDS;//本地指令 通信器;
    private static LockDatagramSocketThread lockDatagramSocketThread;
    private static Handler dsResult;

    public RNLockModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNLock";
    }

    /**
     * 开启锁控串口
     *
     * @param pathName 串口地址
     * @param baudRate 波特率
     */
    @ReactMethod
    public void openSerialPort(String pathName, int baudRate, Promise promise) {
        try {
            mLockSerialPortManager.openSerialPort(pathName, baudRate);
            promise.resolve("success");
            Log.d(TAG, "已开启锁控串口！");
        } catch (Exception e) {
            String text = "开启串口失败！";
            Log.e(TAG, text, e);
            promise.reject("-1", text);
        }
    }

    /**
     * 启动串口服务 用于处理串口的指令收发
     *
     * @param port 本地端口
     */
    @ReactMethod
    public void startSerialPortServer(int port, Promise promise) {
        try {
            mLockSerialPortManager.startSerialPortServer(port);
            promise.resolve("success");
            Log.d(TAG, "锁控串口服务启动完成！");
        } catch (Exception e) {
            String text = "启动串口服务失败！";
            Log.e(TAG, text, e);
            promise.reject("-1", text);
        }
    }

    /**
     * 启动串口响应数据推送服务 用于将收到的串口响应数据推送给服务端
     *
     * @param serverIP   服务端地址
     * @param serverPort 服务端端口
     */
    @ReactMethod
    public void startSendServer(String serverIP, int serverPort, Promise promise) {
        try {
            RNLockModule.serverIP = serverIP;
            RNLockModule.serverPort = serverIP;
            mLockSerialPortManager.startSendServer(serverIP, serverPort);
            promise.resolve("success");
            Log.d(TAG, "锁控数据推送服务启动完成！");
        } catch (Exception e) {
            String text = "锁控数据推送服务启动失败！";
            Log.e(TAG, text, e);
            promise.reject("-1", text);
        }
    }

    /**
     * 获取全部串口名
     */
    @ReactMethod
    public void list(Promise promise) {
        try {
            WritableArray writableArray = Arguments.createArray();
            for (String item : SerialUtil.logList()) {
                writableArray.pushString(item);
            }
            promise.resolve(writableArray);
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject("-1", "获取串口列表失败！");
        }
    }
}