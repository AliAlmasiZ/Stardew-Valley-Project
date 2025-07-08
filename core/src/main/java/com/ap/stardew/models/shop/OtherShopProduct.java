package com.ap.stardew.models.shop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.models.App;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.enums.Season;

import java.io.Serializable;

public class OtherShopProduct extends ShopProduct implements Serializable {
    private Season season;

    @JsonCreator
    public OtherShopProduct(@JsonProperty("name") String name,
                            @JsonProperty("dailyLimit") int dailyLimit,
                            @JsonProperty("price") int price,
                            @JsonProperty("season") Season season) {
        super(name, dailyLimit, price);
        this.season = season;
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

    public boolean isInSeason(Season season) {
        if(season == null || this.season == null)
            return true;
        return this.season.equals(season);
    }

    @Override
    public boolean isAvailable() {
        return this.getStock() != 0 && (season == null || App.getActiveGame().getDate().getSeason().equals(season));
    }

    @Override
    public String toString() {
        if(getStock() == 0 || season == null)
            return super.toString();
        return super.toString() + " available in " + season.toString();
    }


}
