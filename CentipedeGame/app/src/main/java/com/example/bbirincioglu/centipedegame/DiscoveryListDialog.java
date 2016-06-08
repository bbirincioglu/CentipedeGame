package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Dialog for displaying devices that are paired or just discovered.
 */
public class DiscoveryListDialog extends Dialog implements BCReceiverObserver, SimpleDialog {
    private Activity activity;

    public DiscoveryListDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
        setActivity((Activity) context);

        if (getActivity() instanceof BluetoothGameActivity) {
            ((BluetoothGameActivity) getActivity()).getDialogs().add(this);
        }

        if (getActivity() instanceof GamePlayActivity) {
            ((GamePlayActivity) getActivity()).getDialogs().add(this);
        }
    }

    @Override
    public void update(BCReceiver bcReceiver) {
        int currentState = bcReceiver.getCurrentState();
        ArrayList<BluetoothDevice> pairedDevices = bcReceiver.getPairedDevices();

        if (currentState == BCReceiver.STATE_DISCOVERY_NOT_STARTED) {
            updateContentView("DEVICES ALREADY PAIRED", pairedDevices);
        } else if (currentState == BCReceiver.STATE_DISCOVERY_STARTED) {
            setContentView(R.layout.discovery_started);
            setTitle("DISCOVERY STARTED");
        } else if (currentState == BCReceiver.STATE_DISCOVERY_FINISHED) {
            updateContentView("DISCOVERY FINISHED.", pairedDevices);
        }

        if (!isShowing()) {
            show();
        }
    }

    //When new devices are paired or discovered, we update dialogs' rows so that they also become selectable by the user. We somewhat re-construct this dialog.
    private void updateContentView(String title, ArrayList<BluetoothDevice> pairedDevices) {
        setTitle(title);
        setContentView(R.layout.discovery_list_dialog);
        DiscoveryListAdapter discoveryListAdapter = new DiscoveryListAdapter(getActivity(), R.layout.discovery_list_row, pairedDevices);
        ((ListView) findViewById(R.id.discoveryListView)).setAdapter(discoveryListAdapter);

        ButtonListener buttonListener = new ButtonListener();
        final Button connectButton = ((Button) findViewById(R.id.connectButton));
        final Button discoverButton = ((Button) findViewById(R.id.discoverButton));
        connectButton.setOnClickListener(buttonListener);
        discoverButton.setOnClickListener(buttonListener);
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void initialize() {
        BCReceiver bcReceiver = ((BluetoothGameActivity) getActivity()).getController().getBluetoothHandler().getBcReceiver();
        bcReceiver.addObserver(this);
    }

    //To find which device is selected for connection.
    private CheckBox findSelectedCheckBox(ListView listView) {
        CheckBox selectedCheckBox = null;
        int childCount = listView.getChildCount();

        for (int i = 0; i < childCount; i++) {
            LinearLayout child = (LinearLayout) listView.getChildAt(i);
            int childCount2 = child.getChildCount();

            for (int j = 0; j < childCount2; j++) {
                View child2 = child.getChildAt(j);

                if (child2 instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) child2;

                    if (checkBox.isChecked()) {
                        selectedCheckBox = checkBox;
                        break;
                    }
                }
            }

            if (selectedCheckBox != null) {
                break;
            }
        }

        return selectedCheckBox;
    }

    //Button listeners for discover and connect buttons.
    private class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            int buttonID = v.getId();
            BluetoothGameController controller = ((BluetoothGameActivity) getActivity()).getController();

            if (buttonID == R.id.discoverButton) { //Initialize discovery.
                controller.doDiscoverDevices(getActivity());
            } else if (buttonID == R.id.connectButton) {
                LinearLayout container = (LinearLayout) v.getParent().getParent();
                ListView listView = (ListView) container.findViewById(R.id.discoveryListView);
                CheckBox selectedCheckBox = findSelectedCheckBox(listView);

                if (selectedCheckBox != null) {
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) selectedCheckBox.getTag(selectedCheckBox.getId()); //Tag stored in the selected check box
                                                                                                                            //is actually bluetooth device
                                                                                                                            //to be connected. We get it, and

                    if (bluetoothDevice != null) {
                        controller.doOpenClientConnection(getActivity(), bluetoothDevice);  //initialize client connection.
                    } else {
                        System.out.println("IN THE DISCOVERY LIST DIALOG: BLUETOOTH DEVICE NULL.");
                    }
                }
            }
        }
    }
}
