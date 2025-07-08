package models.entities.systems;

import models.App;
import models.Position;
import models.entities.Entity;
import models.entities.components.Forageable;
import models.entities.components.Pickable;
import models.entities.components.PositionComponent;
import models.enums.EntityTag;
import models.enums.TileType;
import models.gameMap.BiomeType;
import models.gameMap.Tile;
import models.gameMap.WorldMap;

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
