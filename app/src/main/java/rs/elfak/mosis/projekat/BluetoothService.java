package rs.elfak.mosis.projekat;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothService extends Service {

    private String name = "bluetooth";
//    private UUID uuid = UUID.fromString("4f9257c9-61c2-4c75-8696-c7b4085316f7");
    private UUID uuid;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private User currentUser;
    ConnectThread mConnectThread;

    public BluetoothService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void startThread() {
        Log.d(ContentValues.TAG, "Start");
    }

    public class AcceptThread extends Thread {
        BluetoothServerSocket mServerSocket;
        @SuppressLint("MissingPermission")
        public AcceptThread(Handler handler) {
            try {
                mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(name, uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            boolean shouldLoop = true;
            while (true) {
                try {
                    BluetoothSocket mBluetoothSocket = mServerSocket.accept();
//                    manageConnectedSocket(mBluetoothSocket,user.);
                    mServerSocket.close();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class ConnectThread extends Thread {
        BluetoothSocket mBluetoothSocket;
        @SuppressLint("MissingPermission")
        public ConnectThread(BluetoothDevice mDevice,User user,UUID btuuid) {
            currentUser = user;
            uuid = btuuid;
            try {
                mBluetoothSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("MissingPermission")
        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            try {
                mBluetoothSocket.connect();
//                manageConnectedSocket();
            } catch (IOException connectException) {
                connectException.printStackTrace();
            }
        }
    }

    public class ConnectionThread extends Thread {

    }

    private void manageConnectedSocket(BluetoothSocket mSocket, String userId) {
//        ConnectionThread conn = new ConnectionThread(mBluetoothSocket, mHandler);
//        mHandler.obtainMessage(DataTransferActivity.SOCKET_CONNECTED, conn)
//                .sendToTarget();
//        conn.start();
    }

    public void start() {

    }
}