package com.example.bbirincioglu.centipedegame;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.parse.ParseObject;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GamePlayActivity extends AppCompatActivity {
    private MessageHandler messageHandler; //For receiving messages from ConnectedThread object (backend thread), and to send message to main thread.
    private ParseConnection parseConnection; //For connecting Parse data base.
    private GamePlayController gamePlayController; //For controlling this activity. (Getting user inputs such as button clicks, text enters etc. and sending to domain objects for processing.)
    private ArrayList<Dialog> dialogs;  //Dialogs currently opened.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        setDialogs(new ArrayList<Dialog>());

        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(this);

        setMessageHandler(new MessageHandler(this));
        SocketSingleton socketSingleton = SocketSingleton.getInstance(); //Singleton which stores whether player is hosted or client, and bluetooth socket.

        ParseObject.registerSubclass(GameResult.class); //register new sub class (sub class of ParseObject) to Parse database. This is required for introducing your new sub class to database for recognition.
        setParseConnection(ParseConnection.getNewInstance()); //First I tried to implement ParseConnection via singleton. It didn't work, thus I created a method which returns new instance each time it is called.

        setGamePlayController(new GamePlayController());
        GamePlayController gamePlayController = getGamePlayController();
        gamePlayController.setConnectedThread(new ConnectedThread(this, socketSingleton.getSocket(), getMessageHandler())); //socket is argument because its inputStream, and outputStream will be used for reading
                                                                                                                            //and writing and messageHandler is argument as it provides communication between
                                                                                                                            //connectedThread and main thread.
        gamePlayController.getConnectedThread().start(); //Start connected thread in order to communicate with the other player.

        getParseConnection().setGamePlayController(gamePlayController);

        BackgroundJobDialog connectServerDialog = (BackgroundJobDialog) dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
        connectServerDialog.setContentView(composeInformativeContentView("Waiting For Server And Opponent Response..."));
        getParseConnection().addObserver(connectServerDialog); //Bind Observer (connectServerDialog) to observable (parseConnection)

        if (socketSingleton.isHosted()) {
            GameSettings gameSettings = GameSettings.loadFromPreferences(this); //Read game settings from android's built-in Preferences.
            gamePlayController.doWrite(gameSettings.toHashMap()); //If hosted player, then send Game Settings to client player.
            displayValues(gameSettings); //display game settings on the screen.
            getParseConnection().createEmptyGameResult(); // go to the database and create an empty row which will be filled during the game play.
        } else {
            getParseConnection().setCurrentState(ParseConnection.STATE_BACKGROUND_JOB_STARTED); //wait for hosted player to send Game Settings, and successful connection to parse database.
        }

        disableOrEnableContainerAndChildren((ViewGroup) findViewById(R.id.settingsTableLayout), false); //disable all the game settings view so that they can't be manipulated by players.
        attachListeners(); //attach any GUI object to their corresponding listeners.
    }

    private void attachListeners() {
        ButtonListener buttonListener = new ButtonListener();
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

        //Receive the message coming from backend thread (ConnectedThread object), and convert that message into original format such as String (normal message) or HashMap (GameSettings).
        @Override
        public void handleMessage(Message msg) {
            String messageAsString = null;
            HashMap<String, String> messageAsGameSettings = null;

            try {
                ByteArrayInputStream bais = new ByteArrayInputStream((byte[])msg.obj);
                ObjectInputStream ois = new ObjectInputStream(bais);
                messageAsGameSettings = (HashMap<String, String>) ois.readObject(); //try to convert HashMap, if it gives an exception, that means object is of type String.
            } catch (Exception e1) {
                e1.printStackTrace();
                try {
                    messageAsString = new String((byte[]) msg.obj, 0, msg.arg1); //then convert it to String.
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

            //The if clauses below are the indicators for which message has been sent by the other player. If it message contains "gameNoOnly", this means message is game number. If this is 5. game for example
            //hosted player sends something like "gameNoOnly:5". Finally Split message into 2 "gameNoOnly:", and "5" to get game number of current game. This is required to access specific row in the database.
            //I consider game number as the primary key for the table in the database.

            if (messageAsString != null) { //If message is of type String
                if (messageAsString.contains("gameNoOnly:")) {
                    System.out.println("split 1: " + messageAsString.split(":")[0] + "split 2: " + messageAsString.split(":")[1]);
                    getParseConnection().setCurrentGameNo(Integer.valueOf(messageAsString.split(":")[1]));
                    getParseConnection().setCurrentState(ParseConnection.STATE_BACKGROUND_JOB_FINISHED);
                } else if (messageAsString.equalsIgnoreCase("committed")){ //If other player committed, it will send you a "committed" string, and you change your state accordingly.
                    setCurrentState(STATE_OTHER_COMMITTED);
                } else if (messageAsString.equals("pass")) { //If other player passes, this means it is your turn to decide.
                    setCurrentState(STATE_DECIDING);
                    getGamePlayController().doUpdateGameDataGridLayout(getContext());
                } else if (messageAsString.contains("stop")) { //If other player stops, this means the game is finished.
                    setCurrentState(STATE_FINISHING);
                    getGamePlayController().doDisplayGameResult2(getContext(), getParseConnection());
                }
            } else if (messageAsGameSettings != null) { //If message is of type HashMap
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

    //ButtonListeners for "Commit", "Pass", and "Stop" Buttons
    public class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            int buttonID = v.getId();
            String buttonText = ((Button) v).getText().toString();
            GamePlayController gamePlayController = getGamePlayController();
            DialogFactory dialogFactory = DialogFactory.getInstance();
            dialogFactory.setContext(v.getContext());
            ParseConnection parseConnection = getParseConnection();

            if (buttonID == R.id.commitmentButton) {
                EditText commitmentEditText = (EditText) findViewById(R.id.commitmentEditText); //Get GUI object on which commitment value is entered.
                String commitment = commitmentEditText.getText().toString(); //Get commitment value as string format.
                int stepNumberEntered;
                int maximumStepNumber = Integer.valueOf(((TextView) findViewById(R.id.maximumStepNumberTextView)).getText().toString()); //Get Maximum Step Number from corresponding text view.
                BackgroundJobDialog backgroundJobDialog = (BackgroundJobDialog) dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB); //Create a background dialog for displaying any error message.
                backgroundJobDialog.setCancelable(true);
                TextView textView = ((TextView) backgroundJobDialog.findViewById(R.id.backgroundJobTextView));

                try {
                    stepNumberEntered = Integer.valueOf(commitment);

                    if (stepNumberEntered > maximumStepNumber) {
                        textView.setText("Maximum Step Number Is " + maximumStepNumber + ". Please Enter A Smaller Number.");
                        backgroundJobDialog.show();
                    } else {
                        boolean isClosedChecked = ((RadioButton) findViewById(R.id.closedCommitmentRadioButton)).isChecked(); //Check whether game is played with closed commitment, or open commitment.

                        gamePlayController.doPrepareTurnDialog(v.getContext()); //Initialize turn dialog. This dialog is invisible when the turn is yours, and visible when the turn is not yours.
                        gamePlayController.doSaveCommitment(v.getContext(), getParseConnection(), commitment); //Save your commitment to server by considering "gameNo" information to find corresponding row.
                        findViewById(R.id.commitmentLinearLayout).setVisibility(View.GONE); //Commitment step is finished. Thus, Close all the related GUI objects.
                        prepareGameDataGridLayout(); //Initializes a view which will be updated when each turn finishes. This gridlayout contains information of current step number, current total, and payoffs.
                        findViewById(R.id.gameDataGridLayout).setVisibility(View.VISIBLE); //make it visible.
                        findViewById(R.id.decisionLinearLayout).setVisibility(View.VISIBLE);// make decision buttons visible ("Pass" button and "Stop" button).
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    textView.setText("Please Enter A Number!!!"); //When you try to commit nothing, this will be shown.
                    backgroundJobDialog.show();
                }
            } else if (buttonID == R.id.passButton) {
                gamePlayController.doPass((Button) v); //Realize pass
            } else if (buttonID == R.id.stopButton) {
                gamePlayController.doStop((Button) v, getParseConnection()); //Realize stop.
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


    //Displays all the game settings on the game play screen.
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

    //To enable (clickable) or disable (unclickable) a GUI object.
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

    //Manipulate a double value (of type String) so that its precision becomes the precision that is given as argument to the method. Example: shrinkDoubleAsString("89.45689", 2) returns "89.45".
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
