package models.shop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.App;
import models.entities.Entity;

public class BuildingShopProduct extends ShopProduct {
    private int woodCost;
    private int stoneCost;

    @JsonCreator
    public BuildingShopProduct(@JsonProperty("name") String name,
                               @JsonProperty("dailyLimit") int dailyLimit,
                               @JsonProperty("woodCost") int woodCost,
                               @JsonProperty("stoneCost") int stoneCost) {
        super(name, dailyLimit);
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
