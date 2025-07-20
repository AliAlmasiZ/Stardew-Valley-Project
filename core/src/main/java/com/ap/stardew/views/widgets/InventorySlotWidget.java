package com.ap.stardew.views.widgets;

import com.ap.stardew.controllers.GameAssetManager;
import com.ap.stardew.models.entities.components.inventory.InventorySlot;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class InventorySlotWidget extends FramedImage {
    public InventorySlotWidget(InventorySlot slot) {
        super(GameAssetManager.getInstance().inventorySlotFrame, GameAssetManager.getInstance().testSlot);
    }
}
