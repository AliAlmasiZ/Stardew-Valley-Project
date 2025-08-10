package com.ap.stardew.views.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class WrapperWithBackground extends Table {
    public WrapperWithBackground(Actor actor, Drawable background){
        add(actor).grow();
        setBackground(background);
    }
}
