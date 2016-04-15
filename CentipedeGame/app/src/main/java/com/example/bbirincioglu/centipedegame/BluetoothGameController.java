package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

/**
 * Created by bbirincioglu on 3/1/2016.
 */
public class BluetoothGameController {
    private BluetoothHandler bluetoothHandler;
    private ServerConnectionThread serverConnectionThread;
    private ClientConnectionThread clientConnectionThread;
    private BluetoothSocket bluetoothSocket;

    public BluetoothGameController() {
        setBluetoothHandler(new BluetoothHandler());
    }

    public void setBluetoothHandler(BluetoothHandler bluetoothHandler) {
        this.bluetoothHandler = bluetoothHandler;
    }

    public BluetoothHandler getBluetoothHandler() {
        return bluetoothHandler;
    }

    public void doListPairedDevices(Context context) {
        getBluetoothHandler().listPairedDevices(context);
        //bluetoothHandler.discoverDevices(context);
    }

    public void doDiscoverDevices(Context context) {
        getBluetoothHandler().discoverDevices(context);
    }

    public void doOpenClientConnection(Activity activity, BluetoothDevice bluetoothDevice) { // It is actually doConnect();
        System.out.println("BLUETOOTHDEVICE INFO: " + bluetoothDevice.toString());
        setClientConnectionThread(new ClientConnectionThread(activity, bluetoothDevice));
        getClientConnectionThread().addObserver(((BluetoothGameActivity) activity).getBackgroundJobDialog());
        getClientConnectionThread().start();
    }

    public void doOpenServerConnection(Activity activity, BluetoothAdapter bluetoothAdapter) {
        setServerConnectionThread(new ServerConnectionThread(activity, bluetoothAdapter));
        getServerConnectionThread().start();
    }

    public void doCancelDiscovery() {
        getBluetoothHandler().cancelDiscovery();
    }

    public ServerConnectionThread getServerConnectionThread() {
        return serverConnectionThread;
    }

    public void setServerConnectionThread(ServerConnectionThread serverConnectionThread) {
        this.serverConnectionThread = serverConnectionThread;
    }

    public ClientConnectionThread getClientConnectionThread() {
        return clientConnectionThread;
    }

    public void setClientConnectionThread(ClientConnectionThread clientConnectionThread) {
        this.clientConnectionThread = clientConnectionThread;
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }
}
