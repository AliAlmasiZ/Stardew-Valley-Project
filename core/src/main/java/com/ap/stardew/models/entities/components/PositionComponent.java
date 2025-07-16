package com.ap.stardew.models.entities.components;

import com.ap.stardew.models.Position;
import com.ap.stardew.models.Vec2;
import com.ap.stardew.models.gameMap.GameMap;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class PositionComponent extends EntityComponent implements Serializable {
    private Position position;


    public PositionComponent(double x, double y, GameMap map){
        position = new Position(x, y, map);
    }
    public PositionComponent(double x, double y){
        position = new Position(x, y);
    }

    public PositionComponent(Vec2 position, GameMap map) {
        this(position.getX(), position.getY(), map);
    }

    public PositionComponent(Vec2 position) {
        this(position.getX(), position.getY());
    }

    public PositionComponent(Position position) {
        this(position.getCol(), position.getRow());
    }

    public PositionComponent(PositionComponent other){
        this.position = new Position(other.getX(), other.getY(), other.getMap());
    }

    public PositionComponent() {
    }

    public Position get(){
        return position;
    }

    public double getX(){
        return position.getX();
    }

    public double getY(){
        return position.getY();
    }

    public int getCol(){
        return position.getCol();
    }

    public int getRow(){
        return position.getRow();
    }

    public Position setPosition(Vec2 position) {
        this.position.setX(position.getX());
        this.position.setY(position.getY());
        return this.position;
    }

    public Position setPosition(Position position){
        this.position.setX(position.getX());
        this.position.setY(position.getY());
        return this.position;
    }

    public Position setPosition(int x, int y){
        this.position.setX(x);
        this.position.setY(y);
        return this.position;
    }

    public Position setPosition(double x, double y){
        this.position.setX(x);
        this.position.setY(y);
        return this.position;
    }

    public Position setMap(GameMap map){
        position.setMap(map);
        return position;
    }
    public GameMap getMap(){
        return position.getMap();
    }

    @Override
    public EntityComponent clone() {
        return new PositionComponent(this);
    }

    public void move(Vector2 direction, float length) {
        Vector2 dir = new Vector2(direction).nor().scl(length);
        this.position.add(dir.x, dir.y);
    }
}
