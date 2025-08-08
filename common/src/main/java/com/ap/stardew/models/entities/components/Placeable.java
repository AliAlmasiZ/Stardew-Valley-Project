package com.ap.stardew.models.entities.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ap.stardew.models.enums.TileType;

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

    public Placeable(boolean isWalkable, TileType[][] exterior, String exteriorName) {
        this.isWalkable = isWalkable;
//        this.exterior = exterior;
        this.exteriorName = exteriorName;
    }
    public Placeable(boolean isWalkable) {
        this(isWalkable, null, null);
    }
    private Placeable(Placeable other){
        this.isWalkable = other.isWalkable;
//        this.exterior = other.exterior;
        this.exteriorName = other.exteriorName;
    }
    private Placeable(){}

    public boolean isWalkable() {
        return isWalkable;
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
        String exteriorName = null;
        TileType[][] exterior = null;

        if(isWalkableNode != null) isWalkable = isWalkableNode.asBoolean();
        if(exteriorNameNode != null) exteriorName = exteriorNameNode.asText();
        if(exteriorMapNode != null) exterior = p.getCodec().treeToValue(exteriorMapNode, TileType[][].class);

        return new Placeable(isWalkable, exterior,exteriorName);
    }
}
