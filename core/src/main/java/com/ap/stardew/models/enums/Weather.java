package com.ap.stardew.models.enums;

public enum Weather {
    SUNNY(1.5f, 1),
    RAINY(1.2f, 1.5f),
    STORMY(0.5f, 1.5f),
    SNOWY(1, 2);

    private final double fishingEffect;
    private final double energyEffect;

    Weather(double fishingEffect, double energyEffect) {
        this.fishingEffect = fishingEffect;
        this.energyEffect = energyEffect;
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
}
