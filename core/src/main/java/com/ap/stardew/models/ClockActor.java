package com.ap.stardew.models;

import com.ap.stardew.controllers.GameAssetManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;


public class ClockActor extends Actor {
    private TextureRegion background;
    private TextureRegion hand;
    private TextureRegion weatherIcon;
    private TextureRegion seasonIcon;
    private BitmapFont font;
    private Label infoLabel;
    private String infoText;
    private boolean isHovered = false;

    private String timeText = "6:00 am";
    private String dateText = "Mon. 1";
    private String goldText = "";
    private float angle = 0f;

    public ClockActor() {
        GameAssetManager manager = GameAssetManager.getInstance();
        this.background = manager.clock;
        this.hand = manager.clockHand;
        this.weatherIcon = App.getActiveGame().getTodayWeather().getTextureRegion();
        this.seasonIcon = App.getActiveGame().getDate().getSeason().getTextureRegion();
        this.font = manager.getFont();


        // Set size to background size
        setSize(background.getRegionWidth(), background.getRegionHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);

        infoLabel = new Label("", GameAssetManager.getInstance().getSkin());
        infoLabel.setVisible(false);
        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                isHovered = true;
                infoLabel.setText(infoText);
                infoLabel.setVisible(true);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                isHovered = false;
                infoLabel.setVisible(false);
            }
        });
    }

    public void setTime(String timeText) {
        this.timeText = timeText;
    }

    public void setDate(String dateText) {
        this.dateText = dateText;
    }

    public void setHandAngle(float angleDegrees) {
        this.angle = angleDegrees;
    }

    public void setWeatherIcon(TextureRegion icon) {
        this.weatherIcon = icon;
    }

    public void setSeasonIcon(TextureRegion icon) {
        this.seasonIcon = icon;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        final float SCALE = 3f; // Scaling factor

        // Draw background (3x size)
        batch.draw(background,
                getX(),
                getY(),
                getWidth() * SCALE,
                getHeight() * SCALE
        );

        // Calculate center of scaled background
        float centerX = getX() + (getWidth() * SCALE) / 2;
        float centerY = getY() + (getHeight() * SCALE) / 2;

        // Draw rotated hand (3x size)
        batch.draw(
                hand,
                centerX - (15.5f * SCALE) , // Position X
                centerY + (10.5f * SCALE), // Position Y
                hand.getRegionWidth() / 2f, // Origin X (rotation center)
                0, // Origin Y
                hand.getRegionWidth(), // Original width
                hand.getRegionHeight(), // Original height
                SCALE, SCALE, // Scale X/Y
                angle
        );

        // Draw weather icon (3x size, positions scaled)
        batch.draw(weatherIcon,
                getX() + 29 * SCALE,
                getY() + 34 * SCALE,
                13 * SCALE,
                9 * SCALE
        );

        // Draw season icon (3x size, positions scaled)
        batch.draw(seasonIcon,
                getX() + 54 * SCALE,
                getY() + 34 * SCALE,
                13 * SCALE,
                9 * SCALE
        );

        // Draw text at scaled positions
        Color originalColor = font.getColor();
        font.setColor(Color.BLACK);
        font.draw(batch, timeText,
                getX() + 42 * SCALE,
                getY() + 31 * SCALE
        );

        font.draw(batch, dateText,
                getX() + 31 * SCALE,
                getY() + getHeight() * SCALE - 8 * SCALE
        );

        font.draw(batch, goldText,
                getX() + 20 * SCALE,
                getY() + 5 * SCALE
        );

        font.setColor(originalColor);

        // info Label
        if (isHovered) {
            infoLabel.setText(infoText);
            infoLabel.setPosition(getX() + getWidth() / 2, getY() - 20);
            infoLabel.draw(batch, parentAlpha);

        }
    }
    public void update(float delta) {
        Game game = App.getActiveGame();
        Date date = game.getDate();
        timeText = String.valueOf(date.getHour());
        dateText = date.getWeekDay().getShortName() + ". " + date.getDay();
        goldText = String.format("%.2f", game.getCurrentPlayer().getWallet().getBalance());
        infoText = game.getTodayWeather().name() + " " + game.getDate().getSeason().name();

        angle = (float) (180 * date.getHour()) / 24;

    }
}
