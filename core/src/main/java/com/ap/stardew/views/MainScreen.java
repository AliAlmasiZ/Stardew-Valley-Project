package com.ap.stardew.views;

import com.ap.stardew.StardewGame;
import com.ap.stardew.controllers.GameAssetManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainScreen extends AbstractScreen {
    private TextButton registerButton;
    private TextButton loginButton;
    private TextButton guestButton;

    public MainScreen() {
        super();
        registerButton = new TextButton("Register", skin);
        loginButton = new TextButton("Login", skin);
        guestButton = new TextButton("Play as Guest", skin);

        rootTable.add(registerButton);
        rootTable.row();
        rootTable.add(loginButton);
        rootTable.row();
        rootTable.add(guestButton);
        rootTable.row();

        // Adding listeners
        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StardewGame.getInstance().setScreen(new SignupScreen());
                dispose();
            }
        });

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StardewGame.getInstance().setScreen(new LoginScreen());
            }
        });

        guestButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //TODO
            }
        });
    }

    public void registerDialog() {
        //TODO
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        //test---------------
        Texture texture = new Texture("Content(unpacked)/LooseSprites/JunimoNoteMobile.png");
        stage.getBatch().begin();
        stage.getBatch().draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        //---------------------
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
    }
}
