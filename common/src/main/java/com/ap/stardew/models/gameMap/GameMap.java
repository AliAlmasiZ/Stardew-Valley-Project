package com.ap.stardew.models.gameMap;

import com.ap.stardew.models.Vec2;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.EntityList;
import com.ap.stardew.models.entities.components.EntityComponent;
import org.tiledreader.*;
import com.badlogic.gdx.maps.tiled.TiledMap;

import java.io.Serializable;
import java.util.ArrayList;

public class GameMap implements Serializable {
    protected Tile[][] tiles;
    protected TiledMap mapData;
    protected org.tiledreader.TiledMap rawMapData;
    protected String mapDataPath;
    protected int width, height;
    protected Environment environment;
    protected final EntityList entities = new EntityList();
    protected Entity building = null;

    public Entity getBuilding() {
        return building;
    }

    public void setBuilding(Entity building) {
        this.building = building;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {
        if (entities.contains(entity))
            throw new RuntimeException("you fucked up somewhere and now the entity " + entity.getEntityName() + " is getting " +
                    "added twice int the map.");
        this.entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        if (!entities.contains(entity))
            throw new RuntimeException("you fucked up somewhere and now the entity " + entity.getEntityName() + " is getting " +
                    "removed from the map, but it doesnt exist on the map");
        this.entities.remove(entity);
    }

    public ArrayList<Entity> getEntitiesWithComponent(Class<? extends EntityComponent> clazz) {
        ArrayList<Entity> out = new ArrayList<>();
        for (Entity e : entities) {
            if (e.getComponent(clazz) != null) out.add(e);
        }
        return out;
    }

    public GameMap(){

    }

    public Tile[][] getTiles() {
        return tiles.clone();
    }

    public Tile getTileByPosition(Vec2 position) {
        return getTileByPosition(position.getRow(), position.getCol());
    }

    public Tile getTileByPosition(int row, int col) {
        if (row >= tiles.length || row < 0 || col >= tiles[0].length || col < 0)
            return null;
        return tiles[row][col];
    }
    public Tile getTileByPosition(float row, float col) {
        return getTileByPosition(Math.round(row), Math.round(col));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public <T extends EntityComponent> ArrayList<T> getComponentsOfType(Class<T> clazz){
        return entities.getComponentsOfType(clazz);
    }

    public TiledMap getMapData() {
        return mapData;
    }

    public String getMapDataPath() {
        return mapDataPath;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public void setMapData(TiledMap mapData) {
        this.mapData = mapData;
    }

    public void setMapDataPath(String mapDataPath) {
        this.mapDataPath = mapDataPath;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
