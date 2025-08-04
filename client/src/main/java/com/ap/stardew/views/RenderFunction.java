package com.ap.stardew.views;

import com.ap.stardew.controllers.GameAssetManager;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.Renderable;
import com.ap.stardew.models.entities.components.Pickable;
import com.ap.stardew.models.entities.components.Placeable;
import com.ap.stardew.models.entities.components.PositionComponent;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;


public enum RenderFunction {
    SIMPLE{
        @Override
        public void render(Entity entity, Batch batch) {
            Pickable component = entity.getComponent(Pickable.class);
            Renderable renderable = entity.getComponent(Renderable.class);

            if(component == null || component.getIcon() == null || component.getIcon().isEmpty()) return;

            Position position = entity.getComponent(PositionComponent.class).get();
            Texture texture = getCurrentTexture(entity);

            Texture shadow = GameAssetManager.getInstance().shadow;

            batch.draw(shadow, position.x + texture.getWidth() / 2f - shadow.getWidth()/2f, position.y);

            if(renderable.getState().get("hovered") != null){
                batch.end();

                ShaderProgram outlineShader = GameAssetManager.getInstance().outlineShader;
                batch.setShader(outlineShader);
                outlineShader.bind();

                batch.begin();
                outlineShader.setUniformf("u_outlineColor", 1f, 1f, 1f, 1f);
                outlineShader.setUniformf("u_thickness", 0.004f * GameAssetManager.getInstance().ppiX);
                outlineShader.setUniformf("u_alphaThreshold", 0.9f);
                outlineShader.setUniformf("u_center", position.x + texture.getWidth() / 2f, position.y + texture.getHeight() / 2f, 0);
                outlineShader.setUniformf("u_scale", 1.2f);
                outlineShader.setUniformf("u_textureSize", texture.getWidth(), texture.getHeight());

                batch.draw(texture, position.x, position.y);

                batch.end();
                batch.setShader(null);
                batch.begin();

                if(entity.getComponent(Pickable.class) != null && entity.getComponent(Placeable.class) == null){
                    Vector2 cursorPos = (Vector2) renderable.getState().get("cursorPos");

                    Texture smallPlus = GameAssetManager.getInstance().smallPlus;
                    batch.draw(smallPlus, cursorPos.x + 2, cursorPos.y);
                }
            }
            batch.draw(texture, position.x, position.y);
        }

        @Override
        public Texture getCurrentTexture(Entity entity) {
            Renderable renderable = entity.getComponent(Renderable.class);
            if(renderable.getState().get("sprite") == null){
                Pickable component = entity.getComponent(Pickable.class);
                if(component == null || component.getIcon() == null || component.getIcon().isEmpty())
                    return GameAssetManager.getInstance().redCross;

                return GameAssetManager.getInstance().get(component.getIcon(), Texture.class);
            }else {
                return GameAssetManager.getInstance().get((String) renderable.getState().get("sprite"), Texture.class);
            }
        }
    },
    MINERAL_NODE{
        @Override
        public void render(Entity entity, Batch batch) {
            Renderable component = entity.getComponent(Renderable.class);

            if(component.getState().get("node") == null) {
                batch.draw(GameAssetManager.getInstance().redCross, entity.getComponent(PositionComponent.class).getX(),
                    entity.getComponent(PositionComponent.class).getY());
            }

            batch.draw(GameAssetManager.getInstance().get((String) component.getState().get("node"), Texture.class),
                entity.getComponent(PositionComponent.class).getX(),
                entity.getComponent(PositionComponent.class).getY());
        }
        @Override
        public Texture getCurrentTexture(Entity entity) {
            Renderable component = entity.getComponent(Renderable.class);

            if(component.getState().get("node") == null) return GameAssetManager.getInstance().redCross;

            return GameAssetManager.getInstance().get((String) component.getState().get("node"));
        }
    };
    public abstract void render(Entity entity, Batch batch);
    public abstract Texture getCurrentTexture(Entity entity);
}
