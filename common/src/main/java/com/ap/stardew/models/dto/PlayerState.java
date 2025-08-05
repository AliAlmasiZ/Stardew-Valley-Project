package com.ap.stardew.models.dto;

import com.ap.stardew.models.player.Player;
import com.badlogic.gdx.math.Vector2;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class PlayerState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public String username;
    public Vector2 position;
    public double energy;
    public Player.State state;

//    public Map<Integer, Integer> inventory; //TODO

}
