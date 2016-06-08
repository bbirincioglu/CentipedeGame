package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by bbirincioglu on 2/28/2016.
 */
public class BluetoothHandler {
    public static int BLUETOOTH_SUCCESSFULL = 100;
    public static int DISCOVERABILITY_SUCCESSFULL = 101;
    private BCReceiver bcReceiver;

    public BluetoothHandler() {
        setBcReceiver(new BCReceiver());
        getBcReceiver().getPairedDevices().addAll(BluetoothAdapter.getDefaultAdapter().getBondedDevices()); //Find already bounded (paired) devices
                                                                                                            // and add them to array list of bcReceiver.
        System.out.println("INITIAL SIZE OF: " + getBcReceiver().getPairedDevices().size());
    }

    //Check whether phone supports bluetooth connection.
    public boolean isBluetoothSupported() {
        boolean result = true;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            result = false;
        }

        return result;
    }

    //Check whether bluetooth is open.
    public boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    //Check whether phone is in state discoverable.
    public boolean isDiscoverable() {
        boolean result = false;

        if (BluetoothAdapter.getDefaultAdapter().getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            result = true;
        }

        return result;
    }

    //Enable bluetooth.
    public void enableBluetooth(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ((Activity) context).startActivityForResult(bluetoothIntent, BLUETOOTH_SUCCESSFULL);
    }

    //Make device discoverable for 300 seconds so that when discovery process has started, the device can be recognized by other device.
    public void enableDiscoverability(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        ((Activity) context).startActivityForResult(discoverableIntent, DISCOVERABILITY_SUCCESSFULL);
    }

    //Discovery all devices via intentFilter.
    public void discoverDevices(Context context) {
        BCReceiver bcReceiver = getBcReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(bcReceiver, intentFilter);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();
    }

    //notifying bcReceiver's observers means that, any GUI object (usually dialog) that are binded to bcReceiver will update their appearance.
    //GUI object will take all the paired devices stored in the "pairedDevices" arrayList of bcReceiver object, and display their names, IDs etc. on the dialog.
    public void listPairedDevices(Context context) {
        BCReceiver bcReceiver = getBcReceiver();
        bcReceiver.notifyObservers();
    }

    //Finalize discovery.
    public void cancelDiscovery() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();
    }

    public BCReceiver getBcReceiver() {
        return bcReceiver;
    }

    public void setBcReceiver(BCReceiver bcReceiver) {
        this.bcReceiver = bcReceiver;
    }
}
