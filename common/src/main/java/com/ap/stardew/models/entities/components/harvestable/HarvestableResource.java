package com.ap.stardew.models.entities.components.harvestable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.io.Serializable;

@JsonDeserialize(builder = HarvestableResource.Builder.class)
public class HarvestableResource implements Serializable {
    @JsonProperty("entity")
    String entity;
    @JsonProperty("probability")
    float probability;
    @JsonProperty("min")
    int min;
    @JsonProperty("max")
    int max;

    private HarvestableResource(Builder b){
        if(b.entity == null){
            throw new RuntimeException("The harvestable resource doesnt have an entity specified");
        }
        if(b.max == 0 && b.min == 0){
            throw new RuntimeException("The harvestable resource \"" + b.entity + "\" doesn't have min or max. it should at least have one");
        }
        if(b.max == 0){
            b.max = b.min;
        }
        if(b.min == 0){
            b.min = b.max;
        }

        this.entity = b.entity;
        this.min = b.min;
        this.max = b.max;
        this.probability = b.probability;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder{
        String entity = null;
        float probability = 1;
        int min = 0, max = 0;

        private void reset(){
            this.entity = null;
            this.probability = 1;
            this.min = 0;
            this.max = 0;
        }

        public Builder entity(String entity){
            this.entity = entity;
            return this;
        }
        public Builder probability(float probability){
            this.probability = probability;
            return this;
        }
        public Builder min(int min){
            this.min = min;
            return this;
        }
        public Builder max(int max){
            this.max = max;
            return this;
        }
        public HarvestableResource build(){
            HarvestableResource resource = new HarvestableResource(this);
            this.reset();
            return resource;
        }
    }
}
