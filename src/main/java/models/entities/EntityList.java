package models.entities;

import models.entities.components.EntityComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntityList extends ArrayList<Entity> implements EntityObserver{
    private Map<Class<? extends EntityComponent>, ArrayList<EntityComponent>> components = new HashMap<>();

    @Override
    public boolean add(Entity entity) {
        boolean b = super.add(entity);
        entity.addObserver(this);

        for(EntityComponent c : entity.getComponents()){
            components.putIfAbsent(c.getClass(), new ArrayList<>());
            components.get(c.getClass()).add(c);
        }

        return b;
    }

    @Override
    public boolean remove(Object o) {
        if(contains(o)){
            boolean b = super.remove(o);
            if(o instanceof Entity entity){
                (entity).removeObserveer(this);
                for(EntityComponent c : entity.getComponents()){
                    components.putIfAbsent(c.getClass(), new ArrayList<>());
                    components.get(c.getClass()).remove(c);
                }
            }

            return b;
        }
        return false;
    }

    @Override
    public void onDelete(Entity entity) {
        this.remove(entity);
    }

    @SuppressWarnings("unchecked")
    public <T extends EntityComponent> ArrayList<T> getComponentsOfType(Class<T> clazz){
        components.putIfAbsent(clazz, new ArrayList<>());
        return (ArrayList<T>) components.get(clazz);
    }
}

