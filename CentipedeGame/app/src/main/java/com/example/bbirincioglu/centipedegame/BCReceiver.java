package com.example.bbirincioglu.centipedegame;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * This class is responsible from storing, and getting paired devices (Devices that are previously connected or just connected).
 */
public class BCReceiver extends BroadcastReceiver {
    private ArrayList<BCReceiverObserver> observers;
    private ArrayList<BluetoothDevice> pairedDevices;
    public static final int STATE_DISCOVERY_NOT_STARTED = -1;
    public static final int STATE_DISCOVERY_STARTED = 0;
    public static final int STATE_DISCOVERY_FINISHED = 1;
    private int currentState;

    public BCReceiver() {
        setObservers(new ArrayList<BCReceiverObserver>());
        setPairedDevices(new ArrayList<BluetoothDevice>());
        setCurrentState(STATE_DISCOVERY_NOT_STARTED);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
            setCurrentState(STATE_DISCOVERY_STARTED);
            notifyObservers();
        } else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
            BluetoothDevice newDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            ArrayList<BluetoothDevice> pairedDevices = getPairedDevices();

            if (!pairedDevices.contains(newDevice)) {
                pairedDevices.add(newDevice);
            }
        } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            setCurrentState(STATE_DISCOVERY_FINISHED);
            notifyObservers();
        }
    }

    public void setPairedDevices(ArrayList<BluetoothDevice> pairedDevices) {
        this.pairedDevices = pairedDevices;
    }

    public ArrayList<BluetoothDevice> getPairedDevices() {
        return pairedDevices;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public int getCurrentState() {
        return currentState;
    }

    private void setObservers(ArrayList<BCReceiverObserver> observers) {
        this.observers = observers;
    }

    private ArrayList<BCReceiverObserver> getObservers() {
        return observers;
    }

    public void addObserver(BCReceiverObserver observer) {
        getObservers().add(observer);
    }

    public void removeObserver(BCReceiverObserver observer) {
        getObservers().remove(observer);
    }

    public void notifyObservers() {
        ArrayList<BCReceiverObserver> observers = getObservers();
        int size = observers.size();

        for (int i = 0; i < size; i++) {
            observers.get(i).update(this);
        }
    }
}
