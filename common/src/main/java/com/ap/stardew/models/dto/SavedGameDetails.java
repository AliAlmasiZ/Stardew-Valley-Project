package com.ap.stardew.models.dto;

import java.util.List;

public class SavedGameDetails {
    public String inGameDate;
    public List<String> players;
    public int gold;
    public String farm;
    public int gameId;

    public SavedGameDetails() {
    }

    public SavedGameDetails(String inGameDate, List<String> players, int gold, String farm, int gameId) {
        this.inGameDate = inGameDate;
        this.players = players;
        this.gold = gold;
        this.farm = farm;
        this.gameId = gameId;
    }
}
