package com.ap.stardew.controllers;

import com.ap.stardew.models.App;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Growable;
import com.ap.stardew.models.enums.EntityTag;
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

import java.util.ArrayList;
import java.util.HashMap;

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

    //Trees and Crops
    HashMap<String, Sprite[]> plantsSprites;


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

    public Sprite getPlantSprite(String plantName, int Stage, boolean fruit) {
        if (!plantsSprites.containsKey(plantName)) loadPlantSprite(plantName);
        Sprite[] sprites = plantsSprites.get(plantName);

        if (fruit) return sprites[Stage];
        else return sprites[Stage - 1];
    }

    public void loadPlantSprite(String plantName) {
        Entity plant = App.entityRegistry.makeEntity(plantName);
        if (plant.hasTag(EntityTag.CROP))  setCropSprites(plant);
        if (plant.hasTag(EntityTag.TREE)) setTreeSprites(plant);
    }

    private void setCropSprites(Entity plant) {
        int stagesNumber = plant.getComponent(Growable.class).getStages().size();
        String name = plant.getEntityName();
        Sprite[] cropSprites = new Sprite[stagesNumber + 3];
        ArrayList<Sprite> spritesToLoad = new ArrayList<>();
        for (int i = 0; i < stagesNumber * 2; i++) {
            int j = i + 1;
            try {
                Sprite s = new Sprite(get("Content/Crops/" + name + "_Stage_" + j + ".png", Texture.class));

                spritesToLoad.add(s);
            } catch (Exception e) {
                break;
            }
        }

        for (int i = 0; i < stagesNumber; i++) {
            cropSprites[i] = spritesToLoad.get(i);
        }
        cropSprites[stagesNumber] = spritesToLoad.get(spritesToLoad.size() - 1);
        plantsSprites.put(plant.getEntityName(), cropSprites);
    }

    public void setTreeSprites(Entity plant) {
        int stagesNumber = plant.getComponent(Growable.class).getStages().size();
        String name = plant.getEntityName();
        Sprite[] treeSprites = new Sprite[stagesNumber + 3];

        Sprite[] sprites = new Sprite[stagesNumber + 3];

        for (int i = 0; i < stagesNumber; i++) {
            int j = i + 1;
            sprites[i] = new Sprite(get("Content/Trees/" + name + "_Stage_" + j + ".png", Texture.class));
        }

        try {
            int t = stagesNumber + 1;
            Texture texture = get("Content/Trees/" + name + "_Stage_" + t + ".png", Texture.class);
            TextureRegion textureRegion = new TextureRegion(texture);
            float width = texture.getWidth();
            float height = texture.getHeight();
            if (width > height * 2) {
                textureRegion = new TextureRegion(texture, 0, 0, height, width / 4);
            }
            sprites[stagesNumber] = new Sprite(textureRegion);
        } catch (Exception e) {
            sprites[stagesNumber] = sprites[stagesNumber - 1];
        }

        try {
            sprites[stagesNumber + 1] = new Sprite(get("Content/Trees/" + name + "_Stage_5_Fruit" + ".png", Texture.class));
        } catch (Exception e) {
            sprites[stagesNumber + 1] = sprites[stagesNumber];
        }

        plantsSprites.put(plant.getEntityName(), sprites);
    }
}
