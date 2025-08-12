package com.ap.stardew.views.widgets;

import com.ap.stardew.view.VariableDurationAnimation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

public class AnimatedDrawable extends BaseDrawable {
    private final VariableDurationAnimation<TextureRegion> animation;
    private float stateTime = 0f;

    public AnimatedDrawable(VariableDurationAnimation<TextureRegion> animation) {
        this.animation = animation;
        setMinWidth(animation.getKeyFrame(0, true).getRegionWidth());
        setMinHeight(animation.getKeyFrame(0, true).getRegionHeight());
    }

    public void update(float delta) {
        stateTime += delta;
    }


    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        batch.draw(frame, x, y, width, height);
    }
}
