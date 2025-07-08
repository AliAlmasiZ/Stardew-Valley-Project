package com.ap.stardew.models.enums;

public enum Material {
    COAL(0),
    STONE(0),
    INITIAL(0),
    COPPER(1),
    IRON(2),
    GOLD(3),
    IRIDIUM(4),

    ;

    private final int level;

    Material(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static Material getMaterialByLevel(int level) {
        if(level == 0)
            return INITIAL;
        for (Material value : Material.values()) {
            if(value.getLevel() == level)
                return value;
        }
        return null;
    }


    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
