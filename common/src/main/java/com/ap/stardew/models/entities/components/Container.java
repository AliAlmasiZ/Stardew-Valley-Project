package com.ap.stardew.models.entities.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Container extends EntityComponent implements Serializable {

    private int maxCharge;
    private int charge;


    @JsonCreator
    public Container(@JsonProperty("maxCharge") int charge) {
        this.maxCharge = charge;
        this.charge = charge;
    }

    Container(Container other) {
        this.maxCharge = other.maxCharge;
        this.charge = this.maxCharge;

    }

    public int getCharge() {
        return charge;
    }

    public void decreaseCharge(int charge) {
        this.charge -= charge;
        if(this.charge < 0)
            this.charge = 0;
    }
    public void decreaseCharge() {
        decreaseCharge(1);
    }

    public void fillContainer() {
        this.charge = this.maxCharge;
    }

    public void upgrade() {
        //TODO ???
    }

    @Override
    public EntityComponent clone() {
        return new Container(this);
    }
}
