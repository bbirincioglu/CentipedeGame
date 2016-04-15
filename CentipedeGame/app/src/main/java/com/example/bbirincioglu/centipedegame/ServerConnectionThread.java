package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by bbirincioglu on 3/2/2016.
 */
public class ServerConnectionThread extends Thread implements ConnectionThread {
    public static final String UUID_STRING = "64a4c657-722c-4c25-828e-067dabef1724";
    public static UUID UUID;
    private final BluetoothServerSocket bluetoothServerSocket;
    private BluetoothSocket bluetoothSocket;

    private ArrayList<ConnectionThreadObserver> observers;
    private int currentStatus;
    private Activity activity;

    public ServerConnectionThread(Activity activity, BluetoothAdapter bluetoothAdapter) {
        this.bluetoothSocket = null;
        UUID = UUID.fromString(UUID_STRING);
        BluetoothServerSocket temp = null;

        try {
            temp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Prisoners' Dilemma", UUID);
        } catch(Exception e) {
            e.printStackTrace();
        }

        bluetoothServerSocket = temp;
        setObservers(new ArrayList<ConnectionThreadObserver>());
        setCurrentStatus(ConnectionThread.STATUS_INITIALIZED);
        this.activity = activity;
    }

    public void run() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setCurrentStatus(ConnectionThread.STATUS_WAITING_FOR_SOMEONE_TO_JOIN_GAME);
            }
        });

        while (true) {
            try {
                setBluetoothSocket(getBluetoothServerSocket().accept());
                SocketSingleton.getInstance().setSocket(getBluetoothSocket());
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            if (getBluetoothSocket() != null) {
                //BluetoothGameActivity.bluetoothSocket = getBluetoothSocket();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCurrentStatus(ConnectionThread.STATUS_SOMEONE_JOINED_GAME);
                    }
                });

                cancel();
                break;
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCurrentStatus(ConnectionThread.STATUS_CONNECTION_FAILED);
                    }
                });
            }
        }
    }

    public BluetoothServerSocket getBluetoothServerSocket() {
        return bluetoothServerSocket;
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    public void cancel() {
        try {
            getBluetoothServerSocket().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public ArrayList<ConnectionThreadObserver> getObservers() {
        return observers;
    }

    public void setObservers(ArrayList<ConnectionThreadObserver> observers) {
        this.observers = observers;
    }

    public static String getUuidString() {
        return UUID_STRING;
    }

    public static java.util.UUID getUUID() {
        return UUID;
    }

    public static void setUUID(java.util.UUID UUID) {
        ServerConnectionThread.UUID = UUID;
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
