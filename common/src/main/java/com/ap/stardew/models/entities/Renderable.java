package com.ap.stardew.models.entities;

import com.ap.stardew.models.entities.components.EntityComponent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Renderable extends EntityComponent implements Serializable {
    @JsonProperty("renderFunction")
    private RenderFunction renderFunction;
    @JsonProperty("state")
    private Map<String, Object> state;
    @JsonIgnore
    protected String spritePath;
    protected float timeLeftForStatue = 0.0f;

    public enum Statue {
        NORMAL,
        IDLE,
        RIGHT_WALKING,
        LEFT_WALKING,
        EATING,
        PET,
    }

    protected Statue currentStatue = Statue.NORMAL;

    public Renderable() {
    }

    private Renderable(Renderable other) {
        this.state = new HashMap<>();
        if(other.state != null){
            this.state.putAll(other.state);
        }

        this.renderFunction = other.renderFunction;
    }

    public Statue getCurrentStatue() {
        return currentStatue;
    }

    public void reduceTimeLeftForStatue(float time) {
        this.timeLeftForStatue -= time;
    }

    @Override
    public EntityComponent clone() {
        return new Renderable(this);
    }

    public String getSpritePath() {
        return spritePath;
    }

    public void setSpritePath(String spritePath) {
        this.spritePath = spritePath;
    }

    public void setStatue(Statue statue, float duration) {
        currentStatue = statue;
        timeLeftForStatue = duration;
    }

    public void setCurrentStatue(Statue statue) {
        currentStatue = statue;
    }

    public float getTimeLeftForStatue() {
        return timeLeftForStatue;
    }

    public RenderFunction getRenderFunction() {
        return renderFunction;
    }

    public Map<String, Object> getState() {
        return state;
    }

    public void setState(Map<String, Object> state) {
        this.state = state;
    }

    public void setRenderFunction(RenderFunction renderFunction) {
        this.renderFunction = renderFunction;
    }
}
