package models.entities.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import models.entities.CollisionEvent;
import models.enums.TileType;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

@JsonDeserialize(using = PlaceableDeserializer.class)
public class Placeable extends EntityComponent implements Serializable {

    @JsonProperty("exteriorName")
    private String exteriorName;
//    @JsonProperty("exteriorMap")
//    private TileType[][] exterior;
    @JsonProperty("isWalkable")
    private boolean isWalkable;
    @JsonProperty("collisionFunctions")
    private ArrayList<CollisionEvent> collisionFunctions = new ArrayList<>();

    public Placeable(boolean isWalkable, TileType[][] exterior, String exteriorName, CollisionEvent... collisionFunctions) {
        this.isWalkable = isWalkable;
        if(collisionFunctions != null){
            this.collisionFunctions.addAll(Arrays.asList(collisionFunctions));
        }
//        this.exterior = exterior;
        this.exteriorName = exteriorName;
    }
    public Placeable(boolean isWalkable, CollisionEvent... collisionFunctions) {
        this(isWalkable, null, null, collisionFunctions);
    }
    private Placeable(Placeable other){
        this.isWalkable = other.isWalkable;
        this.collisionFunctions.addAll(other.collisionFunctions);
//        this.exterior = other.exterior;
        this.exteriorName = other.exteriorName;
    }

    public boolean isWalkable() {
        return isWalkable;
    }


    public ArrayList<CollisionEvent> getCollisionEvents() {
        return collisionFunctions;
    }

    @Override
    public String toString() {
        return "Placeable{" +
                "isWalkable=" + isWalkable +
                '}';
    }

    public String getExteriorName() {
        return exteriorName;
    }
//    public TileType[][] getExterior() {
//        return exterior;
//    }


    @Override
    public EntityComponent clone() {
        return new Placeable(this);
    }
}
class PlaceableDeserializer extends JsonDeserializer<Placeable> {
    @Override
    public Placeable deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode root = p.getCodec().readTree(p);

        JsonNode isWalkableNode = root.get("isWalkable");
        JsonNode collisionFunctionsNode = root.get("collisionFunctions");
        JsonNode exteriorNameNode = root.get("exteriorName");
        JsonNode exteriorMapNode = root.get("exteriorMap");


        boolean isWalkable = false;
        CollisionEvent[] collisionFunctions = null;
        String exteriorName = null;
        TileType[][] exterior = null;

        if(isWalkableNode != null) isWalkable = isWalkableNode.asBoolean();
        if(collisionFunctionsNode != null) collisionFunctions = p.getCodec().treeToValue(collisionFunctionsNode, CollisionEvent[].class);
        if(exteriorNameNode != null) exteriorName = exteriorNameNode.asText();
        if(exteriorMapNode != null) exterior = p.getCodec().treeToValue(exteriorMapNode, TileType[][].class);


        return new Placeable(isWalkable, exterior,exteriorName, collisionFunctions);
    }
}