package com.ap.stardew.models.entities.components.inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.EntityObserver;

import java.io.Serializable;

/***
 * when you remove an item from inventory, the capacity shouldn't change. So you cant use arrayList.remove. java doesn't have
 * pointers so you cant give the pointer to a slot to remove the item in that slot. so I made this class.
 * it just holds an entity. plus, in the future, slots may hold more information.
 */
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
