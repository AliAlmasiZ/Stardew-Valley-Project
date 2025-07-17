package com.ap.stardew.models;

import com.ap.stardew.controllers.GameAssetManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;


public class ClockActor extends Actor {
    private TextureRegion background;
    private TextureRegion hand;
    private TextureRegion weatherIcon;
    private TextureRegion seasonIcon;
    private BitmapFont font;

    private String timeText = "6:00 am";
    private String dateText = "Mon. 1";
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
        // Draw background
        batch.draw(background, getX(), getY(), getWidth(), getHeight());

        // Draw rotated hand
        batch.draw(
            hand,
            getX() + getWidth() / 2 - hand.getRegionWidth() / 2,
            getY() + getHeight() / 2 - hand.getRegionHeight() / 2,
            hand.getRegionWidth() / 2f,
            hand.getRegionHeight() / 2f,
            hand.getRegionWidth(),
            hand.getRegionHeight(),
            1f, 1f,
            angle
        );

        // Draw weather icon (top-left inside clock)
        batch.draw(weatherIcon, getX() + 4, getY() + getHeight() - 20, 16, 16);

        // Draw season icon (top-right inside clock)
        batch.draw(seasonIcon, getX() + getWidth() - 20, getY() + getHeight() - 20, 16, 16);

        // Draw time and date labels (below clock)
        font.draw(batch, timeText, getX() + getWidth() + 10, getY() + getHeight() - 10);
        font.draw(batch, dateText, getX() + getWidth() + 10, getY() + getHeight() - 30);
    }

    public void update(float delta) {
        Game game = App.getActiveGame();
        Date date = game.getDate();
        timeText = String.valueOf(date.getHour());
        dateText = date.getWeekDay().name() + ". " + date.getDay();

    }
}
