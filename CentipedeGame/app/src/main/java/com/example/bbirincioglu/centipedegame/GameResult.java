package com.example.bbirincioglu.centipedegame;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

/**
 * A custom class for storing / receiving game result to / from Parse server.
 */
@ParseClassName("GameResult")
public class GameResult extends ParseObject {
    public static final String[] HEADERS = new String[]{"GAME_NO", "PLAYER_FINISHED", /*Column Names while extracting to excel.
                                                                                     They are not used while obtaining column values from database table.
                                                                                     Instead of those we use Keys.RATIO, Keys.INITIAL_TOTAL etc.*/
            "P1_NAME", "P1_SURNAME", "P1_COMMITMENT", "P1_PAYOFF",
            "P2_NAME", "P2_SURNAME", "P2_COMMITMENT", "P2_PAYOFF",
            "MAXIMUM_STEP_NUM", "FINAL_STEP_NUM", "RATIO", "INITIAL_TOTAL",
            "FINAL_TOTAL", "MULTIPLICATOR", "COMMITMENT_TYPE", "PUNISHMENT"};
    public static final String SPLIT_WITH = "___";

    public GameResult() {
        super();
    }

    //In the database, we have two tables (GameNo, and GameResult). GameNo has only one row which stores current game number. In order to create a row
    //in the GameResult table, we need to receive game number from GameNo table first, and create new record in the GameResult table with this game number.
    //Finally we increase the game number in the GameNo table.

    public void obtainGameNo() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GameNo");

        try {
            ParseObject parseObject = query.get("jGIX8ngJE8"); //Object ID for selecting row in the GameNo table.
            setGameNo(parseObject.getInt(Keys.GAME_NO));
            parseObject.put(Keys.GAME_NO, getGameNo() + 1);
            parseObject.saveInBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Key value pairs for setting and getting values from this object.
    public String getPlayerFinished() {
        return getString(Keys.PLAYER_FINISHED);
    }

    public void setPlayerFinished(String playerFinished) {
        put(Keys.PLAYER_FINISHED, playerFinished);
    }

    public String getID() {
        return getString(Keys.PARSE_OBJECT_ID);
    }

    public void setID(String id) {
        put(Keys.PARSE_OBJECT_ID, id);
    }

    public int getGameNo() {
        return getInt(Keys.GAME_NO);
    }

    public void setGameNo(int gameNo) {
        put(Keys.GAME_NO, gameNo);
    }

    public String getP1Name() {
        return getString(Keys.PLAYER_1_NAME);
    }

    public void setP1Name(String p1Name) {
        put(Keys.PLAYER_1_NAME, p1Name);
    }

    public String getP1Surname() {
        return getString(Keys.PLAYER_1_SURNAME);
    }

    public void setP1Surname(String p1Surname) {
        put(Keys.PLAYER_1_SURNAME, p1Surname);
    }

    public String getP1Commitment() {
        return getString(Keys.PLAYER_1_COMMITMENT);
    }

    public void setP1Commitment(String p1Commitment) { put(Keys.PLAYER_1_COMMITMENT, p1Commitment); }

    public String getP2Name() {
        return getString(Keys.PLAYER_2_NAME);
    }

    public void setP2Name(String p2Name) {
        put(Keys.PLAYER_2_NAME, p2Name);
    }

    public String getP2Surname() {
        return getString(Keys.PLAYER_2_SURNAME);
    }

    public void setP2Surname(String p2Surname) {
        put(Keys.PLAYER_2_SURNAME, p2Surname);
    }

    public String getP2Commitment() {
        return getString(Keys.PLAYER_2_COMMITMENT);
    }

    public void setP2Commitment(String p2Commitment) { put(Keys.PLAYER_2_COMMITMENT, p2Commitment); }

    public void setP1Payoff(String p1Payoff) {
        put(Keys.PLAYER_1_PAYOFF, p1Payoff);
    }

    public String getP1Payoff() {
        return getString(Keys.PLAYER_1_PAYOFF);
    }

    public void setP2Payoff(String p2Payoff) {
        put(Keys.PLAYER_2_PAYOFF, p2Payoff);
    }

    public String getP2Payoff() {
        return getString(Keys.PLAYER_2_PAYOFF);
    }

    public String getMaximumStepNumber() {
        return getString(Keys.MAXIMUM_STEP_NUMBER);
    }

    public void setMaximumStepNumber(String maximumStepNumber) {
        put(Keys.MAXIMUM_STEP_NUMBER, maximumStepNumber);
    }

    public String getFinalStepNumber() {return getString(Keys.FINAL_STEP_NUMBER);}

    public void setFinalStepNumber(String finalStepNumber) {
        put(Keys.FINAL_STEP_NUMBER, finalStepNumber);
    }

    public String getRatio() {
        return getString(Keys.RATIO);
    }

    public void setRatio(String ratio) {
        put(Keys.RATIO, ratio);
    }

    public String getInitialTotal() {
        return getString(Keys.INITIAL_TOTAL);
    }

    public void setInitialTotal(String initialTotal) {
        put(Keys.INITIAL_TOTAL, initialTotal);
    }

    public String getFinalTotal() {
        return getString(Keys.FINAL_TOTAL);
    }

    public void setFinalTotal(String finalTotal) {
        put(Keys.FINAL_TOTAL, finalTotal);
    }

    public String getMultiplicator() {
        return getString(Keys.MULTIPLICATOR);
    }

    public void setMultiplicator(String multiplicator) {
        put(Keys.MULTIPLICATOR, multiplicator);
    }

    public String getCommitmentType() {
        return getString(Keys.COMMITMENT_TYPE);
    }

    public void setCommitmentType(String commitmentType) {
        put(Keys.COMMITMENT_TYPE, commitmentType);
    }

    public String getPunishment() {
        return getString(Keys.PUNISHMENT);
    }

    public void setPunishment(String punishment) {
        put(Keys.PUNISHMENT, punishment);
    }

    public String toString() {
        String gameResultAsString;
        gameResultAsString = getGameNo() + SPLIT_WITH
                + getPlayerFinished() + SPLIT_WITH
                + getP1Name() + SPLIT_WITH
                + getP1Surname() + SPLIT_WITH
                + getP1Commitment() + SPLIT_WITH
                + getP1Payoff() + SPLIT_WITH
                + getP2Name() + SPLIT_WITH
                + getP2Surname() + SPLIT_WITH
                + getP2Commitment() + SPLIT_WITH
                + getP2Payoff() + SPLIT_WITH
                + getMaximumStepNumber() + SPLIT_WITH
                + getFinalStepNumber() + SPLIT_WITH
                + getRatio() + SPLIT_WITH
                + getInitialTotal() + SPLIT_WITH
                + getFinalTotal() + SPLIT_WITH
                + getMultiplicator() + SPLIT_WITH
                + getCommitmentType() + SPLIT_WITH
                + getPunishment() + SPLIT_WITH;
        return gameResultAsString;
    }
}
