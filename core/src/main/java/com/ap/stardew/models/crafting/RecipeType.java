package com.ap.stardew.models.crafting;

public enum RecipeType {
    COOKING,
    CRAFTING,
    ARTISAN
;

    public static RecipeType fromName(String name) {
        for (RecipeType value : RecipeType.values()) {
            if(value.name().equalsIgnoreCase(name.trim()))
                return value;
        }
        return null;
    }
}
