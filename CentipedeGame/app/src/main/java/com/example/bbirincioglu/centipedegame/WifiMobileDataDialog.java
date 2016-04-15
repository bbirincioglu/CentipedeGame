package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

/**
 * Created by bbirincioglu on 3/6/2016.
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
        setContentView(R.layout.wifi_mobile_data_dialog);

        Button wifiButton = (Button) findViewById(R.id.wifiButton);
        wifiButton.setTag(wifiButton.getId(), this);
        Button mobileDataButton = (Button) findViewById(R.id.mobileDataButton);
        mobileDataButton.setTag(mobileDataButton.getId(), this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        wifiButton.getLayoutParams().width = (int) (width * 0.35);
        mobileDataButton.getLayoutParams().width = (int) (width * 0.35);

        wifiButton.setOnClickListener(buttonListener);
        mobileDataButton.setOnClickListener(buttonListener);
    }

    private class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            Button button = (Button) v;
            int buttonID = button.getId();
            MainMenuController controller = MainMenuController.getInstance();

            if (buttonID == R.id.wifiButton) {
                controller.doEnableWifi(getActivity());
                ((Dialog) button.getTag(buttonID)).cancel();
            } else if (buttonID == R.id.mobileDataButton) {
                controller.doEnableMobileData(getActivity());
                ((Dialog) button.getTag(buttonID)).cancel();
            }

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
