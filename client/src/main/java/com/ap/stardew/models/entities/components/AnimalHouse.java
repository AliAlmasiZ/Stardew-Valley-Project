package com.ap.stardew.models.entities.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.models.animal.Animal;
import com.ap.stardew.models.animal.AnimalHouseLevel;
import com.ap.stardew.models.animal.AnimalHouseType;

import java.io.Serializable;
import java.util.ArrayList;

public class AnimalHouse extends EntityComponent implements Serializable {
    @JsonProperty("houseType")
    private AnimalHouseType type;
    @JsonProperty("level")
    private AnimalHouseLevel level;
    private String name;
    private ArrayList<Animal> animals = new ArrayList<>();

    public AnimalHouse(AnimalHouseType type, AnimalHouseLevel level) {
        this.type = type;
        this.level = level;
    }

    public AnimalHouse(AnimalHouse other){
        this.type = other.type;
        this.level = other.level;
    }

    public AnimalHouse() {
    }

    public AnimalHouseType getType() {
        return type;
    }

    public void setType(AnimalHouseType type) {
        this.type = type;
    }

    public int getCapacity() {
        return level.getCapacity();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(ArrayList<Animal> animals) {
        this.animals = animals;
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public int getAvailableCapacity() {
        int occupiedCapacity = 0;
        for (Animal animal : animals) {
            occupiedCapacity ++;
        }

        return getCapacity() - occupiedCapacity;
    }

    public String getDetail () {
        StringBuilder detail = new StringBuilder();
        detail.append("Name: ").append(name).append("\t");
        detail.append("Type: ").append(entity.getEntityName()).append("\t");
        detail.append("Available capacity: ").append(getAvailableCapacity()).append("\t");

        return detail.toString();
    }

    @Override
    public EntityComponent clone() {
        return new AnimalHouse(this);
    }
}
