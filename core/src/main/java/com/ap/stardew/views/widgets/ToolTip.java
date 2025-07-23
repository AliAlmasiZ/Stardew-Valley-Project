package com.ap.stardew.views.widgets;

import com.ap.stardew.controllers.GameAssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ToolTip extends Table {
    private final Actor actor;
    private ClickListener clickListener;

    public ToolTip(Actor actor) {
        super();
        this.actor = actor;
    }

    public void show(){
        this.setBackground(GameAssetManager.getInstance().getCustomSkin().getDrawable("smallPanelNinePatch"));
        actor.getStage().addActor(this);
        pack();
        setTouchable(Touchable.enabled);

        getStage().addListener(clickListener = new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Convert stage coordinates to actor's local coordinates
                Vector2 local = stageToLocalCoordinates(new Vector2(x, y));

                // Check if the click is inside the actor
                Actor hit = hit(local.x, local.y, true);
                if (hit == null || !hit.isDescendantOf(ToolTip.this)) {
                    hide();
                }
                return false;
            }
        });
    }

    @Override
    public void act(float delta) {
        if(!actor.isVisible()){
            hide();
        }

        super.act(delta);


        Vector2 actorPos = actor.localToStageCoordinates(new Vector2(0, 0));
        setPosition(actorPos.x + actor.getWidth() / 2 - getWidth() / 2, actorPos.y - getHeight());
    }

    public void hide() {
        getStage().removeListener(clickListener);
        remove();
    }
}
