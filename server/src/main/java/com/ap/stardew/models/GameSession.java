package com.ap.stardew.models;

import com.ap.stardew.models.player.Player;

import java.util.HashMap;
import java.util.Map;

public class GameSession {
    private final Game game;
    private final Map<String, Player> userPlayerMap = new HashMap<>();
    private int id;

    public GameSession(Game game) {
        this.game = game;
    }

    public void addUserToSession(String username, Player player){
        userPlayerMap.put(username, player);
    }

    public Game getGame() {
        return game;
    }

    public Map<String, Player> getUserPlayerMap() {
        return userPlayerMap;
    }
}
