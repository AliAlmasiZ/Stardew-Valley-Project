package com.ap.stardew.models.entities.components;

import com.badlogic.gdx.graphics.g2d.Sprite;
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
    protected Sprite sprite;


    public Renderable(char character, Color color) {
        this.character = character;
        this.color = color;
    }
    private Renderable(Renderable other){
        this.character = other.character;
        this.color = other.color;
    }
    public Renderable(){
        this(' ', null);
    }

    public char getCharacter() {
        return character;
    }
    public Color getColor() {
        return color;
    }

    @Override
    public EntityComponent clone() {
        return new Renderable(this);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
}
