package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by bbirincioglu on 3/6/2016.
 */
public class SettingsController implements TextWatcher, View.OnClickListener, SeekBar.OnSeekBarChangeListener, RadioButton.OnCheckedChangeListener {
    private Activity activity;
    private ArrayList<EditText> editTexts;

    public SettingsController(Context context) {
        this.editTexts = new ArrayList<EditText>();
        setActivity((Activity) context);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        Activity activity = getActivity();
        RadioButton closedRadioButton = ((RadioButton) activity.findViewById(R.id.closedCommitmentRadioButton));
        RadioButton openRadioButton = ((RadioButton) activity.findViewById(R.id.openCommitmentRadioButton));

        closedRadioButton.setOnCheckedChangeListener(null);
        openRadioButton.setOnCheckedChangeListener(null);

        if (isChecked) {
            closedRadioButton.setChecked(false);
            openRadioButton.setChecked(false);
        }

        buttonView.setChecked(true);
        closedRadioButton.setOnCheckedChangeListener(this);
        openRadioButton.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Activity activity = getActivity();

        if (id == R.id.settingsSaveButton) {
            System.out.println("IN THE SAVE BUTTON");
            String maximumStepNum = ((TextView) activity.findViewById(R.id.maximumStepNumberTextView)).getText().toString();
            String ratio = ((EditText) activity.findViewById(R.id.ratioEditText)).getText().toString();
            String initialTotal = ((EditText) activity.findViewById(R.id.initialTotalEditText)).getText().toString();
            String multiplicator = ((EditText) activity.findViewById(R.id.multiplicatorEditText)).getText().toString();
            String punishment = ((EditText) activity.findViewById(R.id.punishmentEditText)).getText().toString();
            String commitmentType = "Closed";
            boolean isChecked = ((RadioButton) activity.findViewById(R.id.openCommitmentRadioButton)).isChecked();

            if (isChecked) {
                commitmentType = "Open";
            }

            GameSettings gameSettings = new GameSettings(activity, maximumStepNum, ratio, initialTotal, multiplicator, commitmentType, punishment);
            gameSettings.saveIntoPreferences();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBar.setOnSeekBarChangeListener(null);
        int incrementValue = 5;
        int quotient = progress / incrementValue;

        if (quotient * incrementValue != progress) {
            quotient += 1;
        }

        seekBar.setProgress(incrementValue * quotient);
        ((TextView) getActivity().findViewById(R.id.maximumStepNumberTextView)).setText(String.valueOf(seekBar.getProgress()));
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        addOrRemoveListenersFromEditTexts("remove");
        EditText editTextFocused = null;
        ArrayList<EditText> editTexts = getEditTexts();
        int size = editTexts.size();

        for (int i = 0; i < size; i++) {
            EditText editText = editTexts.get(i);

            if (editText.hasFocus()) {
                editTextFocused = editTexts.get(i);
                break;
            }
        }

        if (editTextFocused != null) {
            String text = s.toString();
            int id = editTextFocused.getId();

            if (id == R.id.ratioEditText) {
                String validatedRatio = validateRatio(text);
                s.clear();
                s.append(validatedRatio);
            } else if (id == R.id.initialTotalEditText){
                String validatedInitialTotal = validateInitialTotal(text);
                s.clear();
                s.append(validatedInitialTotal);
            } else if (id == R.id.multiplicatorEditText) {

            } else if (id == R.id.punishmentEditText) {
                String validatedPunishment = validatePunishment(text);
                s.clear();
                s.append(validatedPunishment);
            }
        } else {
            System.out.println("THERE IS DEFINITELY A PROBLEM. DON'T UNDERESTIMATE ME!!!. FIND ME.");
        }

        addOrRemoveListenersFromEditTexts("add");
    }

    /*private String fixOtherRatio(String otherRatio) {
        String fixedOtherRatio = "";
        int length = otherRatio.length();

        if (length > 10) {
            char character = otherRatio.charAt(7);

            if (character == '9') {
                fixedOtherRatio = otherRatio.substring(0, 1);
                int index = -2;

                for (int i = 1; i < length; i++) {
                    char charAtI = otherRatio.charAt(i);
                    index++;

                    if (charAtI == '9') {
                        fixedOtherRatio += charAtI;
                        break;
                    } else {
                        fixedOtherRatio += charAtI;
                    }
                }

                String fixer = "0.";

                for (int i = 0; i < index; i++) {
                    fixer += '0';
                }

                fixer += '1';
                fixedOtherRatio = String.valueOf(Double.valueOf(fixedOtherRatio) + Double.valueOf(fixer));
            } else if (character == '0') {
                fixedOtherRatio = otherRatio.substring(0, 1);

                for (int i = 1; i < length; i++) {
                    char charAtI = otherRatio.charAt(i);

                    if (charAtI == '0') {
                        break;
                    } else {
                        fixedOtherRatio += charAtI;
                    }
                }
            }
        } else {
            fixedOtherRatio = otherRatio;
        }

        return fixedOtherRatio;
    }*/

    private String validateRatio(String ratio) {
        String validatedRatio = "0.";
        String control = "0123456789";
        int length = ratio.length();

        for (int i = 2; i < length; i++) {
            char charAtI = ratio.charAt(i);

            if (control.contains(charAtI + "")) {
                validatedRatio += charAtI;
            }
        }

        return validatedRatio;
    }

    private String validateInitialTotal(String initialTotal) {
        String validatedInitialTotal = "";
        String control = "0123456789";
        int length = initialTotal.length();

        for (int i = 0; i < length; i++) {
            char charAtI = initialTotal.charAt(i);

            if (i == 0) {
                if (control.substring(1, control.length()).contains(charAtI + "")) {
                    validatedInitialTotal += charAtI;
                }
            } else {
                if (control.contains(charAtI + "")) {
                    validatedInitialTotal += charAtI;
                }
            }
        }

        return validatedInitialTotal;
    }

    public String validatePunishment(String punishment) {
        String validatedPunishment = "-";
        String control = "0123456789";
        int length = punishment.length();

        for (int i = 1; i < length; i++) {
            char charAtI = punishment.charAt(i);

            if (i == 1) {
                if (charAtI != '0') {
                    validatedPunishment += charAtI;
                }
            } else {
                if (control.contains(charAtI + "")) {
                    validatedPunishment += charAtI;
                }
            }
        }

        return validatedPunishment;
    }

    private ArrayList<EditText> getEditTexts() {
        return editTexts;
    }

    private void setEditTexts(ArrayList<EditText> editTexts) {
        this.editTexts = editTexts;
    }

    public void addEditText(EditText editText) {
        if (!getEditTexts().contains(editText)) {
            getEditTexts().add(editText);
        }
    }

    public void removeEditText(EditText editText) {
        getEditTexts().remove(editText);
    }

    private EditText findEditText(int id) {
        EditText wanted = null;
        ArrayList<EditText> editTexts = getEditTexts();

        for (EditText editText : editTexts) {
            if (editText.getId() == id) {
                wanted = editText;
                break;
            }
        }

        return wanted;
    }

    private void addOrRemoveListenersFromEditTexts(String addOrRemove) {
        ArrayList<EditText> editTexts = getEditTexts();

        for (EditText editText : editTexts) {
            if (addOrRemove.equalsIgnoreCase("add")) {
                editText.addTextChangedListener(this);
            } else if (addOrRemove.equalsIgnoreCase("remove")) {
                editText.removeTextChangedListener(this);
            }
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
