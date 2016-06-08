package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * The class providing all Parse Server CRUD operations.
 */
public class ParseConnection {
    public static final int STATE_NO_BACKGROUND_JOB = -1;
    public static final int STATE_BACKGROUND_JOB_STARTED = 0;
    public static final int STATE_BACKGROUND_JOB_FINISHED = 1;
    private static ParseConnection instance;
    private ArrayList<ParseConnectionObserver> observers;
    private List<Object> objects;
    private int currentState;
    private int currentGameNo;
    private boolean myCommitmentSaved;
    private GamePlayController gamePlayController;

    private ParseConnection() {
        setObservers(new ArrayList<ParseConnectionObserver>());
        setObjects(new ArrayList<Object>());
        setCurrentState(STATE_NO_BACKGROUND_JOB);
        setCurrentGameNo(0);
        setMyCommitmentSaved(false);
    }

    public boolean isMyCommitmentSaved() {
        return myCommitmentSaved;
    }

    public void setMyCommitmentSaved(boolean myCommitmentSaved) {
        this.myCommitmentSaved = myCommitmentSaved;
    }

    public static ParseConnection getInstance() {
        if (instance == null) {
            instance = new ParseConnection();
        }

        return instance;
    }

    /*
        Method takes "className" (the name of the table in database) as argument, and gets the all rows in that table with each row being an instance of "className".
        It also updates this object's state so that corresponding background dialog appears / disappears on the screen.
        This method works asynchronously with main thread.
     */
    public void obtainObjects(String className) {
        setCurrentState(STATE_BACKGROUND_JOB_STARTED);
        ParseQuery query = ParseQuery.getQuery(className);
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {
                if (e == null) {
                    setObjects(list);
                } else {
                    System.out.println("Background Job Problem.");
                    e.printStackTrace();
                }

                setCurrentState(STATE_BACKGROUND_JOB_FINISHED);
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (throwable == null) {
                    setObjects((List) o);
                } else {
                    System.out.println("Background Job Problem.");
                    throwable.printStackTrace();
                }

                setCurrentState(STATE_BACKGROUND_JOB_FINISHED);
            }
        });
    }

    /*
        It takes className (name of the table), columnName in the table, and columnValue stored in that columnName, and returns corresponding row. In other words,
        this method returns a row (ParseObject) which belongs to "className" table, and has a columnName.value = uniqueColumnValue. This method works synchronously
        with main thread.
     */
    public ParseObject obtainObject(String className, String columnName, Object uniqueColumnValue) {
        ParseObject object = null;
        ParseQuery query = ParseQuery.getQuery(className);
        query.whereEqualTo(columnName, uniqueColumnValue);

        try {
            object = query.getFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    public List<Object> getObjects() {
        return objects;
    }

    public void setObjects(List<Object> objects) {
        this.objects = objects;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
        notifyObservers();
    }

    private ArrayList<ParseConnectionObserver> getObservers() {
        return observers;
    }

    private void setObservers(ArrayList<ParseConnectionObserver> observers) {
        this.observers = observers;
    }

    public void addObserver(ParseConnectionObserver observer) {
        if (!getObservers().contains(observer)) {
            getObservers().add(observer);
        }
    }

    public void removeObserver(ParseConnectionObserver observer) {
        getObservers().remove(observer);
    }

    public void removeAllObservers() {
        ArrayList<ParseConnectionObserver> observers = getObservers();
        int size = observers.size();

        for (int i = 0; i < size; i++) {
            observers.remove(0);
        }
    }

    public void notifyObservers() {
        ArrayList<ParseConnectionObserver> observers = getObservers();

        for (ParseConnectionObserver observer : observers) {
            observer.update(this);
        }
    }

    //It creates an empty row with specific game number value in the GameResult table whenever a new game is started.
    //This method also works asynchronously.
    public void createEmptyGameResult() {
        setCurrentState(STATE_BACKGROUND_JOB_STARTED);
        final GameResult gr = new GameResult();
        class MyRunnable implements Runnable {
            public void run() {
                try {
                    gr.obtainGameNo();
                    gr.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                setCurrentGameNo(gr.getGameNo());
                                getGamePlayController().getConnectedThread().write("gameNoOnly:" + gr.getGameNo());
                                setCurrentState(STATE_BACKGROUND_JOB_FINISHED);
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        new Thread(new MyRunnable()).start();
    }

    /*
        When a player commits, the commitment value is sent to the server via this method. After the commitment value is sent, this method also sends message
        to other phone to inform it about this commitment.
     */
    public void saveGameResultForCommitment(Context context, final GameResult gameResult) {
        setCurrentState(STATE_BACKGROUND_JOB_STARTED);
        setMyCommitmentSaved(true);
        final Activity activity = (Activity) context;

        class MyRunnable implements Runnable {
            public void run() {
                try {
                    gameResult.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            ConnectedThread connectedThread = getGamePlayController().getConnectedThread();
                            final GamePlayActivity.MessageHandler messageHandler = connectedThread.getMessageHandler();
                            connectedThread.write("committed");

                            if (e == null) {
                                setCurrentState(STATE_BACKGROUND_JOB_FINISHED);
                                messageHandler.notifyObservers();
                            } else {
                                System.out.println("*********************************************************************");
                                e.printStackTrace();
                                e.printStackTrace();
                                e.printStackTrace();
                                e.printStackTrace();
                                e.printStackTrace();
                                System.out.println("*********************************************************************");
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        new Thread(new MyRunnable()).start();
    }

    public GamePlayController getGamePlayController() {
        return gamePlayController;
    }

    public void setGamePlayController(GamePlayController gamePlayController) {
        this.gamePlayController = gamePlayController;
    }

    public int getCurrentGameNo() {
        return currentGameNo;
    }

    public void setCurrentGameNo(int currentGameNo) {
        this.currentGameNo = currentGameNo;
    }

    public static ParseConnection getNewInstance() {
        return new ParseConnection();
    }

    public int getObserverNum() {
        return getObservers().size();
    }
}