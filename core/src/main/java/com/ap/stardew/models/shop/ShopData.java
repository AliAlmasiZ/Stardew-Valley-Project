package com.ap.stardew.models.shop;

import java.io.Serializable;
import java.util.ArrayList;

public class ShopData implements Serializable {
    String name;
    int startHour;
    int endHour;
    ArrayList<BuildingShopProduct> buildings = new ArrayList<>();
    ArrayList<AnimalShopProduct> animals = new ArrayList<>();
    ArrayList<OtherShopProduct> products = new ArrayList<>();
    ArrayList<UpgradableShopProduct> upgrades = new ArrayList<>();


    public ShopData() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public ArrayList<BuildingShopProduct> getBuildings() {
        return buildings;
    }

    public void setBuildings(ArrayList<BuildingShopProduct> buildings) {
        this.buildings = buildings;
    }

    public ArrayList<AnimalShopProduct> getAnimals() {
        return animals;
    }

    public void setAnimals(ArrayList<AnimalShopProduct> animals) {
        this.animals = animals;
    }

    public ArrayList<OtherShopProduct> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<OtherShopProduct> products) {
        this.products = products;
    }

    public ArrayList<UpgradableShopProduct> getUpgrades() {
        return upgrades;
    }

    public void setUpgrades(ArrayList<UpgradableShopProduct> upgrades) {
        this.upgrades = upgrades;
    }
}
