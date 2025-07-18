package com.ap.stardew.models.animal;

import com.ap.stardew.controllers.GameAssetManager;
import com.ap.stardew.models.enums.FishMovement;
import com.ap.stardew.views.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class FishingMiniGame extends Group {
    private final GameScreen gameScreen;
    private Image fishingPole;
    private Image fish;
    private Image bar;
    private Image catchZone;

    private double fishMovementDelay;
    private double catchingProgress = 0.25;
    private Image progressBar;

    private final FishMovement fishMovement;
    private int lastMove = 1; //-1 for down, 1 for up, 0 for not moving
    private float timeToMove = 0.5f;


    public FishingMiniGame(GameScreen gameScreen, FishMovement fishMovement) {
        this.gameScreen = gameScreen;
        this.fishMovement = fishMovement;
        // adding Bar
        bar = new Image(new Texture("Content/FishingMiniGame/fishing bar.png"));
        bar.setSize(300, 750);
        bar.setPosition(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 - 300);
        addActor(bar);

        // fish
        fish = new Image(new Texture("Content/FishingMiniGame/Salmon.png"));
        fish.setSize(bar.getWidth() / 3 - 50, bar.getWidth() / 3 - 50);
        fish.setPosition(bar.getX() + bar.getWidth() / 3 + 25, bar.getY() + bar.getHeight() / 2);
        addActor(fish);

        // catch Zone
        catchZone = new Image(new Texture("Content/FishingMiniGame/catchZone.png"));
        catchZone.setSize(fish.getWidth(), fish.getHeight() * 2.5f);
        catchZone.setPosition(fish.getX(), fish.getY() - 20);
        catchZone.setColor(1, 1, 1, 0.5f);
        addActor(catchZone);

        //Progress Bar
        Image backGround = new Image(new Texture("Content/FishingMiniGame/catchZone.png"));
        backGround.setColor(Color.GRAY);
        backGround.setSize(bar.getWidth() / 6, bar.getHeight());
        backGround.setPosition(bar.getX() + bar.getWidth(), bar.getY());
        addActor(backGround);
        progressBar = new Image(new Texture("Content/FishingMiniGame/catchZone.png"));
        progressBar.setPosition(bar.getX() + bar.getWidth(), bar.getY());
        progressBar.setSize(backGround.getWidth(), (float) (bar.getHeight() * catchingProgress));
        addActor(progressBar);





    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Fish Movement
        if (timeToMove < 0) {
            lastMove = fishMovement.moveFish(fish, bar.getY() + 25, bar.getY() + bar.getHeight() - fish.getHeight() - 25, lastMove);
            timeToMove = 0.5f;
        } else {
            timeToMove -= delta;
        }

        // progress
        if (fish.getY() + fish.getHeight() / 3 > catchZone.getY() && fish.getY() + fish.getHeight() / 3 < catchZone.getHeight() + catchZone.getY() ) {
            catchingProgress += delta * 0.07f;
        } else {
            catchingProgress -= delta * 0.07f;
        }
        progressBar.setHeight((float) (bar.getHeight() * catchingProgress));

        if (catchingProgress >= 1) gameScreen.stopFishing(true);
        if (catchingProgress < 0) gameScreen.stopFishing(false);

        // Handle input
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) catchZone.setY(catchZone.getY() + delta * 200);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) catchZone.setY(catchZone.getY() - delta * 200);
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) gameScreen.stopFishing(false);

        // fix catch Zone
        catchZone.setPosition(catchZone.getX(), MathUtils.clamp(catchZone.getY(), bar.getY() + 25,
            bar.getY() + bar.getHeight() - catchZone.getHeight()));
    }
}
