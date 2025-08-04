package com.ap.stardew.models.enums;

public enum TileType {
    GRASS('#'),
    WATER('.', false),
    PLOWED('#'),
    DIRT('#'),
    STONE('#'),
    WOOD('#'),
    PLANTED_GROUND('#'),
    DOOR('#'),

    WALL('#', false),
    ROAD('.'),
    ;

    TileType(char character) {
        this.character = character;
        this.isWalkable = true;
    }

    TileType(char character, boolean isWalkable) {
        this.character = character;
        this.isWalkable = isWalkable;
    }

    public final char character;
    public final boolean isWalkable;
}
