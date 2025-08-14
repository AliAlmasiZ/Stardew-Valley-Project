package com.ap.stardew.views;

import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.view.VariableDurationAnimation;
import com.ap.stardew.views.managers.ActorAnimManager;
import com.ap.stardew.views.managers.TransitionManager;
import com.ap.stardew.views.widgets.AnimatedDrawable;
import com.ap.stardew.views.widgets.PopUpMessage;
import com.ap.stardew.views.widgets.TransformWidgetWrapper;
import com.ap.stardew.views.widgets.WrapperWithBackground;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

import java.security.SecureRandom;
import java.util.Arrays;

public class InfoScreen extends AbstractMenuScreen{
    String text = "This is a Stardew Valley high-copy, developed by three novice computer engineer as their Advanced Programing course's final project. The only part which our dear Gpt friend was not involved in, is this very text.\n Would you like me to also make it sound more formal and polished?";
    int karNemide = 0;
    SecureRandom random = new SecureRandom();

    public InfoScreen(){
        Table mainTable = new Table();
        Table textTable = new Table();
        textTable.setBackground(customSkin.getDrawable("bigButtonNinePatch"));

        mainTable.defaults().spaceBottom(5);

        Label textLabel = new Label(text, customSkin);
        textLabel.setWrap(true);
        textLabel.setColor(Color.BLACK);

        textTable.add(textLabel).grow().width(300);

        mainTable.add(textTable).colspan(3).row();

        Image chick = new Image(new AnimatedDrawable(loadFrames("Content/Animal/chicken/walking.png", 4, 0.2f))){
            @Override
            public void act(float delta) {
                ((AnimatedDrawable) this.getDrawable()).update(delta);
                super.act(delta);
            }
        };
        Image duck = new Image(new AnimatedDrawable(loadFrames("Content/Animal/duck/walking.png", 4, 0.2f))){
            @Override
            public void act(float delta) {
                ((AnimatedDrawable) this.getDrawable()).update(delta);
                super.act(delta);
            }
        };
        Image rab = new Image(new AnimatedDrawable(loadFrames("Content/Animal/rabbit/walking.png", 4, 0.2f))){
            @Override
            public void act(float delta) {
                ((AnimatedDrawable) this.getDrawable()).update(delta);
                super.act(delta);
            }
        };

        chick.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                chick.addAction(
                    Actions.sequence(
                        Actions.run(() -> {
                            chick.setDrawable(new AnimatedDrawable(loadFrames("Content/Animal/chicken/pet.png", 4, 0.2f)));
                            new PopUpMessage("jic jic jic").show(AbstractMenuScreen.getFrontStage());
                        }),
                        Actions.delay(4),
                        Actions.run(() -> chick.setDrawable(new AnimatedDrawable(loadFrames("Content/Animal/chicken/walking.png", 4, 0.2f))))
                    )
                );
            }
        });
        duck.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                duck.addAction(
                    Actions.sequence(
                        Actions.run(() -> {
                            duck.setDrawable(new AnimatedDrawable(loadFrames("Content/Animal/duck/pet.png", 4, 0.2f)));
                            duck.moveBy(random.nextFloat() * 10 - 5, random.nextFloat() * 10 - 5);
                            new PopUpMessage("dude you ruined the style").show(AbstractMenuScreen.getFrontStage());
                        }),
                        Actions.delay(4),
                        Actions.run(() -> duck.setDrawable(new AnimatedDrawable(loadFrames("Content/Animal/duck/walking.png", 4, 0.2f))))
                    )
                );
            }
        });
        rab.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                rab.addAction(
                    Actions.sequence(
                        Actions.run(() -> {
                            if(karNemide == 1){
                                throw new RuntimeException("kar nadad");
                            }
                            karNemide++;
                            rab.setDrawable(new AnimatedDrawable(loadFrames("Content/Animal/rabbit/pet.png", 4, 0.2f)));
                            new PopUpMessage("nazan amoo kar nemide").show(AbstractMenuScreen.getFrontStage());
                        }),
                        Actions.delay(4),
                        Actions.run(() -> rab.setDrawable(new AnimatedDrawable(loadFrames("Content/Animal/rabbit/walking.png", 4, 0.2f))))
                    )
                );
            }
        });

        chick.setScaling(Scaling.fit);
        duck.setScaling(Scaling.fit);
        rab.setScaling(Scaling.fit);

        mainTable.add(chick).fill();
        mainTable.add(duck).fill();
        mainTable.add(rab).row();
        mainTable.add(new WrapperWithBackground(new Label("IYxTL", customSkin){{setColor(Color.WHITE);}}, customSkin.getDrawable("smallPanelNinePatch"))).top();
        mainTable.add(new WrapperWithBackground(new Label("Parsios", customSkin){{setColor(Color.WHITE);}}, customSkin.getDrawable("smallPanelNinePatch"))).top();
        mainTable.add(new WrapperWithBackground(new Label("AliAlm", customSkin){{setColor(Color.WHITE);}}, customSkin.getDrawable("smallPanelNinePatch"))).top();

        rootTable.add(mainTable);


        TransformWidgetWrapper<Button> backButtonWrapper         = new TransformWidgetWrapper<>(new Button(customSkin, "back"));
        Table backButtonTable = new Table();
        backButtonTable.setFillParent(true);
        backButtonTable.bottom().right().pad(5);
        backButtonTable.add(backButtonWrapper);
        uiStage.addActor(backButtonTable);
        ActorAnimManager.addHorizontalElastic(backButtonWrapper, true);
        backButtonWrapper.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MainMenuScreen mainMenuScreen = new MainMenuScreen();
                uiStage.addAction(
                    Actions.sequence(
                        Actions.run(() -> mainMenuScreen.prepareForAnim(false)),
                        Actions.run(() -> TransitionManager.horizontalTransition(mainMenuScreen, InfoScreen.this, true, 1.5f, Interpolation.smoother)),
                        Actions.delay(0.5f),
                        Actions.run(() -> mainMenuScreen.enterAnim(false, true))
                    )
                );
            }
        });

        uiStage.addActor(backButtonTable);
    }

    public static VariableDurationAnimation<TextureRegion> loadFrames(String path, int numFrames, float frameDuration) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        int frameWidth = texture.getWidth() / numFrames;
        int frameHeight = texture.getHeight();

        TextureRegion[] frames = new TextureRegion[numFrames];
        for (int i = 0; i < numFrames; i++) {
            frames[i] = new TextureRegion(texture, i * frameWidth, 0, frameWidth, frameHeight);
        }

        float[] durations = new float[numFrames];
        Arrays.fill(durations, frameDuration);

        return new VariableDurationAnimation<>(frames, durations);
    }
}
