package com.ap.stardew.models.Actors;

import com.ap.stardew.controllers.GameAssetManager;
import com.ap.stardew.models.App;
import com.ap.stardew.models.NPC.NPC;
import com.ap.stardew.models.entities.components.PositionComponent;
import com.ap.stardew.models.entities.Renderable;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.views.GameScreen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class DialogActor extends Actor {
    private final NPC npc;
    private final GameScreen screen;
    private Sprite texture = GameAssetManager.getInstance().dialog;
    private float a = 0.7f;

    public DialogActor(NPC npc, GameScreen screen) {
        this.npc = npc;
        this.screen = screen;

        setSize(10, 8);
        Sprite sprite = GameAssetManager.getInstance().getNormalSprite(npc);
        setX(sprite.getX() + sprite.getWidth() - this.getWidth() / 2);
        setY(sprite.getY() + sprite.getHeight());

        setTouchable(Touchable.enabled);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isInRange()) {
                    screen.showNPCDialog(npc);
                    remove();
                }
            }
        });

        addListener(new InputListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                a = 1;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                a = 0.5f;
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if (isInRange()) {
            texture.setColor(1, 1, 1, a);
            batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        }
    }

    private boolean isInRange() {
        Player player = App.getActiveGame().getCurrentPlayer();
        return player.getPosition().dst(npc.getComponent(PositionComponent.class).get()) < GameScreen.DISTANCE;
    }


}
