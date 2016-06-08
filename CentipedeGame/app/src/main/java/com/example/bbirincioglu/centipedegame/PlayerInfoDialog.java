package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * This class is used to get player name and surname with one space in between. If there is no one space between name, and surname entered we display proper error
 * message.
 */
public class PlayerInfoDialog extends Dialog implements SimpleDialog {
    private Activity activity;
    private PlayerInfoDialog reference;

    public PlayerInfoDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
        setTitle("PLAYER INFORMATION");
        setActivity((Activity) context);
        setCancelable(false);
        reference = this;
    }

    //Load dialog layout from xml, make dialog width half of the screen, and bind listeners to buttons.
    public void initialize() {
        Activity activity = getActivity();
        setContentView(R.layout.player_info_dialog);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        LinearLayout container = (LinearLayout) findViewById(R.id.playerInfoDialogContainer);
        container.getLayoutParams().width = screenWidth / 2;

        Button button = (Button) findViewById(R.id.continueButton);
        button.setOnClickListener(new ButtonListener());
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String nameSurname = ((EditText) findViewById(R.id.nameSurnameEditText)).getText().toString();
            MainMenuController.getInstance().doCheckPlayerInfo(reference, nameSurname); //check name, surname.
        }
    }
}
