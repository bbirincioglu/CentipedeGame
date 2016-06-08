package com.example.bbirincioglu.centipedegame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
/*
    The class for displaying, changing and storing Game Settings.
 */
public class SettingsActivity extends AppCompatActivity {
    private SettingsController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setController(new SettingsController(this));
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(this);
        dialogFactory.create(DialogFactory.DIALOG_PASSWORD).show(); //This section of the application requires password, thus we display password dialog.
        initialize();
    }

    private void initialize() {
        SettingsController controller = getController();
        GameSettings settings = GameSettings.loadFromPreferences(this); //Receive GameSettings object from Preferences, and insert values into GUI objects such as
                                                                        //TextViews, EditTexts, RadioButtons etc...
        String maximumStepNumber = settings.getMaximumStepNumber();
        String ratio = settings.getRatio();
        String initialTotal = settings.getInitialTotal();
        String multiplicator = settings.getMultiplicator();
        String commitmentType = settings.getCommitmentType();
        String punishment = settings.getPunishment();

        TextView textView;
        EditText editText;
        SeekBar seekBar;
        RadioButton radioButton1;
        RadioButton radioButton2;

        textView = ((TextView) findViewById(R.id.maximumStepNumberTextView));
        textView.setText(maximumStepNumber);

        seekBar = ((SeekBar) findViewById(R.id.maximumStepNumberSeekBar));
        seekBar.setOnSeekBarChangeListener(controller);
        seekBar.setMax(100);
        seekBar.setProgress(Integer.valueOf(maximumStepNumber));

        editText = ((EditText) findViewById(R.id.ratioEditText));
        editText.setText(ratio);
        editText.addTextChangedListener(controller);
        controller.addEditText(editText);

        editText = ((EditText) findViewById(R.id.initialTotalEditText));
        editText.setText(initialTotal);
        editText.addTextChangedListener(controller);
        controller.addEditText(editText);

        editText = ((EditText) findViewById(R.id.multiplicatorEditText));
        editText.setText(multiplicator);
        editText.setOnClickListener(controller);
        controller.addEditText(editText);

        radioButton1 = ((RadioButton) findViewById(R.id.openCommitmentRadioButton));
        radioButton2 = ((RadioButton) findViewById(R.id.closedCommitmentRadioButton));

        if (commitmentType.equals("Closed")) {
            radioButton1.setChecked(false);
            radioButton2.setChecked(true);
        } else if (commitmentType.equals("Open")) {
            radioButton1.setChecked(true);
            radioButton2.setChecked(false);
        }

        radioButton1.setOnCheckedChangeListener(controller);
        radioButton2.setOnCheckedChangeListener(controller);

        editText = (EditText) findViewById(R.id.punishmentEditText);
        editText.setText(punishment);
        editText.addTextChangedListener(controller);
        controller.addEditText(editText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    //Returns to main activity when the back button is pressed.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Keys.RETURN_FROM_ACTIVITY, true);
        new ActivitySwitcher().fromPreviousToNext(this, MainMenuActivity.class, bundle, true);
    }

    public SettingsController getController() {
        return controller;
    }

    public void setController(SettingsController controller) {
        this.controller = controller;
    }

    public void onClick(View v) {
        getController().onClick(v);
    }
}
