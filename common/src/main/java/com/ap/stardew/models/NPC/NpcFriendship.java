package com.ap.stardew.models.NPC;

import java.io.Serializable;

public class NpcFriendship implements Serializable {
    private boolean wasMetToday;
    private boolean wasGiftedToday;
    private int timeSinceFriend;

    private int level;
    private int xp;

    public boolean wasMetToday() {
        return wasMetToday;
    }

    public void setWasMetToday(boolean wasMetToday) {
        this.wasMetToday = wasMetToday;
    }

    public boolean isWasGiftedToday() {
        return wasGiftedToday;
    }

    public void setWasGiftedToday(boolean wasGiftedToday) {
        this.wasGiftedToday = wasGiftedToday;
    }

    public int getTimeSinceFriend() {
        return timeSinceFriend;
    }

    public void setTimeSinceFriend(int timeSinceFriend) {
        this.timeSinceFriend = timeSinceFriend;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void addXp(int xp) {
        this.xp += xp;
        if (this.xp >= 800) {
            this.xp = 799;
        }

        this.level = (this.xp / 200);
    }

    public void updatePerDay() {
        wasMetToday = false;
        wasGiftedToday = false;
    }
}
