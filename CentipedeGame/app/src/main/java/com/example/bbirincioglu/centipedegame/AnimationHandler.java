package com.example.bbirincioglu.centipedegame;

import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

/**
 * Created by bbirincioglu on 2/28/2016.
 */
public class AnimationHandler {
    public static final int INVALID = Integer.MAX_VALUE;

    public long animateOn(View v, int[] animations, int duration, int repeatMode, int repeatCount) {
        AnimationSet animationSet = new AnimationSet(true);
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
