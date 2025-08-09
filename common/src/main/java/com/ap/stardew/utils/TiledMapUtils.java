package com.ap.stardew.utils;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.Vec2;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Placeable;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.enums.TileType;
import com.ap.stardew.models.gameMap.*;
import org.tiledreader.*;

import java.util.HashMap;
import java.util.Map;

public class TiledMapUtils {
    private static final TiledReader tiledReader = new FileSystemTiledReader();
    public static TiledLayer getLayer(TiledMap map, String name){
        for (TiledLayer layer : map.getTopLevelLayers()) {
            if(layer.getName().equals(name)) return layer;
        }
        return null;
    }
    public static TiledTileset getTileSet(TiledMap map, String name){
        for (TiledTileset layer : map.getTilesets()) {
            if(layer.getName().equals(name)) return layer;
        }
        return null;
    }
    public static <T> T getProperty(TiledObject object, String name, Class<T> tClass){
        Object property = object.getProperty(name);

        return (T) property;
    }
    public static <T> T getProperty(TiledTile object, String name, Class<T> tClass){
        Object property = object.getProperty(name);

        return (T) property;
    }
    private static TiledMap loadMapFromFile(String path, GameMap gameMap){
        TiledMap map = tiledReader.getMap(path);
        gameMap.setMapDataPath(path);

        ArrayTileLayer backLayer        = (ArrayTileLayer) TiledMapUtils.getLayer(map, "Back");
        ArrayTileLayer buildingsLayer   = (ArrayTileLayer) TiledMapUtils.getLayer(map, "Buildings");

        int width = map.getWidth();
        int height = map.getHeight();

        gameMap.setHeight(height);
        gameMap.setWidth(width);

        Tile[][] tiles = new Tile[height][width];

        if(backLayer != null){
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    TiledTile tileData = backLayer.getTile(j, height - i - 1);

                    tiles[i][j] = null;
                    if(tileData != null){
                        boolean diggable = tileData.getProperties().get("Diggable") != null;
                        boolean isWater = tileData.getProperties().get("Water")  != null;

                        Tile tile = new Tile(new Position(j, i), TileType.GRASS, gameMap);

                        if(diggable){
                            tile.setType(TileType.DIRT);
                        }else if(isWater){
                            tile.setType(TileType.WATER);
                        }
                        tiles[i][j] = tile;

                    }
                    if(buildingsLayer!=null){
                        TiledTile buildingCell = buildingsLayer.getTile(j, height - i - 1);
                        if(buildingCell != null){
                            boolean isPassable = buildingCell.getProperties().get("Passable") != null;

                            if(tiles[i][j] == null){
                                tiles[i][j] = new Tile(new Position(j, i), TileType.GRASS, gameMap);
                            }
                            if(isPassable){
                                tiles[i][j].setWalkable(true);
                            }else {
                                tiles[i][j].setWalkable(false);
                            }
                        }
                    }
                }
            }
        }
        gameMap.setTiles(tiles);

        TiledObjectLayer objectsLayer = (TiledObjectLayer) TiledMapUtils.getLayer(map, "Objects");

        if(objectsLayer != null){
            for (TiledObject object : objectsLayer.getObjects()) {
                if(object.getName().equals("Fridge")){
                    Entity fridge = App.entityRegistry.makeEntity("fridge");
                    EntityPlacementSystem.placeEntity(fridge, new Vec2(object.getX(),
                        height * 16 - object.getY() - 16), gameMap);
                }else if(object.getName().equals("Shop")){
                    Entity shopCounter = new Entity("shopCounter");
                    shopCounter.addComponent(new Placeable(false));
                    EntityPlacementSystem.placeEntity(shopCounter, new Vec2(object.getX(),
                        height * 16 - object.getY() - 16), gameMap);
                }
            }
        }

        return map;
    }
    public static GameMap loadMapFromFile(String path){
        GameMap gameMap = new GameMap();
        loadMapFromFile(path, gameMap);

        return gameMap;
    }
    public static WorldMap loadWorldMapFromFile(String path){
        WorldMap worldMap = new WorldMap();
        TiledMap mapData = loadMapFromFile(path, worldMap);

        HashTileLayer regionLayer = (HashTileLayer) getLayer(mapData, "Regions");
        HashTileLayer biomesLayer = (HashTileLayer) getLayer(mapData, "Biomes");

        TiledTileset regions = getTileSet(mapData, "Regions");

        if(regions == null){
            throw new RuntimeException("world map should have a regions tileset. (" + path + ")");
        }


        for (TiledTile region : regions.getTiles()) {
            if(region.getType() == null) continue;
            worldMap.addRegion(new MapRegion(region.getType(),
                    region.getProperties().get("playerFarm") != null));
        }

        int height = worldMap.getHeight();
        int width = worldMap.getWidth();

        Tile[][] tiles = worldMap.getTiles();

        if(regionLayer != null) {
            MapRegion[][] regionMap = new MapRegion[height][width];
            worldMap.setRegionMap(regionMap);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    TiledTile tile = regionLayer.getTile(j, height - i - 1);

                    if(tile != null){
                        MapRegion region = worldMap.getRegion(tile.getType());
                        region.addTile(tiles[i][j]);

                        regionMap[i][j] = region;
                    }
                }
            }
        }
        if(biomesLayer != null){
            BiomeType[][] biomeMap = new BiomeType[height][width];
            worldMap.setBiomeMap(biomeMap);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    TiledTile tile = biomesLayer.getTile(j, height - i - 1);

                    if(tile != null){
                        biomeMap[i][j] = BiomeType.valueOf(tile.getType());
                    }
                }
            }
        }

        Map<MapRegion, FarmDetails> detailsMap = new HashMap<>();

        for (MapRegion region : worldMap.getRegions()) {
            detailsMap.put(region, new FarmDetails());
        }

        TiledObjectLayer objectsLayer = (TiledObjectLayer) TiledMapUtils.getLayer(mapData, "Objects");

        if(objectsLayer != null){
            for (TiledObject object : objectsLayer.getObjects()) {
                if(object.getName().equals("Building")){
                    Entity building = App.entityRegistry.makeEntity(TiledMapUtils.getProperty(object, "building", String.class));
                    EntityPlacementSystem.placeEntity(building, new Vec2(object.getX(),
                        height * 16 - object.getY() - object.getHeight()), worldMap);

                    if(object.getProperty("playerHouse") != null){
                        detailsMap.get(worldMap.getRegion((int)(object.getX() / 16f),
                                (int)((height * 16 - object.getY()) / 16f))).farmHouse = building;
                    }
                }
            }
        }

        worldMap.setFarmsDetail(detailsMap);

        return worldMap;
    }

    public static WorldMap getRegionData(String path){
        TiledMap mapData = tiledReader.getMap(path);

        WorldMap worldMap = new WorldMap();


        TiledTileset regions = getTileSet(mapData, "Regions");

        for (TiledTile region : regions.getTiles()) {
            if(region.getType() == null) continue;
            worldMap.addRegion(new MapRegion(region.getType(),
                region.getProperties().get("playerFarm") != null));
        }

        int height = mapData.getHeight();
        int width = mapData.getWidth();

        worldMap.setWidth(width);
        worldMap.setHeight(height);

        HashTileLayer regionLayer = (HashTileLayer) getLayer(mapData, "Regions");

        if(regionLayer != null) {
            MapRegion[][] regionMap = new MapRegion[height][width];
            worldMap.setRegionMap(regionMap);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    TiledTile tile = regionLayer.getTile(j, height - i - 1);

                    if(tile != null){
                        MapRegion region = worldMap.getRegion(tile.getType());
                        region.addTile(j * 16, i * 16);

                        regionMap[i][j] = region;
                    }
                }
            }
        }

        return worldMap;
    }
}
