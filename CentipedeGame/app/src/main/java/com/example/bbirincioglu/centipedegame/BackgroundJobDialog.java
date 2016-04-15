package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by bbirincioglu on 3/6/2016.
 */
public class BackgroundJobDialog extends Dialog implements SimpleDialog, ParseConnectionObserver, ConnectionThreadObserver, GamePlayActivity.MessageHandlerObserver, WriterObserver {
    private Activity activity;

    public BackgroundJobDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
        setActivity((Activity) context);
        setTitle("BACKGROUND JOB");
        setCancelable(false);

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
        setContentView(R.layout.background_job_dialog);
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void update(ParseConnection parseConnection) {
        int currentState = parseConnection.getCurrentState();

        if (currentState == ParseConnection.STATE_NO_BACKGROUND_JOB) {
            System.out.println("BACKGROUND JOB DIALOG PARSE CONNECTION UPDATE 1");
        } else if (currentState == ParseConnection.STATE_BACKGROUND_JOB_STARTED) {
            show();
            System.out.println("BACKGROUND JOB DIALOG PARSE CONNECTION UPDATE 2");
        } else if (currentState == ParseConnection.STATE_BACKGROUND_JOB_FINISHED) {
            cancel();
            System.out.println("BACKGROUND JOB DIALOG PARSE CONNECTION UPDATE 3");
        }
    }

    @Override
    public void update(ConnectionThread connectionThread) {
        ServerConnectionThread serverConnectionThread;
        ClientConnectionThread clientConnectionThread;
        int currentStatus;
        TextView textView = ((TextView) findViewById(R.id.backgroundJobTextView));

        if (connectionThread instanceof ServerConnectionThread) {
            serverConnectionThread = (ServerConnectionThread) connectionThread;
            currentStatus = serverConnectionThread.getCurrentStatus();

            if (currentStatus == ConnectionThread.STATUS_WAITING_FOR_SOMEONE_TO_JOIN_GAME) {
                textView.setText("Waiting For Someone To Join The Game");
                show();
            } else if (currentStatus == ConnectionThread.STATUS_SOMEONE_JOINED_GAME) {
                textView.setVisibility(View.GONE);
                Button startGameButton = ((Button) findViewById(R.id.startGameButton));
                startGameButton.setVisibility(View.VISIBLE);
                startGameButton.setOnClickListener(new ButtonListener());
            } else if (currentStatus == ConnectionThread.STATUS_CONNECTION_FAILED) {
                setCancelable(true);
                textView.setText("Connection Failed.");
            }
        } else if (connectionThread instanceof ClientConnectionThread) {
            clientConnectionThread = (ClientConnectionThread) connectionThread;
            currentStatus = clientConnectionThread.getCurrentStatus();

            if (currentStatus == ConnectionThread.STATUS_CONNECTING) {
                textView.setText("Connecting...");
                show();
            } else if (currentStatus == ConnectionThread.STATUS_CONNECTED) {
                textView.setVisibility(View.GONE);
                Button startGameButton = ((Button) findViewById(R.id.startGameButton));
                startGameButton.setVisibility(View.VISIBLE);
                startGameButton.setOnClickListener(new ButtonListener());
            } else if (currentStatus == ConnectionThread.STATUS_CONNECTION_FAILED) {
                setCancelable(true);
                textView.setText("Connection Failed.");
            }
        }
    }

    @Override
    public void update(GamePlayActivity.MessageHandler messageHandler) {
        int currentState = messageHandler.getCurrentState();
        TextView textView = (TextView) findViewById(R.id.backgroundJobTextView);
        ParseConnection pc = ((GamePlayActivity) getActivity()).getParseConnection();
        String commitmentText = "Waiting For Other Player's Commitment...";
        String decisionText = "Waiting For Other Player's Decision...";

        if (currentState == GamePlayActivity.MessageHandler.STATE_OTHER_NOT_COMMITTED) {
            textView.setText(commitmentText);
            show();
            System.out.println("BACKGROUND JOB DIALOG MESSAGE HANDLER UPDATE 1");
        } else if (currentState == GamePlayActivity.MessageHandler.STATE_OTHER_COMMITTED) {
            textView.setText(decisionText);

            if (SocketSingleton.getInstance().isHosted()) {
                hide();
            } else {
                if (!isShowing()) {
                    show();
                }
            }
            System.out.println("BACKGROUND JOB DIALOG MESSAGE HANDLER UPDATE 2");
        } else if (currentState == GamePlayActivity.MessageHandler.STATE_WAITING) {
            show();
            System.out.println("BACKGROUND JOB DIALOG MESSAGE HANDLER UPDATE 3");
        } else if (currentState == GamePlayActivity.MessageHandler.STATE_DECIDING) {
            hide();
            System.out.println("BACKGROUND JOB DIALOG MESSAGE HANDLER UPDATE 4");
        } else if (currentState == GamePlayActivity.MessageHandler.STATE_FINISHING) {
            cancel();
            System.out.println("BACKGROUND JOB DIALOG MESSAGE HANDLER UPDATE 5");
        }

        if (!((GamePlayActivity) getActivity()).getParseConnection().isMyCommitmentSaved()) {
            hide();
            System.out.println("BACKGROUND JOB DIALOG MESSAGE HANDLER UPDATE 6");
        }
    }

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

    private class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            new ActivitySwitcher().fromPreviousToNext(getActivity(), GamePlayActivity.class, null, true);
        }
    }
}
