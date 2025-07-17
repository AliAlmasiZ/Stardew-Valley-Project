package com.ap.stardew.models.enums;

import com.ap.stardew.controllers.GameAssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum Weather {
    SUNNY(1.5f, 1, 7),
    RAINY(1.2f, 1.5f, 6),
    STORMY(0.5f, 1.5f, 11),
    SNOWY(1, 2, 9);

    private final double fishingEffect;
    private final double energyEffect;
    private final int imageNumber;

    Weather(double fishingEffect, double energyEffect, int imageNumber) {
        this.fishingEffect = fishingEffect;
        this.energyEffect = energyEffect;
        this.imageNumber = imageNumber;
    }

    public static Weather getweather(String weatherString) {
        for (Weather weather : Weather.values()) {
            if (weather.name().equalsIgnoreCase(weatherString)) {
                return weather;
            }
        }
        return null;
    }

    public double getFishingEffect() {
        return fishingEffect;
    }

    public double getEnergyEffect() {
        return energyEffect;
    }

    public int getImageNumber() {
        return imageNumber;
    }

    public TextureRegion getTextureRegion() {
        return GameAssetManager.getInstance().icons[imageNumber];
    }
}
