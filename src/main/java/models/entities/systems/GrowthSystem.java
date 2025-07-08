package models.entities.systems;

import models.entities.Entity;
import models.entities.components.Growable;
import models.entities.components.InteriorComponent;
import models.gameMap.GameMap;
import models.gameMap.Tile;
import records.Result;

import java.util.ArrayList;

public class GrowthSystem {
    public static Result plantOnTile(Entity seed, Tile tile){
        return null;
    }
    public static void updatePerDay(GameMap map){
        ArrayList<Growable> growables = map.getComponentsOfType(Growable.class);

        if(growables == null) return;

        for (Growable growable : growables) {
            growable.updatePerDay();
        }

        ArrayList<Entity> toDelete = new ArrayList<>();
        for (Growable growable : growables) {
            if (growable.getDaysPastFromWatered() >= 3) {
                toDelete.add(growable.getEntity());
            }
        }

        int size = toDelete.size();
        for (int i = 0; i < size; i++) {
            Entity entity = toDelete.get(i);
            entity.delete();
        }

        ArrayList<InteriorComponent> buildings = map.getComponentsOfType(InteriorComponent.class);
        for(InteriorComponent c : buildings){
            //TODO is this enough for greenhouse logic? i don know.
            updatePerDay(c.getMap());
        }
    }

    public static void waterAll(GameMap map){
        ArrayList<Growable> growables = map.getComponentsOfType(Growable.class);

        if(growables == null) return;
        for (Growable growable : growables) {
            growable.setWateredToday(true);
        }
    }
}
