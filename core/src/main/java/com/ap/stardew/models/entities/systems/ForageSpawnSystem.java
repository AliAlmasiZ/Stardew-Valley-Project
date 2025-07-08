package com.ap.stardew.models.entities.systems;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Forageable;
import com.ap.stardew.models.entities.components.Pickable;
import com.ap.stardew.models.entities.components.PositionComponent;
import com.ap.stardew.models.enums.EntityTag;
import com.ap.stardew.models.enums.TileType;
import com.ap.stardew.models.gameMap.BiomeType;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.gameMap.WorldMap;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Map;

public class ForageSpawnSystem {
    public static void updatePerDay(){
        placeForageables();
    }
    public static void updatePerHour(){

    }
    public static void placeForageables(){
        SecureRandom random = new SecureRandom();
        WorldMap map = App.getActiveGame().getMainMap();
        Tile[][] tiles = map.getTiles();
        BiomeType[][] biomeMap = map.getBiomeMap();
        Map<BiomeType, ArrayList<BiomeType.Spawnable>> candidates = BiomeType.getCandidates(App.getActiveGame().getDate().getSeason());

        double[][] weightMap = new double[tiles.length][tiles[0].length];

        for(int i = 0 ; i < tiles.length; i++){
            for(int j = 0 ; j < tiles[0].length; j++) {
                weightMap[i][j] = 1;
            }
        }
        if( map.getComponentsOfType(Pickable.class) != null){
            for(Pickable pickable : map.getComponentsOfType(Pickable.class)){
                Position position = pickable.getEntity().getComponent(PositionComponent.class).get();
                weightMap[position.getRow()][position.getCol()] = 0;
            }
        }
        for(int i = 0 ; i < tiles.length; i++){
            for(int j = 0 ; j < tiles[0].length; j++) {
                if(!EntityPlacementSystem.canPlace(tiles[i][j])){
                    weightMap[i][j] = 0;
                }
            }
        }

        for(int i = 0 ; i < tiles.length; i++){
            for(int j = 0 ; j < tiles[0].length; j++){
                BiomeType biome = biomeMap[i][j];

                if(weightMap[i][j] < 0.1) continue;

                if(random.nextFloat() > 0.01) continue;

                if(candidates.get(biome) == null) continue;

                if(!candidates.get(biome).isEmpty()){

                    Entity entity;
                    do{
                        entity = App.entityRegistry.makeEntity(candidates.get(biome).get(random.nextInt(candidates.get(biome).size())).getEntity());
                    }
                    while (entity.hasTag(EntityTag.SEED) && tiles[i][j].getType() != TileType.PLOWED);

                    Forageable forageable = new Forageable();
                    //TODO spawn date
                    //forageable.setDateAdded(App.getActiveGame().getDate());
                    entity.addComponent(forageable);
                    if(entity.getComponent(Pickable.class) != null){
                        entity.getComponent(Pickable.class).setStackSize(1);
                    }

                    EntityPlacementSystem.placeOnTile(entity, tiles[i][j]);
                }

            }
        }
    }

}
