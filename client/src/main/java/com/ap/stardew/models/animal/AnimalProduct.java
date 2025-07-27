package com.ap.stardew.models.animal;

import com.ap.stardew.models.entities.Entity;

import java.io.Serializable;

public class AnimalProduct implements Serializable {
    private final Entity item;
    private final double baseProbability;
    private final boolean moreWhenHappy;        // oonaee ke ba afzayesh khoshhaly ehtemaleshoon bishtare
    private final int minimumHappiness;     // bara pashm boz
    private final int prodctionInterval;

    public AnimalProduct(Entity item, double baseProbability, int prodctionInterval, boolean moreWhenHappy, int minimumHappiness) {
        this.item = item;
        this.moreWhenHappy = moreWhenHappy;
        this.baseProbability = baseProbability;
        this.minimumHappiness = minimumHappiness;
        this.prodctionInterval = prodctionInterval;
    }

    public AnimalProduct(Entity item, double baseProbability, int prodctionInterval, boolean moreWhenHappy) {
        this(item, baseProbability, prodctionInterval, moreWhenHappy, 0);
    }

    public AnimalProduct(Entity item, double baseProbability, int prodctionInterval) {
        this(item, baseProbability, prodctionInterval, false, 0);
    }

    public double getProbabillity(Animal animal) {
        return 1;
    }
}
