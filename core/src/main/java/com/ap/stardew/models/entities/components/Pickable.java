package com.ap.stardew.models.entities.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.models.entities.Entity;

import java.io.Serializable;

public class Pickable extends EntityComponent implements Serializable {
    @JsonProperty("maxStack")
    private int maxStack;
    @JsonProperty("stackSize")
    private int stackSize;
    @JsonProperty("icon")
    private String icon;

    public Pickable(int maxStack, int stackSize, String iconPath){
        this.maxStack = maxStack;
        this.stackSize = stackSize;
        icon = iconPath;
    }
    public Pickable(int maxStack, int stackSize){
        this(maxStack, stackSize, null);
    }
    public Pickable(int maxStack){
        this(maxStack, 0);
    }
    private Pickable(Pickable other){
        this.maxStack = other.maxStack;
        this.stackSize = other.stackSize;
        this.icon = other.icon;
    }
    public Pickable(){
        this(1, 0);
    }

    public int getMaxStack() {
        return maxStack;
    }
    public void setStackSize(int amount){
        this.stackSize = amount;
        if(stackSize == 0){
            entity.delete();
        }
    }
    public int getStackSize(){
        return stackSize;
    }
    public void changeStackSize(int amount){
        this.stackSize += amount;

        if(stackSize == 0){
            entity.delete();
        }
        if(stackSize < 0) throw new RuntimeException("The stackSize of " + entity.getEntityName() + " became negative");
    }
    public Entity split(int amount){
        if(amount > stackSize) throw new RuntimeException("Splitting more than the stack size in " + entity.getEntityName());
        if(amount == stackSize) throw new RuntimeException("Splitting the same amount as the stack size in" + entity.getEntityName());

        Entity copiedEntity = entity.clone();
        copiedEntity.getComponent(Pickable.class).setStackSize(amount);
        this.changeStackSize(-amount);
        return copiedEntity;
    }

    @Override
    public EntityComponent clone() {
        return new Pickable(this);
    }

    public String getIcon() {
        return icon;
    }
}
