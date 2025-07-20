package com.ap.stardew.views.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Scaling;

public class FramedImage extends Group {
    public Image frame;
    public Image image;

    public FramedImage(TextureRegion frameTexture, TextureRegion imageTexture) {
        frame = new Image(frameTexture);
        image = new Image(imageTexture);

        Table imageTable = new Table();
        imageTable.pad(Value.percentHeight(0.15f));
        addActor(frame);
        imageTable.add(image).growX().growY();
        addActor(imageTable);

        imageTable.setFillParent(true);
        frame.setFillParent(true);
        image.setScaling(Scaling.fit);
    }
    public FramedImage(Texture frameTexture, Texture imageTexture) {
        this(new TextureRegion(frameTexture), new TextureRegion(imageTexture));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        float oldAlpha = batch.getColor().a;
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        super.draw(batch, parentAlpha);

        // Reset alpha to prevent affecting subsequent draws
        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, oldAlpha);
    }
}
