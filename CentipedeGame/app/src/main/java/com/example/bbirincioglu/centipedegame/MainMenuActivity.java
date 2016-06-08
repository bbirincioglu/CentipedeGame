package com.example.bbirincioglu.centipedegame;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
/*
    The first activity which contains main menu of application.
 */
public class MainMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Intent intent = getIntent();

        if (intent != null) {
            boolean returnFromActivity = intent.getBooleanExtra(Keys.RETURN_FROM_ACTIVITY, false);

            if (!returnFromActivity) { //check whether the application has just started, or we came this activity from previous activites.
                InternetHandler internetHandler = new InternetHandler(this);
                boolean isWifiEnabled = internetHandler.isWifiEnabled(); //check wifi, and mobile data because they are needed to connect Parse Server.
                boolean isMobileDataEnabled = internetHandler.isMobileDataEnabled();

                if (!isWifiEnabled && !isMobileDataEnabled) { //If none of them are available, display appropriate dialog to force user to enable one of them.
                    DialogFactory dialogFactory = DialogFactory.getInstance();
                    dialogFactory.setContext(this);
                    Dialog dialog = dialogFactory.create(DialogFactory.DIALOG_WIFI_MOBILE_DATA);
                    dialog.show();
                } else { //If they are available display player info dialog so that they can enter their names, and surnames.
                    DialogFactory dialogFactory = DialogFactory.getInstance();
                    dialogFactory.setContext(this);
                    Dialog dialog = dialogFactory.create(DialogFactory.DIALOG_PLAYER_INFO);
                    dialog.show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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

    //ButtonListener method for buttons on the screen.
    public void onClick(View v) {
        Button button = (Button) v;
        int buttonID = button.getId();
        MainMenuController controller = MainMenuController.getInstance();

        if (buttonID == R.id.bluetoothGameButton) {
            controller.doBluetoothGameActivity(button);
        } else if (buttonID == R.id.settingsButton) {
            controller.doSettingsActivity(button);
        } else if (buttonID == R.id.gameResultsButton) {
            controller.doGameResultsActivity(button);
        } else if (buttonID == R.id.exitButton) {
            controller.doExitGame(this);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainMenuController controller = MainMenuController.getInstance();
        controller.doEnableDiscoverability(this);
    }

    @Override
    public void onBackPressed() {

    }
}
