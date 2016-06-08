package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Basic informative dialog. This just displays a message for informing players.
 */
public class InformativeDialog extends Dialog implements SimpleDialog {
    private Activity activity;

    public InformativeDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
        setCancelable(false);
        setActivity((Activity) context);
        setTitle("MESSAGE");
    }

    @Override
    public void initialize() {
        setContentView(R.layout.informative_dialog);
        LinearLayout container = (LinearLayout) findViewById(R.id.informativeDialogContainer);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels; //screenWidth
        int height = displayMetrics.heightPixels; //screenHeight
        container.setLayoutParams(new LinearLayout.LayoutParams(width / 2, LinearLayout.LayoutParams.WRAP_CONTENT)); //set width, and height of the dialog according to screen width and height.

        Button button = (Button) findViewById(R.id.informativeDialogButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    public void setText(String text) {
        ((TextView) findViewById(R.id.informativeDialogTextView)).setText(text);
    }

    public String getText() {
        return ((TextView) findViewById(R.id.informativeDialogTextView)).getText().toString();
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
