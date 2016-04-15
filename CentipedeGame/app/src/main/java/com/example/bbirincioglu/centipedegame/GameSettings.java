package com.example.bbirincioglu.centipedegame;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by bbirincioglu on 3/6/2016.
 */
public class GameSettings implements Serializable {
    public static final String[] KEYS = new String[]{Keys.MAXIMUM_STEP_NUMBER, Keys.RATIO, Keys.INITIAL_TOTAL, Keys.MULTIPLICATOR, Keys.COMMITMENT_TYPE, Keys.PUNISHMENT};
    private Context context;
    private String maximumStepNumber;
    private String ratio;
    private String initialTotal;
    private String multiplicator;
    private String commitmentType;
    private String punishment;

    public GameSettings(Context context, String maximumStepNumber, String ratio, String initialTotal, String multiplicator, String commitmentType, String punishment) {
        this.context = context;
        this.maximumStepNumber = maximumStepNumber;
        this.ratio = ratio;
        this.initialTotal = initialTotal;
        this.multiplicator = multiplicator;
        this.commitmentType = commitmentType;
        this.punishment = punishment;
    }

    public static GameSettings loadFromPreferences(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Keys.SETTINGS_PREFERENCES, Context.MODE_PRIVATE);
        String maximumStepNumber = sp.getString(Keys.MAXIMUM_STEP_NUMBER, "10");
        String ratio = sp.getString(Keys.RATIO, "0.8");
        String initialTotal = sp.getString(Keys.INITIAL_TOTAL, "100");
        String multiplicator = sp.getString(Keys.MULTIPLICATOR, "2");
        String commitmentType = sp.getString(Keys.COMMITMENT_TYPE, "Closed");
        String punishment = sp.getString(Keys.PUNISHMENT, "0");
        return new GameSettings(context, maximumStepNumber, ratio, initialTotal, multiplicator, commitmentType, punishment);
    }

    public void saveIntoPreferences() {
        SharedPreferences sp = getContext().getSharedPreferences(Keys.SETTINGS_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = sp.edit();
        editor.putString(Keys.MAXIMUM_STEP_NUMBER, getMaximumStepNumber());
        editor.putString(Keys.RATIO, getRatio());
        editor.putString(Keys.INITIAL_TOTAL, getInitialTotal());
        editor.putString(Keys.MULTIPLICATOR, getMultiplicator());
        editor.putString(Keys.COMMITMENT_TYPE, getCommitmentType());
        editor.putString(Keys.PUNISHMENT, getPunishment());
        editor.commit();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getMaximumStepNumber() {
        return maximumStepNumber;
    }

    public void setMaximumStepNumber(String maximumStepNumber) {
        this.maximumStepNumber = maximumStepNumber;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getRatio() {
        return ratio;
    }

    public String getInitialTotal() {
        return initialTotal;
    }

    public void setInitialTotal(String initialTotal) {
        this.initialTotal = initialTotal;
    }

    public String getMultiplicator() {
        return multiplicator;
    }

    public void setMultiplicator(String multiplicator) {
        this.multiplicator = multiplicator;
    }

    public String getCommitmentType() {
        return commitmentType;
    }

    public void setCommitmentType(String commitmentType) {
        this.commitmentType = commitmentType;
    }

    public String getPunishment() {
        return punishment;
    }

    public void setPunishment(String punishment) {
        this.punishment = punishment;
    }

    public HashMap<String, String> toHashMap() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        String[] KEYS = GameSettings.KEYS;
        hashMap.put(KEYS[0], getMaximumStepNumber());
        hashMap.put(KEYS[1], getRatio());
        hashMap.put(KEYS[2], getInitialTotal());
        hashMap.put(KEYS[3], getMultiplicator());
        hashMap.put(KEYS[4], getCommitmentType());
        hashMap.put(KEYS[5], getPunishment());
        return hashMap;
    }

    public static GameSettings fromHashMap(Context context, HashMap<String, String> hashMap) {
        String[] KEYS = GameSettings.KEYS;
        String maximumStepNumber = hashMap.get(KEYS[0]);
        String ratio = hashMap.get(KEYS[1]);
        String initialTotal = hashMap.get(KEYS[2]);
        String multiplicator = hashMap.get(KEYS[3]);
        String commitmentType = hashMap.get(KEYS[4]);
        String punishment = hashMap.get(KEYS[5]);
        return new GameSettings(context, maximumStepNumber, ratio, initialTotal, multiplicator, commitmentType, punishment);
    }
}