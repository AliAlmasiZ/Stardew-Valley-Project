package com.ap.stardew.models.animal;

public enum AnimalHouseLevel {
    NORMAL(4),
    BIG(8),
    DELUXE(12);

    private final int capacity;

    AnimalHouseLevel(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }
}
