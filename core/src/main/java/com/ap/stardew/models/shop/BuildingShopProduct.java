package com.ap.stardew.models.shop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.models.App;
import com.ap.stardew.models.entities.Entity;

import java.io.Serializable;

public class BuildingShopProduct extends ShopProduct implements Serializable {
    private int woodCost;
    private int stoneCost;

    @JsonCreator
    public BuildingShopProduct(@JsonProperty("name") String name,
                               @JsonProperty("dailyLimit") int dailyLimit,
                               @JsonProperty("price") int price,
                               @JsonProperty("woodCost") int woodCost,
                               @JsonProperty("stoneCost") int stoneCost) {
        super(name, dailyLimit, price);
        this.woodCost = woodCost;
        this.stoneCost = stoneCost;
    }

    @Override
    public Entity getEntity() {
        return App.buildingRegistry.makeEntity(this.name);
    }

    public int getStoneCost() {
        return stoneCost;
    }

    public int getWoodCost() {
        return woodCost;
    }

    @Override
    public boolean isAvailable() {
        return this.getStock() != 0;
    }
}
