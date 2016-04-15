package com.example.bbirincioglu.centipedegame;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bbirincioglu on 3/1/2016.
 */
public class DiscoveryListAdapter extends ArrayAdapter<BluetoothDevice> {
    private ArrayList<BluetoothDevice> devices;
    private Context context;

    public DiscoveryListAdapter(Context context, int rowResourceID, ArrayList<BluetoothDevice> devices) {
        super(context, rowResourceID, devices);
        this.devices = devices;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.discovery_list_row, parent, false);

        TextView rowTextView = (TextView) row.findViewById(R.id.discoveryListRowTextView);
        CheckBox checkBox = (CheckBox) row.findViewById(R.id.discoveryListRowCheckBox);
        checkBox.setOnClickListener(new CheckBoxListener());

        if (position == 0) {
            checkBox.setChecked(true);
        }

        checkBox.setTag(R.id.discoveryListRowCheckBox, getDevices().get(position));

        BluetoothDevice device = getDevices().get(position);
        String name = device.getName();
        int bondState = device.getBondState();
        String address = device.getAddress();

        rowTextView.setText(name + bondState + address);
        return row;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<BluetoothDevice> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<BluetoothDevice> devices) {
        this.devices = devices;
    }

    private class CheckBoxListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            System.out.println("IN THE ONCLICK");
            CheckBox checkBox = (CheckBox) v;
            boolean isChecked = checkBox.isChecked();

            if (isChecked) {
                ListView listView = findListView(checkBox);
                int childCount = listView.getChildCount();

                for (int i = 0; i < childCount; i++) {
                    View row = listView.getChildAt(i);
                    ((CheckBox) row.findViewById(R.id.discoveryListRowCheckBox)).setChecked(false);
                }
            }

            checkBox.setChecked(true);
        }
    }

    private ListView findListView(View view) {
        ListView wanted = null;
        ViewParent parent = view.getParent();

        while (!(parent instanceof ListView)) {
            parent = parent.getParent();
        }

        wanted = (ListView) parent;
        return wanted;
    }
}
