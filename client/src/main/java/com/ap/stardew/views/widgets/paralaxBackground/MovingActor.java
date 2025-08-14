package com.ap.stardew.views.widgets.paralaxBackground;

import com.ap.stardew.views.widgets.AnimatedDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class MovingActor extends Image {
    private final float speed;
    private final float verticalSpeed;

    public MovingActor(Drawable drawable, float speed, float verticalSpeed) {
        super(drawable);
        this.speed = speed;
        this.verticalSpeed = verticalSpeed;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(getDrawable() instanceof AnimatedDrawable animatedDrawable){
            animatedDrawable.update(delta);
        }
        this.moveBy(-delta * speed, 0);

        if(this.getX() + this.getImageWidth() < -5){
            onExit();
        }
    }

    public void onExit(){

    }

    public float getVerticalSpeed() {
        return verticalSpeed;
    }
}
