package com.ap.stardew.views.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class TransformWidgetWrapper<T extends Actor> extends Table {
    private final T actor;
    private T lastHit;
    public TransformWidgetWrapper(T actor){
        this.actor = actor;
//        actor.setTouchable(Touchable.disabled);
        setTouchable(Touchable.enabled);
        add(actor).grow();
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()){
            for (EventListener listener : actor.getListeners()) {
                if(listener instanceof InputListener inputListener){
                    inputListener.exit(null, x, y, -1, null);
                }
            }
            return null;
        } else{
            for (EventListener listener : actor.getListeners()) {
                if(listener instanceof InputListener inputListener){
                    inputListener.enter(null, x, y, -1, null);
                }
            }
            return this;
        }
    }

    public T getActor() {
        return actor;
    }
}
