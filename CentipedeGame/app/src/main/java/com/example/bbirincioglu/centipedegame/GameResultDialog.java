package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Final dialog displayed to the players when the game is finished. Contains every information such as names, surnames, final total, final step number, payoffs etc.
 */
public class GameResultDialog extends Dialog implements SimpleDialog {
    private Activity activity;

    public GameResultDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
        setCancelable(false);
        setActivity((Activity) context);

        if (getActivity() instanceof BluetoothGameActivity) {
            ((BluetoothGameActivity) getActivity()).getDialogs().add(this);
        }

        if (getActivity() instanceof GamePlayActivity) {
            ((GamePlayActivity) getActivity()).getDialogs().add(this);
        }
    }

    @Override
    public void initialize() {
        setContentView(R.layout.game_result_dialog);
        ButtonListener buttonListener = new ButtonListener();
        findViewById(R.id.restartButton).setOnClickListener(buttonListener);
        findViewById(R.id.exitButton).setOnClickListener(buttonListener);
    }

    //Data stored in the game result object will be inserted into this GUI object's components. Most of the components are of type TextView.
    public void injectContent(GameResult gameResult) {
        Activity activity = getActivity();
        GameSettings gameSettings = GameSettings.loadFromPreferences(activity);
        displayValues(gameSettings);

        String p1Name = gameResult.getP1Name();
        String p1Surname = gameResult.getP1Surname();
        String p1Commitment = gameResult.getP1Commitment();
        String p1Payoff = gameResult.getP1Payoff();

        String p2Name = gameResult.getP2Name();
        String p2Surname = gameResult.getP2Surname();
        String p2Commitment = gameResult.getP2Commitment();
        String p2Payoff = gameResult.getP2Payoff();

        ((TextView) findViewById(R.id.finalStepNumberEditText)).setText("FINAL STEP NUM: " + gameResult.getFinalStepNumber());
        ((TextView) findViewById(R.id.finalTotalEditText)).setText("FINAL TOTAL: " + gameResult.getFinalTotal());

        ((TextView) findViewById(R.id.p1NameEditText)).setText(p1Name);
        ((TextView) findViewById(R.id.p1CommitmentEditText)).setText(p1Commitment);
        ((TextView) findViewById(R.id.p1PayoffEditText)).setText(p1Payoff);

        ((TextView) findViewById(R.id.p2NameEditText)).setText(p2Name);
        ((TextView) findViewById(R.id.p2CommitmentEditText)).setText(p2Commitment);
        ((TextView) findViewById(R.id.p2PayoffEditText)).setText(p2Payoff);

        ((SeekBar) findViewById(R.id.maximumStepNumberSeekBar)).setVisibility(View.GONE);
        fixParams((TableLayout) findViewById(R.id.settingsTableLayout)); //Fix alignments of components.
    }

    //Not only inserts data stored in the game result object, but it also inserts some final values to gameResult object such as finalStepNumber, finalTotal, p1Payoff etc.
    //Other than that, it is the same as injectContent();
    public void injectContentAndCompleteGameResult(GameResult gameResult) {
        Activity activity = getActivity();
        GameSettings gameSettings = GameSettings.loadFromPreferences(activity);
        displayValues(gameSettings);

        String separator = ": ";
        String finalStepNumber = ((TextView) activity.findViewById(R.id.currentStepNumberTextView)).getText().toString().split(separator)[1];
        String finalTotal = ((TextView) activity.findViewById(R.id.currentTotalTextView)).getText().toString().split(separator)[1];
        String p1PayoffWithoutPunishment = ((TextView) activity.findViewById(R.id.currentP1PayoffTextView)).getText().toString().split(separator)[1];
        String p2PayoffWithoutPunishment = ((TextView) activity.findViewById(R.id.currentP2PayoffTextView)).getText().toString().split(separator)[1];

        gameResult.setFinalStepNumber(finalStepNumber);
        gameResult.setFinalTotal(finalTotal);
        gameResult.setP1Payoff(p1PayoffWithoutPunishment);
        gameResult.setP2Payoff(p2PayoffWithoutPunishment);

        if (SocketSingleton.getInstance().isHosted()) {
            gameResult.setPlayerFinished("P1");
        } else {
            gameResult.setPlayerFinished("P2");
        }

        double[] result = new GameResultEvaluator().evaluate(gameResult);
        gameResult.setP1Payoff(String.valueOf(result[0]));
        gameResult.setP2Payoff(String.valueOf(result[1]));

        String p1Name = gameResult.getP1Name();
        String p1Surname = gameResult.getP1Surname();
        String p1Commitment = gameResult.getP1Commitment();
        String p1Payoff = gameResult.getP1Payoff();

        String p2Name = gameResult.getP2Name();
        String p2Surname = gameResult.getP2Surname();
        String p2Commitment = gameResult.getP2Commitment();
        String p2Payoff = gameResult.getP2Payoff();

        ((TextView) findViewById(R.id.finalStepNumberEditText)).setText("FINAL STEP NUM: " + gameResult.getFinalStepNumber());
        ((TextView) findViewById(R.id.finalTotalEditText)).setText("FINAL TOTAL: " + gameResult.getFinalTotal());

        ((TextView) findViewById(R.id.p1NameEditText)).setText(p1Name);
        ((TextView) findViewById(R.id.p1CommitmentEditText)).setText(p1Commitment);
        ((TextView) findViewById(R.id.p1PayoffEditText)).setText(p1Payoff);

        ((TextView) findViewById(R.id.p2NameEditText)).setText(p2Name);
        ((TextView) findViewById(R.id.p2CommitmentEditText)).setText(p2Commitment);
        ((TextView) findViewById(R.id.p2PayoffEditText)).setText(p2Payoff);

        ((SeekBar) findViewById(R.id.maximumStepNumberSeekBar)).setVisibility(View.GONE);
        fixParams((TableLayout) findViewById(R.id.settingsTableLayout));
    }

    private void fixParams(ViewGroup container) {
        int childCount = container.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View childAtI = container.getChildAt(i);

            if (childAtI instanceof ViewGroup) {
                fixParams((ViewGroup) childAtI);
            } else {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) childAtI.getLayoutParams();

                if (params != null) {
                    params.rightMargin = 0;

                    if (childAtI instanceof EditText) {
                        params.width = 100;
                    }
                }
            }
        }
    }

    public class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            int buttonID = v.getId();

            if (buttonID == R.id.restartButton) {
                new ActivitySwitcher().fromPreviousToNext(getActivity(), BluetoothGameActivity.class, null, true);
            } else if (buttonID == R.id.exitButton) {
                try {
                    SocketSingleton.getInstance().getSocket().close();
                    getActivity().finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private void disableOrEnableContainerAndChildren(ViewGroup container, boolean enabled) {
        container.setEnabled(enabled);
        int childCount = container.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = container.getChildAt(i);
            child.setEnabled(enabled);

            if (child instanceof ViewGroup) {
                disableOrEnableContainerAndChildren((ViewGroup) child, enabled);
            }
        }
    }

    //Display game settings on the screen.
    private void displayValues(GameSettings gameSettings) {
        String maximumStepNumber = gameSettings.getMaximumStepNumber();
        String ratio = gameSettings.getRatio();
        String initialTotal = gameSettings.getInitialTotal();
        String multiplicator = gameSettings.getMultiplicator();
        String commitmentType = gameSettings.getCommitmentType();
        String punishment = gameSettings.getPunishment();

        TextView textView;
        EditText editText;
        SeekBar seekBar;
        RadioButton radioButton1;
        RadioButton radioButton2;

        textView = ((TextView) findViewById(R.id.maximumStepNumberTextView));
        textView.setText(maximumStepNumber);

        seekBar = ((SeekBar) findViewById(R.id.maximumStepNumberSeekBar));
        seekBar.setMax(100);
        seekBar.setProgress(Integer.valueOf(maximumStepNumber));

        editText = ((EditText) findViewById(R.id.initialTotalEditText));
        editText.setText(initialTotal);

        editText = ((EditText) findViewById(R.id.multiplicatorEditText));
        editText.setText(multiplicator);

        editText = ((EditText) findViewById(R.id.ratioEditText));
        editText.setText(ratio);

        editText = (EditText) findViewById(R.id.punishmentEditText);
        editText.setText(punishment);

        radioButton1 = ((RadioButton) findViewById(R.id.openCommitmentRadioButton));
        radioButton2 = ((RadioButton) findViewById(R.id.closedCommitmentRadioButton));

        if (commitmentType.equals("Closed")) {
            radioButton1.setChecked(false);
            radioButton2.setChecked(true);
        } else if (commitmentType.equals("Open")) {
            radioButton1.setChecked(true);
            radioButton2.setChecked(false);
        }

        disableOrEnableContainerAndChildren((TableLayout) findViewById(R.id.settingsTableLayout), false);
        findViewById(R.id.settingsSaveButton).setVisibility(View.GONE);
    }
}
