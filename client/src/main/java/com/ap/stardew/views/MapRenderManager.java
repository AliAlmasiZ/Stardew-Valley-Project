package com.ap.stardew.views;

import com.ap.stardew.models.App;
import com.ap.stardew.models.gameMap.GameMap;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapRenderManager {
    private final static TmxMapLoader mapLoader = new TmxMapLoader();
    public static class SepratedMapData{
        public final TiledMap mapData;
        public final int[] backLayerIndices;
        public final int[] frontLayerIndices;

        public SepratedMapData(TiledMap mapData) {
            this.mapData = mapData;

            ArrayList<Integer> backLayers = new ArrayList<>();
            ArrayList<Integer> frontLayers = new ArrayList<>();
            for (int i = 0; i < App.getActiveGame().getActiveMap().getMapData().getLayers().size(); i++) {
                MapLayer mapLayer = App.getActiveGame().getActiveMap().getMapData().getLayers().get(i);
                if (mapLayer.getName().contains("Back") || mapLayer.getName().contains("Buildings")) {
                    backLayers.add(i);
                } else {
                    frontLayers.add(i);
                }
            }
            backLayerIndices = new int[backLayers.size()];
            for (int i = 0; i < backLayers.size(); i++) {
                backLayerIndices[i] = backLayers.get(i);
            }

            frontLayerIndices = new int[frontLayers.size()];
            for (int i = 0; i < frontLayers.size(); i++) {
                frontLayerIndices[i] = frontLayers.get(i);
            }
        }
    }
    private final Map<GameMap, SepratedMapData> map = new HashMap<>();

    public void renderBackLayers(OrthogonalTiledMapRenderer renderer, GameMap gameMap){
        if(gameMap.getMapData() == null) {
            gameMap.setMapData(mapLoader.load(gameMap.getMapDataPath()));
            map.put(gameMap, new SepratedMapData(gameMap.getMapData()));
        }

        renderer.setMap(gameMap.getMapData());
        renderer.render(map.get(gameMap).backLayerIndices);
    }
    public void renderFrontLayers(OrthogonalTiledMapRenderer renderer, GameMap gameMap){
        if(gameMap.getMapData() == null) {
            gameMap.setMapData(mapLoader.load(gameMap.getMapDataPath()));
            map.put(gameMap, new SepratedMapData(gameMap.getMapData()));
        }

        renderer.setMap(gameMap.getMapData());
        renderer.render(map.get(gameMap).frontLayerIndices);
    }
}
