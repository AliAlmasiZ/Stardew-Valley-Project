package com.ap.stardew.controllers;

import com.ap.stardew.models.App;
import com.ap.stardew.models.animal.Animal;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.Renderable;
import com.ap.stardew.models.entities.components.Growable;
import com.ap.stardew.models.enums.EntityTag;
import com.ap.stardew.models.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

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
    public final Texture giftIcon = new Texture("Content/GiftIcon.png");

    //inventory
    public final Texture inventorySlotFrame;
    public final Texture inventorySlotFrameSelected;
    public final Texture testSlot;
    public final Texture redCross;
    public final Texture emptyTexture;

    public final Texture tileSelectionBox;

    //Trees and Crops
    private HashMap<String, Sprite[]> plantsSprites = new HashMap<>();

    // Animals
    private HashMap<String, Animation<Sprite>> walkingAnimation = new HashMap<>();
    private HashMap<String, Animation<Sprite>> eatingAnimation = new HashMap<>();
    private HashMap<String, Animation<Sprite>> petAnimation = new HashMap<>();

    private HashMap<String, Sprite> normalSprite = new HashMap<>();


    private GameAssetManager() {
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

    public Texture getTexture(String filePath) {
        try {
            return super.get(filePath);
        } catch (Exception e) {
            return redCross;
        }
    }

    public Sprite getEntitySpriteToRender(Entity entity, float deltaTime) {
        if (entity.hasTag(EntityTag.TREE) || entity.hasTag(EntityTag.CROP)) return getPlantSprite(entity);

        if (entity instanceof Player) {
            Player player = (Player) entity;
            return player.getSprite();
        }

        Renderable renderable = entity.getComponent(Renderable.class);
        Renderable.Statue currentStatue = renderable.getCurrentStatue();
        float timeLeftForStatue = renderable.getTimeLeftForStatue();

        switch (currentStatue) {
            case NORMAL -> {
                if (!normalSprite.containsKey(entity.getEntityName())) loadNormalSprite(entity);
                return normalSprite.get(entity.getEntityName());
            }
            case LEFT_WALKING -> {
                renderable.reduceTimeLeftForStatue(deltaTime);
                if (timeLeftForStatue <= 0.0f) {
                    renderable.setCurrentStatue(Renderable.Statue.NORMAL);
                }

                if (!walkingAnimation.containsKey(entity.getEntityName())) loadWalkingAnimation(entity);

                Sprite result = new Sprite(walkingAnimation.get(entity.getEntityName()).getKeyFrame(2000000 - timeLeftForStatue));
                result.flip(true, false);
                return result;
            }
            case RIGHT_WALKING -> {
                renderable.reduceTimeLeftForStatue(deltaTime);
                if (timeLeftForStatue <= 0.0f) {
                    renderable.setCurrentStatue(Renderable.Statue.NORMAL);
                }

                if (!walkingAnimation.containsKey(entity.getEntityName())) loadWalkingAnimation(entity);

                return walkingAnimation.get(entity.getEntityName()).getKeyFrame(2000000 - timeLeftForStatue);
            }
            case EATING -> {
                renderable.reduceTimeLeftForStatue(deltaTime);
                if (timeLeftForStatue <= 0.0f) {
                    renderable.setCurrentStatue(Renderable.Statue.NORMAL);
                }

                if (!eatingAnimation.containsKey(entity.getEntityName())) loadEatingAnimation(entity);

                return eatingAnimation.get(entity.getEntityName()).getKeyFrame(2000000 - timeLeftForStatue);
            }
            case PET -> {
                renderable.reduceTimeLeftForStatue(deltaTime);
                if (timeLeftForStatue <= 0.0f) {
                    renderable.setCurrentStatue(Renderable.Statue.NORMAL);
                }

                if (!petAnimation.containsKey(entity.getEntityName())) loadPetAnimation(entity);

                return petAnimation.get(entity.getEntityName()).getKeyFrame(200 - timeLeftForStatue);
            }
        }


        return null;
    }

    public Sprite getPlantSprite(Entity entity) {
        String plantName = entity.getEntityName();
        if (!plantsSprites.containsKey(plantName)) loadPlantSprite(plantName);
        Sprite[] sprites = plantsSprites.get(plantName);

        Growable growable = entity.getComponent(Growable.class);
        int stage = growable.getStage();

        if (growable.canCollectProduct().isSuccessful()) return sprites[stage + 1];
        else if (growable.getDaysPastFromPlant() >= growable.getTotalHarvestTime()) return sprites[stage];
        else return sprites[stage - 1];
    }

    public Sprite getNormalSprite(Entity entity) {
        String normalName = entity.getEntityName();
        if (!normalSprite.containsKey(normalName)) loadNormalSprite(entity);

        return normalSprite.get(normalName);
    }

    private void loadPlantSprite(String plantName) {
        Entity plant = App.entityRegistry.makeEntity(plantName);
        if (plant.hasTag(EntityTag.CROP)) setCropSprites(plant);
        if (plant.hasTag(EntityTag.TREE)) setTreeSprites(plant);
    }

    private void setCropSprites(Entity plant) {
        int stagesNumber = plant.getComponent(Growable.class).getStages().size();
        String name = plant.getEntityName();
        Sprite[] cropSprites;

        try {
            cropSprites = cropSprites(name, stagesNumber);
        } catch (Exception e) {
            name = "Artichoke";
            cropSprites = cropSprites(name, stagesNumber);
        }

        for (int i = 0; i <= stagesNumber + 1; i++) {
            if (cropSprites[i] != null) {
                cropSprites[i].setSize(18, 18); //TODO : It should be tile size
            }
        }

        plantsSprites.put(plant.getEntityName(), cropSprites);
    }

    private Sprite[] cropSprites(String name, int stageNumber) {
        ArrayList<Sprite> spritesToLoad = new ArrayList<>();
        Sprite[] sprites = new Sprite[12];
        for (int i = 0; i < stageNumber * 2; i++) {
            int j = i + 1;
            try {
                Sprite s = new Sprite(get("Content/Crops/" + name + "_Stage_" + j + ".png", Texture.class));

                spritesToLoad.add(s);
            } catch (Exception e) {
                break;
            }
        }

        for (int i = 0; i < stageNumber; i++) {
            sprites[i] = spritesToLoad.get(i);
        }
        sprites[stageNumber] = spritesToLoad.get(spritesToLoad.size() - 1);

        return sprites;
    }

    private void setTreeSprites(Entity plant) {
        int stagesNumber = plant.getComponent(Growable.class).getStages().size();
        String name = plant.getEntityName();
        Sprite[] treeSprites = new Sprite[stagesNumber + 3];

        try {
            treeSprites = treeSprites(name, stagesNumber);

        } catch (Exception e) {
            try {
                name = name.replace("_Tree", "");
                treeSprites = treeSprites(name, stagesNumber);
            }  catch (Exception e1) {
                name = "Mango";
                treeSprites = treeSprites(name, stagesNumber);
            }
        }

        for (int i = 0; i <= stagesNumber + 1; i++) {
            if (treeSprites[i] != null) {
                treeSprites[i].setSize(20, 40);
            }
        }
        plantsSprites.put(plant.getEntityName(), treeSprites);
    }

    private Sprite[] treeSprites(String name, int stageNumber) {
        Sprite[] sprites = new Sprite[stageNumber + 10];
        for (int i = 0; i < stageNumber; i++) {
            int j = i + 1;
            sprites[i] = new Sprite(get("Content/Trees/" + name + "_Stage_" + j + ".png", Texture.class));
        }

        try {
            int t = stageNumber + 1;
            Texture texture = get("Content/Trees/" + name + "_Stage_" + t + ".png", Texture.class);
            TextureRegion textureRegion = new TextureRegion(texture);
            float width = texture.getWidth();
            float height = texture.getHeight();
            if (width > height * 2) {
                textureRegion = new TextureRegion(texture, 0, 0, height, width / 4);
            }
            sprites[stageNumber] = new Sprite(textureRegion);
        } catch (Exception e) {
            sprites[stageNumber] = sprites[stageNumber - 1];
        }

        try {
            sprites[stageNumber + 1] = new Sprite(get("Content/Trees/" + name + "_Stage_5_Fruit" + ".png", Texture.class));
        } catch (Exception e) {
            sprites[stageNumber + 1] = sprites[stageNumber - 1];
        }

        return sprites;
    }

    private void loadWalkingAnimation(Entity entity) {
        if (entity instanceof Animal) {
            Animal animal = (Animal) entity;

            Texture idleImage = new Texture("Content/Animal/" + animal.getAnimalType().name().toLowerCase() + "/walking.png");

            Animation<Sprite> walkingAnimationToAdd = new Animation<>(0.25f, getSplitSprites(idleImage, 4));
            walkingAnimationToAdd.setPlayMode(Animation.PlayMode.LOOP);

            walkingAnimation.put(animal.getEntityName(), walkingAnimationToAdd);
        }

    }

    private void loadPetAnimation(Entity entity) {
        if (entity instanceof Animal) {
            Animal animal = (Animal) entity;

            Texture idleImage = new Texture("Content/Animal/" + animal.getAnimalType().name().toLowerCase() + "/pet.png");

            Animation<Sprite> walkingAnimationToAdd = new Animation<>(0.05f, getSplitSprites(idleImage, 4));
            walkingAnimationToAdd.setPlayMode(Animation.PlayMode.LOOP_RANDOM);

            petAnimation.put(animal.getEntityName(), walkingAnimationToAdd);
        }
    }

    private void loadEatingAnimation(Entity entity) {
        if (entity instanceof Animal) {
            Animal animal = (Animal) entity;

            Texture idleImage = new Texture("Content/Animal/" + animal.getAnimalType().name().toLowerCase() + "/eating.png");

            Animation<Sprite> walkingAnimationToAdd = new Animation<>(0.2f, getSplitSprites(idleImage, 4));
            walkingAnimationToAdd.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

            eatingAnimation.put(animal.getEntityName(), walkingAnimationToAdd);
        }
    }

    private void loadNormalSprite(Entity entity) {
        Sprite sprite;
        try {
            sprite = new Sprite(get(entity.getComponent(Renderable.class).getSpritePath(), Texture.class));
        } catch (Exception e) {
            sprite = new Sprite(getTexture(entity.getEntityName()));
        }
        if (entity instanceof Animal) {
            sprite.setSize(32, 32);
        }

        normalSprite.put(entity.getEntityName(), sprite);
    }

    private Sprite[] getSplitSprites(Texture texture, int number) {
        int tileWidth = texture.getWidth() / number;
        int tileHeight = texture.getHeight();
        Sprite[] sprites = new Sprite[number];

        TextureRegion[][] tiles = TextureRegion.split(texture, tileWidth, tileHeight);

        for (int i = 0; i < number; i++) {
            sprites[i] = new Sprite(tiles[0][i]);
        }
        return sprites;
    }
}
