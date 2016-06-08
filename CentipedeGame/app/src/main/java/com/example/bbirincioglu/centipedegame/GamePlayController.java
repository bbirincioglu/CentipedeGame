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
 * Controller for GamePlayActivity.
 */
public class GamePlayController {
    private ConnectedThread connectedThread; //this is for communicating with other phone using bluetooth socket, and corresponding input-output streams.

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

    /*
        Saves commitment of the player to Parse server.
     */
    public void doSaveCommitment(Context context, ParseConnection parseConnection, String commitment) {
        DialogFactory dialogFactory = DialogFactory.getInstance();
        GameResult gameResult = (GameResult) parseConnection.obtainObject("GameResult", "gameNo", parseConnection.getCurrentGameNo()); //According to game number, get
                                                                                                                                    // game result object which points
                                                                                                                                    //to a specific row in the database.
        GameSettings gameSettings = GameSettings.loadFromPreferences(context);  //obtain game settings from Preferences.

        //Get name and surname of the player for commitment.
        SharedPreferences sp = context.getSharedPreferences(Keys.PLAYER_INFO_PREFERENCES, Context.MODE_PRIVATE);
        String name = sp.getString(Keys.PLAYER_NAME, "DEFAULT");
        String surname = sp.getString(Keys.PLAYER_SURNAME, "DEFAULT");

        if (SocketSingleton.getInstance().isHosted()) {//Check whether it is first player (Hosted) or second player (Client).
            gameResult.setP1Name(name);
            gameResult.setP1Surname(surname);
            gameResult.setP1Commitment(commitment);
        } else {
            gameResult.setP2Name(name);
            gameResult.setP2Surname(surname);
            gameResult.setP2Commitment(commitment);
        }

        //We not only save commitment values, but we also save game results to the server.
        gameResult.setMaximumStepNumber(gameSettings.getMaximumStepNumber());
        gameResult.setRatio(gameSettings.getRatio());
        gameResult.setInitialTotal(gameSettings.getInitialTotal());
        gameResult.setMultiplicator(gameSettings.getMultiplicator());
        gameResult.setCommitmentType(gameSettings.getCommitmentType());
        gameResult.setPunishment(gameSettings.getPunishment());

        //Initializes background-job-dialog while saving is proceeding.
        BackgroundJobDialog backgroundJobDialog = (BackgroundJobDialog) dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
        ((TextView) backgroundJobDialog.findViewById(R.id.backgroundJobTextView)).setText("Saving Commitment...");

        parseConnection.removeAllObservers();
        parseConnection.addObserver(backgroundJobDialog);
        parseConnection.saveGameResultForCommitment(context, gameResult); //In this step, only game settings and commitment values together with name, and
                                                                            // surname of the player are saved.
    }

    //Dialog which prevents you do anything when the turn is not yours, and disappears when the turn is yours.
    public void doPrepareTurnDialog(Context context) {
        GamePlayActivity.MessageHandler messageHandler = getConnectedThread().getMessageHandler();
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(context);
        BackgroundJobDialog dialog = (BackgroundJobDialog) dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
        messageHandler.addObserver(dialog);//Bind messageHandler with dialog. When the turn is yours, message handler will update its state, and dialog will become
        //invisible. When the turn is not yours, message handler will also update its state, and dialog will become visible to prevent you to do something on screen.
    }

    //Handle pass choice.
    public void doPass(Button button) {
        GamePlayActivity.MessageHandler messageHandler = getConnectedThread().getMessageHandler();
        messageHandler.setCurrentState(GamePlayActivity.MessageHandler.STATE_WAITING); //the player's state becomes waiting as he chooses to pass turn to other player.
        getConnectedThread().write(button.getText().toString().toLowerCase()); //Also we have to send this message to other phone.
        doUpdateGameDataGridLayout(button.getContext());    //Also we need to update game data (currentStepNumber increased, payoffs changed, currentTotal increased.)
    }

    //Handle stop choice.
    public void doStop(Button button, ParseConnection parseConnection) {
        GamePlayActivity.MessageHandler messageHandler = getConnectedThread().getMessageHandler();
        messageHandler.setCurrentState(GamePlayActivity.MessageHandler.STATE_FINISHING); //Game is finished.
        doDisplayGameResult1(button.getContext(), parseConnection); //Display game result for player 1.
    }

    //When a player decides to "Pass", View containing game data (currentStepNumber, payoffs, currentTotal) has to be updated accordingly.
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

    //Display game result for player 1. It doesn't only display the game results, but it also sends all the game result information to server.
    private void doDisplayGameResult1(Context context, ParseConnection parseConnection) {
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(context);

        final GameResultDialog gameResultDialog = (GameResultDialog) dialogFactory.create(DialogFactory.DIALOG_GAME_RESULT);
        final GameResult gameResult = (GameResult) parseConnection.obtainObject("GameResult", "gameNo", parseConnection.getCurrentGameNo()); //Obtain related row.
        gameResultDialog.injectContentAndCompleteGameResult(gameResult); //all data in the gameResult object will be "injected" into GUI object for presentation.

        final Dialog dialog = dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
        ((TextView) dialog.findViewById(R.id.backgroundJobTextView)).setText("Sending Results To Server..."); //initializes a background dialog and display it until
                                                                                                            //results are saved on the server side.
        dialog.show();

        gameResult.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    dialog.dismiss(); //close the dialog.
                    gameResultDialog.show(); //display the game result dialog.
                    getConnectedThread().write("stop"); // send other phone that the results are saved in the Parse server so that other phone can call "doDisplayGameResult2()" method.
                } else {
                    ((TextView) dialog.findViewById(R.id.backgroundJobTextView)).setText(e.getLocalizedMessage()); //Error message is displayed if anything goes wrong.
                }
            }
        });
    }

    //Display game result for player 2. Unlike the method above, this method only retreives the game result from server, and displays it accordingly.
    //This method waits for the method above to be completed.
    public void doDisplayGameResult2(Context context, ParseConnection parseConnection) {
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(context);

        GameResultDialog gameResultDialog = (GameResultDialog) dialogFactory.create(DialogFactory.DIALOG_GAME_RESULT);
        GameResult gameResult = (GameResult) parseConnection.obtainObject("GameResult", "gameNo", parseConnection.getCurrentGameNo());
        gameResultDialog.injectContent(gameResult);
        gameResultDialog.show();
    }
}
