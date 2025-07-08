package models.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.animal.Animal;
import models.animal.AnimalType;

import java.io.Serializable;

public class AnimalShopProduct extends ShopProduct implements Serializable {

    private String requiredBuildingName;

    public AnimalShopProduct(@JsonProperty("name") String name,
                             @JsonProperty("dailyLimit") int dailyLimit,
                             @JsonProperty("price") int price,
                             @JsonProperty("requiredBuildingName") String requiredBuildingName) {
        super(name, dailyLimit, price);
        this.requiredBuildingName = requiredBuildingName;
    }

    @Override
    public Animal getEntity() {
        return new Animal(AnimalType.getAnimalTypeByString(this.name));
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
        return this.getStock() != 0;
    }
}
