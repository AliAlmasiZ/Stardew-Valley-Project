package models.gameMap;

import models.enums.Season;

import java.util.*;

public enum BiomeType {
    FOREST(
            Map.of(
                Season.SPRING, Arrays.asList(
                        new Spawnable("Daffodil", 10),
                        new Spawnable("Dandelion", 10),
                        new Spawnable("Leek", 10),
                        new Spawnable("Morel", 10),
                        new Spawnable("Salmonberry", 10),
                        new Spawnable("Spring Onion", 10),
                        new Spawnable("Wild Horseradish", 10),
                        new Spawnable("Jazz Seeds", 10),
                        new Spawnable("Carrot Seeds", 10),
                        new Spawnable("Cauliflower Seeds", 10),
                        new Spawnable("Coffee Bean", 10),
                        new Spawnable("Garlic Seeds", 10),
                        new Spawnable("Bean Starter", 10),
                        new Spawnable("Kale Seeds", 10),
                        new Spawnable("Parsnip Seeds", 10),
                        new Spawnable("Potato Seeds", 10),
                        new Spawnable("Rhubarb Seeds", 10),
                        new Spawnable("Strawberry Seeds", 10),
                        new Spawnable("Tulip Bulb", 10),
                        new Spawnable("Rice Shoot", 10),
                        new Spawnable("Common Mushroom", 10),
                        new Spawnable("Ancient Seed", 10),
                        new Spawnable("Spring Mixed Seed", 10)
                ),
                Season.SUMMER, Arrays.asList(
                        new Spawnable("Fiddlehead Fern", 10),
                        new Spawnable("Grape", 10),
                        new Spawnable("Red Mushroom", 10),
                        new Spawnable("Spice Berry", 10),
                        new Spawnable("Sweet Pea", 10),
                        new Spawnable("Blueberry Seeds", 10),
                        new Spawnable("Corn Seeds", 10),
                        new Spawnable("Hops Starter", 10),
                        new Spawnable("Pepper Seeds", 10),
                        new Spawnable("Melon Seeds", 10),
                        new Spawnable("Poppy Seeds", 10),
                        new Spawnable("Radish Seeds", 10),
                        new Spawnable("Red Cabbage Seeds", 10),
                        new Spawnable("Starfruit Seeds", 10),
                        new Spawnable("Spangle Seeds", 10),
                        new Spawnable("Summer Squash Seeds", 10),
                        new Spawnable("Sunflower Seeds", 10),
                        new Spawnable("Tomato Seeds", 10),
                        new Spawnable("Wheat Seeds", 10),
                        new Spawnable("Common Mushroom", 10),
                        new Spawnable("Ancient Seed", 10),
                        new Spawnable("Summer Mixed Seed", 10)

                ),
                Season.FALL, Arrays.asList(
                        new Spawnable("Blackberry", 10),
                        new Spawnable("Chanterelle", 10),
                        new Spawnable("Hazelnut", 10),
                        new Spawnable("Purple Mushroom", 10),
                        new Spawnable("Wild Plum", 10),
                        new Spawnable("Amaranth Seeds", 10),
                        new Spawnable("Artichoke Seeds", 10),
                        new Spawnable("Beet Seeds", 10),
                        new Spawnable("Bok Choy Seeds", 10),
                        new Spawnable("Broccoli Seeds", 10),
                        new Spawnable("Cranberry Seeds", 10),
                        new Spawnable("Eggplant Seeds", 10),
                        new Spawnable("Fairy Seeds", 10),
                        new Spawnable("Grape Starter", 10),
                        new Spawnable("Pumpkin Seeds", 10),
                        new Spawnable("Yam Seeds", 10),
                        new Spawnable("Rare Seed", 10),
                        new Spawnable("Common Mushroom", 10),
                        new Spawnable("Ancient Seed", 10),
                        new Spawnable("Fall Mixed Seed", 10)

                ),
                Season.WINTER, Arrays.asList(
                        new Spawnable("Crocus", 10),
                        new Spawnable("Crystal Fruit", 10),
                        new Spawnable("Holly", 10),
                        new Spawnable("Snow Yam", 10),
                        new Spawnable("Winter Root", 10),
                        new Spawnable("Powdermelon Seeds", 10),
                        new Spawnable("Common Mushroom", 10),
                        new Spawnable("Ancient Seed", 10),
                        new Spawnable("Winter Mixed Seed", 10)

                )
            ),
            Arrays.asList(
                    new Spawnable("Common Mushroom", 10),
                    new Spawnable("Ancient Seed", 10),
                    new Spawnable("Acorns", 10),
                    new Spawnable("Maple Seeds", 10),
                    new Spawnable("Pine Cones", 10),
                    new Spawnable("Mahogany Seeds", 10),
                    new Spawnable("Mushroom Tree Seeds", 10),
                    new Spawnable("Oak Tree", 10),
                    new Spawnable("Maple Tree", 10),
                    new Spawnable("Pine Tree", 10),
                    new Spawnable("Mahogany Tree", 10),
                    new Spawnable("Mushroom Tree", 10),
                    new Spawnable("Broken Branch", 10)
            )
    ),
    MINE(
            Map.of(
            ),
            Arrays.asList(
                    new Spawnable("Rock", 10),
                    new Spawnable("Quartz Vein", 10),
                    new Spawnable("Earth Crystal Node", 10),
                    new Spawnable("Frozen Tear Cluster", 10),
                    new Spawnable("Fire Quartz Node", 10),
                    new Spawnable("Emerald Deposit", 10),
                    new Spawnable("Aquamarine Vein", 10),
                    new Spawnable("Ruby Deposit", 10),
                    new Spawnable("Amethyst Node", 10),
                    new Spawnable("Topaz Cluster", 10),
                    new Spawnable("Jade Deposit", 10),
                    new Spawnable("Diamond Node", 10),
                    new Spawnable("Prismatic Shard Deposit", 10),
                    new Spawnable("Copper Vein", 10),
                    new Spawnable("Iron Vein", 10),
                    new Spawnable("Gold Vein", 10),
                    new Spawnable("Iridium Vein", 10),
                    new Spawnable("Coal Node", 10)
            )
    ),
    TOWN(
            Map.of(
                Season.SPRING,
                Arrays.asList(

                )
            ),
            Arrays.asList(
    ));

    public Map<Season, List<Spawnable>> seasonalForageables;
    public List<Spawnable> yearRoundForageables;

    public double totalWeight = 0;

    BiomeType(Map<Season, List<Spawnable>> seasonalForageables,
              List<Spawnable> yearRoundForageables) {
        this.seasonalForageables = seasonalForageables;
        this.yearRoundForageables = yearRoundForageables;
    }

    public static class Spawnable {
        String entity;
        double weight;

        public Spawnable(String entity, double weight) {
            this.entity = entity;
            this.weight = weight;
        }

        public String getEntity() {
            return entity;
        }

        public double getWeight() {
            return weight;
        }
    }

    public static Map<BiomeType, ArrayList<Spawnable>> getCandidates(Season season){
        Map<BiomeType, ArrayList<Spawnable>> result = new HashMap<>();
        for(BiomeType b : BiomeType.values()){
            ArrayList<Spawnable> candidates = new ArrayList<>(b.yearRoundForageables);
            if(b.seasonalForageables.get(season) != null){
                candidates.addAll(b.seasonalForageables.get(season));
            }
            result.put(b, candidates);
        }
        return result;
    }
}
