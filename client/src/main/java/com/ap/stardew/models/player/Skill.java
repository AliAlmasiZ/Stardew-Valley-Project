package com.ap.stardew.models.player;

import java.io.Serializable;

public class Skill implements Serializable {
    private int experience;
    private int level;

    public Skill() {
        level = 0;
        experience = 0;
    }

    public int addExperience(int experience) {
        if (level == 4) return 0;
        int currentLevel = level;
        this.experience += experience;
        while (this.experience >= 100 * this.level + 50) {
            this.experience -= 100 * this.level + 50;
            level++;
            if (level == 4) this.experience = 0;
        }
        return level - currentLevel;
    }

    private void addLevel() {

    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public void reset() {
        experience = 0;
        level = 0;
    }
    public int getMaxXp(){
        return 100 * this.level + 50;
    }
}
