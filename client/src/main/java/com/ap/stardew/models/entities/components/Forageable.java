package com.ap.stardew.models.entities.components;

import com.ap.stardew.models.Date;

import java.io.Serializable;

public class Forageable extends EntityComponent implements Serializable {
    private boolean foraged = false;
    private Date dateAdded = null;

    public boolean isForaged() {
        return foraged;
    }

    public Date getDateAdded() {
        return dateAdded;
    }
    public Forageable(Forageable other){
        this.foraged = other.foraged;
        //TODO
        this.dateAdded = other.dateAdded;
    }

    public Forageable() {
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setForaged(boolean foraged) {
        this.foraged = foraged;
    }

    @Override
    public EntityComponent clone() {
        return new Forageable(this);
    }
}
