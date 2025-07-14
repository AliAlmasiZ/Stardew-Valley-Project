package com.ap.stardew;

import com.ap.stardew.models.App;
import com.ap.stardew.views.GameScreen;
import com.ap.stardew.views.MainScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class StardewGame extends Game {
    private static StardewGame instance;
    private Batch batch;



    public static StardewGame getInstance() {
        return instance;
    }

    public Batch getBatch() {
        return batch;
    }

    @Override
    public void create() {
        loadDatas();
        App.loadState();
        instance = this;
        batch = new SpriteBatch();
        setScreen(new MainScreen());
//        setScreen(new GameScreen());
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0.1f, 0f, 1);
        Gdx.gl.glClear(GL32.GL_COLOR_BUFFER_BIT);
        super.render();
    }

    private static void loadDatas() {
        App.getView().log("Loading Game...");
        App.shopRegistry.load("./data/shops");
        /* should load recipes first (because artisan has recipes) */
        App.recipeRegistry.loadRecipes("./data/recipes");
        App.entityRegistry.load("./data/entities");
        App.mapRegistry.load("data/maps");
        /* to check is Json entities ok or not */
        App.recipeRegistry.checkIngredients();
        App.buildingRegistry.load("data/buildings");

        App.entityRegistry.addChild(App.buildingRegistry);

        App.entityRegistry.listEntities();

        App.getView().log("Done.");
    }

    @Override
    public void dispose() {
        App.saveState();
        batch.dispose();
    }
}
