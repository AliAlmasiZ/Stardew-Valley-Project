package models.entities.components.harvestable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import models.App;
import models.entities.Entity;
import models.entities.components.EntityComponent;
import models.entities.components.Pickable;
import models.enums.Material;

import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;


public class Harvestable extends EntityComponent implements Serializable {
    @JsonProperty("material")
    private Material material;
    @JsonProperty("resources")
    private final ArrayList<HarvestableResource> resources = new ArrayList<>();

    public Harvestable(Material material, ArrayList<HarvestableResource> resources){
        this.material = material;
        this.resources.addAll(resources);
    }
    public Harvestable(Material material, String entity, int amount, float probability){
        this.material = material;
        this.resources.add(new HarvestableResource.Builder().min(amount).entity(entity).probability(probability).build());
    }
    public Harvestable(Harvestable other){
        this.resources.addAll(other.resources);
        this.material = other.material;
    }
    public Harvestable(){
        this(Material.STONE, new ArrayList<>());
    }

    public ArrayList<Entity> harvest(){
        SecureRandom random = new SecureRandom();
        ArrayList<Entity> generatedEntities = new ArrayList<>();
        for(HarvestableResource r : resources){
            if(random.nextFloat() < r.probability){
                Entity entity = App.entityRegistry.makeEntity(r.entity);
                entity.getComponent(Pickable.class).setStackSize(random.nextInt(r.min, r.max+1));
                generatedEntities.add(entity);
            }
        }
        return generatedEntities;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public String toString() {
        return "Harvestable";
    }

    @Override
    public EntityComponent clone() {
        return new Harvestable(this);
    }
}
