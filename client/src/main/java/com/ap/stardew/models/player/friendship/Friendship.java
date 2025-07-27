package com.ap.stardew.models.player.friendship;

abstract public class Friendship {
    protected int amount;
    protected int level;

    abstract void changeAmount(int amount);
}
