package com.ht.lock;

import android.os.Handler;
import android.util.Log;

import com.app.serial.SerialPortConfig;
import com.app.serial.SerialPortManager;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * 锁控串口管理
 */
class LockSerialPortManager implements BaseSerialPort {
    private final String PATH_USB = "/dev/ttyUSB0";
    private final String PATH_TTY = "ttyS";
    private final int BAUD_RATE = 9600;
    private static final String TAG = "LockSerialPortManager";

    public HexResponseTimeoutRunnable runnable;
    private DatagramSocket mDatagramSocket;
    private UDPThread mUDPThread;

    /**
     * 向串口发送指令
     */
    private Handler sendHandler;
    /**
     * 接收串口响应数据
     */
    private ResponseHandle responseHexHandler;

    private SerialPortManager mSerialPortManager;

    public void usb () {
        int index = 0;
        while (index < 30) {
            try {
                SerialPortConfig lockConfig = new SerialPortConfig(PATH_USB+index, BAUD_RATE);
                Log.d(TAG, "LockSerialPortManager: "+lockConfig.getPath());
                this.mSerialPortManager = new SerialPortManager(lockConfig);
            } catch (Exception e) {
            }
            index ++;
            if(this.mSerialPortManager != null){
                break;
            }
        }
    }

    private void tty () {
        int index = 0;
        while (index < 10) {
            try {
                SerialPortConfig lockConfig = new SerialPortConfig(PATH_TTY+index, BAUD_RATE);
                Log.d(TAG, "LockSerialPortManager: "+lockConfig.getPath());
                this.mSerialPortManager = new SerialPortManager(lockConfig);
            } catch (Exception e) {
            }
            index ++;
            if(this.mSerialPortManager != null){
                break;
            }
        }

        if(this.mSerialPortManager == null) {
            SerialPortConfig lockConfig = new SerialPortConfig("tty", BAUD_RATE);
            try {
                this.mSerialPortManager = new SerialPortManager(lockConfig);
            } catch (Exception e) {
            }
        }
    }

    public void connect () throws Exception {
        SerialPortConfig lockConfig = new SerialPortConfig(PATH_USB, BAUD_RATE);
        new SerialPortManager(lockConfig);
    }

    /**
     * 开启串口
     * @param pathName 串口地址
     * @param baudRate 波特率
     */
    public void openSerialPort (String pathName,int baudRate) throws Exception {
        SerialPortConfig lockConfig = new SerialPortConfig(pathName, baudRate);
        this.mSerialPortManager = new SerialPortManager(lockConfig);
    }

    /**
     * 启动数据推送服务 发送指令到串口后 将接收到的响应数据推送给服务端
     * @param serverIP 服务端 地址
     * @param serverPort 服务端 端口
     */
    public void startSendServer (String serverIP,int serverPort) throws Exception {
        if(mDatagramSocket != null){
            responseHexHandler.setCallback(new SendServerHexHandler(this,mDatagramSocket,serverIP,serverPort));
        } else {
            throw new Exception("mDatagramSocket is null");
        }
    }

    /**
     * 启动串口服务 用于处理本地锁控相关的指令的接收与执行
     * @param lockPort 服务端口
     */
    public void startSerialPortServer (int lockPort) throws Exception {
        if(mDatagramSocket == null && mSerialPortManager != null){
            mDatagramSocket = new DatagramSocket(lockPort);
            this.sendHandler = new SendHexHandler(this);
            mUDPThread = new UDPThread(mDatagramSocket,sendHandler);
            mUDPThread.start();
            initResponse();
        } else {
            if(mDatagramSocket == null){
                throw new Exception("请先关闭后在重新开启！");
            } else {
                throw new Exception("mSerialPortManager is null 串口未开启！");
            }
        }
    }

    /**
     *处理串口响应数据
     */
    private void initResponse () {
        responseHexHandler = new ResponseHandle(this.runnable);
        mSerialPortManager.setHandler(responseHexHandler);
    }

    /**
     * 响应后端指令服务相关
     */
    private void initServer (String ip,int port) {
        responseHexHandler.setCallback(new SendServerHexHandler(this,mDatagramSocket,ip,port));
    }


    /**
     * 串口响应超时
     */
    @Override
    public void responseTimeout() {
        Log.d(TAG, "responseTimeout: 串口响应超时！");
        this.mUDPThread.resumeThread();
    }

    /**
     * 串口响应成功
     */
    @Override
    public void responseSuccess() {
        Log.d(TAG, "responseSuccess: 串口响应完成");
        this.mUDPThread.resumeThread();
    }

    @Override
    public void setRunnable(HexResponseTimeoutRunnable runnable) {
        this.runnable = runnable;
        this.responseHexHandler.setRunnable(runnable);
    }

    @Override
    public void sendHex(String hex) {
        try {
            mSerialPortManager.send(hex);
        } catch (Exception e) {
            Log.d(TAG, "sendHex: 指令异常！"+hex);
        }
    }

    @Override
    public void close() {
        if(mDatagramSocket != null){
            mDatagramSocket.close();
            mDatagramSocket = null;
        }

        if(mUDPThread != null){
            mUDPThread.interrupt();
            mUDPThread = null;
        }

        if(this.mSerialPortManager != null ){
            this.mSerialPortManager.close();
            this.mSerialPortManager = null;
        }
        Log.d(TAG, "关闭串口");
    }
}
