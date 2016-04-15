package com.example.bbirincioglu.centipedegame;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * Created by bbirincioglu on 3/14/2016.
 */
public class GamePlayController {
    private ConnectedThread connectedThread;

    public GamePlayController() {

    }

    public ConnectedThread getConnectedThread() {
        return connectedThread;
    }

    public void setConnectedThread(ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }

    public void doWrite(Object message) {
        getConnectedThread().write(message);
    }

    public void doSaveCommitment(Context context, ParseConnection parseConnection, String commitment) {
        DialogFactory dialogFactory = DialogFactory.getInstance();
        GameResult gameResult = (GameResult) parseConnection.obtainObject("GameResult", "gameNo", parseConnection.getCurrentGameNo());
        GameSettings gameSettings = GameSettings.loadFromPreferences(context);

        SharedPreferences sp = context.getSharedPreferences(Keys.PLAYER_INFO_PREFERENCES, Context.MODE_PRIVATE);
        String name = sp.getString(Keys.PLAYER_NAME, "DEFAULT");
        String surname = sp.getString(Keys.PLAYER_SURNAME, "DEFAULT");

        if (SocketSingleton.getInstance().isHosted()) {
            gameResult.setP1Name(name);
            gameResult.setP1Surname(surname);
            gameResult.setP1Commitment(commitment);
        } else {
            gameResult.setP2Name(name);
            gameResult.setP2Surname(surname);
            gameResult.setP2Commitment(commitment);
        }

        gameResult.setMaximumStepNumber(gameSettings.getMaximumStepNumber());
        gameResult.setRatio(gameSettings.getRatio());
        gameResult.setInitialTotal(gameSettings.getInitialTotal());
        gameResult.setMultiplicator(gameSettings.getMultiplicator());
        gameResult.setCommitmentType(gameSettings.getCommitmentType());
        gameResult.setPunishment(gameSettings.getPunishment());

        BackgroundJobDialog backgroundJobDialog = (BackgroundJobDialog) dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
        ((TextView) backgroundJobDialog.findViewById(R.id.backgroundJobTextView)).setText("Saving Commitment...");

        parseConnection.removeAllObservers();
        parseConnection.addObserver(backgroundJobDialog);
        parseConnection.saveGameResultForCommitment(context, gameResult);
    }

    public void doPrepareTurnDialog(Context context) {
        GamePlayActivity.MessageHandler messageHandler = getConnectedThread().getMessageHandler();
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(context);
        BackgroundJobDialog dialog = (BackgroundJobDialog) dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
        messageHandler.addObserver(dialog);
    }

    public void doPass(Button button) {
        GamePlayActivity.MessageHandler messageHandler = getConnectedThread().getMessageHandler();
        messageHandler.setCurrentState(GamePlayActivity.MessageHandler.STATE_WAITING);
        getConnectedThread().write(button.getText().toString().toLowerCase());
        doUpdateGameDataGridLayout(button.getContext());
    }

    public void doStop(Button button, ParseConnection parseConnection) {
        GamePlayActivity.MessageHandler messageHandler = getConnectedThread().getMessageHandler();
        messageHandler.setCurrentState(GamePlayActivity.MessageHandler.STATE_FINISHING);
        doDisplayGameResult1(button.getContext(), parseConnection);
    }

    public void doUpdateGameDataGridLayout(Context context) {
        final int precision = 3;
        GamePlayActivity activity = (GamePlayActivity) context;
        TextView t1 = (TextView) activity.findViewById(R.id.currentStepNumberTextView);
        TextView t2 = (TextView) activity.findViewById(R.id.currentTotalTextView);
        TextView t3 = (TextView) activity.findViewById(R.id.currentP1PayoffTextView);
        TextView t4 = (TextView) activity.findViewById(R.id.currentP2PayoffTextView);
        final String separator = ": ";

        int currentStepNumber = Integer.valueOf(t1.getText().toString().split(separator)[1]);
        double currentTotal = Double.valueOf(t2.getText().toString().split(separator)[1]);
        double multiplicator = Double.valueOf(((EditText) activity.findViewById(R.id.multiplicatorEditText)).getText().toString());
        double ratio = Double.valueOf(((EditText) activity.findViewById(R.id.ratioEditText)).getText().toString());
        double updatedP1Payoff;
        double updatedP2Payoff;

        currentStepNumber = currentStepNumber + 1;
        currentTotal = currentTotal * multiplicator;

        if (SocketSingleton.getInstance().isHosted()) {
            updatedP1Payoff = currentTotal * ratio;
            updatedP2Payoff = currentTotal - updatedP1Payoff;
        } else {
            updatedP2Payoff = currentTotal * ratio;
            updatedP1Payoff = currentTotal - updatedP2Payoff;
        }

        final String stringCSN = "Current Step Number: ";
        final String stringCT = "Current Total: ";
        final String stringCP1P = "Current P1 Payoff: ";
        final String stringCP2P = "Current P2 Payoff: ";
        t1.setText(stringCSN + currentStepNumber);
        t2.setText(stringCT + currentTotal);
        t3.setText(stringCP1P + activity.shrinkDoubleAsStrings("" + updatedP1Payoff, precision));
        t4.setText(stringCP2P + activity.shrinkDoubleAsStrings("" + updatedP2Payoff, precision));
    }

    private void doDisplayGameResult1(Context context, ParseConnection parseConnection) {
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(context);

        final GameResultDialog gameResultDialog = (GameResultDialog) dialogFactory.create(DialogFactory.DIALOG_GAME_RESULT);
        final GameResult gameResult = (GameResult) parseConnection.obtainObject("GameResult", "gameNo", parseConnection.getCurrentGameNo());
        gameResultDialog.injectContentAndCompleteGameResult(gameResult);

        final Dialog dialog = dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
        ((TextView) dialog.findViewById(R.id.backgroundJobTextView)).setText("Sending Results To Server...");
        dialog.show();

        gameResult.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    dialog.dismiss();
                    gameResultDialog.show();
                    getConnectedThread().write("stop");
                } else {
                    ((TextView) dialog.findViewById(R.id.backgroundJobTextView)).setText(e.getLocalizedMessage());
                }
            }
        });
    }

    public void doDisplayGameResult2(Context context, ParseConnection parseConnection) {
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(context);

        GameResultDialog gameResultDialog = (GameResultDialog) dialogFactory.create(DialogFactory.DIALOG_GAME_RESULT);
        GameResult gameResult = (GameResult) parseConnection.obtainObject("GameResult", "gameNo", parseConnection.getCurrentGameNo());
        gameResultDialog.injectContent(gameResult);
        gameResultDialog.show();
    }
}
