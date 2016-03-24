package com.example.bbirincioglu.centipedegame;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by bbirincioglu on 3/24/2016.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this);
    }
}
