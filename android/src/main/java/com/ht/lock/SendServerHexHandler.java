package com.ht.lock;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.app.serial.HexHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import androidx.annotation.NonNull;

class SendServerHexHandler extends Handler {
    private static final String TAG = "SendServerHexHandler";
    private String serverAddress;
    private int serverPort;
    private DatagramSocket mDatagramSocket;
    private BaseSerialPort mBaseSerialPort;

    public SendServerHexHandler(BaseSerialPort mBaseSerialPort, DatagramSocket mDatagramSocket, String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.mDatagramSocket = mDatagramSocket;
        this.mBaseSerialPort = mBaseSerialPort;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        String hex = msg.getData().getString(HexHandler.HEX);
        byte[] sendArray = hex.getBytes();
        try {
            DatagramPacket dp = new DatagramPacket(sendArray, sendArray.length, InetAddress.getByName(serverAddress), serverPort);
            mDatagramSocket.send(dp);
            mBaseSerialPort.responseSuccess();
            Log.d(TAG, "向服务端 " + serverAddress + ":" + serverPort +"发送 : " + hex);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
