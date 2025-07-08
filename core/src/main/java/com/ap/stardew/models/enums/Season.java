package com.ap.stardew.models.enums;

public enum Season {
    SPRING {
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
    SUMMER {
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
    FALL {
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
    WINTER {
        @Override
        public Weather getWeather() {
            return Weather.SNOWY;
        }
    };

    public void updatePlant() {

    }

    ;

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
}
