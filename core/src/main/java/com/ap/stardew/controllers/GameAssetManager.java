package com.ap.stardew.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameAssetManager extends AssetManager {
    private static GameAssetManager gameAssetManager;
    private static Skin skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));

    public static GameAssetManager getGameAssetManager() {
        if (gameAssetManager == null) {
            gameAssetManager = new GameAssetManager();
        }

        return gameAssetManager;
    }

    public static Skin getSkin() {
        return skin;
    }


}
