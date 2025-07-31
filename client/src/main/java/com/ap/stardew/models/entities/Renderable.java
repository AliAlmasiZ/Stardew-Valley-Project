package com.ap.stardew.models.entities;

import com.ap.stardew.models.entities.components.EntityComponent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.views.old.inGame.Color;

import java.io.Serializable;

public class Renderable extends EntityComponent implements Serializable {
    @JsonProperty("char")
    protected char character;
    @JsonProperty("color")
    protected Color color;
    @JsonIgnore
    protected String spritePath;
    protected float timeLeftForStatue = 0.0f;

    public enum Statue {
        NORMAL,
        IDLE,
        RIGHT_WALKING,
        LEFT_WALKING,
        EATING,
        PET,
    }

    protected Statue currentStatue = Statue.NORMAL;


    public Renderable(char character, Color color) {
        this.character = character;
        this.color = color;
    }

    private Renderable(Renderable other) {
        this.character = other.character;
        this.color = other.color;
    }

    public Statue getCurrentStatue() {
        return currentStatue;
    }

    public Renderable() {
        this(' ', null);
    }

    public char getCharacter() {
        return character;
    }

    public void reduceTimeLeftForStatue(float time) {
        this.timeLeftForStatue -= time;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public EntityComponent clone() {
        return new Renderable(this);
    }

    public String getSpritePath() {
        return spritePath;
    }

    public void setSpritePath(String spritePath) {
        this.spritePath = spritePath;
    }


    public void setStatue(Statue statue, float duration) {
        currentStatue = statue;
        timeLeftForStatue = duration;
    }

    public void setCurrentStatue(Statue statue) {
        currentStatue = statue;
    }

    public float getTimeLeftForStatue() {
        return timeLeftForStatue;
    }

}
