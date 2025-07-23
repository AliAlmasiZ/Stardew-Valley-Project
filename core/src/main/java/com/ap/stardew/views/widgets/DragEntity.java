package com.ap.stardew.views.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class DragEntity extends Image {
    private float offsetX, offsetY;

    public DragEntity(Drawable drawable, Stage stage) {
        super(drawable);

        stage.addActor(this);

        setTouchable(Touchable.disabled);
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        getStage().screenToStageCoordinates(mouse);
        setPosition(mouse.x + offsetX, mouse.y + offsetX);
    }
}
