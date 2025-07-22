package com.ap.stardew.models.entities.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    protected Animation<Sprite> petSprites;
    protected Animation<Sprite> walkingSprites;
    protected Animation<Sprite> eatingSprites;
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

    public Sprite getRenderingSprite(float deltaTime) {
        switch (currentStatue) {
            case NORMAL -> {
                return sprite;
            }
            case LEFT_WALKING -> {
                timeLeftForStatue -= deltaTime;
                if (timeLeftForStatue <= 0.0f) {
                    currentStatue = Statue.NORMAL;
                }

                Sprite result = new Sprite(walkingSprites.getKeyFrame(2000000 - timeLeftForStatue));
                result.flip(true, false);
                return result;
            }
            case RIGHT_WALKING -> {
                timeLeftForStatue -= deltaTime;
                if (timeLeftForStatue <= 0.0f) {
                    currentStatue = Statue.NORMAL;
                }
                return walkingSprites.getKeyFrame(2000000 - timeLeftForStatue);
            }
            case EATING -> {
                timeLeftForStatue -= deltaTime;
                if (timeLeftForStatue <= 0.0f) {
                    currentStatue = Statue.NORMAL;
                }
                return eatingSprites.getKeyFrame(2000000 - timeLeftForStatue);
            }
            case PET -> {
                timeLeftForStatue -= deltaTime;
                if (timeLeftForStatue <= 0.0f) {
                    currentStatue = Statue.NORMAL;
                }
                return petSprites.getKeyFrame(200 - timeLeftForStatue);
            }
        }

        return sprite;
    }

    public void setWalkingSprites(Texture idleImage, int number) {
        walkingSprites = new Animation<>(0.5f, getSplitSprites(idleImage, number));
        walkingSprites.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void setEatingSprites(Texture idleImage, int number) {
        eatingSprites = new Animation<>(0.2f, getSplitSprites(idleImage, number));
        eatingSprites.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }

    public void setPetSprites(Texture idleImage, int number) {
        petSprites = new Animation<>(0.05f, getSplitSprites(idleImage, number));
        petSprites.setPlayMode(Animation.PlayMode.LOOP_RANDOM);
    }

    public void setStatue(Statue statue, float duration) {
        currentStatue = statue;
        timeLeftForStatue = duration;
    }


    private Sprite[] getSplitSprites( Texture texture, int number) {
        int tileWidth = texture.getWidth() / number;
        int tileHeight = texture.getHeight();
        Sprite[] sprites = new Sprite[number];

        TextureRegion[][] tiles = TextureRegion.split(texture, tileWidth, tileHeight);

        for (int i = 0; i < number; i++) {
            sprites[i] = new Sprite(tiles[0][i]);
        }
        return sprites;
    }
}
