package models.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.App;
import models.entities.components.*;
import models.enums.EntityTag;
import models.utils.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

public class EntityRegistry extends Registry<Entity> {
    public void addChild(EntityRegistry child) {
        registry.putAll(child.registry);
    }

    @Override
    protected void loadJson(JsonNode jsonRoot, ObjectMapper mapper, Path path) throws IOException {
        ArrayList<Entity> entities = new ArrayList<>();
        if(jsonRoot.get("entities") == null){
            throw new RuntimeException("The structure of entity data file is invalid! (" + path.toString() + ")");
        }
        for(JsonNode n : jsonRoot.get("entities")){
            if(n.get("name") == null){
                throw new RuntimeException("an entity in the file " + path.toString() + "has no name");
            }
            String name = n.get("name").asText();
            try {
                entities.add(mapper.treeToValue(n, Entity.class));
            }catch (JsonProcessingException e) {
                System.err.println("there was a problem in the entity " + name + ", file " + path.toString() + "\n");
                throw new RuntimeException(e);
            }
        }
        EntityTag[] commonTags              = null;
        String[] requiredComponents         = null;
        EntityComponent[] commonComponents  = null;

        if(jsonRoot.get("common components") != null){
            commonComponents = mapper.treeToValue(jsonRoot.get("common components"),EntityComponent[].class);
        }
        if(jsonRoot.get("required components") != null){
            requiredComponents = mapper.treeToValue(jsonRoot.get("required components"), String[].class);
        }
        if(jsonRoot.get("common tags") != null){
            commonTags = mapper.treeToValue(jsonRoot.get("common tags"), EntityTag[].class);
        }

        for(Entity e : entities){
            if(commonComponents != null){
                for(EntityComponent c : commonComponents){
                    e.addComponent(c.clone());
                }
            }
            if(requiredComponents != null){
                for(String component : requiredComponents){
                    boolean found = false;
                    for(EntityComponent ec : e.getComponents()){
                        if(ec.getClass().getSimpleName().equalsIgnoreCase(component)){
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        throw new RuntimeException("The entity \"" + e.getEntityName() +"\n in the data file " + path + " doesn't have the" +
                                "required component: " + component.getClass());
                    }
                }
            }
            if(commonTags != null){
                for(EntityTag t : commonTags){
                    e.addTag(t);
                }
            }
            if(registry.get(e.getEntityName()) != null){
                System.err.println("entity " + e.getEntityName() + " is getting added twice to the registry");
            }
            this.registry.putIfAbsent(e.getEntityName().toLowerCase(), e);
        }
    }

    @Override
    public Entity getData(String name) {
//        return registry.get(name);
        return get(name);
    }


    public boolean doesEntityExist(String entityName){
        Entity entity = get(entityName.toLowerCase());
        return entity != null;
    }

    public Entity makeEntity(String name){
        Entity entity = get(name.toLowerCase());

        if(entity == null)
        {
            Entity data = App.buildingRegistry.getData(name);
            if(data == null)
                throw new RuntimeException("no entity found with the name " + name);
            return data.clone();//TODO:
        }
        return entity.clone();
    }
    public Entity getEntityDetails(String name){
        return get(name.toLowerCase());
    }

    private Entity get(String name){
        for(String key : registry.keySet()){
            if(StringUtils.isNamesEqual(key , name)) return registry.get(key);
        }
        return null;
    }
    public void listEntities(){
        for(Map.Entry<String, Entity> e : this.registry.entrySet()){
            System.out.println("--------------\n" + e.getValue());
        }
    }
}
