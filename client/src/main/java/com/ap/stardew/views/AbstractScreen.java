package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.view.GameAssetManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.IOException;

public class AbstractScreen implements Screen {
    protected static float uiScaling = 2.5f;
    protected static Stage frontStage;
    protected static Table networkStatusTable;

    static {
        frontStage = new Stage(new ScreenViewport(), ClientGame.getInstance().getBatch());
        frontStage.getCamera().viewportWidth = frontStage.getCamera().viewportWidth / Gdx.graphics.getPpiX() * 120 / uiScaling;
        frontStage.getCamera().viewportHeight = frontStage.getCamera().viewportHeight / Gdx.graphics.getPpiY() * 120 / uiScaling;

        Table wrapperTable = new Table();
        wrapperTable.setFillParent(true);
        wrapperTable.top().right().pad(2);
        networkStatusTable = new Table();
        wrapperTable.add(networkStatusTable);
        frontStage.addActor(wrapperTable);
    }

    protected Stage uiStage;
    protected Table rootTable;
    protected Skin skin;
    protected Skin customSkin;

    public AbstractScreen() {
        uiStage = new Stage(new ScreenViewport(), ClientGame.getInstance().getBatch());
        uiStage.getCamera().viewportWidth = uiStage.getCamera().viewportWidth / Gdx.graphics.getPpiX() * 120 / uiScaling;
        uiStage.getCamera().viewportHeight = uiStage.getCamera().viewportHeight / Gdx.graphics.getPpiY() * 120 / uiScaling;


        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();
        uiStage.addActor(rootTable);
        skin = GameAssetManager.getInstance().getSkin();
        customSkin = GameAssetManager.getInstance().getCustomSkin();
    }

    @Override
    public void show() {
        for (ObjectMap.Entry<String, BitmapFont> entry : skin.getAll(BitmapFont.class)) {
            entry.value.getData().setScale(0.6f / uiScaling);
        }
//        for (ObjectMap.Entry<String, BitmapFont> entry : customSkin.getAll(BitmapFont.class)) {
//            entry.value.getData().setScale(2f / uiScaling);
//        }

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(frontStage);
        inputMultiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        uiStage.act(delta);
        frontStage.act(delta);

        uiStage.draw();
        frontStage.draw();
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

        frontStage.getViewport().setScreenSize(width, height);
        frontStage.getViewport().setWorldWidth(width / Gdx.graphics.getPpiX() * 120 / uiScaling);
        frontStage.getViewport().setWorldHeight(height / Gdx.graphics.getPpiY() * 120 / uiScaling);
        frontStage.getCamera().viewportHeight = height / Gdx.graphics.getPpiY() * 120 / uiScaling;
        frontStage.getCamera().viewportWidth = width / Gdx.graphics.getPpiX() * 120 / uiScaling;
        frontStage.getCamera().position.x = frontStage.getCamera().viewportWidth / 2;
        frontStage.getCamera().position.y = frontStage.getCamera().viewportHeight / 2;
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

    public Stage getUiStage() {
        return uiStage;
    }

    public static Stage getFrontStage() {
        return frontStage;
    }

    public static Table getNetworkStatusTable() {
        return networkStatusTable;
    }

    public static void updateNetworkStatus(String status){
        switch (status){
            case "connected" ->{
                Label label = new Label("connected", GameAssetManager.getInstance().getCustomSkin());
                label.setColor(ColorPalette.green);
                label.setFontScale(0.2f);
                TextButton button = new TextButton("disconnect", GameAssetManager.getInstance().getCustomSkin());
                button.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ClientApp.getGameClient().close();
                    }
                });
                Table networkStatusTable = AbstractScreen.getNetworkStatusTable();
                networkStatusTable.clearChildren();
                networkStatusTable.add(label).row();
                networkStatusTable.add(button);
            }
            case "disconnected" -> {
                Label label = new Label("disconnected", GameAssetManager.getInstance().getCustomSkin());
                label.setColor(ColorPalette.red);
                label.setFontScale(0.2f);
                TextButton button = new TextButton("reconnect", GameAssetManager.getInstance().getCustomSkin());
                button.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ClientApp.reconnect();
                    }
                });
                Table networkStatusTable = AbstractScreen.getNetworkStatusTable();
                networkStatusTable.clearChildren();
                networkStatusTable.add(label).row();
                networkStatusTable.add(button);
            }
            case "tryingToConnect" -> {
                Label label = new Label("connecting...", GameAssetManager.getInstance().getCustomSkin());
                label.setColor(ColorPalette.yellow);
                label.setFontScale(0.2f);
                Table networkStatusTable = AbstractScreen.getNetworkStatusTable();
                networkStatusTable.clearChildren();
                networkStatusTable.add(label);
            }
        }
    }
}
