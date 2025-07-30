package com.ap.stardew.views.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import org.w3c.dom.Text;

public class FramedImage extends Table {
    protected Image frame;
    protected Image image;

    public FramedImage(TextureRegion frameTexture, TextureRegion imageTexture, float padPercent) {
        frame = new Image(frameTexture);

        if(imageTexture == null){
            image = new Image((TextureRegion) null);
        }else {
            image = new Image(imageTexture);
        }
        Stack stack = new Stack();
        Table imageTable = new Table();
        Table frameTable = new Table();

        stack.add(frameTable);
        stack.add(imageTable);

        frame.setScaling(Scaling.fit);
        frameTable.add(frame).grow();

        image.setScaling(Scaling.fit);
        imageTable.center().pad(padPercent);
        imageTable.add(image).grow().center();

        add(stack).grow();
    }
    public FramedImage(Texture frameTexture, Texture imageTexture, float padPercent){
        this(new TextureRegion(frameTexture), new TextureRegion(imageTexture), padPercent);
    }
    public FramedImage(Texture frameTexture, Texture imageTexture){
        this(new TextureRegion(frameTexture), new TextureRegion(imageTexture), 0);
    }
}
