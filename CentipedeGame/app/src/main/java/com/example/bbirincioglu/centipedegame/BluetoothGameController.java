package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

/**
 * Controller class of BluetoothGameActivity.
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
        getBluetoothHandler().listPairedDevices(context); //list devices that are already paired.
    }

    public void doDiscoverDevices(Context context) {
        getBluetoothHandler().discoverDevices(context); //discover new devices and add them to dialog which displays already paired devices.
    }

    public void doOpenClientConnection(Activity activity, BluetoothDevice bluetoothDevice) {
        System.out.println("BLUETOOTHDEVICE INFO: " + bluetoothDevice.toString());
        setClientConnectionThread(new ClientConnectionThread(activity, bluetoothDevice));
        getClientConnectionThread().addObserver(((BluetoothGameActivity) activity).getBackgroundJobDialog());
        getClientConnectionThread().start();    //Start thread which handles client connection.
    }

    public void doOpenServerConnection(Activity activity, BluetoothAdapter bluetoothAdapter) {
        setServerConnectionThread(new ServerConnectionThread(activity, bluetoothAdapter));
        getServerConnectionThread().start();    //Start thread which handles hosted (server) connection.
    }

    public void doCancelDiscovery() {
        getBluetoothHandler().cancelDiscovery(); //Finalize device discovery.
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
