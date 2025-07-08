package com.ap.stardew.models.building;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.entities.CollisionEvent;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Placeable;
import com.ap.stardew.models.entities.components.Renderable;
import com.ap.stardew.records.Result;
import com.ap.stardew.views.old.inGame.Color;

import java.io.Serializable;

public class Door extends Entity implements Serializable {
    private Position destination;
    public Door() {
        super("DOOR");
        this.addComponent(new Renderable('D', new Color(78, 52, 46)));
        this.addComponent( new Placeable(true, new CollisionEvent() {
            @Override
            public Result onEnter() {
                App.getActiveGame().setActiveMap(Door.this.destination.getMap());
                App.getActiveGame().getCurrentPlayer().setPosition(Door.this.destination);
                return new Result(false, "");
            }
        }));
    }
    public void setDestination(Position position){
        this.destination = position;

    }
}
