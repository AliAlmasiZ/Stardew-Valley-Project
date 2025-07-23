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

    public InventoryGrid(Inventory inventory, int columns){
        super();

        this.inventory = inventory;

        int k = 0;
        for (InventorySlot slot : inventory.getSlots()) {
            if(k == columns){
                row();
            }

            InventorySlotWidget slotWidget = new InventorySlotWidget(slot);

            add(slotWidget).size(20, 20).pad(0.5f);
            k++;
        }
    }
}
