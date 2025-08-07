package com.ap.stardew.models.building;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.entities.CollisionEvent;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Placeable;
import com.ap.stardew.models.entities.Renderable;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.Result;

import java.io.Serializable;

public class Door extends Entity implements Serializable {
    private Position destination;
    public Door() {
        super("DOOR");
        this.addComponent(new Renderable());
        addComponent(new Placeable(true));
    }
    public void setDestination(Position position){
        this.destination = position.cpy();
    }

    public Position getDestination() {
        return destination;
    }
}
