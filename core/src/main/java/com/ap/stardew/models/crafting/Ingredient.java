package com.ap.stardew.models.crafting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.models.App;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.enums.EntityTag;
import com.ap.stardew.models.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ingredient implements Serializable {
    private List<String> contents;
    private List<EntityTag> validTags;
    private int amount = 1;

    @JsonCreator
    private Ingredient(
            @JsonProperty("contents") List<String> contents,
            @JsonProperty("validTags") List<EntityTag> validTags,
            @JsonProperty("amount") int amount) {
        this.contents = contents != null ? new ArrayList<>(contents) : new ArrayList<>();
        this.validTags = validTags != null ? new ArrayList<>(validTags) : new ArrayList<>();
        this.amount = amount;


    }

    public static Ingredient fromEntities(List<String> entities, int amount) {
        return new Ingredient(entities, new ArrayList<>(), amount);
    }

    public static Ingredient fromValidTags(List<EntityTag> tags, int amount) {
        return new Ingredient(new ArrayList<>(), tags, amount);
    }

    public void removeTag(EntityTag tag) {
        validTags.remove(tag);
    }

    public int getAmount() {
        return amount;
    }

    public boolean isInIngredient(Entity e, int availableAmount) {
        if (!contents.isEmpty()) {
            return contents.stream().anyMatch(s -> StringUtils.isNamesEqual(s, e.getEntityName())) && availableAmount >= amount;
        }

        if (!validTags.isEmpty()) {
            return e.getTags().stream().anyMatch(validTags::contains) && availableAmount >= amount;
        }
        return true;
    }

    public boolean isInIngredient(Entity e) {
        if (!contents.isEmpty()) {
            return contents.contains(e.getEntityName());
        }

        if (!validTags.isEmpty()) {
            return e.getTags().stream().anyMatch(validTags::contains);
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (!contents.isEmpty()) {
            result.append("(");
            for (String content : contents) {
                result.append(content).append(" or ");
            }
            result.delete(result.length() - 3, result.length());
            result.append(")");
        }
        if (!validTags.isEmpty()) {
            result.append("(");
            for (EntityTag tag : validTags) {
                result.append(tag.toString().toLowerCase()).append(" or ");
            }
            result.delete(result.length() - 3, result.length());
            result.append(")");
        }

        result.append(" x").append(amount);
        return result.toString();
    }

    protected void checkIngredient() {
        for (String content : contents) {
            App.entityRegistry.getEntityDetails(content);
        }
    }
}
