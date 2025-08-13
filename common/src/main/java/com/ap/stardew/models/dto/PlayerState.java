package com.ap.stardew.models.dto;

import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.reaction.Reaction;
import com.badlogic.gdx.math.Vector2;

import java.io.Serial;
import java.io.Serializable;

public class PlayerState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public String username;
    public Vector2 position;
    public double energy;
    public Player.Action action;
    public Reaction reaction;
    public Entity actionItem;
    public float stateTime;
    public Vector2 lastDir;
//    public Map<Integer, Integer> inventory; //TODO

}
