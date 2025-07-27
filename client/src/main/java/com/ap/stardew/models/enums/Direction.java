package com.ap.stardew.models.enums;

public enum Direction {
    LEFT(4, -1, 0),
    RIGHT(6, 1, 0),
    UP(8, 0, -1),
    DOWN(2, 0, 1),
    UPLEFT(7, -1, -1),
    UPRIGHT(9, 1, -1),
    DOWNLEFT(1, -1, 1),
    DOWNRIGHT(3, 1, 1),
    ;
    public final int dx;
    public final int dy;
    public final int key;

    Direction(int key, int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
        this.key = key;
    }

    public static Direction getDirection(int i) {
        for (Direction d : Direction.values()) {
            if (d.key == i) {
                return d;
            }
        }
        return null;
    }
}
