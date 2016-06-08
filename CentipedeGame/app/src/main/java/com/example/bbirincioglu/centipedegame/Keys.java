package com.example.bbirincioglu.centipedegame;

/**
 * Created by bbirincioglu on 3/6/2016.
 */
public class Keys {
    //The keys below are related with Preferences which stores Game Settings. SETTINGS_PREFERENCES gives you the file in which MAXIMUM_STEP_NUMBER, FINAL_STEP_NUMBER,
    //......, PUNISHMENT values are stored.

    public static final String SETTINGS_PREFERENCES = "settings";
    public static final String MAXIMUM_STEP_NUMBER = "maxStepNum";
    public static final String FINAL_STEP_NUMBER = "finalStepNum";
    public static final String RATIO = "ratio";
    public static final String INITIAL_TOTAL = "initialTotal";
    public static final String FINAL_TOTAL = "finalTotal";
    public static final String MULTIPLICATOR = "multiplicator";
    public static final String COMMITMENT_TYPE = "commitmentType";
    public static final String PUNISHMENT = "punishment";

    //The keys below are related with Parse Server. They all are the column names of the tables in the Parse Server. By Using Them, we can obtain column values.
    public static final String PARSE_OBJECT_ID = "objectId";
    public static final String GAME_NO = "gameNo";
    public static final String PLAYER_FINISHED = "playerFinished";
    public static final String PLAYER_1_NAME = "p1Name";
    public static final String PLAYER_1_SURNAME = "p1Surname";
    public static final String PLAYER_1_COMMITMENT = "p1Commitment";
    public static final String PLAYER_1_PAYOFF = "p1Payoff";
    public static final String PLAYER_2_NAME = "p2Name";
    public static final String PLAYER_2_SURNAME = "p2Surname";
    public static final String PLAYER_2_COMMITMENT = "p2Commitment";
    public static final String PLAYER_2_PAYOFF = "p2Payoff";

    public static final String RETURN_FROM_ACTIVITY = "returnFromActivity";
    public static final String PASSWORD_RESTRICTED_AREA = ""; //Password of the sections of the applications such as GameResultsActivity.

    //Preferences related with player names, and surnames which are obtained when the application first started.
    public static final String PLAYER_INFO_PREFERENCES = "playerInfoPreferences";
    public static final String PLAYER_NAME = "playerName";
    public static final String PLAYER_SURNAME = "playerSurname";
}
