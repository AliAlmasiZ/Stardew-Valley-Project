package com.ap.stardew.models.enums;

import com.ap.stardew.controllers.GameAssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum Season {
    SPRING(3) {
        @Override
        public Weather getWeather() {
            int random = (int) (Math.random() * 3);
            return switch (random) {
                case 0 -> Weather.SUNNY;
                case 1 -> Weather.RAINY;
                default -> Weather.STORMY;
            };
        }
    },
    SUMMER(1) {
        @Override
        public Weather getWeather() {
            int random = (int) (Math.random() * 3);
            return switch (random) {
                case 0 -> Weather.SUNNY;
                case 1 -> Weather.RAINY;
                default -> Weather.STORMY;
            };
        }
    },
    FALL(2) {
        @Override
        public Weather getWeather() {
            int random = (int) (Math.random() * 3);
            return switch (random) {
                case 0 -> Weather.SUNNY;
                case 1 -> Weather.RAINY;
                default -> Weather.STORMY;
            };
        }
    },
    WINTER(10) {
        @Override
        public Weather getWeather() {
            return Weather.SNOWY;
        }
    };

    Season(int imageNumber) {
        this.imageNumber = imageNumber;
    }

    public void updatePlant() {

    }

    ;

    private final int imageNumber;

    public static Season nextSeason(Season season) {
        switch (season) {
            case SPRING -> {
                return SUMMER;
            }
            case SUMMER -> {
                return FALL;
            }
            case FALL -> {
                return WINTER;
            }
            case WINTER -> {
                return SPRING;
            }
        }

        return null;
    }

    public abstract Weather getWeather();


    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public int getImageNumber() {
        return imageNumber;
    }

    public TextureRegion getTextureRegion() {
        return GameAssetManager.getInstance().icons[imageNumber];
    }
}
