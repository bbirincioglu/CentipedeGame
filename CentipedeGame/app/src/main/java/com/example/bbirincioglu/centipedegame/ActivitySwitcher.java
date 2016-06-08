package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * This class is responsible from switching between activities such as GameResultActivity, MainMenuActivity, BluetoothGameActivity etc.
 */
public class ActivitySwitcher {
    public ActivitySwitcher() {

    }

    /*
    * This method takes 4 arguments: instance of previous activity class, Class object of next activity, any extra data that must be
    * transferred from previous activity to next activity, and whether we want previous activity to be killed manually or not.
    * */
    public void fromPreviousToNext(Activity previous, Class next, Bundle data, boolean isKillPrevious) {
        ArrayList<Dialog> dialogs = null;

        /* 3 Activity mentioned below in the if clauses have some dialogs appearing on the screen. In order to prevent screen leak problems
            such we first close all the dialogs related to these activites then we kill them.
         */
        if (previous instanceof BluetoothGameActivity) {
            dialogs = ((BluetoothGameActivity) previous).getDialogs();
        } else if (previous instanceof GamePlayActivity) {
            dialogs = ((GamePlayActivity) previous).getDialogs();
        } else if (previous instanceof GameResultsActivity) {
            dialogs = ((GameResultsActivity) previous).getDialogs();
        }

        //This is where we kill the dialogs.
        if (dialogs != null) {
            for (Dialog dialog : dialogs) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }

        Intent intent = new Intent(previous, next);

        if (data != null) {
            intent.putExtras(data); //Put any extra data if any.
        }

        if (isKillPrevious) {
            previous.finish(); //Kill previous if needed.
        }

        previous.startActivity(intent); // start next activity using intent object.
    }
}
