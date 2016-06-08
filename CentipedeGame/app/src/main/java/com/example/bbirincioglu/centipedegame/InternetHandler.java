package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

/**
 * The class for checking wifi status, and enabling or disabling it. This is just a wrapper class.
 */
public class InternetHandler {
    private Activity activity;

    public InternetHandler(Context context) {
        setActivity((Activity) context);
    }

    public boolean isWifiEnabled() {
        Activity activity = getActivity();
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Activity.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public void enableWifi() {
        Activity activity = getActivity();
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Activity.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    public void disableWifi() {
        Activity activity = getActivity();
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Activity.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
    }

    public void setEnableMobileData(boolean enabled) {
       //INTERNET HANDLER SONRA DA PLAYER INFO VEYA DO ENABLE DISCOVERY.
    }

    public boolean isMobileDataEnabled() {
        boolean isEnabled = false;
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true);
            isEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isEnabled;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
