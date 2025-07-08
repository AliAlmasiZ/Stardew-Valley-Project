package com.ap.stardew.models.shop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.models.App;
import com.ap.stardew.models.entities.Entity;

import java.io.Serializable;

public class UpgradableShopProduct extends ShopProduct implements Serializable {

    String ingredientName;
    int count = 5;
    boolean isTrashCanAvailable = true;
    double trashCanModifier = 0.5;
    @JsonCreator
    public UpgradableShopProduct(@JsonProperty("name") String name,
                                 @JsonProperty("dailyLimit") int dailyLimit,
                                 @JsonProperty("price") int price,
                                 @JsonProperty("ingredient") String ingredient,
                                 @JsonProperty("count") int count
    ) {
        super(name, dailyLimit, price);
        this.ingredientName = ingredient;
        this.count = count;
        this.dailyLimit = 1;
    }

    @Override
    public Entity getEntity() {
        return App.entityRegistry.makeEntity(this.name);
    }

    @Override
    public int getWoodCost() {
        return 0;
    }

    @Override
    public int getStoneCost() {
        return 0;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void reset() {
        super.reset();
        this.isTrashCanAvailable = true;
    }

    public int getTrashCanPrice() {
        if(!isTrashCanAvailable)
            throw new RuntimeException("trashCan isn't available bro!");
        isTrashCanAvailable = false;
        return (int) (this.price * trashCanModifier);
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public int getIgredientCount() {
        return this.count;
    }
}
