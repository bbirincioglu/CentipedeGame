package com.example.bbirincioglu.centipedegame;

import android.content.Context;
import android.view.View;

/**
 * Created by bbirincioglu on 3/24/2016.
 */
public class MainMenuController {
    private MainMenuActivity mainMenuActivity;
    private ActivitySwitcher activitySwitcher;
    private AnimationHandler animationHandler;

    public MainMenuController(Context context) {
        setMainMenuActivity((MainMenuActivity) context);
        setActivitySwitcher(new ActivitySwitcher());
        setAnimationHandler(new AnimationHandler());
    }

    public void doSwitchActivity(int viewID, final Class next) {
        View v = getMainMenuActivity().findViewById(viewID);
        int invalid = AnimationHandler.INVALID;
        long duration = getAnimationHandler().animate(v, new int[] {R.anim.fade_out, R.anim.scale_up}, invalid, invalid, invalid);
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivitySwitcher().fromPreviousToNext(getMainMenuActivity(), next, null, true);
            }
        }, duration * 5 / 6);
    }

    public void doExit() {
        getMainMenuActivity().finish();
    }

    public MainMenuActivity getMainMenuActivity() {
        return mainMenuActivity;
    }

    public void setMainMenuActivity(MainMenuActivity mainMenuActivity) {
        this.mainMenuActivity = mainMenuActivity;
    }

    public ActivitySwitcher getActivitySwitcher() {
        return activitySwitcher;
    }

    public void setActivitySwitcher(ActivitySwitcher activitySwitcher) {
        this.activitySwitcher = activitySwitcher;
    }

    public AnimationHandler getAnimationHandler() {
        return animationHandler;
    }

    public void setAnimationHandler(AnimationHandler animationHandler) {
        this.animationHandler = animationHandler;
    }
}
