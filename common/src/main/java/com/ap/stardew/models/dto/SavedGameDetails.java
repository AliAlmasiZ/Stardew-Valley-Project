package com.ap.stardew.models.dto;

import java.util.List;
import java.util.Map;

public class SavedGameDetails {
    public String inGameDate;
    public List<String> players;
    public Map<String, Integer> gold;
    public Map<String, String> farms;
    public int gameId;

    public SavedGameDetails() {
    }
}
