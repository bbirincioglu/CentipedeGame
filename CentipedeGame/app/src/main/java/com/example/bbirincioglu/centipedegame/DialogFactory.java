package com.example.bbirincioglu.centipedegame;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by bbirincioglu on 3/3/2016.
 */
public class DialogFactory {
    public static final String DIALOG_PLAYER_INFO = "dialogPlayerInfo";
    public static final String DIALOG_DISCOVERY_LIST = "dialogDiscoveryList";
    public static final String DIALOG_INFORMATIVE = "dialogInformative";
    public static final String DIALOG_BACKGROUND_JOB = "dialogBackgroundJob";
    public static final String DIALOG_WIFI_MOBILE_DATA = "dialogWifiMobileData";
    public static final String DIALOG_PASSWORD = "dialogPassword";
    public static final String DIALOG_GAME_RESULT = "dialogGameResult";

    private static DialogFactory instance;
    private Context context;

    private DialogFactory() {

    }

    public static DialogFactory getInstance() {
        if (instance == null) {
            instance = new DialogFactory();
        }

        return instance;
    }

    public Dialog create(String type) {
        Dialog dialog = null;
        Context context = getContext();

        if (type.equals(DIALOG_PLAYER_INFO)) {
            dialog = new PlayerInfoDialog(context);
        } else if (type.equals(DIALOG_DISCOVERY_LIST)) {
            dialog = new DiscoveryListDialog(context);
        } else if (type.equals(DIALOG_INFORMATIVE)) {
            dialog = new InformativeDialog(context);
        } else if (type.equals(DIALOG_BACKGROUND_JOB)) {
            dialog = new BackgroundJobDialog(context);
        } else if (type.equals(DIALOG_WIFI_MOBILE_DATA)) {
            dialog = new WifiMobileDataDialog(context);
        } else if (type.equals(DIALOG_PASSWORD)) {
            dialog = new PasswordDialog(context);
        } else if (type.equals(DIALOG_GAME_RESULT)) {
            dialog = new GameResultDialog(context);
        }

        ((SimpleDialog) dialog).initialize();

        return dialog;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
