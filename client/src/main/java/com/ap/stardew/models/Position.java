package com.ap.stardew.models;

import com.ap.stardew.models.gameMap.GameMap;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class Position extends Vec2 implements Serializable {
    GameMap map;

    public Position(float x, float y, GameMap map){
        super(x, y);
        this.map = map;
    }
    public Position(float x, float y) {
        this(x, y, null);
    }
    public Position(int x, int y){
        this(x * 16, y * 16, null);
    }
    public Position(Vector2 vec){
        this(vec.x, vec.y, null);
    }
    public Position(Vector2 vec, GameMap map){
        this(vec.x, vec.y, map);
    }
    public Position cpy(){
        return new Position(super.cpy(), map);
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "<" + getCol() + ", " + getRow() + ">";
    }

    public Position changeByDirection(String direction){
        return switch (direction) {
            case "left" -> {
                add(-1, 0);
                yield this;
            }
            case "right" -> {
                add(1, 0);
                yield this;
            }
            case "up" -> {
                add(0, -1);
                yield this;
            }
            case "down" -> {
                add(0, 1);
                yield this;
            }
            case "upleft" -> {
                add(-1, -1);
                yield this;
            }
            case "upright" -> {
                add(1, -1);
                yield this;
            }
            case "downleft" -> {
                add(-1, 1);
                yield this;
            }
            case "downright" -> {
                add(1, 1);
                yield this;
            }
            default -> null;
        };
    }

}
