package com.ap.stardew.views;

import com.ap.stardew.StardewGame;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.App;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.enums.Menu;
import com.ap.stardew.records.GameStartingDetails;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

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
                Game game = new Game();
                App.setActiveGame(game);

                Account[] accounts = {App.getUserByUsername("parsa"), App.getUserByUsername("ali"), App.getUserByUsername("ilia")};

                game.initGame(new GameStartingDetails(true, "asd", accounts, null, null, null));
                App.setCurrentMenu(Menu.GAME_MENU);
                StardewGame.getInstance().setScreen(new GameScreen());
            }
        });
    }

    public void registerDialog() {
        //TODO
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void render(float delta) {
        uiStage.act(delta);
        //test---------------
        Texture texture = new Texture("Content(unpacked)/LooseSprites/JunimoNoteMobile.png");
        uiStage.getBatch().begin();
        uiStage.getBatch().draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiStage.getBatch().end();
        //---------------------
        uiStage.draw();
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
