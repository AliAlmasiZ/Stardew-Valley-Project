package com.ap.stardew.views.widgets;

import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.components.inventory.InventorySlot;
import com.ap.stardew.models.entities.systems.InventorySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import javax.xml.transform.Source;

public class InventoryGrid extends Table {
    private final Inventory inventory;
    private final static DragAndDrop dragAndDrop = new DragAndDrop();

    public InventoryGrid(Inventory inventory, int columns){
        super();

        this.inventory = inventory;

        int k = 0;
        for (InventorySlot slot : inventory.getSlots()) {
            if(k == columns){
                row();
            }

            InventorySlotWidget slotWidget = new InventorySlotWidget(slot);
            dragAndDrop.addSource(new DragAndDrop.Source(slotWidget) {
                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    Entity entity = slot.getEntity();

                    if(entity == null) return null;

                    Drawable item = slotWidget.getImage();
                    if (item == null) return null;

                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    Image ghost = new Image(item);
                    ghost.setColor(1, 1, 1, 0.5f);
                    ghost.setSize(slotWidget.getWidth(), slotWidget.getHeight());

                    payload.setObject(slot);
                    payload.setDragActor(ghost);
                    payload.setValidDragActor(ghost);
                    payload.setInvalidDragActor(ghost);
                    dragAndDrop.setDragActorPosition(ghost.getWidth() - x, -y);

//                    slot.setItem(null); // remove item temporarily
                    return payload;
                }
            });
            dragAndDrop.addTarget(new DragAndDrop.Target(slotWidget) {
                private boolean hovered = false;
                @Override
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    System.out.println(hovered);
                    boolean entered = !hovered;
                    hovered = true;

                    if((payload == null) || !(payload.getObject() instanceof InventorySlot sourceSlot)){
                        return false;
                    }

                    Entity entity = slot.getEntity();
                    Entity draggedEntity = sourceSlot.getEntity();
                    if(entity == null) {
                        if(entered){
                            slotWidget.frame.setOrigin(slotWidget.frame.getWidth()/2f, slotWidget.frame.getHeight()/2f);
                            slotWidget.frame.addAction(
                                Actions.parallel(
                                    Actions.scaleTo(0.9f, 0.9f, 0.2f, Interpolation.swingOut),
                                    Actions.alpha(0.3f, 0.2f)
                                )
                            );
                        }
                        return true;
                    }
                    if(!draggedEntity.isTheSameAs(entity)){
                        if(entered){
                            slotWidget.image.addAction(
                                Actions.sequence(
                                    Actions.moveBy(1, 0, 0.05f, Interpolation.swingOut),
                                    Actions.moveBy(-2, 0, 0.1f, Interpolation.swingOut),
                                    Actions.moveBy(1, 0, 0.05f, Interpolation.swingOut)
                                )
                            );
                        }
                        return false;
                    }
                    return false;
                }

                @Override
                public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                    boolean exit = hovered;
                    hovered = false;

                    if(exit){
                        slotWidget.frame.addAction(
                            Actions.parallel(
                                Actions.scaleTo(1f, 1f, 0.2f, Interpolation.swingOut),
                                Actions.alpha(1f, 0.2f)
                            )
                        );
                    }
                }

                @Override
                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    if((payload == null) || !(payload.getObject() instanceof InventorySlot sourceSlot)){
                        return;
                    }
                    System.out.println("asdasd");

                    Entity draggedEntity = sourceSlot.getEntity();

                    Entity leftOver = Inventory.addItemToSlot(draggedEntity, slot);
                    sourceSlot.setEntity(null);
                    sourceSlot.setEntity(leftOver);
                }
            });

            add(slotWidget).size(20, 20).pad(0.5f);
            k++;
        }
    }
}
