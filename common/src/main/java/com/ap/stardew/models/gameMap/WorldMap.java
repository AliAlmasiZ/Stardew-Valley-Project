package com.ap.stardew.models.gameMap;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.Vec2;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.entities.systems.ForageSpawnSystem;
import com.ap.stardew.models.enums.TileType;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;

import java.io.Serializable;
import java.util.*;


public class WorldMap extends GameMap implements Serializable {
    private final Map<String, MapRegion> regions = new HashMap<>();
    private MapRegion[][] regionMap;
    private BiomeType[][] biomeMap;
    private transient final Map<MapRegion, FarmDetails> farmsDetail = new HashMap<>();

    public WorldMap(MapData data) {
        super(data, Environment.OUTDOOR);

        regionMap = data.getRegionMap();
//        if (data.getRegions() != null) {
//            this.regions.addAll(data.getRegions());
//        }
        biomeMap = data.getBiomeMap();

        App.getActiveGame().setMainMap(this);
        App.getActiveGame().setActiveMap(this);

//        for(MapRegion r : regions){
//            farmsDetail.put(r, new FarmDetails());
//        }

        for (MapData.MapLayerData<String>.ObjectData d : data.getBuildings()) {
            if(d.getProperty("type") != null && d.getProperty("type").asString.equals("playerHouse")){
                farmsDetail.get(regionMap[d.y][d.x]).cottage = d;

            }else if(d.value != null && d.value.equals("Abandoned Greenhouse")){
                farmsDetail.get(regionMap[d.y][d.x]).abandonedGreenhouse = d;
            }else{
                Entity building = App.entityRegistry.makeEntity(d.value);
                EntityPlacementSystem.placeEntity(building, new Vec2(d.x, d.y));
            }
        }
    }
    public WorldMap(String path){
        super(path);

        TiledMapTileLayer regionLayer = (TiledMapTileLayer) mapData.getLayers().get("Regions");
        TiledMapTileLayer biomesLayer = (TiledMapTileLayer) mapData.getLayers().get("Biomes");

        TiledMapTileSet regions = mapData.getTileSets().getTileSet("Regions");

        for (TiledMapTile region : regions) {
            if(region.getProperties().get("type", String.class) == null) continue;
            this.regions.put(region.getProperties().get("type", String.class),
                new MapRegion(region.getProperties().get("type", String.class),
                region.getProperties().get("playerFarm") != null));
        }


        if(regionLayer != null) {
            height = regionLayer.getHeight();
            width = regionLayer.getWidth();

            regionMap = new MapRegion[height][width];

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    TiledMapTileLayer.Cell cell = regionLayer.getCell(j, i);

                    if(cell != null){
                        MapRegion region = this.regions.get(cell.getTile().getProperties().get("type", String.class));
                        region.addTile(tiles[i][j]);

                        regionMap[i][j] = region;
                    }
                }
            }
        }
        if(biomesLayer != null){
            height = biomesLayer.getHeight();
            width = biomesLayer.getWidth();

            biomeMap = new BiomeType[height][width];

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    TiledMapTileLayer.Cell cell = biomesLayer.getCell(j, i);

                    if(cell != null){
                        biomeMap[i][j] = BiomeType.valueOf(cell.getTile().getProperties().get("type", String.class));
                    }
                }
            }
        }
    }

    public MapRegion getRegion(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y > height) {
            return null;
        }
        return regionMap[y][x];
    }

    public MapRegion getRegion(Tile tile) {
        return getRegion(tile.getPosition().getCol(), tile.getPosition().getRow());
    }

    public Collection<MapRegion> getRegions() {
        return regions.values();
    }

    public void initRandomElements() {
//        SecureRandom random = new SecureRandom();
//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                if (Math.random() > 0.8) {
//                    BiomeType biome = biomeMap[i][j];
//                    if (biome != null && tiles[i][j].getContent() == null && tiles[i][j].getType() != TileType.WALL) {
//                        BiomeType.Spawnable spawnable = biome.spawnData.get(biome.spawnData.size() - 1);
//
//                        for (BiomeType.Spawnable s : biome.spawnData) {
//                            if (Math.random() > s.weight / biome.totalWeight) {
//                                spawnable = s;
//                            }
//                        }
//
//                        Entity plant = App.entityRegistry.makeEntity(spawnable.entity );
//                        EntityPlacementSystem.placeOnTile(plant, tiles[i][j]);
//                        Game game = App.getActiveGame();
//                        if (plant.getComponent(Growable.class) != null) {
//                            game.getPlantedEntities().add(plant);
//                        }
//
//                    }
//                }
//            }
//        }
        for(int i = 0 ; i < 20 ; i++){
            ForageSpawnSystem.updatePerDay();
        }
    }

    public BiomeType[][] getBiomeMap() {
        return biomeMap;
    }

    public MapRegion[][] getRegionMap() {
        return regionMap;
    }

    public Map<MapRegion, FarmDetails> getFarmsDetail() {
        return farmsDetail;
    }
}
