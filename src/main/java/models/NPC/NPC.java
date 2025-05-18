package models.NPC;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.entities.Entity;
import models.entities.components.Renderable;
import models.enums.Season;
import models.enums.Weather;
import models.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import views.inGame.Color;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NPC extends Entity implements Serializable {
    private String name;
    private ArrayList<String> favorites = new ArrayList<>();
    private ArrayList<String> gifts = new ArrayList<>();
    private ArrayList<Dialogue> dialogues = new ArrayList<>();


    public String getName() {
        return name;
    }

    public NPC(String name) {
        super(name);
        this.name = name;
        addComponent(new Renderable(this.name.toUpperCase().charAt(0), new Color(255, 255 ,255)));
    }

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<String> favorites) {
        this.favorites = favorites;
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