package com.ap.stardew.views.widgets;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class MoveYAction extends TemporalAction {
    private float startY;
    private float deltaY;

    public MoveYAction(float targetY, float duration) {
        setDuration(duration);
        this.deltaY = targetY;
    }

    @Override
    protected void begin() {
        startY = actor.getY();
    }

    @Override
    protected void update(float percent) {
        float newY = startY + deltaY * percent;
        actor.setY(newY);
    }
}
