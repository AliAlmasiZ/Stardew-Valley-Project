package com.ap.stardew.models.entities.components;

import com.ap.stardew.controllers.GameAssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.models.App;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.enums.EntityTag;
import com.ap.stardew.models.enums.Season;
import com.ap.stardew.models.enums.Weather;
import com.ap.stardew.models.utils.StringUtils;
import com.ap.stardew.records.Result;

import java.io.Serializable;
import java.util.ArrayList;

public class Growable extends EntityComponent implements Serializable {
    @JsonProperty("seasons")
    private ArrayList<Season> growingSeasons = new ArrayList<>();
    @JsonProperty("fruit")
    private final String fruit;
    @JsonProperty("seed")
    private final String seed;
    @JsonProperty("stages")
    private ArrayList<Integer> stages = new ArrayList<>();
    @JsonProperty("totalHarvestTime")
    private int totalHarvestTime;
    private boolean wateredToday;
    private int wateredFertilizedDays;
    private int daysPastFromWatered;
    @JsonProperty("canBecomeGiant")
    private boolean canBecomeGiant;
    private int daysPastFromPlant;
    private boolean isFertilized;
    @JsonProperty("regrowthTime")
    private int regrowthTime;
    private int daysPastFromRegrowth = 0;
    @JsonProperty("oneTime")
    private boolean oneTime;
    private boolean grown;

    @JsonIgnore
    private boolean isSpritesLoaded = false; // Guess its the safest way
    private Sprite[] sprites = new Sprite[10];

    public Growable(ArrayList<Season> growingSeasons, String fruit, String seed, int totalHarvestTime) {
        this.growingSeasons = growingSeasons;
        this.fruit = fruit;
        this.seed = seed;
        this.totalHarvestTime = totalHarvestTime;
    }

    public Growable(Growable other) {
        this.growingSeasons.addAll(other.growingSeasons);
        this.fruit = other.fruit;
        this.seed = other.seed;
        this.totalHarvestTime = other.totalHarvestTime;
        this.wateredToday = other.wateredToday;
        this.canBecomeGiant = other.canBecomeGiant;
        this.daysPastFromPlant = other.daysPastFromPlant;
        this.isFertilized = other.isFertilized;
        this.oneTime = other.oneTime;
        this.regrowthTime = other.regrowthTime;
        this.stages = other.stages;
    }

    public Growable() {
        this.seed = null;
        this.fruit = null;
    }

    public int getTotalHarvestTime() {
        return totalHarvestTime;
    }

    public ArrayList<Season> getGrowingSeasons() {
        return growingSeasons;
    }

    public void setGrowingSeasons(ArrayList<Season> growingSeasons) {
        this.growingSeasons = growingSeasons;
    }

    public String getFruit() {
        return fruit;
    }

    public String getSeed() {
        return seed;
    }

    public ArrayList<Integer> getStages() {
        return stages;
    }

    @JsonIgnore
    public Sprite[] getSprites() {
        return sprites;
    }

    public boolean isWateredToday() {
        return wateredToday;
    }

    public void setStages(ArrayList<Integer> stages) {
        this.stages = stages;
    }

    public int getDaysPastFromWatered() {
        return daysPastFromWatered;
    }

    public int getWateredFertilizedDays() {
        return wateredFertilizedDays;
    }

    public void setWateredFertilizedDays(int wateredFertilizedDays) {
        this.wateredFertilizedDays = wateredFertilizedDays;
    }

    public void addWateredFertilizedDays(int wateredFertilizedDays) {
        this.wateredFertilizedDays += wateredFertilizedDays;
    }

    public void setDaysPastFromWatered(int daysPastFromWatered) {
        this.daysPastFromWatered = daysPastFromWatered;
    }

    public int getDaysPastFromRegrowth() {
        return daysPastFromRegrowth;
    }

    public void setDaysPastFromRegrowth(int daysPastFromRegrowth) {
        this.daysPastFromRegrowth = daysPastFromRegrowth;
    }

    public boolean isGrown() {
        return grown;
    }

    public void setGrown(boolean grown) {
        this.grown = grown;
    }

    public void setWateredToday(boolean wateredToday) {
        this.wateredToday = wateredToday;
    }

    public boolean isCanBecomeGiant() {
        return canBecomeGiant;
    }

    public void setTotalHarvestTime(int totalHarvestTime) {
        this.totalHarvestTime = totalHarvestTime;
    }

    public int getDaysPastFromPlant() {
        return daysPastFromPlant;
    }

    public void setDaysPastFromPlant(int daysPastFromPlant) {
        this.daysPastFromPlant = daysPastFromPlant;
    }

    public boolean isFertilized() {
        return isFertilized;
    }

    public void setFertilized(boolean fertilized) {
        isFertilized = fertilized;
    }

    public void setCanBecomeGiant(boolean canBecomeGiant) {
        this.canBecomeGiant = canBecomeGiant;
    }

    @Override
    public EntityComponent clone() {
        return new Growable(this);
    }

    public String getInfo() {
        StringBuilder message = new StringBuilder();
        message.append("Days left to harvest: ").append(totalHarvestTime - daysPastFromPlant).append("\n");
        message.append("Stage: ").append(getStage()).append("\n");
        message.append("IsWateredToday: ").append(wateredToday).append("\n");
        message.append("IsFertilized: ").append(isFertilized).append("");

        return message.toString();
    }

    public int getRegrowthTime() {
        return regrowthTime;
    }

    public void setRegrowthTime(int regrowthTime) {
        this.regrowthTime = regrowthTime;
    }

    public boolean isOneTime() {
        return oneTime;
    }

    public void setOneTime(boolean oneTime) {
        this.oneTime = oneTime;
    }

    public int getStage() {
        int count = 0;
        int stage = 0;
        for (int j = 0; j < stages.size(); j++) {
            count += stages.get(j);
            stage++;
            if (count >= daysPastFromPlant) {
                return stage;
            }
        }
        return stage;
    }

    public void updatePerDay() {
        Game game = App.getActiveGame();
        Season season = game.getDate().getSeason();
        if (isInGreenhouse() || this.getGrowingSeasons().contains(season)) {
            if (daysPastFromPlant < totalHarvestTime) {
                daysPastFromPlant++;
                if (daysPastFromPlant == totalHarvestTime) {
                    daysPastFromRegrowth = regrowthTime;
                }
            } else if (!isOneTime() && daysPastFromRegrowth < regrowthTime) {
                daysPastFromRegrowth++;
            }
        }

        /*----------------------- Handle watered --------------------- */
        if (!isWateredToday()) {
            daysPastFromWatered++;
        } else {
            daysPastFromWatered = 0;
        }

        if (wateredFertilizedDays > 0) {
            wateredFertilizedDays--;
        }else {
            setWateredToday(false);
        }

        Weather weather = App.getActiveGame().getTodayWeather();
        if (weather == Weather.STORMY || weather == Weather.RAINY) {
            setWateredToday(true);
        }
        /*-------------------------------------------------------*/


    }

    public Result canCollectProduct() {
        Game game = App.getActiveGame();
        Season season = game.getDate().getSeason();

        if (!this.getGrowingSeasons().contains(season)) {
            return new Result(false, "You can't collect product form this plant in this season!");
        }
        if (isOneTime() && daysPastFromPlant < totalHarvestTime) {
            return new Result(false, "Its not time to collect product");
        }
        if (!isOneTime() && daysPastFromRegrowth < regrowthTime) {
            return new Result(false, "Its not time to collect product");
        }

        return new Result(true, "Collected successfully!");
    }

    public Entity collectFruit() {
        if (!canCollectProduct().isSuccessful()) {
            return null;
        }

        if (entity.hasTag(EntityTag.CROP)) {
            Entity fruit = entity.clone();
            return fruit;
        } else if (entity.hasTag(EntityTag.TREE)) {
            Entity fruit = App.entityRegistry.makeEntity(this.fruit);
            return fruit;
        }

        return null;
    }

    public Sprite getCurrentSprite() {
        if (!isSpritesLoaded) loadSprites();

        int currentStage = getStage();

        if (entity.hasTag(EntityTag.CROP)) {
            if (currentStage < stages.size()) return sprites[currentStage - 1];
            if (daysPastFromPlant >= totalHarvestTime) return sprites[currentStage];
        } else if (entity.hasTag(EntityTag.TREE)) {
            if (currentStage < stages.size()) return sprites[currentStage - 1];
            if (canCollectProduct().isSuccessful()) return sprites[currentStage + 1];
            if (daysPastFromPlant >= totalHarvestTime) return sprites[currentStage];
        }

        return sprites[0];
    }

    private void loadSprites() {
        String name = entity.getEntityName().replaceAll(" ", "_");
        GameAssetManager gameAssetManager = GameAssetManager.getInstance();

        if (entity.hasTag(EntityTag.CROP)) {
            ArrayList<Sprite> spritesToLoad = new ArrayList<>();
            for (int i = 0; i < stages.size() * 2; i++) {
                int j = i + 1;
                try {
                Sprite s = new Sprite(gameAssetManager.get("Content/Crops/" + name + "_Stage_" + j + ".png", Texture.class));

                spritesToLoad.add(s);
                } catch (Exception e) {
                    break;
                }
            }

            for (int i = 0; i < stages.size(); i++) {
                sprites[i] = spritesToLoad.get(i);
            }
            sprites[stages.size()] = spritesToLoad.get(spritesToLoad.size() - 1);

        } else if (entity.hasTag(EntityTag.TREE)) {
            for (int i = 0; i < stages.size(); i++) {
                int j = i + 1;
                sprites[i] = new Sprite(gameAssetManager.get("Content/Trees/" + name + "_Stage_" + j + ".png", Texture.class));
            }

            try {
                int t = stages.size() + 1;
                Texture texture = gameAssetManager.get("Content/Trees/" + name + "_Stage_" + t + ".png", Texture.class);
                TextureRegion textureRegion = new TextureRegion(texture);
                float width = texture.getWidth();
                float height = texture.getHeight();
                if (width > height * 2) {
                    textureRegion = new TextureRegion(texture, 0 , 0, height, width / 4);
                }
                sprites[stages.size()] = new Sprite(textureRegion);
            } catch (Exception e) {
                sprites[stages.size()] = sprites[stages.size() - 1];
            }

            try {
                sprites[stages.size() + 1] = new Sprite(gameAssetManager.get("Content/Trees/" + name + "_Stage_5_Fruit" + ".png", Texture.class));
            } catch (Exception e) {
                sprites[stages.size() + 1] = sprites[stages.size() - 1];
            }
        }

        for (int i = 0; i <= stages.size() + 1; i++) {
            sprites[i].scale(0.8f);
        }

    }

    private boolean isInGreenhouse() {
        Entity building = this.entity.getComponent(PositionComponent.class).getMap().getBuilding();
        if (building == null) return false;
        return StringUtils.isNamesEqual(building.getEntityName(), "greenhouse");
    }
}
