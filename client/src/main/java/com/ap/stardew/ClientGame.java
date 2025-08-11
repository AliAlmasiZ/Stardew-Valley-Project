package com.ap.stardew;

import com.ap.stardew.app.ClientApp;
import com.ap.stardew.view.CharacterSpriteManager;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.MainScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ClientGame extends Game {
    private static ClientGame instance;
    private SpriteBatch batch;

    public static ClientGame getInstance() {
        return instance;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void create() {
        ClientApp.startClients();
        ClientApp.connectClients();

        loadDatas();
        instance = this;
        batch = new SpriteBatch();


        setScreen(new MainScreen());
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0.1f, 0f, 1);
        Gdx.gl.glClear(GL32.GL_COLOR_BUFFER_BIT);
        super.render();
    }

    private static void loadDatas() {
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/Tools"));
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/Crops"));
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/Trees"));
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/Animal"));
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/NPC"));
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/Minerals"));
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/ForagingCrops"));
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/Workstations"));
        GameAssetManager.getInstance().finishLoading();
        GameAssetManager.getInstance().characterSpriteManager = new CharacterSpriteManager();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
