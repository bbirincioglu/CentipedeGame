package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * This class is responsible from displaying a dialog with appropriate message when there is a backend job going on. It is a part of Observer Design Pattern.
 * It is observer because it is a GUI object. When there is a backend job related to database for example, it listens related domain object (an instance of
 * ParseConnection in this case) by implemeting ParseConnectionObserver. When there is a bluetooth backend processing, it listens MessageHandler object
 * which is a subclass of GamePlayActivity by implemeting GamePlayActivity.MessageHandlerObserver etc.
 */
public class BackgroundJobDialog extends Dialog implements SimpleDialog, ParseConnectionObserver, ConnectionThreadObserver, GamePlayActivity.MessageHandlerObserver, WriterObserver {
    private Activity activity;

    public BackgroundJobDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog); // Dialog style
        setActivity((Activity) context); // activity on which dialog is displayed.
        setTitle("BACKGROUND JOB");
        setCancelable(false); // By doing this, we prevent user to close this activity by touching anywhere on the screen. He will not be able to close it.

        /*Next 3 if clauses are there, whenever we create a dialog, we add it into activities' dialog array list so that when someone switch from one activity
        to another, we can close the dialogs first, and close the activity.*/

        if (getActivity() instanceof BluetoothGameActivity) {
            ((BluetoothGameActivity) getActivity()).getDialogs().add(this);
        }

        if (getActivity() instanceof GamePlayActivity) {
            ((GamePlayActivity) getActivity()).getDialogs().add(this);
        }

        if (getActivity() instanceof GameResultsActivity) {
            ((GameResultsActivity) getActivity()).getDialogs().add(this);
        }
    }

    @Override
    public void initialize() {
        setContentView(R.layout.background_job_dialog); //GUI objects contained by this dialog are defined in the xml. In other words layout of this dialogs are
                                                        //defined in the xml files.
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /*
        When there is a change in the parseConnection object, it notifies all the observers by calling the method below. It gives itself as the argument
        to the method so that GUI objects understand in which state parseConnection object is, and update their appearance accordingly.
     */
    @Override
    public void update(ParseConnection parseConnection) {
        int currentState = parseConnection.getCurrentState();

        if (currentState == ParseConnection.STATE_NO_BACKGROUND_JOB) {  //Do nothing as there is no database connection work.
            System.out.println("BACKGROUND JOB DIALOG PARSE CONNECTION UPDATE 1");
        } else if (currentState == ParseConnection.STATE_BACKGROUND_JOB_STARTED) {  //Open this dialog as there is some work started.
            show();
            System.out.println("BACKGROUND JOB DIALOG PARSE CONNECTION UPDATE 2");
        } else if (currentState == ParseConnection.STATE_BACKGROUND_JOB_FINISHED) { //Close this dialog as work has finished.
            cancel();
            System.out.println("BACKGROUND JOB DIALOG PARSE CONNECTION UPDATE 3");
        }
    }

    /*
        The method below observers connectionThread object. When two player tries to build a bluetooth connection, hosted player's phone (ServerConnectionThread)
        constantly listens for the other player to join (ClientConnectionThread). Don't forget that ConnectionThread is actually an interface implemented by
        ServerConnectionThread and ClientConnectionThread.
     */
    @Override
    public void update(ConnectionThread connectionThread) {
        ServerConnectionThread serverConnectionThread;
        ClientConnectionThread clientConnectionThread;
        int currentStatus;
        TextView textView = ((TextView) findViewById(R.id.backgroundJobTextView));

        if (connectionThread instanceof ServerConnectionThread) { //This means it is hosted player.
            serverConnectionThread = (ServerConnectionThread) connectionThread;
            currentStatus = serverConnectionThread.getCurrentStatus();

            if (currentStatus == ConnectionThread.STATUS_WAITING_FOR_SOMEONE_TO_JOIN_GAME) { //This means there is no one joining the game. Thus, show the dialog.
                textView.setText("Waiting For Someone To Join The Game");
                show();
            } else if (currentStatus == ConnectionThread.STATUS_SOMEONE_JOINED_GAME) { // Someone joined the game, change the dialog content, put "Start Game" button.
                textView.setVisibility(View.GONE);
                Button startGameButton = ((Button) findViewById(R.id.startGameButton));
                startGameButton.setVisibility(View.VISIBLE);
                startGameButton.setOnClickListener(new ButtonListener());
            } else if (currentStatus == ConnectionThread.STATUS_CONNECTION_FAILED) { //If connection failed, make dialog cancelable so that user can cancel the dialog and restart all steps.
                setCancelable(true);
                textView.setText("Connection Failed.");
            }
        } else if (connectionThread instanceof ClientConnectionThread) {
            clientConnectionThread = (ClientConnectionThread) connectionThread;
            currentStatus = clientConnectionThread.getCurrentStatus();

            if (currentStatus == ConnectionThread.STATUS_CONNECTING) { //If client tries to connect in the backend, show dialog with suitable message.
                textView.setText("Connecting...");
                show();
            } else if (currentStatus == ConnectionThread.STATUS_CONNECTED) { //If client is connected, put "Start Game" button to initialize the game.
                textView.setVisibility(View.GONE);
                Button startGameButton = ((Button) findViewById(R.id.startGameButton));
                startGameButton.setVisibility(View.VISIBLE);
                startGameButton.setOnClickListener(new ButtonListener());
            } else if (currentStatus == ConnectionThread.STATUS_CONNECTION_FAILED) { // If connection is failed, make dialog cancelable so that user can retry to connect.
                setCancelable(true);
                textView.setText("Connection Failed.");
            }
        }
    }

    //This method is called during the actual game play. We display dialogs with various message to make one player wait, while other is commiting so something
    //or when the game turn is others.
    @Override
    public void update(GamePlayActivity.MessageHandler messageHandler) {
        int currentState = messageHandler.getCurrentState();
        TextView textView = (TextView) findViewById(R.id.backgroundJobTextView);
        ParseConnection pc = ((GamePlayActivity) getActivity()).getParseConnection();
        String commitmentText = "Waiting For Other Player's Commitment...";
        String decisionText = "Waiting For Other Player's Decision...";

        if (currentState == GamePlayActivity.MessageHandler.STATE_OTHER_NOT_COMMITTED) { //If other not commited, you have to wait for other's commitment.
            textView.setText(commitmentText);
            show();
            System.out.println("BACKGROUND JOB DIALOG MESSAGE HANDLER UPDATE 1");
        } else if (currentState == GamePlayActivity.MessageHandler.STATE_OTHER_COMMITTED) { //If other commited, we have to check whether you are hosted player
                                                                                            // or not.
            textView.setText(decisionText);

            if (SocketSingleton.getInstance().isHosted()) { //Check hosted player or not. If hosted, hosted player has the first turn. Thus close the dialog
                                                            // so that  hosted one can play the game.
                hide();
            } else {
                if (!isShowing()) { //If not hosted, display the dialog so that client player waits for hosted player's action.
                    show();
                }
            }
            System.out.println("BACKGROUND JOB DIALOG MESSAGE HANDLER UPDATE 2");
        } else if (currentState == GamePlayActivity.MessageHandler.STATE_WAITING) { //This player doesn't have the turn, so display waiting dialog.
            show();
            System.out.println("BACKGROUND JOB DIALOG MESSAGE HANDLER UPDATE 3");
        } else if (currentState == GamePlayActivity.MessageHandler.STATE_DECIDING) { //This player has the turn, so hide the dialog so that the player can play.
            hide();
            System.out.println("BACKGROUND JOB DIALOG MESSAGE HANDLER UPDATE 4");
        } else if (currentState == GamePlayActivity.MessageHandler.STATE_FINISHING) { //The game has finished, Close the dialog rather than hiding.
            cancel();
            System.out.println("BACKGROUND JOB DIALOG MESSAGE HANDLER UPDATE 5");
        }

        if (!((GamePlayActivity) getActivity()).getParseConnection().isMyCommitmentSaved()) {
            hide();
            System.out.println("BACKGROUND JOB DIALOG MESSAGE HANDLER UPDATE 6");
        }
    }

    /*
        This method below is when we extract all the game results into excel file by using instance of Writer class.
     */
    @Override
    public void update(Writer writer) {
        int currentState = writer.getCurrentState();
        TextView textView = (TextView) findViewById(R.id.backgroundJobTextView);

        System.out.println("CURRENT STATE OF WRITER: " + currentState);

        if (currentState == Writer.STATE_NO_WRITING) {
            if (isShowing()) {
                dismiss();
            }
        } else if (currentState == Writer.STATE_WRITING) {
            textView.setText("Writing Into Secure Digital Card with File Name: \"gameResults.xls\"");
            System.out.println(textView.getText());
            show();
        } else if (currentState == Writer.STATE_WRITING_FAILED) {
            textView.setText("Writing Failed: " + writer.getError());
            show();
        }
    }

    //Basic button listener which is binded to "Start Game" button. When this button is clicked, we switch from the current activity(BluetoothGameActivity) to
    // GamePlayActivity.
    private class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            new ActivitySwitcher().fromPreviousToNext(getActivity(), GamePlayActivity.class, null, true);
        }
    }
}
