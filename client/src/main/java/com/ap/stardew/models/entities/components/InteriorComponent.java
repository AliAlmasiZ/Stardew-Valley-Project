package com.ap.stardew.models.entities.components;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ap.stardew.models.gameMap.GameMap;

import java.io.IOException;
import java.io.Serializable;

@JsonDeserialize(using = InteriorComponentDeserializer.class)
public class InteriorComponent extends EntityComponent implements Serializable {
    private GameMap interiorMap;
    private String interiorName;

    public InteriorComponent(String interiorName) {
        this.interiorName = interiorName;
    }
    public InteriorComponent(GameMap interiorMap) {
        this.interiorMap = interiorMap;
    }

    private InteriorComponent(InteriorComponent other) {
        this.interiorName = other.interiorName;
    }

    public GameMap getMap() {
        return this.interiorMap;
    }

    public String getInteriorName() {
        return interiorName;
    }

    public void setInteriorMap(GameMap interiorMap) {
        this.interiorMap = interiorMap;
    }

    @Override
    public EntityComponent clone() {
        return new InteriorComponent(this);
    }
}

class InteriorComponentDeserializer extends JsonDeserializer<InteriorComponent> {
    @Override
    public InteriorComponent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        JsonNode interiorName = node.get("interiorName");
        JsonNode interiorMap = node.get("interiorMap");

        if(interiorName != null){
            return new InteriorComponent(interiorName.asText());
        }else if(interiorMap != null){
            return new InteriorComponent(p.getCodec().treeToValue(interiorMap, GameMap.class));
        }else{
            throw new RuntimeException("Interior component either needs a name for its map (interiorName), or the acutal " +
                    "map (interiorMap). it has none in " + node.toString());
        }
    }
}
