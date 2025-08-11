package com.ap.stardew.views.widgets;

import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.EntityComponent;
import com.ap.stardew.models.entities.components.PositionComponent;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class FollowerActor extends EntityComponent {
    @Override
    public EntityComponent clone() {
        return null;
    }

    public FollowerActor(Entity followedEntity, Actor actor){
        this.entity = entity;

        if(entity.getComponent(PositionComponent.class) == null){
            this.entity = null;
        }
    }
}
