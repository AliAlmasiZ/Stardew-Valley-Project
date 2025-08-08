package com.ap.stardew.models.gameMap;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Game;
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
import java.security.SecureRandom;
import java.util.*;


public class WorldMap extends GameMap implements Serializable {
    private transient final Map<String, MapRegion> regions = new HashMap<>();
    private MapRegion[][] regionMap;
    private BiomeType[][] biomeMap;
    private transient Map<MapRegion, FarmDetails> farmsDetail = new HashMap<>();

//    public WorldMap(MapData data) {
//        super(data, Environment.OUTDOOR);
//
//        regionMap = data.getRegionMap();
////        if (data.getRegions() != null) {
////            this.regions.addAll(data.getRegions());
////        }
//        biomeMap = data.getBiomeMap();
//
//        App.getActiveGame().setMainMap(this);
//        App.getActiveGame().setActiveMap(this);
//
////        for(MapRegion r : regions){
////            farmsDetail.put(r, new FarmDetails());
////        }
//
//        for (MapData.MapLayerData<String>.ObjectData d : data.getBuildings()) {
//            if(d.getProperty("type") != null && d.getProperty("type").asString.equals("playerHouse")){
//                farmsDetail.get(regionMap[d.y][d.x]).cottage = d;
//
//            }else if(d.value != null && d.value.equals("Abandoned Greenhouse")){
//                farmsDetail.get(regionMap[d.y][d.x]).abandonedGreenhouse = d;
//            }else{
//                Entity building = App.entityRegistry.makeEntity(d.value);
//                EntityPlacementSystem.placeEntity(building, new Vec2(d.x, d.y));
//            }
//        }
//    }

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

    public BiomeType[][] getBiomeMap() {
        return biomeMap;
    }

    public MapRegion[][] getRegionMap() {
        return regionMap;
    }

    public Map<MapRegion, FarmDetails> getFarmsDetail() {
        return farmsDetail;
    }

    public void setFarmsDetail(Map<MapRegion, FarmDetails> farmsDetail) {
        this.farmsDetail = farmsDetail;
    }

    public void setRegionMap(MapRegion[][] regionMap) {
        this.regionMap = regionMap;
    }

    public void setBiomeMap(BiomeType[][] biomeMap) {
        this.biomeMap = biomeMap;
    }

    public void addRegion(MapRegion region){
        if(!regions.containsKey(region.getName())){
            regions.put(region.getName(), region);
        }
    }
    public MapRegion getRegion(String name){
        return regions.get(name);
    }
}
