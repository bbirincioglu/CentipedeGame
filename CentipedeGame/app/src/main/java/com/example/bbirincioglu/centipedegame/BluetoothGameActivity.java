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
    private DiscoveryListDialog discoveryListDialog; //Dialog for displaying all the devices that are just discovered, and / or previously paired.
    private BackgroundJobDialog backgroundJobDialog; //Dialog appearing when there is a backend process going on. (Such as waiting for device discovery finishes.
    private BluetoothGameController controller; //Corresponding controller. Controller gets user inputs and transfer them to domain objects.
    private ArrayList<Dialog> dialogs; //All the dialogs are stored in this list so that they can be closed before switching from this activity to another.

    //Initialize everything. Dialogs are created via dialogFactory, and we want only one factory during the whole application time. Thus, it is implemented
    //via Singleton Pattern.
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

    //Listener methods for buttons or other UI objects.
    public void onClick(View v) {
        int id = v.getId();
        BluetoothGameController controller = getController();
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(this);

        //There are two buttons on the screen, Host Game, and Join Game buttons.
        if (id == R.id.hostGameButton) {
            SocketSingleton.getInstance().setHosted(true); //Save the user as hosted.
            controller.doOpenServerConnection(this, BluetoothAdapter.getDefaultAdapter()); //controller got the signal, and transferred it to related domain
                                                                                            //objects. (Look which objects are used in doOpenServerConnection()
                                                                                            // method.
            controller.getServerConnectionThread().addObserver(getBackgroundJobDialog()); //ServerConnectionThread object is observable, and background job dialog
                                                                                            //is observer. Thus connect observable to observer.
            controller.getServerConnectionThread().notifyObservers();   //notify observers of ServerConnectionThread manually so that waiting dialog appears.
        } else if (id == R.id.joinGameButton) {
            SocketSingleton.getInstance().setHosted(false); //Save the user as client.
            setDiscoveryListDialog((DiscoveryListDialog) DialogFactory.getInstance().create(DialogFactory.DIALOG_DISCOVERY_LIST));
            controller.doListPairedDevices(this); //Initialize a process to list all the paired devices in the DiscoveryListDialog.
        }
    }

    public DiscoveryListDialog getDiscoveryListDialog() {
        return discoveryListDialog;
    }

    public void setDiscoveryListDialog(DiscoveryListDialog discoveryListDialog) {
        this.discoveryListDialog = discoveryListDialog;
    }

    //When back button of the phone is pressed, this means we close this activity and go to the previous one which is MainMenuActivity.
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
