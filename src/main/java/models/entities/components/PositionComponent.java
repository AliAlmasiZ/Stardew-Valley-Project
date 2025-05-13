package models.entities.components;

import models.Game;
import models.Position;
import models.Vec2;
import models.gameMap.GameMap;

public class PositionComponent extends EntityComponent{
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
        this.position.setX(position.getCol());
        this.position.setY(position.getRow());
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
}
