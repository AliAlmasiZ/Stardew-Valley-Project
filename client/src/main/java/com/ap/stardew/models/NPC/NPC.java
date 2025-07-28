package com.ap.stardew.models.NPC;

import com.ap.stardew.models.entities.components.Placeable;
import com.ap.stardew.models.entities.components.PositionComponent;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.Renderable;
import com.ap.stardew.models.enums.Season;
import com.ap.stardew.models.enums.Weather;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.views.old.inGame.Color;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NPC extends Entity implements Serializable {
    private String name;
    private ArrayList<String> favorites = new ArrayList<>();
    private ArrayList<String> gifts = new ArrayList<>();
    private ArrayList<Dialogue> dialogues = new ArrayList<>();
    private Image avatar;


    public String getName() {
        return name;
    }

    public NPC(String name) {
        super(name);
        this.name = name;
        addComponent(new Placeable(true));
        addComponent(new PositionComponent());
        addComponent(new Renderable(this.name.toUpperCase().charAt(0), new Color(255, 255 ,255)));
        Renderable renderable = this.getComponent(Renderable.class);
        renderable.setSprite(new Sprite(new Texture("Content/NPC/" + name + "/parts/image_part_001.png")));
        avatar = new Image(new Texture("Content/NPC/" + name + ".png"));
    }

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<String> favorites) {
        this.favorites = favorites;
    }

    public Image getAvatar() {
        return avatar;
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
        for (Dialogue dialogue : dialogues) {
            if (dialogue.checkConditions(season, friendLevel, weather, isDay)) {
                return dialogue.getDialogue();
            }
        }

        return null;
    }


    public String getRandomGift() {
        int random = (int) (Math.random() * (favorites.size() + 1));
        if (random < favorites.size()) {
            return favorites.get(random);
        }
        return null;
    }

}
