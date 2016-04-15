package com.example.bbirincioglu.centipedegame;

import android.app.Dialog;
import android.content.Context;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by bbirincioglu on 3/10/2016.
 */
public class GameResultsController {
    private ParseConnection parseConnection;

    public GameResultsController() {
        setParseConnection(ParseConnection.getNewInstance());
    }

    public void doGetResults(Context context) {
        ParseObject.registerSubclass(GameResult.class);
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(context);
        Dialog dialog = dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
        ParseConnection connection = getParseConnection();
        connection.addObserver((ParseConnectionObserver) context);
        connection.addObserver((ParseConnectionObserver) dialog);
        connection.obtainObjects("GameResult");
    }

    public void doSaveResults(Context context) {
        ParseConnection parseConnection = getParseConnection();
        List<Object> gameResults = parseConnection.getObjects();
        Writer writer = new Writer(context);
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(context);
        Dialog dialog = dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB);
        writer.addObserver((WriterObserver) dialog);
        writer.writeExcel("gameResults_Centipede.xls", "gameResults", GameResult.HEADERS, gameResults);
    }

    public ParseConnection getParseConnection() {
        return parseConnection;
    }

    public void setParseConnection(ParseConnection parseConnection) {
        this.parseConnection = parseConnection;
    }
}
