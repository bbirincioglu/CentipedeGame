package com.example.bbirincioglu.centipedegame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GamePlayActivity extends AppCompatActivity {
    private MessageHandler messageHandler;
    private ParseConnection parseConnection;
    private GamePlayController gamePlayController;
    private ArrayList<Dialog> dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        setDialogs(new ArrayList<Dialog>());

        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(this);

        setMessageHandler(new MessageHandler(this));
        SocketSingleton socketSingleton = SocketSingleton.getInstance();

        ParseObject.registerSubclass(GameResult.class);
        setParseConnection(ParseConnection.getNewInstance());

        setGamePlayController(new GamePlayController());
        GamePlayController gamePlayController = getGamePlayController();
        gamePlayController.setConnectedThread(new ConnectedThread(this, socketSingleton.getSocket(), getMessageHandler()));
        gamePlayController.getConnectedThread().start();

        getParseConnection().setGamePlayController(gamePlayController);

        BackgroundJobDialog connectServerDialog = (BackgroundJobDialog) dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
        connectServerDialog.setContentView(composeInformativeContentView("Waiting For Server And Opponent Response..."));
        getParseConnection().addObserver(connectServerDialog);

        if (socketSingleton.isHosted()) {
            GameSettings gameSettings = GameSettings.loadFromPreferences(this);
            gamePlayController.doWrite(gameSettings.toHashMap());
            displayValues(gameSettings);
            getParseConnection().createEmptyGameResult();
        } else {
            getParseConnection().setCurrentState(ParseConnection.STATE_BACKGROUND_JOB_STARTED);
        }

        disableOrEnableContainerAndChildren((ViewGroup) findViewById(R.id.settingsTableLayout), false);
        attachListeners();
    }

    private void attachListeners() {
        ButtonListener buttonListener = new ButtonListener();
        //BIR DE COMMITMENT EDIT TEXTI DINLEMEN LAZIM.
        findViewById(R.id.commitmentButton).setOnClickListener(buttonListener);
        findViewById(R.id.stopButton).setOnClickListener(buttonListener);
        findViewById(R.id.passButton).setOnClickListener(buttonListener);
    }

    public LinearLayout composeInformativeContentView(String message) {
        TextView informativeTextView = new TextView(this);
        informativeTextView.setText(message);
        informativeTextView.setGravity(Gravity.CENTER);
        informativeTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout dummyContainer = new LinearLayout(this);
        dummyContainer.setGravity(Gravity.CENTER);
        dummyContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ((LinearLayout.LayoutParams) dummyContainer.getLayoutParams()).gravity = Gravity.CENTER;
        dummyContainer.addView(informativeTextView);
        return dummyContainer;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_play, menu);
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

    public interface MessageHandlerObserver {
        public void update(MessageHandler messageHandler);
    }

    public class MessageHandler extends Handler {
        private Context context;
        public static final int STATE_OTHER_NOT_COMMITTED = -2;
        public static final int STATE_OTHER_COMMITTED = -1;
        public static final int STATE_WAITING = 0;
        public static final int STATE_DECIDING = 1;
        public static final int STATE_FINISHING = 2;
        private int currentState;
        private ArrayList<MessageHandlerObserver> observers;

        public MessageHandler(Context context) {
            this.context = context;
            this.observers = new ArrayList<MessageHandlerObserver>();
            setCurrentState(STATE_OTHER_NOT_COMMITTED);
        }

        @Override
        public void handleMessage(Message msg) {
            String messageAsString = null;
            HashMap<String, String> messageAsGameSettings = null;

            try {
                ByteArrayInputStream bais = new ByteArrayInputStream((byte[])msg.obj);
                ObjectInputStream ois = new ObjectInputStream(bais);
                messageAsGameSettings = (HashMap<String, String>) ois.readObject();
            } catch (Exception e1) {
                e1.printStackTrace();
                try {
                    messageAsString = new String((byte[]) msg.obj, 0, msg.arg1);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

            if (messageAsString != null) {
                if (messageAsString.contains("gameNoOnly:")) {
                    System.out.println("split 1: " + messageAsString.split(":")[0] + "split 2: " + messageAsString.split(":")[1]);
                    getParseConnection().setCurrentGameNo(Integer.valueOf(messageAsString.split(":")[1]));
                    getParseConnection().setCurrentState(ParseConnection.STATE_BACKGROUND_JOB_FINISHED);
                } else if (messageAsString.equalsIgnoreCase("committed")){
                    setCurrentState(STATE_OTHER_COMMITTED);
                } else if (messageAsString.equals("pass")) {
                    setCurrentState(STATE_DECIDING);
                    getGamePlayController().doUpdateGameDataGridLayout(getContext());
                } else if (messageAsString.contains("stop")) {
                    setCurrentState(STATE_FINISHING);
                    getGamePlayController().doDisplayGameResult2(getContext(), getParseConnection());
                }
            } else if (messageAsGameSettings != null) {
                GameSettings gameSettings = GameSettings.fromHashMap(getContext(), messageAsGameSettings);
                gameSettings.saveIntoPreferences();
                displayValues(gameSettings);
            }
        }

        public Context getContext() {return context;}
        public void setContext(Context context) {this.context = context;}
        public int getCurrentState() {return currentState;}
        public void setCurrentState(int currentState) {this.currentState = currentState; notifyObservers();}
        private ArrayList<MessageHandlerObserver> getObservers() {
            return observers;
        }
        private void setObservers(ArrayList<MessageHandlerObserver> observers) {this.observers = observers;}
        public void addObserver(MessageHandlerObserver observer) {if (!getObservers().contains(observer)) {             getObservers().add(observer);}}
        public void removeObserver(MessageHandlerObserver observer) {getObservers().remove(observer);}
        public void notifyObservers() {ArrayList<MessageHandlerObserver> observers = getObservers();int size = observers.size();for (MessageHandlerObserver observer : observers) {observer.update(this);}}
    }

    public class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            int buttonID = v.getId();
            String buttonText = ((Button) v).getText().toString();
            GamePlayController gamePlayController = getGamePlayController();
            DialogFactory dialogFactory = DialogFactory.getInstance();
            dialogFactory.setContext(v.getContext());
            ParseConnection parseConnection = getParseConnection();

            if (buttonID == R.id.commitmentButton) {
                EditText commitmentEditText = (EditText) findViewById(R.id.commitmentEditText);
                String commitment = commitmentEditText.getText().toString();
                int stepNumberEntered;
                int maximumStepNumber = Integer.valueOf(((TextView) findViewById(R.id.maximumStepNumberTextView)).getText().toString());
                BackgroundJobDialog backgroundJobDialog = (BackgroundJobDialog) dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
                backgroundJobDialog.setCancelable(true);
                TextView textView = ((TextView) backgroundJobDialog.findViewById(R.id.backgroundJobTextView));

                try {
                    stepNumberEntered = Integer.valueOf(commitment);

                    if (stepNumberEntered > maximumStepNumber) {
                        textView.setText("Maximum Step Number Is " + maximumStepNumber + ". Please Enter A Smaller Number.");
                        backgroundJobDialog.show();
                    } else {
                        boolean isClosedChecked = ((RadioButton) findViewById(R.id.closedCommitmentRadioButton)).isChecked();

                        gamePlayController.doPrepareTurnDialog(v.getContext());
                        gamePlayController.doSaveCommitment(v.getContext(), getParseConnection(), commitment);
                        findViewById(R.id.commitmentLinearLayout).setVisibility(View.GONE);
                        prepareGameDataGridLayout();
                        findViewById(R.id.gameDataGridLayout).setVisibility(View.VISIBLE);
                        findViewById(R.id.decisionLinearLayout).setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    textView.setText("Please Enter A Number!!!");
                    backgroundJobDialog.show();
                }
            } else if (buttonID == R.id.passButton) {
                gamePlayController.doPass((Button) v);
            } else if (buttonID == R.id.stopButton) {
                gamePlayController.doStop((Button) v, getParseConnection());
            }
        }
    }

    public ParseConnection getParseConnection() {
        return parseConnection;
    }

    public void setParseConnection(ParseConnection parseConnection) {
        this.parseConnection = parseConnection;
    }

    public GamePlayController getGamePlayController() {
        return gamePlayController;
    }

    public void setGamePlayController(GamePlayController gamePlayController) {
        this.gamePlayController = gamePlayController;
    }

    public ArrayList<Dialog> getDialogs() {
        return dialogs;
    }

    public void setDialogs(ArrayList<Dialog> dialogs) {
        this.dialogs = dialogs;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            SocketSingleton.getInstance().getSocket().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayValues(GameSettings settings) {
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

        SharedPreferences sp = getSharedPreferences(Keys.PLAYER_INFO_PREFERENCES, Context.MODE_PRIVATE);
        String name = sp.getString(Keys.PLAYER_NAME, "DEFAULT");
        String surname = sp.getString(Keys.PLAYER_SURNAME, "DEFAULT");
        String textViewText;
        textView = ((TextView) findViewById(R.id.nameSurnameEditText));

        if (SocketSingleton.getInstance().isHosted()) {
            textViewText = name + " " + surname + " " + "(Player 1)";
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            textViewText = name + " " + surname + " " + "(Player 2)";
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        }

        textView.setText(textViewText);
        findViewById(R.id.settingsSaveButton).setVisibility(View.GONE);
        TableLayout settingsTableLayout = (TableLayout) findViewById(R.id.settingsTableLayout);
        int childCount = settingsTableLayout.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = settingsTableLayout.getChildAt(i);

            if (child instanceof TableRow) {
                TableRow tableRow = (TableRow) child;
                ((TableLayout.LayoutParams) tableRow.getLayoutParams()).bottomMargin = 0;
            }
        }
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

    private void prepareGameDataGridLayout() {
        final int precision = 3;
        final String stringCSN = "Current Step Number: ";
        final String stringCT = "Current Total: ";
        final String stringCP1P = "Current P1 Payoff: ";
        final String stringCP2P = "Current P2 Payoff: ";
        final String separator = ": ";

        GridLayout gameDataGridLayout = (GridLayout) findViewById(R.id.gameDataGridLayout);
        TextView textView;

        textView = (TextView) gameDataGridLayout.findViewById(R.id.currentStepNumberTextView);
        textView.setText(stringCSN + "0");

        textView = (TextView) gameDataGridLayout.findViewById(R.id.currentTotalTextView);
        textView.setText(stringCT + ((TextView) findViewById(R.id.initialTotalEditText)).getText());

        double currentTotal = Double.valueOf(textView.getText().toString().split(separator)[1]);
        double ratio = Double.valueOf(((TextView) findViewById(R.id.ratioEditText)).getText().toString());

        textView = (TextView) gameDataGridLayout.findViewById(R.id.currentP1PayoffTextView);
        textView.setText(stringCP1P + shrinkDoubleAsStrings(String.valueOf(currentTotal * ratio), precision));
        textView = (TextView) gameDataGridLayout.findViewById(R.id.currentP2PayoffTextView);
        textView.setText(stringCP2P + shrinkDoubleAsStrings(String.valueOf(currentTotal - (currentTotal * ratio)), precision));
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public String shrinkDoubleAsStrings(String doubleAsString, int precision) {
        String shrinkedDoubleAsString = "";
        int currentPrecision = doubleAsString.length() - (doubleAsString.indexOf(".") + 1);

        if (precision >= currentPrecision) {
            shrinkedDoubleAsString = doubleAsString;
        } else {
            boolean isDotSeen = false;
            int length = doubleAsString.length();

            for (int i = 0; i < length; i++) {
                char charAtI = doubleAsString.charAt(i);

                if (charAtI == '.') {
                    isDotSeen = true;
                } else if (isDotSeen) {
                    if (precision == 0) {
                        break;
                    }

                    precision--;
                }

                shrinkedDoubleAsString += charAtI;
            }
        }

        return shrinkedDoubleAsString;
    }
}
