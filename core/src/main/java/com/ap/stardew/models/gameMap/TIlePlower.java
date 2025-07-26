package com.ap.stardew.models.gameMap;

import com.ap.stardew.models.enums.TileType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

import java.util.Map;

public class TIlePlower {
    private static Texture plowTexture = new Texture("Content/tilledSoil.png");
    private static final int[][] indexMap = {
        {0, 0},
        {0, 3},
        {1, 3},
        {1, 2},
        {0, 1},
        {0, 2},
        {1, 0},
        {1, 1},
        {3, 3},
        {3, 2},
        {2, 3},
        {2, 2},
        {3, 0},
        {3, 1},
        {2, 0},
        {2, 1}
    };
    private final static StaticTiledMapTile tileset[][] = new StaticTiledMapTile[12][12];

    static {
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                tileset[i][j] = new StaticTiledMapTile(new TextureRegion(plowTexture, i * 16, j * 16, 16, 16));
            }
        }
    }

    public static void plowTile(Tile tile){
        GameMap activeMap = tile.getMap();

        updateTile(tile);

        Tile top = activeMap.getTileByPosition(tile.getPosition().copy().add(0, 16));
        Tile right = activeMap.getTileByPosition(tile.getPosition().copy().add(16, 0));
        Tile bottom = activeMap.getTileByPosition(tile.getPosition().copy().add(0, -16));
        Tile left = activeMap.getTileByPosition(tile.getPosition().copy().add(-16, 0));

        if(top != null){
            if(top.getType() == TileType.PLOWED){
                updateTile(top);
            }
        }
        if(right != null){
            if(right.getType() == TileType.PLOWED){
                updateTile(right);
            }
        }
        if(bottom != null){
            if(bottom.getType() == TileType.PLOWED){
                updateTile(bottom);
            }
        }
        if(left != null){
            if(left.getType() == TileType.PLOWED){
                updateTile(left);
            }
        }
    }
    public static StaticTiledMapTile getTileRegion(int index) {
        int[] coords = indexMap[index];
        return tileset[coords[0]][coords[1]];
    }
    public static void updateTile(Tile tile){
        GameMap activeMap = tile.getMap();
        int index = 0;

        Tile top = activeMap.getTileByPosition(tile.getPosition().copy().add(0, 16));
        Tile right = activeMap.getTileByPosition(tile.getPosition().copy().add(16, 0));
        Tile bottom = activeMap.getTileByPosition(tile.getPosition().copy().add(0, -16));
        Tile left = activeMap.getTileByPosition(tile.getPosition().copy().add(-16, 0));

        if(top != null){
            if(top.getType() == TileType.PLOWED){
                index += 1;
            }
        }
        if(right != null){
            if(right.getType() == TileType.PLOWED){
                index += 2;
            }
        }
        if(bottom != null){
            if(bottom.getType() == TileType.PLOWED){
                index += 4;
            }
        }
        if(left != null){
            if(left.getType() == TileType.PLOWED){
                index += 8;
            }
        }

        TiledMap mapData = activeMap.getMapData();
        TiledMapTileLayer backLayer = (TiledMapTileLayer) mapData.getLayers().get("Back2");
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(TIlePlower.getTileRegion(index));
        backLayer.setCell(tile.getCol(), tile.getRow(), cell);
    }
}
