package com.ap.stardew.models.crafting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ap.stardew.models.utils.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class RecipeRegistry {
    private final Map<String, Recipe> registry = new HashMap<>();

    public void loadRecipes(String pathStr) {
        Path root = Paths.get(pathStr);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try (Stream<Path> files = Files.walk(root)) {
            files.filter(p -> p.toString().endsWith(".json")).forEach(path -> {

                try {
                    JsonNode jsonRoot = mapper.readTree(path.toFile());

                    String typeName = jsonRoot.get("type").asText();
                    RecipeType type = RecipeType.valueOf(typeName);
                    Recipe[] recipes = mapper.treeToValue(jsonRoot.get("recipes"), Recipe[].class);
                    if(recipes == null){
                        throw new RuntimeException("The structure of recipe data file is invalid! (" + path.toString() + ")");
                    }
                    for(Recipe e : recipes){
                        e.setType(type);
                        this.registry.putIfAbsent(e.getName(), e);
                    }
                } catch (IOException e) {
                    System.err.println("----------------------------------------------------------------------");
                    System.err.println("Error in reading " + path);
                    System.err.println("----------------------------------------------------------------------\nlogs:");
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Recipe> getUnlockedRecipes() {
        ArrayList<Recipe> result = new ArrayList<>();
        registry.forEach(((string, recipe) -> {
            if(recipe.isUnlocked())
                result.add(recipe);
        }));
        return result;
    }

    public Recipe getRecipe(String name) {
//        return registry.get(name);
        for (String s : registry.keySet()) {
            if(StringUtils.isNamesEqual(s, name))
                return registry.get(s);
        }
        return null;
    }

    public void checkIngredients() {
        for(Recipe r : registry.values()){
            for (Ingredient i : r.getIngredients()) {
                i.checkIngredient();
            }
        }
    }



}
