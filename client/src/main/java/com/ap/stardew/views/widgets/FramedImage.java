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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import org.w3c.dom.Text;

public class FramedImage extends Table {
    protected Image frame;
    protected Image image;
    protected Stack stack;
    protected Table frameTable;

    public FramedImage(Drawable frameDrawable, Drawable imageDrawable){
        frame = new Image(frameDrawable);

        if(imageDrawable == null){
            image = new Image((TextureRegion) null);
        }else {
            image = new Image(imageDrawable);
        }
        stack = new Stack();
        Table imageTable = new Table();
        frameTable = new Table();

        stack.add(frameTable);
        stack.add(imageTable);

        frame.setScaling(Scaling.fit);
        frameTable.add(frame).grow();

        image.setScaling(Scaling.fit);
        imageTable.center();
        imageTable.add(image).center().pad(0, 1, 1, 0);

        add(stack).grow();
    }
    public FramedImage(TextureRegion frameTexture, TextureRegion imageTexture, float padPercent) {
        this(new TextureRegionDrawable(frameTexture), (imageTexture != null) ?  new TextureRegionDrawable(imageTexture) : null);
    }
    public FramedImage(Texture frameTexture, Texture imageTexture, float padPercent){
        this(new TextureRegion(frameTexture), new TextureRegion(imageTexture), padPercent);
    }
    public FramedImage(Texture frameTexture, Texture imageTexture){
        this(new TextureRegion(frameTexture), new TextureRegion(imageTexture), 0);
    }
}
