package com.ap.stardew.models.animal;

import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Sellable;
import com.ap.stardew.models.enums.FishMovement;
import com.ap.stardew.views.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;


public class FishingMiniGame extends Group {
    private final GameScreen gameScreen;
    private Image fishingPole;
    private Image fishImage;
    private Image bar;
    private Image catchZone;
    private Image progressBar;

    private double catchingProgress = 0.25;
    private final FishMovement fishMovement;
    private int lastMove = 1; //-1 for down, 1 for up, 0 for not moving
    private float timeToMove = 0.5f;

    private final Entity fish;
    private boolean isPerfect = true;
    private boolean isSuccessful = false;


    public FishingMiniGame(GameScreen gameScreen, FishMovement fishMovement, Entity fish) {
        this.gameScreen = gameScreen;
        this.fishMovement = fishMovement;
        this.fish = fish;
        // adding Bar
        bar = new Image(new Texture("Content/FishingMiniGame/fishing bar.png"));
        bar.setSize(300, 750);
        bar.setPosition(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 - 300);
        addActor(bar);

        // fish
        fishImage = new Image(new Texture("Content/FishingMiniGame/Salmon.png"));
        if (fish.getComponent(Sellable.class).getBasePrice() > 500) {
            fishImage = new Image(new Texture("Content/FishingMiniGame/" + fish.getEntityName() + ".png"));
        }
        fishImage.setSize(bar.getWidth() / 3 - 50, bar.getWidth() / 3 - 50);
        fishImage.setPosition(bar.getX() + bar.getWidth() / 3 + 25, bar.getY() + bar.getHeight() / 2);
        addActor(fishImage);

        // catch Zone
        catchZone = new Image(new Texture("Content/FishingMiniGame/catchZone.png"));
        catchZone.setSize(fishImage.getWidth(), fishImage.getHeight() * 3.5f);
        catchZone.setPosition(fishImage.getX(), fishImage.getY() - 20);
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
            lastMove = fishMovement.moveFish(fishImage, bar.getY() + 25, bar.getY() + bar.getHeight() - fishImage.getHeight() - 25, lastMove);
            timeToMove = 0.5f;
        } else {
            timeToMove -= delta;
        }

        // progress
        if (fishImage.getY() + fishImage.getHeight() / 3 > catchZone.getY() && fishImage.getY() + fishImage.getHeight() / 3 < catchZone.getHeight() + catchZone.getY() ) {
            catchingProgress += delta * 0.07f;
        } else {
            isPerfect = false;
            catchingProgress -= delta * 0.07f;
        }
        progressBar.setHeight((float) (bar.getHeight() * catchingProgress));

        if (catchingProgress >= 1) {
            isSuccessful = true;
            gameScreen.stopFishing(this);
        }
        if (catchingProgress < 0) {
            isSuccessful = false;
            gameScreen.stopFishing(this);
        }

        // Handle input
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) catchZone.setY(catchZone.getY() + delta * 200);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) catchZone.setY(catchZone.getY() - delta * 200);
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) gameScreen.stopFishing(this);
        if (Gdx.input.isKeyPressed(Input.Keys.B))
            gameScreen.showTemporaryMessage(fish.getEntityName(), 2, Color.CYAN, bar.getX(), bar.getY() + bar.getHeight() / 2, 3f );

        // fix catch Zone
        catchZone.setPosition(catchZone.getX(), MathUtils.clamp(catchZone.getY(), bar.getY() + 25,
            bar.getY() + bar.getHeight() - catchZone.getHeight()));
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public boolean isPerfect() {
        return isPerfect;
    }

    public Entity getFish() {
        return fish;
    }
}
