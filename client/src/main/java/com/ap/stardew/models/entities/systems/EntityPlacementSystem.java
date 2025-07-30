package com.ap.stardew.models.entities.systems;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Position;

import com.ap.stardew.models.Vec2;
import com.ap.stardew.models.building.Door;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.InteriorComponent;
import com.ap.stardew.models.entities.components.Pickable;
import com.ap.stardew.models.entities.components.Placeable;
import com.ap.stardew.models.entities.components.PositionComponent;
import com.ap.stardew.models.enums.TileType;
import com.ap.stardew.models.gameMap.Environment;
import com.ap.stardew.models.gameMap.GameMap;
import com.ap.stardew.models.gameMap.MapData;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.records.Result;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntityPlacementSystem {
    private static final TmxMapLoader mapLoader = new TmxMapLoader();

    public static Result placeOnTile(Entity entity, Tile tile){
        Entity tileEntity = tile.getContent();
        if(tileEntity != null){
            return new Result(false, "tile is full");
        }

        if(entity.getComponent(Placeable.class) == null) return placeOnMap(entity, tile.getPosition(), tile.getMap());

        tile.setContent(entity);
        tile.getMap().addEntity(entity);

        if(entity.getComponent(PositionComponent.class) == null){
            entity.addComponent(new PositionComponent(0, 0));
        }
        entity.getComponent(PositionComponent.class).setPosition(tile.getPosition()).setMap(tile.getMap());
        return new Result(true, "placed");
    }
    public static Result emptyTile(Tile tile){
        ArrayList<Pickable> pickables = tile.getMap().getComponentsOfType(Pickable.class);
        //TODO o(n ^ 123123123)
        for(int z = 0; z < pickables.size(); z++){
            Pickable pickable = pickables.get(z);
            if(pickable.getEntity().getComponent(PositionComponent.class).get().dst(tile.getPosition()) < 0.1){
                pickable.getEntity().delete();
            }
        }
        Entity tileEntity = tile.getContent();
        if(tileEntity != null){
            tileEntity.removeObserveer(tile);
            tile.getMap().removeEntity(tileEntity);
            tileEntity.removeComponent(PositionComponent.class);
        }
        tile.setContent(null);
        return new Result(true, "");
    }
    public static Result placeOnMap(Entity entity, Position position, GameMap map){
        map.addEntity(entity);

        if(entity.getComponent(PositionComponent.class) == null){
            entity.addComponent(new PositionComponent(0, 0));
        }
        if(entity.getComponent(PositionComponent.class).getMap() != null){
            entity.getComponent(PositionComponent.class).getMap().removeEntity(entity);
        }
        entity.getComponent(PositionComponent.class).setPosition(position).setMap(map);

        return new Result(true, "");
    }

    public static Result placeEntity(Entity entity, Vec2 position, GameMap map) {
        Placeable placeable = entity.getComponent(Placeable.class);
        if(placeable == null)
            return new Result(false, "This entity isn't placeable");

        InteriorComponent interior = entity.getComponent(InteriorComponent.class);
        Result result = placeExterior(entity, position, map);
        if(!result.isSuccessful())
            return result;

        if(interior != null){
            buildBuilding(entity, position, map);
        }

        return new Result(true, "placed");
    }
    public static Result placeEntity(Entity entity, Vec2 position) {
        return placeEntity(entity, position, App.getActiveGame().getActiveMap());
    }

    private static Result buildBuilding(Entity building, Vec2 position, GameMap map){
        InteriorComponent interiorComponent = building.getComponent(InteriorComponent.class);
        Placeable placeable = building.getComponent(Placeable.class);

        TiledMap exteriorData =
            mapLoader.load("./Content(unpacked)/Maps/" + building.getComponent(Placeable.class).getExteriorName() + ".tmx");
        TiledMap interiorData =
            mapLoader.load("./Content(unpacked)/Maps/" + building.getComponent(InteriorComponent.class).getInteriorName() + ".tmx");

        interiorComponent.
            setInteriorMap(
                new GameMap(
                    "./Content(unpacked)/Maps/" + building.getComponent(InteriorComponent.class).getInteriorName() + ".tmx"
                )
            );

//        if(entities != null){
//            for (MapObject entity : entities) {
//                String entityName = entity.getProperties().get().asString;
//                if(!App.entityRegistry.doesEntityExist(entityName)){
//                    throw new RuntimeException("no entity with tha name " + entityName + " exists. (in game map "
//                                                + interiorComponent.getInteriorName());
//                }
//
//                Entity entity = App.entityRegistry.makeEntity(entityName);
//                placeOnTile(entity, interiorComponent.getMap().getTileByPosition(e.y, e.x));
//            }
//        }

        MapObjects interiorObjects = interiorData.getLayers().get("Objects").getObjects();
        MapObjects exteriorObjects = exteriorData.getLayers().get("Objects").getObjects();


        GameMap interiorMap = interiorComponent.getMap();

        Map<Integer, Door> exteriorDoors = new HashMap<>();
        Map<Integer, Door> interiorDoors = new HashMap<>();
        Map<Integer, Integer> inOutRefs = new HashMap<>();
        Map<Integer, Integer> outInRefs = new HashMap<>();

        for (MapObject object : exteriorObjects) {
            if(object.getName().equals("Door")){
                int id = object.getProperties().get("id", Integer.class);
                int dest = object.getProperties().get("destId", Integer.class);

                int x = Math.round(object.getProperties().get("x", Float.class) / 16);
                int y = Math.round(object.getProperties().get("y", Float.class) / 16);

                Door door = new Door();
                exteriorDoors.putIfAbsent(id, door);
                EntityPlacementSystem.placeOnTile(door, map.getTileByPosition(position.getRow() + y, position.getCol() + x));
                outInRefs.putIfAbsent(id, dest);
            }
        }
        for (MapObject object : interiorObjects) {
            if(object.getName().equals("Door")){
                int id = object.getProperties().get("id", Integer.class);
                int dest = object.getProperties().get("destId", Integer.class);

                int x = Math.round(object.getProperties().get("x", Float.class) / 16);
                int y = Math.round(object.getProperties().get("y", Float.class) / 16);

                Door door = new Door();
                interiorDoors.putIfAbsent(id, door);
                EntityPlacementSystem.placeOnTile(door, interiorMap.getTileByPosition(y, x));
                inOutRefs.putIfAbsent(id, dest);
            }
        }

        for(Map.Entry<Integer, Integer> p : outInRefs.entrySet()){
            exteriorDoors.get(p.getKey()).setDestination(interiorDoors.get(p.getValue()).getComponent(PositionComponent.class).get());
            exteriorDoors.get(p.getKey()).getDestination().add(0, 16);
        }
        for(Map.Entry<Integer, Integer> p : inOutRefs.entrySet()){
            interiorDoors.get(p.getKey()).setDestination(exteriorDoors.get(p.getValue()).getComponent(PositionComponent.class).get());
            interiorDoors.get(p.getKey()).getDestination().add(0, -16);
        }

        interiorMap.setBuilding(building);

        return new Result(true, "");
    }

    private static Result placeExterior(Entity entity, Vec2 position, GameMap map){
        if(entity.getComponent(Placeable.class).getExteriorName() == null){
            return placeOnTile(entity, map.getTileByPosition(position.getRow(), position.getCol()));
        }

        GameMap exteriorMap =
            new GameMap("./Content(unpacked)/Maps/" + entity.getComponent(Placeable.class).getExteriorName() + ".tmx");
        TiledMap data = exteriorMap.getMapData();

        TiledMap mainMapData = map.getMapData();

        int width = 0;
        int height = 0;

        for (MapLayer layer : data.getLayers()) {
            if(layer instanceof TiledMapTileLayer tiledLayer){
                TiledMapTileLayer mainMapLayer = (TiledMapTileLayer) mainMapData.getLayers().get(layer.getName());
                if(mainMapLayer == null){
                    throw new RuntimeException("layer " + layer.getName() + "does not exist on world map. (placing " +
                        entity.getComponent(Placeable.class).getExteriorName() + " on ground)");
                }

                width = tiledLayer.getWidth();
                height = tiledLayer.getHeight();

                for(int i = 0 ; i < height  ; i++){
                    for(int j = 0 ; j < width; j++){
                        Cell exteriorCell = tiledLayer.getCell(j, i);
                        if(exteriorCell != null){
                            mainMapLayer.setCell(j + position.getCol(), i + position.getRow(), exteriorCell);
                        }
                    }
                }
            }
            for(int i = 0 ; i < height  ; i++){
                for(int j = 0 ; j < width; j++){
                    Tile exteriorTile = exteriorMap.getTileByPosition(i, j);
                    Tile activeTile = map.getTileByPosition(i + position.getRow(), j + position.getCol());
                    if (exteriorTile != null) {
                        activeTile.setType(exteriorTile.getType());
                        activeTile.setWalkable(exteriorTile.isWalkable());
                    }
                }
            }
        }

        map.addEntity(entity);
        entity.removeComponent(PositionComponent.class);
        entity.addComponent(new PositionComponent(position, map));

        return new Result(true, "");
    }
    public static Result removeExterior(Entity entity){
        Position position = entity.getComponent(PositionComponent.class).get();
        GameMap map = position.getMap();

        if(entity.getComponent(Placeable.class).getExteriorName() == null){
            return emptyTile(map.getTileByPosition(position.getCol(), position.getRow()));
        }

        TileType[][] exterior = App.mapRegistry.getData(entity.getComponent(Placeable.class).getExteriorName()).getTypeMap();

        for(int i = 0 ; i < exterior.length ; i++){
            for(int j = 0 ; j < exterior[0].length; j++){
                Tile activeTile = map.getTileByPosition(i + position.getRow(), j + position.getCol());
                TileType exteriorTile = exterior[i][j];

                if (exteriorTile != null) {
                    activeTile.setType(TileType.DIRT);
                }
            }
        }

        map.removeEntity(entity);
        entity.removeComponent(PositionComponent.class);

        return new Result(true, "");
    }

    public static boolean canPlace(int x, int y, Placeable placeable, GameMap map) {
        if (placeable == null || placeable.getExteriorName() == null) {
            Tile tile = map.getTileByPosition(y, x);
            return tile.isWalkable() && (tile.getContent() == null) ;
        }
        TileType[][] exterior = App.mapRegistry.getData(placeable.getExteriorName()).getTypeMap();

        for(int i = y; i < exterior.length; i++){
            for(int j = x; j < exterior[0].length; j++){
                if(exterior[i][j] != null){
                    Tile tile = App.getActiveGame().getMainMap().getTileByPosition(y, x);
                    if(tile == null || tile.getContent() != null) return false;
                }
            }
        }
        return true;
    }
    public static boolean canPlace(int x, int y, Placeable placeable) {
        return canPlace(x, y, placeable, App.getActiveGame().getMainMap());
    }
    public static boolean canPlace(int x, int y, GameMap map) {
        return canPlace(x, y, null, map);
    }
    public static boolean canPlace(int x, int y) {
        return canPlace(x, y, null, App.getActiveGame().getActiveMap());
    }
    public static boolean canPlace(Tile tile){
        return canPlace(tile.getCol(), tile.getRow());
    }
    public static void clearArea(int x, int y, Placeable placeable) {
        GameMap map = App.getActiveGame().getMainMap();

        if(placeable.getExteriorName() != null){
            ArrayList<Pickable> pickables = map.getComponentsOfType(Pickable.class);
            TileType[][] exterior = App.mapRegistry.getData(placeable.getExteriorName()).getTypeMap();

            for(int i = y; i < exterior.length + y; i++){
                for(int j = x; j < exterior[0].length + x; j++){
                    Tile tile = map.getTileByPosition(i, j);
                    //TODO o(n ^ 123123123)
                    for(int z = 0; z < pickables.size(); z++){
                        Pickable pickable = pickables.get(z);
                        if(pickable.getEntity().getComponent(PositionComponent.class).get().dst(tile.getPosition()) < 0.1){
                            pickable.getEntity().delete();
                        }
                    }
                    if(tile.getContent() != null) tile.getContent().delete();
                }
            }
        }
        else{
            emptyTile(map.getTileByPosition(y, x));
        }
    }
    public static void removeFromMap(Entity entity){
        PositionComponent position = entity.getComponent(PositionComponent.class);
        GameMap map = position.getMap();

        if(map == null) return;
        map.removeEntity(entity);
        position.setMap(null);
    }
}
