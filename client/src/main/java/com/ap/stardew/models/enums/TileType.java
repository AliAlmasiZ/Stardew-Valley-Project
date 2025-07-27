package com.ap.stardew.models.enums;

import com.ap.stardew.views.old.inGame.Color;

public enum TileType {
    GRASS('#', new Color(76,167,30)),
    WATER('.', new Color(23,176,186), false),
    PLOWED('#', new Color(77, 55, 18)),
    DIRT('#', new Color(224,175,83)),
    STONE('#', new Color(97, 97, 97)),
    WOOD('#', new Color(107, 60, 21)),
    PLANTED_GROUND('#', new Color(107, 60, 21)),
    DOOR('#', Color.BLACK),

    WALL('#', new Color(200, 200, 200), false),
    ROAD('.', new Color(158, 158, 158)),
    ;

    TileType(char character, Color color) {
        this.character = character;
        this.color = color;
        this.isWalkable = true;
    }

    TileType(char character, Color color, boolean isWalkable) {
        this.character = character;
        this.color = color;
        this.isWalkable = isWalkable;
    }

    public final char character;
    public final Color color;
    public final boolean isWalkable;
}
