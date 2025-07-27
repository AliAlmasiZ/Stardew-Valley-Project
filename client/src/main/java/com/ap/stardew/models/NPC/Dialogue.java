package com.ap.stardew.models.NPC;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.models.enums.Season;
import com.ap.stardew.models.enums.Weather;

import java.io.Serializable;

public class Dialogue implements Serializable {
    private final String dialogue;
    private final String npcName;

    private final Season season;
    private final int friendshipLevel;
    private final Weather weather;
    private final boolean isDay;

    @JsonCreator
    public Dialogue(
            @JsonProperty("dialogue") String dialogue,
            @JsonProperty("npcName") String npcName,
            @JsonProperty("season") Season season,
            @JsonProperty("friendshipLevel") int friendshipLevel,
            @JsonProperty("weather") Weather weather,
            @JsonProperty("isDay") boolean isDay
    ) {
        this.dialogue = dialogue;
        this.npcName = npcName;
        this.season = season;
        this.friendshipLevel = friendshipLevel;
        this.weather = weather;
        this.isDay = isDay;
    }

    // Getters
    public String getDialogue() {
        return dialogue;
    }

    public String getNpcName() {
        return npcName;
    }

    public Season getSeason() {
        return season;
    }

    public int getFriendshipLevel() {
        return friendshipLevel;
    }

    public Weather getWeather() {
        return weather;
    }

    public boolean isDay() {
        return isDay;
    }

    public boolean checkConditions(Season season, int friendLevel, Weather weather, boolean isDay) {
        if (this.season != season) {
            return false;
        }

        if (this.friendshipLevel != friendLevel) {
            return false;
        }

        if (this.weather != weather) {
            return false;
        }

        if (this.isDay != isDay) {
            return false;
        }

        return true;
    }
}
