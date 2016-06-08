package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.util.ArrayList;
import java.util.UUID;

/**
 * This class is for listening Bluetooth Connection Requests coming from client side. This class constantly checks whether there is another phone
 * which wants to connect this so called "server" or "host".
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
        //In order to do any GUI related stuff, we need to call runOnUiThread() method. Android doesn't allow us to update GUI from a thread other than main thread.
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setCurrentStatus(ConnectionThread.STATUS_WAITING_FOR_SOMEONE_TO_JOIN_GAME);
            }
        });

        while (true) {
            try {
                setBluetoothSocket(getBluetoothServerSocket().accept()); //Constantly checks whether there is another phone trying to connect this phone's socket.
                SocketSingleton.getInstance().setSocket(getBluetoothSocket());
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            if (getBluetoothSocket() != null) { //If it is not null, this means some other phone joined the game.
                //BluetoothGameActivity.bluetoothSocket = getBluetoothSocket();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCurrentStatus(ConnectionThread.STATUS_SOMEONE_JOINED_GAME);
                    }
                });

                cancel();
                break;
            } else {//If socket is null, this means some didn't or couldn't joined the game.
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
