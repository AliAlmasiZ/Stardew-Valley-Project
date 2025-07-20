package com.ap.stardew.views.widgets;

import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.components.inventory.InventorySlot;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class InventoryGrid extends Table {
    private final Inventory inventory;

    public InventoryGrid(Inventory inventory, int columns){
        super();

        this.inventory = inventory;

        int rows = (int) Math.ceil(((float)inventory.getSize()) / columns);

        int k = 0;
        for (InventorySlot slot : inventory.getSlots()) {
            if(k == columns){
                row();
            }

            InventorySlotWidget slotWidget = new InventorySlotWidget(slot);

            add(slotWidget).size(15, 15).pad(0.3f);

            k++;
        }
    }
}
