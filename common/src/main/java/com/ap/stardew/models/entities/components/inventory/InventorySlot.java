package com.ap.stardew.models.entities.components.inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.EntityObserver;

import java.io.Serializable;

public class InventorySlot implements Serializable, EntityObserver, Cloneable{    @JsonProperty("entity")
    private Entity entity;

    public InventorySlot(Entity entity) {
        this.setEntity(entity);
    }
    public InventorySlot(){
        this(null);
    }
    public void setEntity(Entity entity){
        if(this.entity != null) {
            this.entity.removeObserveer(this);
        }
        this.entity = entity;
        if(entity != null) {
            entity.addObserver(this);
        }
    }
    public Entity getEntity() {
        return entity;
    }

    @Override
    public void onDelete(Entity entity) {
        this.setEntity(null);
    }
}
