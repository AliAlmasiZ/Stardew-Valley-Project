package com.ap.stardew.models.enums;

import java.util.List;
import java.util.Map;

public enum SkillType {
    FARMING (
            Map.of(
                    1, List.of("farmer's lunch", "Sprinkler", "Bee House"),
                    2, List.of("Quality Sprinkler" , "Deluxe Scarecrow", "Cheese Press", "Preserves Jar"),
                    3, List.of("Iridium Sprinkler", "Keg", "Loom", "Oil Maker")

            ),
        "farmingSkillIcon"
    ),
    FORAGING (
            Map.of(
                    1, List.of("Charcoal Klin"),
                    2, List.of("vegetable medley"),
                    3, List.of("survival burger"),
                    4, List.of("Mystic Tree Seed")
            ),
        "foragingSkillIcon"
    ),
    MINING (
            Map.of(
                    1, List.of("miner's treat"),
                    2, List.of(),
                    3, List.of()
            ),
        "miningSkillIcon"
    ),
    FISHING(
            Map.of(
                    1, List.of(),
                    2, List.of("dish O' the Sea"),
                    3, List.of("seaform Pudding")
            ),
        "fishingSkillIcon"
    );

    public final String icon;

    SkillType(Map<Integer, List<String>> recipes, String icon) {
        this.recipes = recipes;
        this.icon = icon;
    }

    Map<Integer, List<String>> recipes;
    public static SkillType getSkillType(String input) {
        for (SkillType type : SkillType.values()) {
            if (type.toString().equalsIgnoreCase(input)) {
                return type;
            }
        }
        return null;
    }
}
