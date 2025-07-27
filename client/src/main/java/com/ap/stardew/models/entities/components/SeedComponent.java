package com.ap.stardew.models.entities.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.models.App;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.enums.Season;

import java.io.Serializable;
import java.util.ArrayList;

public class SeedComponent extends EntityComponent implements Serializable {
    @JsonProperty("growingPlants")
    private ArrayList<String> growingPlants = new ArrayList<>();
    @JsonProperty("canBeForaging")
    private boolean canBeForaging;
    @JsonProperty("foragingSeasons")
    private ArrayList<Season> foragingSeasons = new ArrayList<>();

    public SeedComponent() {
    }

    public SeedComponent(SeedComponent other) {
        this.growingPlants.addAll(other.growingPlants);
    }

    public Entity getGrowingPlant(){
        int random = (int) (growingPlants.size() * Math.random());
        return App.entityRegistry.makeEntity(growingPlants.get(random));
    }

    @Override
    public EntityComponent clone() {
        return new SeedComponent(this);
    }
}
