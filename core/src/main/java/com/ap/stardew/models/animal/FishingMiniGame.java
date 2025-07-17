package com.ap.stardew.models.animal;

import com.ap.stardew.controllers.GameAssetManager;
import com.ap.stardew.views.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class FishingMiniGame extends Group {
    private final GameScreen gameScreen;
    private Texture background;
    private Image fishingPole;
    private Image fish;
    private Image bar;
    private Image catchZone;

    private double fishMovementDelay;
    private double catchingProgress = 0.25;
    private Image progressBar;


    public FishingMiniGame(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        // adding Bar
        bar = new Image(new Texture("Content/FishingMiniGame/fishing bar.png"));
//        bar.setSize();
        bar.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        addActor(bar);

        // fish
        fish = new Image(new Texture("Content/FishingMiniGame/Salmon.png"));
        fish.setSize(bar.getWidth(), bar.getHeight() / 20);
        fish.setPosition(bar.getX() + 5, bar.getY() + 50);
        addActor(fish);

        // catch Zone
        catchZone = new Image(new Texture("Content/FishingMiniGame/catchZone.png"));
        catchZone.setSize(bar.getWidth(), bar.getHeight() / 15);
        catchZone.setPosition(bar.getX() + 5, bar.getY() + 40);
        addActor(catchZone);

        //Progress Bar
        Image backGround = new Image();
        backGround.setSize(bar.getWidth() / 2, bar.getHeight());
        backGround.setPosition(bar.getX() + bar.getWidth() + 20, bar.getY());
        addActor(backGround);
        progressBar = new Image();
        progressBar.setSize(backGround.getWidth(), (float) (bar.getHeight() * catchingProgress));
        addActor(progressBar);


        // Input Listener
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });


    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Fish Movement

        // progress
        if (fish.getY() > catchZone.getY() && fish.getY() + fish.getHeight() < catchZone.getHeight() + catchZone.getY() ) {
            catchingProgress += delta * 0.1f;
        } else {
            catchingProgress -= delta * 0.1f;
        }
        progressBar.setHeight((float) (bar.getHeight() * catchingProgress));

        if (catchingProgress >= 1) gameScreen.stopFishing(true);
        if (catchingProgress < 0) gameScreen.stopFishing(false);

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) catchZone.setY(catchZone.getY() + delta * 8);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) catchZone.setY(catchZone.getY() - delta * 8);
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) gameScreen.stopFishing(false);
    }
}
