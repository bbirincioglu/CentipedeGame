package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by bbirincioglu on 3/24/2016.
 */
public class ActivitySwitcher {
    public ActivitySwitcher() {

    }

    public void fromPreviousToNext(Activity previous, Class next, Bundle data, boolean isPreviousKilled) {
        Intent intent = new Intent(previous, next);

        if (data != null) {
            intent.putExtras(data);
        }

        if (isPreviousKilled) {
            previous.finish();
        }

        previous.startActivity(intent);
    }
}
