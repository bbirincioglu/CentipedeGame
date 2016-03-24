package com.example.bbirincioglu.centipedegame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainMenuActivity extends AppCompatActivity {
    private MainMenuController mainMenuController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setMainMenuController(new MainMenuController(this));
    }

    private void attachListeners() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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

    public class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            int viewID = v.getId();

            if (viewID == R.id.main_menu_activity_bluetooth_game_button) {
                getMainMenuController().doSwitchActivity(viewID, BluetoothGameActivity.class);
            } else if (viewID == R.id.main_menu_activity_settings_button) {
                getMainMenuController().doSwitchActivity(viewID, SettingsActivity.class);
            } else if (viewID == R.id.main_menu_activity_game_results_button) {
                getMainMenuController().doSwitchActivity(viewID, GameResultsActivity.class);
            } else if (viewID == R.id.main_menu_activity_exit_button) {
                getMainMenuController().doExit();
            }
        }
    }

    public MainMenuController getMainMenuController() {
        return mainMenuController;
    }

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }
}
