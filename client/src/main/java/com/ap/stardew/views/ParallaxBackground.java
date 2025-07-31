package com.ap.stardew.views;

import com.ap.stardew.controllers.GameAssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class ParallaxBackground extends Table {
    private static final Skin customSkin = GameAssetManager.getInstance().getCustomSkin();
    private final Image mountains1;
    private final Image mountains2;
    private final Image bushesLeft;
    private final Image bushesRight;

    private float mountains1Speed = 0.1f;
    private float mountains2Speed = 0.3f;
    private float bushesSpeed = 1;

    float altitude;

    public ParallaxBackground(Stage stage) {
        setFillParent(true);
        stage.addActor(this);
        toBack();

        mountains1 = new Image(customSkin.getDrawable("mountains1"));
        mountains2 = new Image(customSkin.getDrawable("mountains2"));
        bushesLeft = new Image(customSkin.getDrawable("backgroundBushesLeft"));
        bushesRight = new Image(customSkin.getDrawable("backgroundBushesRight"));

        mountains1.setScaling(Scaling.fill);
        mountains2.setScaling(Scaling.fill);
        bushesLeft.setScaling(Scaling.fill);
        bushesRight.setScaling(Scaling.fill);

        bushesRight.setOrigin(Align.bottomRight);
        bushesLeft.setOrigin(Align.bottomLeft);
        mountains1.setOrigin(Align.bottom);
        mountains2.setOrigin(Align.bottom);

        bushesRight.scaleBy(0.3f);
        bushesLeft.scaleBy(0.3f);

        bushesRight.setAlign(Align.bottomRight);
        bushesLeft.setAlign(Align.bottomLeft);
        mountains2.setAlign(Align.bottom);
        mountains1.setAlign(Align.bottom);

        bushesLeft.toBack();
        bushesRight.toBack();
        mountains1.toBack();
        mountains2.toBack();

        addActor(mountains1);
        addActor(mountains2);
        addActor(bushesLeft);
        addActor(bushesRight);
    }

    @Override
    public void layout() {
        super.layout();
        mountains1.setWidth(700);
        mountains2.setWidth(800);

        mountains1.setX(0);
        mountains2.setX(0);

        bushesLeft.setX(0);
        bushesRight.setX(getWidth() - bushesRight.getWidth());
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
//        mountains1.draw(batch, parentAlpha);
//        mountains2.draw(batch, parentAlpha);
//        bushesLeft.draw(batch, parentAlpha);
//        bushesRight.draw(batch, parentAlpha);
    }

    public void move(float distance, float duration, Interpolation interpolation) {
        mountains1.addAction(Actions.moveTo(0, -distance * mountains1Speed, duration, interpolation));
        mountains2.addAction(Actions.moveBy(0, -distance * mountains2Speed, duration, interpolation));
        bushesLeft.addAction(Actions.moveBy(0, -distance * bushesSpeed, duration, interpolation));
        bushesRight.addAction(Actions.moveBy(0, -distance * bushesSpeed, duration, interpolation));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
