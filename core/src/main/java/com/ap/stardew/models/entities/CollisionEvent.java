package com.ap.stardew.models.entities;

import com.ap.stardew.models.player.Player;
import com.ap.stardew.records.Result;

import java.io.Serializable;

public abstract class CollisionEvent implements Serializable {
    public Result onCollision(Player player, Entity entity){
        return new Result(false, "");
    }
    public Result onEnter(){
        return new Result(false, "");
    };
    public Result onExit(){
        return new Result(false, "");
    };
}
