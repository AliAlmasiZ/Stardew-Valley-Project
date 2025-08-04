package com.ap.stardew.models.entities.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.models.player.buff.Buff;

import java.io.Serializable;

public class Edible extends EntityComponent implements Serializable {
    @JsonProperty("energy")
    private int energy;
    @JsonProperty("buff")
    private Buff buff;

    @JsonCreator
    public Edible(@JsonProperty("energy") int energy,@JsonProperty("buff") Buff buff){
        this.buff = buff;
        this.energy = energy;
    }

    public Edible(int energy) {
        this.energy = energy;
    }

    public Edible() {
    }

    private Edible(Edible other){
        this.energy = other.energy;
    }

    public int getEnergy() {
        return energy;
    }
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    @Override
    public String toString() {
        return "Edible{" +
                "energy=" + energy +
                '}';
    }

    @Override
    public EntityComponent clone() {
        return new Edible(this);
    }

    @Override
    public boolean isTheSame(EntityComponent other) {
        if(!(other instanceof Edible otherEdible)) return false;

        return this.energy == otherEdible.energy;
    }

    public void setBuff() {
        if(buff == null)
            return;
        buff.setBuff();
    }
}
