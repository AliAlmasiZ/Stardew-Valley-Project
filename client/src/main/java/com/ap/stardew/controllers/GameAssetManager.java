package com.ap.stardew.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import org.w3c.dom.Text;

public class GameAssetManager extends AssetManager {
    private static GameAssetManager instance;
    private Skin skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
    private Skin customSkin = new Skin(Gdx.files.internal("skin/Stardew Valley Skin/skin.json"));
    private BitmapFont font;

    // Clock Images
    public TextureRegion clock;
    public TextureRegion clockHand;
    public TextureRegion[] icons = new TextureRegion[12];

    //NPC Images
    public final Sprite dialog = new Sprite(new Texture("Content/NPC/dialog.png"));

    //Energy bar
    public final Texture energyBar = new Texture("Content/energyBar.png");
    public final Texture energyBarFill = new Texture("Content/energyBarFill.png");

    //inGameMenu
    public final Texture menuBackground;
    public final Texture closeButton;
    public final Texture textBox = new Texture("Content/NPC/textBox.png");

    //inventory
    public final Texture inventorySlotFrame;
    public final Texture inventorySlotFrameSelected;
    public final Texture testSlot;
    public final Texture redCross;
    public final Texture emptyTexture;

    public final Texture tileSelectionBox;


    private GameAssetManager(){
        for (Texture texture : customSkin.getAtlas().getTextures()) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
//        for (ObjectMap.Entry<String, BitmapFont> entry : customSkin.getAll(BitmapFont.class)) {
//            entry.value.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//            entry.value.setUseIntegerPositions(false);
//        }

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

        testSlot = new Texture("Content/Tools/Pickaxe/copper/20.png");
        inventorySlotFrame = new Texture("Content/InventorySlotFrame.png");
        inventorySlotFrameSelected = new Texture("Content/inventorySlotFrameSelected.png");

        tileSelectionBox = new Texture("Content/tileSelectBox.png");

        menuBackground = new Texture("Content/frameNinePatch2.9.png");
        closeButton = new Texture("Content/closeButton.png");

        redCross = new Texture("Content/redCross.png");

        this.load("Content(unpacked)/Characters/Farmer/farmer_base.png", Texture.class);
        this.load("Content(unpacked)/Characters/Farmer/hairstyles.png", Texture.class);
        this.load("Content(unpacked)/Characters/Farmer/pants.png", Texture.class);
        this.load("Content(unpacked)/Characters/Farmer/shirts.png", Texture.class);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();

        emptyTexture = new Texture(pixmap);
        pixmap.dispose();
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

    public void loadTexturesRecursively(FileHandle dir) {
        for (FileHandle file : dir.list()) {
            if (file.isDirectory()) {
                loadTexturesRecursively(file);
            } else if (file.extension().equalsIgnoreCase("png")) {
                String path = file.path();
                load(path, Texture.class);
            }
        }
    }
    public Texture getTexture(String filePath){
        try {
            return super.get(filePath);
        } catch (Exception e) {
            return redCross;
        }
    }
}
