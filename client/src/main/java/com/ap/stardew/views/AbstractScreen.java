package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.controllers.GameAssetManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AbstractScreen implements Screen {
    private float uiScaling = 1;

    protected Stage uiStage;
    protected Table rootTable;
    protected Skin skin;
    protected Skin customSkin;

    public AbstractScreen(float uiScaling) {
        this.uiScaling = uiScaling;

        uiStage = new Stage(new ScreenViewport(), ClientGame.getInstance().getBatch());
        uiStage.getCamera().viewportWidth = uiStage.getCamera().viewportWidth / Gdx.graphics.getPpiX() * 120 / this.uiScaling;
        uiStage.getCamera().viewportHeight = uiStage.getCamera().viewportHeight / Gdx.graphics.getPpiY() * 120 / this.uiScaling;


        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();
        uiStage.addActor(rootTable);
        skin = GameAssetManager.getInstance().getSkin();
        customSkin = GameAssetManager.getInstance().getCustomSkin();
    }
    public AbstractScreen(){
        this(1);
    }

    @Override
    public void show() {
        for (ObjectMap.Entry<String, BitmapFont> entry : skin.getAll(BitmapFont.class)) {
            entry.value.getData().setScale(0.6f / uiScaling);
        }
//        for (ObjectMap.Entry<String, BitmapFont> entry : customSkin.getAll(BitmapFont.class)) {
//            entry.value.getData().setScale(2f / uiScaling);
//        }
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void render(float delta) {
        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().setScreenSize(width, height);
        uiStage.getViewport().setWorldWidth(width / Gdx.graphics.getPpiX() * 120 / uiScaling);
        uiStage.getViewport().setWorldHeight(height / Gdx.graphics.getPpiY() * 120 / uiScaling);
        uiStage.getCamera().viewportHeight = height / Gdx.graphics.getPpiY() * 120 / uiScaling;
        uiStage.getCamera().viewportWidth = width / Gdx.graphics.getPpiX() * 120 / uiScaling;
        uiStage.getCamera().position.x = uiStage.getCamera().viewportWidth / 2;
        uiStage.getCamera().position.y = uiStage.getCamera().viewportHeight / 2;
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
