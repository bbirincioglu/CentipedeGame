package com.example.bbirincioglu.centipedegame;

import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

/**
 * This class is responsible from handling button animations in the game.
 */
public class AnimationHandler {
    public static final int INVALID = Integer.MAX_VALUE;

    /*
       First argument is the view object (generally a button) on which animations (int[] animations) will be applied with some duration, repeatMode
       (whether it is FINITE OR INFINITE), and how many times animations will be repeated.
     */
    public long animateOn(View v, int[] animations, int duration, int repeatMode, int repeatCount) {
        AnimationSet animationSet = new AnimationSet(true); //Set containing all the animations.
        int length = animations.length;

        for (int i = 0; i < length; i++) {
            animationSet.addAnimation(AnimationUtils.loadAnimation(v.getContext(), animations[i])); //Get animations by their animationIDs and add into animation set.
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
        return animationSet.getDuration(); //We return it because after the animation has finished, we sometimes switch into new activity. Thus we have to know
        // when an animation has finished.
    }
}
