package com.ap.stardew.views.widgets;

import com.ap.stardew.models.player.reaction.Emoji;
import com.ap.stardew.views.spriteManagers.EmojiSpriteManager;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class EmojiImage extends Image {
    public Emoji emoji;

    public EmojiImage(Emoji emoji) {
        super(new AnimatedDrawable(EmojiSpriteManager.getInstance().getAnimation(emoji)));
        this.emoji = emoji;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(getDrawable() instanceof  AnimatedDrawable animatedDrawable) animatedDrawable.update(delta);
        setSize(getDrawable().getMinWidth(), getDrawable().getMinHeight());
    }
}
