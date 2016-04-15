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
        getBcReceiver().getPairedDevices().addAll(BluetoothAdapter.getDefaultAdapter().getBondedDevices());
        System.out.println("INITIAL SIZE OF: " + getBcReceiver().getPairedDevices().size());
    }

    public boolean isBluetoothSupported() {
        boolean result = true;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            result = false;
        }

        return result;
    }

    public boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    public boolean isDiscoverable() {
        boolean result = false;

        if (BluetoothAdapter.getDefaultAdapter().getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            result = true;
        }

        return result;
    }

    public void enableBluetooth(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ((Activity) context).startActivityForResult(bluetoothIntent, BLUETOOTH_SUCCESSFULL);
    }

    public void enableDiscoverability(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        ((Activity) context).startActivityForResult(discoverableIntent, DISCOVERABILITY_SUCCESSFULL);
    }

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

    public void listPairedDevices(Context context) {
        BCReceiver bcReceiver = getBcReceiver();
        bcReceiver.notifyObservers();
    }

    public void cancelDiscovery() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();
    }

    /*public BroadcastReceiver getBroadcastReceiver(Context context) {
        final MainMenuActivity mainMenuActivity = (MainMenuActivity) context;

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    System.out.println("MY NAME: " + BluetoothAdapter.getDefaultAdapter().getName() + "," + BluetoothAdapter.getDefaultAdapter().getAddress());

                    if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                        AlertDialog dialog = dialogBuilder.setTitle("DISCOVERY STARTED").setMessage("Discovering Devices...").create();
                        mainMenuActivity.replaceDialog(dialog);
                    } else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                        BluetoothDevice newDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        System.out.println("NewDeviceName: " + newDevice.getName() + "," + newDevice.getBondState() + "," + newDevice.getAddress());

                        if (getPairedDevices().contains(newDevice)) {
                            System.out.println("Already exists.");
                        } else {
                            getPairedDevices().add(newDevice);
                            System.out.println("Size" + getPairedDevices().size());
                        }
                    } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                        Dialog dialog = new Dialog(context, android.R.style.Theme_Holo_Dialog);
                        dialog.setContentView(R.layout.discovery_list_dialog);
                        ListView discoveryListView = (ListView) dialog.findViewById(R.id.discoveryListView);
                        DiscoveryListAdapter discoveryListAdapter = new DiscoveryListAdapter(context, R.layout.discovery_list_row, getPairedDevices());
                        discoveryListView.setAdapter(discoveryListAdapter);
                        mainMenuActivity.replaceDialog(dialog);
                    }
                }
            };
        }

        return broadcastReceiver;
    }*/

    public BCReceiver getBcReceiver() {
        return bcReceiver;
    }

    public void setBcReceiver(BCReceiver bcReceiver) {
        this.bcReceiver = bcReceiver;
    }
}
