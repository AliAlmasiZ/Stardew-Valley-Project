package com.ap.stardew.view;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Arrays;

public class VariableDurationAnimation<T> {
    private T[] keyFrames;
    private float[] frameDurations;
    private float totalDuration;

    public VariableDurationAnimation(T[] keyFrames, float[] frameDurations) {
        if (keyFrames.length != frameDurations.length)
            throw new IllegalArgumentException("Frames and durations must match");

        this.keyFrames = keyFrames;
        this.frameDurations = frameDurations;

        for (float d : frameDurations) totalDuration += d;
    }
    public VariableDurationAnimation(Animation<T> animation) {
        this.keyFrames = animation.getKeyFrames();
        this.frameDurations = new float[animation.getKeyFrames().length];


        for (int i = 0; i < frameDurations.length; i++) {
            frameDurations[i] = animation.getFrameDuration();
        }

        totalDuration = animation.getAnimationDuration();
    }

    public T getKeyFrame(float stateTime, boolean looping) {
        if (looping) {
            stateTime %= totalDuration;
        } else {
            stateTime = Math.min(stateTime, totalDuration);
        }

        float time = 0f;
        for (int i = 0; i < keyFrames.length; i++) {
            time += frameDurations[i];
            if (stateTime < time) {
                return keyFrames[i];
            }
        }
        return keyFrames[keyFrames.length - 1];
    }

    public int getKeyFrameIndex(float stateTime, boolean looping){
        if (looping) {
            stateTime %= totalDuration;
        } else {
            stateTime = Math.min(stateTime, totalDuration);
        }

        float time = 0f;
        for (int i = 0; i < keyFrames.length; i++) {
            time += frameDurations[i];
            if (stateTime < time) {
                return i;
            }
        }
        return keyFrames.length - 1;
    }

    public void addFrame(T frame, float duration) {
        T[] newKeyFrames = Arrays.copyOf(keyFrames, keyFrames.length + 1);
        float[] newDurations = Arrays.copyOf(frameDurations, frameDurations.length + 1);

        newKeyFrames[newKeyFrames.length - 1] = frame;
        newDurations[newDurations.length - 1] = duration;

        keyFrames = newKeyFrames;
        frameDurations = newDurations;

        totalDuration += duration;
    }

    public float getDuration(){
        float duration = 0;
        for (float frameDuration : frameDurations) {
            duration += frameDuration;
        }
        return duration;
    }

    public T[] getKeyFrames() {
        return keyFrames;
    }
}
