package com.example.bbirincioglu.centipedegame;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class BluetoothGameActivity extends AppCompatActivity {
    private DiscoveryListDialog discoveryListDialog;
    private BackgroundJobDialog backgroundJobDialog;
    private BluetoothGameController controller;
    private ArrayList<Dialog> dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_game);
        setDialogs(new ArrayList<Dialog>());
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(this);
        setBackgroundJobDialog((BackgroundJobDialog) dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB));
        setController(new BluetoothGameController());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        int id = v.getId();
        BluetoothGameController controller = getController();
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(this);

        if (id == R.id.hostGameButton) {
            SocketSingleton.getInstance().setHosted(true);
            controller.doOpenServerConnection(this, BluetoothAdapter.getDefaultAdapter());
            controller.getServerConnectionThread().addObserver(getBackgroundJobDialog());
            controller.getServerConnectionThread().notifyObservers();
        } else if (id == R.id.joinGameButton) {
            SocketSingleton.getInstance().setHosted(false);
            setDiscoveryListDialog((DiscoveryListDialog) DialogFactory.getInstance().create(DialogFactory.DIALOG_DISCOVERY_LIST));
            controller.doListPairedDevices(this);
        }
    }

    public DiscoveryListDialog getDiscoveryListDialog() {
        return discoveryListDialog;
    }

    public void setDiscoveryListDialog(DiscoveryListDialog discoveryListDialog) {
        this.discoveryListDialog = discoveryListDialog;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Keys.RETURN_FROM_ACTIVITY, true);
        new ActivitySwitcher().fromPreviousToNext(this, MainMenuActivity.class, bundle, true);
    }

    public BackgroundJobDialog getBackgroundJobDialog() {
        return backgroundJobDialog;
    }

    public void setBackgroundJobDialog(BackgroundJobDialog backgroundJobDialog) {
        this.backgroundJobDialog = backgroundJobDialog;
    }

    public BluetoothGameController getController() {
        return controller;
    }

    public void setController(BluetoothGameController controller) {
        this.controller = controller;
    }

    public ArrayList<Dialog> getDialogs() {
        return dialogs;
    }

    public void setDialogs(ArrayList<Dialog> dialogs) {
        this.dialogs = dialogs;
    }
}
