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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntityPlacementSystem {
public static Result placeOnTile(Entity entity, Tile tile){
    Entity tileEntity = tile.getContent();
    if(tileEntity != null){
        return new Result(false, "tile is full");
    }

    if(entity.getComponent(Placeable.class) == null) return placeOnMap(entity, tile.getPosition(), tile.getMap());

    tile.setContent(entity);
    entity.addObserver(tile);
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
        if(pickable.getEntity().getComponent(PositionComponent.class).get().getDistance(tile.getPosition()) < 0.1){
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

public static Result    placeEntity(Entity entity, Vec2 position, GameMap map) {
    Placeable placeable = entity.getComponent(Placeable.class);
    if(placeable == null)
        return new Result(false, "This entity isn't placeable");

    InteriorComponent interior = entity.getComponent(InteriorComponent.class);
    if(interior != null) map = App.getActiveGame().getMainMap();
    Result result = placeExterior(entity, position, map);
    if(!result.isSuccessful())
        return result;

    if(interior != null){
        buildBuilding(entity, position);
    }

    return new Result(true, "placed");
}
public static Result placeEntity(Entity entity, Vec2 position) {
    return placeEntity(entity, position, App.getActiveGame().getActiveMap());
}

private static Result buildBuilding(Entity building, Vec2 position){
    InteriorComponent interiorComponent = building.getComponent(InteriorComponent.class);
    Placeable placeable = building.getComponent(Placeable.class);

    MapData exteriorData = App.mapRegistry.getData(placeable.getExteriorName());
    MapData interiorData = App.mapRegistry.getData(interiorComponent.getInteriorName());

    //TODO environment?
    interiorComponent.setInteriorMap(new GameMap(interiorData, Environment.BUILDING));

    ArrayList<MapData.MapLayerData<String>.ObjectData> entities = interiorData.getEntities();
    if(entities != null){
        for (MapData.MapLayerData<String>.ObjectData e : entities) {
            String entityName = e.getProperty("entityName").asString;
            if(!App.entityRegistry.doesEntityExist(entityName)){
                throw new RuntimeException("no entity with tha name " + entityName + " exists. (in game map "
                                            + interiorComponent.getInteriorName());
            }

            Entity entity = App.entityRegistry.makeEntity(entityName);
            placeOnTile(entity, interiorComponent.getMap().getTileByPosition(e.y, e.x));
        }
    }

    GameMap interiorMap = interiorComponent.getMap();
    GameMap worldMap = App.getActiveGame().getMainMap();

    if(exteriorData.getDoors() != null){
        Map<Integer, Door> exteriorDoors = new HashMap<>();
        Map<Integer, Door> interiorDoors = new HashMap<>();
        Map<Integer, Integer> inOutRefs = new HashMap<>();
        Map<Integer, Integer> outInRefs = new HashMap<>();
        for (MapData.MapLayerData<String>.ObjectData d : exteriorData.getDoors()) {
            int id = d.getProperty("id").asInt;
            int dest = d.getProperty("destination").asInt;
            Door door = new Door();
            exteriorDoors.putIfAbsent(id, door);
            emptyTile(worldMap.getTileByPosition(position.getRow() + d.y - 1, position.getCol() + d.x));
            worldMap.getTileByPosition(position.getRow() + d.y - 1, position.getCol() + d.x).setType(TileType.WOOD);
            EntityPlacementSystem.placeOnTile(door, worldMap.getTileByPosition(position.getRow() + d.y - 1, position.getCol() + d.x));
            outInRefs.putIfAbsent(id, dest);
        }
        for (MapData.MapLayerData<String>.ObjectData d : interiorData.getDoors()) {
            int id = d.getProperty("id").asInt;
            int dest = d.getProperty("destination").asInt;
            Door door = new Door();
            interiorDoors.putIfAbsent(id, door);
            emptyTile(interiorMap.getTileByPosition(d.y-1, d.x));
            interiorMap.getTileByPosition(d.y-1, d.x).setType(TileType.WOOD);
            EntityPlacementSystem.placeOnTile(door, interiorMap.getTileByPosition(d.y-1, d.x));
            inOutRefs.putIfAbsent(id, dest);
        }

        for(Map.Entry<Integer, Integer> p : outInRefs.entrySet()){
            exteriorDoors.get(p.getKey()).setDestination(interiorDoors.get(p.getValue()).getComponent(PositionComponent.class).get());
        }
        for(Map.Entry<Integer, Integer> p : inOutRefs.entrySet()){
            try {
                interiorDoors.get(p.getKey()).setDestination(exteriorDoors.get(p.getValue()).getComponent(PositionComponent.class).get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
    interiorMap.setBuilding(building);

    return new Result(true, "");
}

private static Result placeExterior(Entity entity, Vec2 position, GameMap map){
    if(entity.getComponent(Placeable.class).getExteriorName() == null){
        return placeOnTile(entity, map.getTileByPosition(position.getRow(), position.getCol()));
    }

    TileType[][] exterior = App.mapRegistry.getData(entity.getComponent(Placeable.class).getExteriorName()).getTypeMap();

    for(int i = 0 ; i < exterior.length ; i++){
        for(int j = 0 ; j < exterior[0].length; j++){
            Tile activeTile = map.getTileByPosition(i + position.getRow(), j + position.getCol());
            TileType exteriorTile = exterior[i][j];

            if (exteriorTile != null) {
                activeTile.setType(exteriorTile);
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
        return tile.getType().isWalkable && (tile.getContent() == null) ;
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
    return canPlace(x, y, null, App.getActiveGame().getMainMap());
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
                    if(pickable.getEntity().getComponent(PositionComponent.class).get().getDistance(tile.getPosition()) < 0.1){
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

}
