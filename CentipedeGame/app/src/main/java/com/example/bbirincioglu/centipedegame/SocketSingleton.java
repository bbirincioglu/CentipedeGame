package com.example.bbirincioglu.centipedegame;

import android.bluetooth.BluetoothSocket;

/**
 * Singleton Object to store bluetooth socket which is used for communicating with other phone (via input-output streams), and whether the player is hosted or not.
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
