package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Thread handling client connection to hosted connection.
 */
public class ClientConnectionThread extends Thread implements ConnectionThread {
    public static final String UUID_STRING = "64a4c657-722c-4c25-828e-067dabef1724"; //random UUID_STRING created by UUID.randomUUID().toString()
    public static UUID UUID;
    private final BluetoothDevice bluetoothDevice;
    private final BluetoothSocket bluetoothSocket;  //bluetooth socket for sending and receiving messages.

    private ArrayList<ConnectionThreadObserver> observers; //observers that observe this thread.
    private int currentStatus;  //status of this thread.
    private Activity activity;

    public ClientConnectionThread(Activity activity, BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.UUID = UUID.fromString(UUID_STRING);
        BluetoothSocket temp = null;

        try {
            temp = getBluetoothDevice().createRfcommSocketToServiceRecord(UUID); //get socket.
        } catch (Exception e) {
            e.printStackTrace();
        }

        bluetoothSocket = temp; //store socket.
        setObservers(new ArrayList<ConnectionThreadObserver>());
        setCurrentStatus(ConnectionThread.STATUS_INITIALIZED);
        this.activity = activity;
    }

    @Override
    public void run() {
        BluetoothGameController controller = ((BluetoothGameActivity) getActivity()).getController();
        controller.doCancelDiscovery(); //discovery is cancelled because device to connect is selected from the list.

        try {
            //to update GUI from another thread you either have to use AsyncTask class or runOnUIThread() method of activity.
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCurrentStatus(ConnectionThread.STATUS_CONNECTING);
                }
            });

            getBluetoothSocket().connect(); // try to connect.
            SocketSingleton.getInstance().setSocket(getBluetoothSocket()); //store the socket in the Singleton object to be used during the game play.
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCurrentStatus(ConnectionThread.STATUS_CONNECTED); //connection is completed, change the status.
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCurrentStatus(ConnectionThread.STATUS_CONNECTION_FAILED); //connection is failed, cancel thread.
                }
            });
            cancel();
            return;
        }
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public void cancel() {
        try {
            getBluetoothSocket().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUuidString() {
        return UUID_STRING;
    }

    public static java.util.UUID getUUID() {
        return UUID;
    }

    public static void setUUID(java.util.UUID UUID) {
        ClientConnectionThread.UUID = UUID;
    }

    public ArrayList<ConnectionThreadObserver> getObservers() {
        return observers;
    }

    public void setObservers(ArrayList<ConnectionThreadObserver> observers) {
        this.observers = observers;
    }

    public void notifyObservers() {
        ArrayList<ConnectionThreadObserver> observers = getObservers();

        for (ConnectionThreadObserver observer : observers) {
            observer.update(this);
        }
    }

    public void addObserver(ConnectionThreadObserver observer) {
        getObservers().add(observer);
    }

    public void removeObserver(ConnectionThreadObserver observer) {
        getObservers().remove(observer);
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
        notifyObservers();
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
