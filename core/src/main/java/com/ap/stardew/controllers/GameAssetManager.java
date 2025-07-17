package com.ap.stardew.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameAssetManager extends AssetManager {
    private static GameAssetManager instance;
    private Skin skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
    private BitmapFont font;

    // Clock Images
    public TextureRegion clock;
    public TextureRegion clockHand;
    public TextureRegion[] icons = new TextureRegion[12];

    private GameAssetManager(){
        this.font = new BitmapFont(Gdx.files.internal("Content/font/khodayaBaseDige.fnt"));
        this.font.getData().setScale(0.65f);

        // Clock Images
        clock = new TextureRegion(new Texture(Gdx.files.internal("Content/ClockImages/Clock.png")));
        clockHand = new TextureRegion(new Texture(Gdx.files.internal("Content/ClockImages/ClockHand.png")));
        Texture texture = new Texture(Gdx.files.internal("Content/ClockImages/ClockIcons.png"));
        TextureRegion[][] tempIcons = TextureRegion.split(texture, 13, 9);
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                icons[index++] = tempIcons[i][j];
            }
        }

    }

    public static GameAssetManager getInstance() {
        if (instance == null) {
            instance = new GameAssetManager();
        }
        return instance;
    }

    public Skin getSkin() {
        return skin;
    }

    public BitmapFont getFont() {
        return font;
    }
}
