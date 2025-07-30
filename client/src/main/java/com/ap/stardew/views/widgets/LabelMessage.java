package com.ap.stardew.views.widgets;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LabelMessage extends Label {
    private final Actor actor;
    public LabelMessage(Actor actor, String message, Skin skin) {
        super(message, skin);

        this.actor = actor;

    }
    public void show(){
        actor.getStage().addActor(this);
        Vector2 actorPos = actor.localToStageCoordinates(new Vector2(0, 0));
        setPosition(actorPos.x + actor.getWidth() / 2 - getWidth() / 2, actorPos.y - getHeight());
        this.addAction(
            Actions.parallel(
                Actions.moveBy(0, -20, 1f, Interpolation.pow2),
                Actions.alpha(0, 1f, Interpolation.pow2)
            )
        );
    }
}
