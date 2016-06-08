package com.example.bbirincioglu.centipedegame;

import android.app.Dialog;
import android.content.Context;

import com.parse.ParseObject;

import java.util.List;

/**
 * Controller for GameResultsActivity
 */
public class GameResultsController {
    private ParseConnection parseConnection;

    public GameResultsController() {
        setParseConnection(ParseConnection.getNewInstance());
    }

    //get the results using parseConnection object.
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

    //extract the results into excel
    public void doSaveResults(Context context) {
        ParseConnection parseConnection = getParseConnection();
        List<Object> gameResults = parseConnection.getObjects(); //get game results
        Writer writer = new Writer(context); //create writer for creating excel sheet, and writing into it.
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(context);
        Dialog dialog = dialogFactory.create(DialogFactory.DIALOG_BACKGROUND_JOB); //create a background job dialog which is displayed during writing process.
        writer.addObserver((WriterObserver) dialog);
        writer.writeExcel("gameResults_Centipede.xls", "gameResults", GameResult.HEADERS, gameResults); //write into a file with name given as first argument.
    }

    public ParseConnection getParseConnection() {
        return parseConnection;
    }

    public void setParseConnection(ParseConnection parseConnection) {
        this.parseConnection = parseConnection;
    }
}
