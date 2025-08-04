package com.ap.stardew.models.entities;

public interface EntityObserver {
    void onDelete(Entity entity);
    default void onTransferToInventory(Entity entity){}
}
