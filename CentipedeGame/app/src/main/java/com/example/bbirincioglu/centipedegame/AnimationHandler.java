package com.example.bbirincioglu.centipedegame;

import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

/**
 * Created by bbirincioglu on 3/24/2016.
 */
public class AnimationHandler {
    public static final int INVALID = Integer.MAX_VALUE;

    public AnimationHandler() {

    }

    public long animate(View v, int[] animations, long duration, int repeatMode, int repeatCount) {
        AnimationSet animationSet = new AnimationSet(false);
        int length = animations.length;

        for (int i = 0; i < length; i++) {
            animationSet.addAnimation(AnimationUtils.loadAnimation(v.getContext(), animations[i]));
        }

        if (duration != INVALID) {
            animationSet.setDuration(duration);
        }

        if (repeatMode != INVALID) {
            animationSet.setRepeatMode(repeatMode);
        }

        if (repeatCount != INVALID) {
            animationSet.setRepeatCount(repeatCount);
        }

        v.startAnimation(animationSet);
        return animationSet.getDuration();
    }
}
