package com.ap.stardew.models.gameMap;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Position;

import com.ap.stardew.models.Vec2;
import com.ap.stardew.models.building.Door;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.EntityList;
import com.ap.stardew.models.entities.components.EntityComponent;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.enums.TileType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;
import java.util.ArrayList;

public class GameMap implements Serializable {
    private static final TmxMapLoader mapLoader = new TmxMapLoader();
    protected Tile[][] tiles;
    protected TiledMap mapData;
    protected int width, height;
    protected Environment environment;
    protected final EntityList entities = new EntityList();
    protected Entity building = null;
    protected String mapDataName;


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

    public GameMap(String path){
        mapData = mapLoader.load(path);

        TiledMapTileLayer backLayer = (TiledMapTileLayer) mapData.getLayers().get("Back");
        TiledMapTileLayer buildingsLayer = (TiledMapTileLayer) mapData.getLayers().get("Buildings");


        if(backLayer != null){
            height = backLayer.getHeight();
            width = backLayer.getWidth();

            tiles = new Tile[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    TiledMapTileLayer.Cell cell = backLayer.getCell(j, i);

                    tiles[i][j] = null;
                    if(cell != null){
                        TiledMapTile tileData = cell.getTile();

                        boolean diggable = tileData.getProperties().get("Diggable") != null;
                        boolean isWater = tileData.getProperties().get("Water")  != null;

                        Tile tile = new Tile(new Position(j, i), TileType.GRASS, this);

                        if(diggable){
                            tile.setType(TileType.DIRT);
                        }else if(isWater){
                            tile.setType(TileType.WATER);
                        }
                        tiles[i][j] = tile;
                    }
                    if(buildingsLayer!=null){
                        TiledMapTileLayer.Cell buildingCell = buildingsLayer.getCell(j, i);
                        if(buildingCell != null){
                            tiles[i][j] = new Tile(new Position(j, i), TileType.GRASS, this);
                            tiles[i][j].setWalkable(false);
                        }
                    }

                }
            }
        }

        MapLayer objectsLayer = mapData.getLayers().get("Objects");
        if(objectsLayer != null){
            for (MapObject object : objectsLayer.getObjects()) {
                if(object.getName().equals("Building")){
                    Entity building = App.entityRegistry.makeEntity(object.getProperties().get("building", String.class));
                    EntityPlacementSystem.placeEntity(building, new Vec2(object.getProperties().get("x", Float.class),
                        object.getProperties().get("y", Float.class)), this);
                }
            }
        }
    }

    private void generateRandomElements(int min, int max) { //inclusive
        //TODO
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
}
