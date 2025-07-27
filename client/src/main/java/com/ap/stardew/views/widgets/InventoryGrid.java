package com.ap.stardew.views.widgets;

import com.ap.stardew.controllers.GameAssetManager;
import com.ap.stardew.models.App;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.components.inventory.InventorySlot;
import com.ap.stardew.models.player.Player;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

public class InventoryGrid extends Table {
    public enum Type{
        SIMPLE, PLAYER_INVENTORY, TOOLBAR
    }

    private final Inventory inventory;
    private final ArrayList<Cell<InventorySlotWidget>> slotWidgets = new ArrayList<>();
    private float slotSize = 20;
    private int selected = 0;
    private final Type type;

    public InventoryGrid(Inventory inventory, int columns){
        this(inventory, columns, 0, Type.SIMPLE);
    }
    public InventoryGrid(Inventory inventory, int columns, Type type){
        this(inventory, columns, 0, type);
    }
    public InventoryGrid(Inventory inventory, int columns, int slots, Type type){
        super();

        this.inventory = inventory;
        if(slots == 0){
            slots = inventory.getSlots().size();
        }

        this.type = type;

        int k = 0;
        int i = 0;

        for (InventorySlot slot : inventory.getSlots()) {
            if(i>=slots) break;

            InventorySlotWidget slotWidget = new InventorySlotWidget(slot);
            Cell<InventorySlotWidget> cell = add(slotWidget).size(slotSize, slotSize).pad(0.5f);
            slotWidgets.add(cell);

            if(i == k){
                if(type == Type.PLAYER_INVENTORY){
                    cell.spaceBottom(5);
                }
            }
            if(k == columns-1){
                row();
                k = -1;
            }

            k++;
            i++;
        }

        if(type == Type.TOOLBAR){
            selected = App.getActiveGame().getCurrentPlayer().getComponent(Inventory.class)
                .getSlots().indexOf(App.getActiveGame().getCurrentPlayer().getActiveSlot());
            slotWidgets.get(selected).getActor()
                .frame.setDrawable(new TextureRegionDrawable(GameAssetManager.getInstance().inventorySlotFrameSelected));

            for (Cell<InventorySlotWidget> cell : slotWidgets) {
                InventorySlotWidget slotWidget = cell.getActor();

                slotWidget.clearListeners();

                slotWidget.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        App.getActiveGame().getCurrentPlayer().setActiveSlot(slotWidget.getSlot());
                    }
                });
                addListener(new InputListener(){
                    @Override
                    public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                        System.out.println("asd");
                        return super.scrolled(event, x, y, amountX, amountY);
                    }
                });
            }
        }
    }
    public void setSlotSize(float size){
        slotSize = size;
        for (Cell<InventorySlotWidget> slotWidget : slotWidgets) {
            slotWidget.size(slotSize, slotSize);
            slotWidget.getActor().setQuantityScale(0.7f * size / 20);
        }
    }

    @Override
    public void act(float delta) {
        if(type == Type.TOOLBAR){
            int newSelected = App.getActiveGame().getCurrentPlayer().getComponent(Inventory.class)
                .getSlots().indexOf(App.getActiveGame().getCurrentPlayer().getActiveSlot());

            if(newSelected != selected){
                slotWidgets.get(selected).getActor()
                    .frame.setDrawable(new TextureRegionDrawable(GameAssetManager.getInstance().inventorySlotFrame));
                selected = newSelected;
                slotWidgets.get(selected).getActor()
                    .frame.setDrawable(new TextureRegionDrawable(GameAssetManager.getInstance().inventorySlotFrameSelected));
            }
        }

        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(1, 1, 1, 1);
    }
}
