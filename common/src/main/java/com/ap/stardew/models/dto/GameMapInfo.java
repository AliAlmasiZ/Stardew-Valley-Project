package com.ap.stardew.models.dto;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.Vec2;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.EntityList;
import com.ap.stardew.models.entities.components.Placeable;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.enums.TileType;
import com.ap.stardew.models.gameMap.Environment;
import com.ap.stardew.models.gameMap.GameMap;
import com.ap.stardew.models.gameMap.TIlePlower;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.utils.TiledMapUtils;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import org.tiledreader.*;

public class GameMapInfo {
    public Tile[][] tiles;
    public String mapDataPath;
    public int width, height;
    public Environment environment;
    public final EntityList entities = new EntityList();
    public Entity building = null;

    public GameMapInfo(GameMap gameMap) {
        tiles = gameMap.getTiles();
        entities.addAll(gameMap.getEntities());
        building = gameMap.getBuilding();
        environment = gameMap.getEnvironment();
        mapDataPath = gameMap.getMapDataPath();

        height = tiles.length;
        width = tiles[0].length;
    }

    public GameMap toGameMap(){
        GameMap gameMap = TiledMapUtils.loadMapFromFile(mapDataPath);

        gameMap.getEntities().addAll(entities);
        gameMap.setBuilding(building);
        gameMap.setEnvironment(environment);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                gameMap.getTiles()[i][j] = tiles[i][j];

                if(tiles[i][j].getType() == TileType.PLOWED){
                    TIlePlower.plowTile(tiles[i][j]);
                }
            }
        }

        return gameMap;
    }
}
