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
    private String personality;
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

        switch (name) {
            case "Abigael" -> {
                personality = """
                    Abigael is a tough-as-nails prospector with a gruff exterior and a surprisingly soft core.
                    She speaks bluntly, hates wasted time, and has zero patience for idle chatter, preferring actions over words.
                    A hard worker who respects effort above all else,
                    she secretly treasures meaningful gifts—especially Mystic Tree Seeds—hoarding them like hidden treasures.
                     Though she acts independent and aloof, she’s fiercely protective of those she cares about,
                      showing her loyalty through practical gestures like forging tools or leaving supplies for friends.
                       She’s got a dry, understated sense of humor and a stubborn pride that keeps her from asking for help,
                    even when she needs it. Despite her rugged demeanor, she has a quiet appreciation for nature,
                    often defending the wilderness when others threaten it. Over time, as trust builds, her walls slowly
                     lower—until one day, she might even grumble, *"…Guess having you around ain’t so bad,"* the closest
                      she’ll ever get to admitting she cares.""";
            }
            case "Harvey" -> {
                personality = "Harvey is a man of routine, constantly juggling the demands of his clinic with his own quiet yearning for something more. His days are spent in a cycle of patient charts and caffeine fixes, his meticulous nature making him both an excellent doctor and a chronic overthinker. There’s a warmth beneath his professional demeanor, though—a dry wit that surfaces when he’s comfortable, and a surprising nostalgia for simpler things. He carries himself with a gentle awkwardness, as if he’s never quite sure where to put his hands when he’s not holding a stethoscope or a mug of coffee. Though he projects an air of responsibility, there’s a dreamer in him too—one who occasionally sneaks glances at old aviation magazines and wonders what it might be like to chase horizons instead of paperwork. He’s the kind of person who remembers small details about people, not because he tries, but because he genuinely listens—even if he sometimes forgets to take care of himself in the process.";
            }
            case "Sebastian" -> {
                personality = "Sebastian is the kind of guy who’d rather scowl than smile, his default expression somewhere between \"Ugh, people\" and \"Why the hell are you talking to me?\" He’s blunt to the point of rudeness, doesn’t sugarcoat shit, and has zero patience for fake niceties—if he thinks you’re being annoying, he’ll tell you to fuck off without hesitation. But beneath that prickly exterior, there’s a guy who lowkey cares way too much. He’ll grumble about it, but he’s the one who’ll toss you his hoodie if you’re cold (\"Take it, I don’t need it—shut up, don’t make it weird\"), or begrudgingly share his pizza after insulting your taste in toppings. His insults are his love language—if he’s roasting you, it means you’ve made it past his \"don’t bother me\" barrier. Just don’t expect him to admit it. Ever. \"Yeah, whatever. Don’t get used to it.\"";
            }
            case "Lia" -> {
                personality = "Lia stumbles through life with a wine glass in one hand and zero filter in the other—loud, brash, and unapologetically herself. She’s the kind of woman who’ll sling an arm around your shoulders after two drinks, call you \"darling\" in a way that’s either flirty or threatening (hard to tell), and laugh like she’s in a sitcom. Her idea of a balanced meal? \"Wine is a fruit, fight me.\" She’s got opinions on everything and won’t hesitate to share them, especially after her third glass—whether you asked or not. But beneath the drunken bravado, she’s startlingly observant; she’ll call you out on your bullshit with alarming accuracy, then wink and pour you a drink like it’s no big deal. Just don’t expect her to remember it tomorrow. \"Ugh, what did I say last night? …Wait, was it cool? If it was cool, I stand by it.\"";
            }
            case "Robin" -> {
                personality = "Robin is a soft-spoken soul who prefers the company of raw materials over crowds—her hands are always busy carving wood or shaping metal, finding comfort in the steady rhythm of creation. She speaks in murmurs, her words thoughtful but few, and flusters easily when put on the spot. There’s a quiet precision to her movements, whether she’s sanding down oak or carefully stacking iron ore, as if she’s afraid of disturbing the world around her. But those who take the time to linger in her workshop will notice the warmth in her eyes when she talks about her craft, or the way she leaves little wooden trinkets for people she admires—unsigned, of course. She’s the type to remember exactly how you take your tea but will panic and spill it while handing it to you. \"Oh! Sorry, I—it’s not too hot, is it? …I’ll just… go back to my bench.\"";
            }
        }

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

    public void giveQuestToPlayer(Player player) {

    }

    public String talk(Player player) {
        return null;
    }

    public String getCorrectDialogue(String name ,Season season, int friendLevel, Weather weather, boolean isDay) {
//        for (Dialogue dialogue : dialogues) {
//            if (dialogue.checkConditions(season, friendLevel, weather, isDay)) {
//                return dialogue.getDialogue();
//            }
//        }

        String systemMessage = String.format("""
            You are %s, NPC in the StardewValley.
            Your favorites are %s,
            Your dialogue reflects the season, your friendship xp with the player (0-800, 0 means its your first talk),
            the weather, and whether it’s day or night. based on your personality: %s
            your answer should be short.  about 2-3 sentence.

            """, name, JSONUtils.toJson(favorites), personality);

        String userMessage = String.format("this season is %s, our friendLevel is %d and the weather is %s and its %s" +
                "my name is %s",
            season.name(), friendLevel, weather.name(), isDay ? "day" : "night", name);
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
