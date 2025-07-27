package com.ap.stardew.views.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import org.w3c.dom.Text;

public class FramedImage extends Group {
    protected Image frame;
    protected Image image;

    public FramedImage(Texture frameTexture, Texture imageTexture, float padPercent) {
        frame = new Image(frameTexture);

        if(imageTexture == null){
            image = new Image((TextureRegion) null);
        }else {
            image = new Image(imageTexture);
        }

        Table imageTable = new Table();
        imageTable.pad(Value.percentHeight(padPercent));
        imageTable.center();
        addActor(frame);
        imageTable.add(image).grow();
        addActor(imageTable);

        imageTable.setFillParent(true);
        frame.setFillParent(true);
        image.setScaling(Scaling.fit);
    }
    public FramedImage(Texture frameTexture, Texture imageTexture){
        this(frameTexture, imageTexture, 0);
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

    public Drawable getFrame() {
        return frame.getDrawable();
    }

    public Drawable getImage() {
        return image.getDrawable();
    }
}
