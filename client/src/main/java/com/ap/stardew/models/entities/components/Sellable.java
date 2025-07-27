package com.ap.stardew.models.entities.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.models.enums.ProductQuality;

import java.io.Serializable;

public class Sellable extends EntityComponent implements Serializable {
    @JsonProperty("price")
    private int price;
    private ProductQuality productQuality;

    public Sellable(int price) {
        this.price = price;
    }
    private Sellable(Sellable other){
        this.price = other.price;
        this.productQuality = other.productQuality;
    }
    public Sellable(){
        this(0);
    }

    public int getPrice() {
        if(productQuality != null){
            return (int) (price * productQuality.getValue());
        }
        return price;
    }

    public int getBasePrice() {
        return this.price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public ProductQuality getProductQuality() {
        return productQuality;
    }

    public void setProductQuality(ProductQuality productQuality) {
        this.productQuality = productQuality;
    }

    @Override
    public EntityComponent clone() {
        return new Sellable(this);
    }

    @Override
    public boolean isTheSame(EntityComponent other) {
        if(!(other instanceof Sellable)) return false;

        Sellable otherSellable = (Sellable) other;
        if (this.productQuality != otherSellable.productQuality) return false;
        return this.price == otherSellable.price;
    }
}
