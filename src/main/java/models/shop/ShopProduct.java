package models.shop;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import models.entities.Entity;

import java.io.Serializable;

/*
public interface ShopProduct {

    int getStock();
    int getPrice();
    int getWoodCost();
    int getStoneCost();
    Entity getEntity();

    void addSold(int amount);

}
*/

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AnimalShopProduct.class, name = "Animal"),
        @JsonSubTypes.Type(value = BuildingShopProduct.class, name = "Building"),
        @JsonSubTypes.Type(value = OtherShopProduct.class, name = "Product"),
        @JsonSubTypes.Type(value = UpgradableShopProduct.class, name = "Upgrade")
})
abstract public class ShopProduct implements Serializable {
    protected String name;
    protected int dailyLimit;
    protected int todaySold;
    protected int price;
    /*
     * costs should be like this:
     *   "price" : 100,
     *   "wood" : 200,
     *   "stone" : 100
     *
     *
     *
     *
     * */

    private ShopProduct() {

    }

    public ShopProduct(String name, int dailyLimit, int price) {
        this.price = price;
        this.name = name;
        this.dailyLimit = dailyLimit;
        this.todaySold = 0;
    }

    public int getStock() {
        if (dailyLimit < 0) {
            return -1;
        }
        return dailyLimit - todaySold;
    }

    public void addSold(int amount) {
        todaySold += amount;
    }

    public int getPrice() {
        return this.price;
    }

    abstract public Entity getEntity();

    abstract public int getWoodCost();

    abstract public int getStoneCost();

    abstract public boolean isAvailable();

    public void reset() {
        this.todaySold = 0;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if(getStock() == 0) {
            return name + " (Sold out)" ;
        }
        if(getStock() == -1)
            return name;
        return name + " x(" + this.getStock() + ")";
    }

}