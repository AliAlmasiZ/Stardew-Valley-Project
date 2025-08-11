package com.ap.stardew.models.NPC;

import com.ap.stardew.controllers.LLMRequest;
import com.ap.stardew.models.entities.components.EntityComponent;
import com.ap.stardew.models.entities.components.Placeable;
import com.ap.stardew.models.entities.components.PositionComponent;
import com.ap.stardew.models.enums.EntityTag;
import com.ap.stardew.utils.JSONUtils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.Renderable;
import com.ap.stardew.models.enums.Season;
import com.ap.stardew.models.enums.Weather;
import com.ap.stardew.models.player.Player;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NPC extends Entity implements Serializable {
    private String name;
    private ArrayList<String> favorites = new ArrayList<>();
    private ArrayList<String> gifts = new ArrayList<>();
    private ArrayList<Dialogue> dialogues = new ArrayList<>();
    private String avatarPath;


    public String getName() {
        return name;
    }

    public NPC() {

    }

    public NPC(String name) {
        super(name);
        this.name = name;
        addComponent(new Placeable(true));
        addComponent(new PositionComponent());
        addComponent(new Renderable());
        Renderable renderable = this.getComponent(Renderable.class);
        renderable.setSpritePath("Content/NPC/" + name + "/parts/image_part_001.png");

        avatarPath = "Content/NPC/" + name + ".png";

    }

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<String> favorites) {
        this.favorites = favorites;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public ArrayList<Dialogue> getDialogues() {
        return dialogues;
    }

    public void setDialogues(ArrayList<Dialogue> dialogues) {
        this.dialogues = dialogues;
    }

    public void giveQuestToPlayer(Player player){

    }

    public String talk(Player player){
        return null;
    }

    public String getCorrectDialogue(Season season, int friendLevel, Weather weather, boolean isDay) {
//        for (Dialogue dialogue : dialogues) {
//            if (dialogue.checkConditions(season, friendLevel, weather, isDay)) {
//                return dialogue.getDialogue();
//            }
//        }

        String systemMessage = String.format( """
            You are %s, a kind-hearted NPC in the StardewValley.
            Your favorites are %s,
            Your dialogue reflects the season, your friendship level with the player (0-10),
            the weather, and whether it’s day or night. In SPRING, talk about planting; in SUMMER,
             mention crop growth; in AUTUMN, discuss harvests; in WINTER, complain about cold. For friendLevel 0-3,
             be polite but distant; 4-7, be friendly and open; 8-10, share personal anecdotes. In SUNNY weather, be cheerful;
             in RAINY or STORMY, grumble about the weather; in CLOUDY, be neutral. During the day (isDay=true), focus on your daily tasks;
              at night (isDay=false), sound tired and keep responses brief. Stay in character,
              use medieval language (e.g., “good sir,” “pray”), avoid modern slang, and keep responses concise (1-2 sentences).
            """, name, JSONUtils.toJson(favorites));

        String userMessage = String.format("this season is %s, our friendLevel is %d and the weather is %s and its %s",
            season.name(), friendLevel, weather.name(), isDay ? "day" : "night");
        LLMRequest request = new LLMRequest();
        try {
            return request.talkToLLM(systemMessage, userMessage);
        } catch (IOException | InterruptedException e) {
            return "I don't know";
        }

    }


    public String getRandomGift() {
        int random = (int) (Math.random() * (favorites.size() + 1));
        if (random < favorites.size()) {
            return favorites.get(random);
        }
        return null;
    }

}
