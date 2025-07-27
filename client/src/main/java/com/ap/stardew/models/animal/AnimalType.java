package com.ap.stardew.models.animal;

import java.util.ArrayList;
import java.util.Arrays;

public enum AnimalType {
    Chicken(800,
            0,
            new ArrayList<>(Arrays.asList("Egg", "Big Egg")),
            AnimalHouseType.COOP,
            AnimalHouseLevel.NORMAL,
            null),
    Duck(1200,
            1,
            new ArrayList<>(Arrays.asList("Duck Egg", "Duck Feather")),
            AnimalHouseType.COOP,
            AnimalHouseLevel.BIG,
            null),
    Rabbit(8000,
            3,
            new ArrayList<>(Arrays.asList("Wool", "Rabbit Leg")),
            AnimalHouseType.COOP,
            AnimalHouseLevel.DELUXE,
            null),
    Dinosaur(14000,
            6,
            new ArrayList<>(Arrays.asList("Dinosaur Egg")),
            AnimalHouseType.COOP,
            AnimalHouseLevel.BIG,
            null),
    Cow(1500,
            0,
            new ArrayList<>(Arrays.asList("Cow Milk", "Cow Big Milk")),
            AnimalHouseType.BARN,
            AnimalHouseLevel.NORMAL,
            "Milk pail"),
    Goat(4000,
            1,
            new ArrayList<>(Arrays.asList("Goat Milk", "Goat Big Milk")),
            AnimalHouseType.BARN,
            AnimalHouseLevel.BIG,
            "Milk pail"),
    Sheep(8000,
            2,
            new ArrayList<>(Arrays.asList("Sheep Wool")),
            AnimalHouseType.BARN,
            AnimalHouseLevel.DELUXE,
            ""),
    Pig(16000,
            0,
            new ArrayList<>(Arrays.asList("Truffle")),
            AnimalHouseType.BARN,
            AnimalHouseLevel.DELUXE,
            null),
    ;

    private final ArrayList<String> products;
    private final AnimalHouseType animalHouseType;
    private final int cost;
    private final int daysBetweenProduces;
    private final AnimalHouseLevel houseLevel;
    private final String neededTool;


    AnimalType(int cost, int daysBetweenProduces, ArrayList<String> products, AnimalHouseType animalHouseType,
               AnimalHouseLevel animalHouseLevel, String neededTool) {
        this.cost = cost;
        this.daysBetweenProduces = daysBetweenProduces;
        this.products = products;
        this.animalHouseType = animalHouseType;
        this.houseLevel = animalHouseLevel;
        this.neededTool = neededTool;
    }

    public ArrayList<String> getProducts() {
        return products;
    }

    public AnimalHouseType getAnimalHouseType() {
        return animalHouseType;
    }

    public int getCost() {
        return cost;
    }

    public int getDaysBetweenProduces() {
        return daysBetweenProduces;
    }

    public AnimalHouseLevel getHouseLevel() {
        return houseLevel;
    }

    public String getNeededTool() {
        return neededTool;
    }

    public static AnimalType getAnimalTypeByString(String animalTypeString) {
        for (AnimalType animalType : AnimalType.values()) {
            if (animalType.name().equalsIgnoreCase(animalTypeString)) {
                return animalType;
            }
        }
        return null;
    }
}
