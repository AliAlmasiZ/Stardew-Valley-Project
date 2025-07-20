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
    private Skin customSkin = new Skin(Gdx.files.internal("skin/Stardew Valley Skin/skin.json"));
    private BitmapFont font;

    // Clock Images
    public TextureRegion clock;
    public TextureRegion clockHand;
    public TextureRegion[] icons = new TextureRegion[12];

    //inGameMenu
    public final TextureRegion inventoryIcon, buildMenuIcon, mapIcon;
    public final Texture menuBackground;

    //inventory
    public final Texture inventorySlotFrame;
    public final Texture testSlot;

    private GameAssetManager(){
        for (Texture texture : customSkin.getAtlas().getTextures()) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        this.font = new BitmapFont(Gdx.files.internal("Content/font/khodayaBaseDige.fnt"));
        this.font.getData().setScale(Gdx.graphics.getDensity() * 0.3f);
        System.out.println(Gdx.graphics.getDensity());

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

        inventoryIcon = new TextureRegion(new Texture("Content/InventoryIcon.png"));
        mapIcon = new TextureRegion(new Texture("Content/MapIcon.png"));
        buildMenuIcon = new TextureRegion(new Texture("Content/BuildingMenuIcon.png"));

        inventoryIcon.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        mapIcon.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        buildMenuIcon.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        testSlot = new Texture("Content/Tools/Pickaxe/copper/20.png");
        inventorySlotFrame = new Texture("Content/InventorySlotFrame.png");

        menuBackground = new Texture("Content/frameNinePatch2.png");

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

    public Skin getCustomSkin() {
        return customSkin;
    }
}
