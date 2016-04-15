package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by bbirincioglu on 3/2/2016.
 */
public class ClientConnectionThread extends Thread implements ConnectionThread {
    public static final String UUID_STRING = "64a4c657-722c-4c25-828e-067dabef1724";
    public static UUID UUID;
    private final BluetoothDevice bluetoothDevice;
    private final BluetoothSocket bluetoothSocket;

    private ArrayList<ConnectionThreadObserver> observers;
    private int currentStatus;
    private Activity activity;

    public ClientConnectionThread(Activity activity, BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.UUID = UUID.fromString(UUID_STRING);
        BluetoothSocket temp = null;

        try {
            temp = getBluetoothDevice().createRfcommSocketToServiceRecord(UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bluetoothSocket = temp;
        setObservers(new ArrayList<ConnectionThreadObserver>());
        setCurrentStatus(ConnectionThread.STATUS_INITIALIZED);
        this.activity = activity;
    }

    @Override
    public void run() {
        BluetoothGameController controller = ((BluetoothGameActivity) getActivity()).getController();
        controller.doCancelDiscovery();

        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCurrentStatus(ConnectionThread.STATUS_CONNECTING);
                }
            });

            getBluetoothSocket().connect();
            SocketSingleton.getInstance().setSocket(getBluetoothSocket());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCurrentStatus(ConnectionThread.STATUS_CONNECTED);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCurrentStatus(ConnectionThread.STATUS_CONNECTION_FAILED);
                }
            });
            cancel();
            return;
        }

        //controller.doHandleBluetoothSocket(getBluetoothSocket());
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
