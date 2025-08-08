package com.ap.stardew.view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
