package com.ap.stardew.views.widgets;

import com.ap.stardew.controllers.GameAssetManager;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TrashCanActor extends Group {
    private final Image trashCan;
    private final Image lid;
    public TrashCanActor() {
        trashCan = new Image(GameAssetManager.getInstance().getCustomSkin().getDrawable("trashCan"));
        lid = new Image(GameAssetManager.getInstance().getCustomSkin().getDrawable("trashCanLid"));

        trashCan.setPosition(0, 0);
        lid.setPosition(-1, trashCan.getHeight() - lid.getHeight() + 1);

        addActor(trashCan);
        addActor(lid);

        addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if(pointer != -1) return;

                if(InventorySlotWidget.draggedEntity != null){
                    lid.addAction(
                        Actions.moveBy(0, 2, 0.2f, Interpolation.swingOut)
                    );
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if(pointer != -1) return;

                if(InventorySlotWidget.draggedEntity != null){
                    lid.addAction(
                        Actions.moveBy(0, -2, 0.2f, Interpolation.exp5Out)
                    );
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(InventorySlotWidget.draggedEntity != null){
                    InventorySlotWidget.draggedEntity.delete();
                    InventorySlotWidget.draggedEntity = null;
                    InventorySlotWidget.sourceSlot = null;
                    InventorySlotWidget.destSlot = null;
                    InventorySlotWidget.ghost.remove();
                    lid.setOrigin(lid.getWidth() + 2, lid.getHeight()/2);
                    lid.addAction(
                        Actions.sequence(
                            Actions.rotateBy(-20, 0.3f, Interpolation.swingOut),
                            Actions.delay(0.1f),
                            Actions.parallel(
                                Actions.rotateBy(20, 0.3f, Interpolation.exp5Out),
                                Actions.moveBy(0, -2, 0.3f, Interpolation.exp5Out)
                            )
                        )
                    );
                }
            }
        });
    }

    @Override
    public float getWidth() {
        return trashCan.getWidth();
    }

    @Override
    public float getHeight() {
        return trashCan.getHeight();
    }
}
