package com.ap.stardew.models.gameMap;

import com.ap.stardew.models.Position;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.EntityList;
import com.ap.stardew.models.entities.components.EntityComponent;
import com.ap.stardew.models.enums.TileType;

import java.io.Serializable;
import java.util.ArrayList;

public class GameMap implements Serializable {
    protected Tile[][] tiles;
    protected int width, height;
    protected Environment environment;
    protected final EntityList entities = new EntityList();
    protected Entity building = null;
    protected final String mapDataName;

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

    public GameMap(MapData data, Environment environment) {
        TileType[][] typeMap = data.getTypeMap();
        this.environment = environment;
        this.height = typeMap.length;
        this.width = typeMap[0].length;
        this.tiles = new Tile[height][width];
        this.mapDataName = data.name;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tiles[i][j] = new Tile(new Position(j, i), typeMap[i][j], this);
            }
        }
    }

    private void generateRandomElements(int min, int max) { //inclusive
        //TODO
    }

    public Tile[][] getTiles() {
        return tiles.clone();
    }

    public Tile getTileByPosition(Position position) {
        return getTileByPosition(position.getRow(), position.getCol());
    }

    public Tile getTileByPosition(int row, int col) {
        if (row >= tiles.length || row < 0 || col >= tiles[0].length || col < 0)
            return null;
        return tiles[row][col];
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
}
