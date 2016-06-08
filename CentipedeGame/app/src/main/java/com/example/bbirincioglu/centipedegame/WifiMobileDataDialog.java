package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

/**
 * The class whose instance is a GUI object displayed when neither Wifi nor MobileData is enabled. User is forced to enable one of the internet connection.
 */
public class WifiMobileDataDialog extends Dialog implements SimpleDialog {
    private Activity activity;

    public WifiMobileDataDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
        setTitle("CONNECTION PROBLEM");
        setActivity((Activity) context);
        setCancelable(false);
    }

    @Override
    public void initialize() {
        ButtonListener buttonListener = new ButtonListener();
        setContentView(R.layout.wifi_mobile_data_dialog); //insert layout from xml.

        Button wifiButton = (Button) findViewById(R.id.wifiButton);
        wifiButton.setTag(wifiButton.getId(), this); //give tags to buttons.
        Button mobileDataButton = (Button) findViewById(R.id.mobileDataButton);
        mobileDataButton.setTag(mobileDataButton.getId(), this); //give tags to buttons.

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels; //screenWidth
        int height = displayMetrics.heightPixels; //screenHeight

        //arrange buttons' width according to screenWidth
        wifiButton.getLayoutParams().width = (int) (width * 0.35);
        mobileDataButton.getLayoutParams().width = (int) (width * 0.35);

        //bind listeners
        wifiButton.setOnClickListener(buttonListener);
        mobileDataButton.setOnClickListener(buttonListener);
    }

    private class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            Button button = (Button) v;
            int buttonID = button.getId();
            MainMenuController controller = MainMenuController.getInstance();

            //Enable wifi or mobile data according to which one is clicked.
            if (buttonID == R.id.wifiButton) {
                controller.doEnableWifi(getActivity());
                ((Dialog) button.getTag(buttonID)).cancel();
            } else if (buttonID == R.id.mobileDataButton) {
                controller.doEnableMobileData(getActivity());
                ((Dialog) button.getTag(buttonID)).cancel();
            }

            //Create and display player info dialog to obtain player name, and surname.
            DialogFactory dialogFactory = DialogFactory.getInstance();
            dialogFactory.setContext(getActivity());
            Dialog dialog = dialogFactory.create(DialogFactory.DIALOG_PLAYER_INFO);
            dialog.show();
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
