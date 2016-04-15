package com.example.bbirincioglu.centipedegame;

import android.bluetooth.BluetoothSocket;

/**
 * Created by bbirincioglu on 3/14/2016.
 */
public class SocketSingleton {
    private static SocketSingleton instance;
    private BluetoothSocket socket;
    private boolean hosted;

    private SocketSingleton() {
        this.socket = null;
        setHosted(false);
    }

    public static SocketSingleton getInstance() {
        if (instance == null) {
            instance = new SocketSingleton();
        }

        return instance;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }

    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    public boolean isHosted() {
        return hosted;
    }

    public void setHosted(boolean hosted) {
        this.hosted = hosted;
    }
}
