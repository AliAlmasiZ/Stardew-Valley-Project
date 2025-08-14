package com.ap.stardew.views.managers;

import com.ap.stardew.models.player.reaction.Emoji;
import com.ap.stardew.view.VariableDurationAnimation;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class EmojiSpriteManager {
    private final Texture baseTexture;
    private final TextureRegion[][] split;
    private final Map<Emoji, VariableDurationAnimation<TextureRegion>> animationMap = new HashMap<>();

    private static EmojiSpriteManager instance;
    public static EmojiSpriteManager getInstance(){
        if(instance == null) instance = new EmojiSpriteManager("./Content/Emotes/Emotes.png");
        return instance;
    }

    public EmojiSpriteManager(String texturePath){
        baseTexture = new Texture(texturePath);

        split = TextureRegion.split(baseTexture, 16, 16);
        initAnimations();
    }

    public void initAnimations(){
        int i = 0;
        for (Emoji value : Emoji.values()) {
            animationMap.put(value, new VariableDurationAnimation<>(new TextureRegion[0], new float[0]));

            animationMap.get(value).addFrame(split[i / 4][i % 4], .2f);

            if(i >= 3 * 4){
                animationMap.get(value).addFrame(split[i / 4][(i % 4) + 4], .2f);
            }
            i++;
        }
    }

    public TextureRegion getFrame(Emoji emoji, float stateTime){
        return animationMap.get(emoji).getKeyFrame(stateTime, true);
    }

    public VariableDurationAnimation<TextureRegion> getAnimation(Emoji emoji) {
        return animationMap.get(emoji);
    }
}
