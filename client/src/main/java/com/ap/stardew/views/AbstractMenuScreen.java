package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AbstractMenuScreen extends AbstractScreen {
    protected static Stage backgroundStage;
    protected static ParallaxBackground parallaxBackground;

    static {
        backgroundStage = new Stage(new ScreenViewport(), ClientGame.getInstance().getBatch());
        backgroundStage.getCamera().viewportWidth = backgroundStage.getCamera().viewportWidth / Gdx.graphics.getPpiX() * 120 / uiScaling;
        backgroundStage.getCamera().viewportHeight = backgroundStage.getCamera().viewportHeight / Gdx.graphics.getPpiY() * 120 / uiScaling;
        parallaxBackground = new ParallaxBackground(backgroundStage);
    }
    @Override
    public void render(float delta) {
        backgroundStage.act(delta);
        uiStage.act(delta);

        backgroundStage.draw();
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        backgroundStage.getViewport().setScreenSize(width, height);
        backgroundStage.getViewport().setWorldWidth(width / Gdx.graphics.getPpiX() * 120 / uiScaling);
        backgroundStage.getViewport().setWorldHeight(height / Gdx.graphics.getPpiY() * 120 / uiScaling);
        backgroundStage.getCamera().viewportHeight = height / Gdx.graphics.getPpiY() * 120 / uiScaling;
        backgroundStage.getCamera().viewportWidth = width / Gdx.graphics.getPpiX() * 120 / uiScaling;
        backgroundStage.getCamera().position.x = backgroundStage.getCamera().viewportWidth / 2;
        backgroundStage.getCamera().position.y = backgroundStage.getCamera().viewportHeight / 2;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        uiStage.dispose();
    }
}
