package com.ap.stardew.views.widgets;

import com.ap.stardew.controllers.GameAssetManager;
import com.ap.stardew.models.entities.components.inventory.InventorySlot;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Null;

public class InventorySlotWidget extends FramedImage {
    public InventorySlotWidget(InventorySlot slot) {
        super(GameAssetManager.getInstance().inventorySlotFrame, GameAssetManager.getInstance().testSlot);

        addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                image.addAction(
                        Actions.moveBy(0, 2, 0.2f, Interpolation.swingOut)
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                image.addAction(
                        Actions.moveBy(0, -2, 0.2f, Interpolation.swingOut)
                );
            }
        });
    }
    @Override
    public @Null Actor hit (float x, float y, boolean touchable) {
        if (touchable && getTouchable() == Touchable.disabled) return null;
        if (!isVisible()) return null;

        if (x >= 0 && x <= frame.getWidth() && y >= 0 && y <= frame.getHeight()) {
            return this;
        }

        return null;
    }
}
