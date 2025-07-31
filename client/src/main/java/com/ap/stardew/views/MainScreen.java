package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.App;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.enums.Menu;
import com.ap.stardew.records.GameStartingDetails;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class MainScreen extends AbstractMenuScreen {
    private TextButton registerButton;
    private TextButton loginButton;
    private TextButton guestButton;
    private TextButton play;

    public MainScreen() {
        super();
        Table mainBox = new Table();

        registerButton = new TextButton("Register", customSkin, "big");
        loginButton = new TextButton("Login", customSkin, "big");
        guestButton = new TextButton("Guest", customSkin, "big");
        play = new TextButton("Play(for now)", customSkin, "big");


        mainBox.defaults().spaceBottom(3);
        mainBox.add(registerButton).growX();
        mainBox.row();
        mainBox.add(loginButton).growX();
        mainBox.row();
        mainBox.add(guestButton).growX();
        mainBox.row();
        mainBox.add(play).growX();
        mainBox.row();

        mainBox.pack();

        rootTable.add(mainBox);

        // Adding listeners
        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ClientGame.getInstance().setScreen(new SignupScreen());
                dispose();
            }
        });

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ClientGame.getInstance().setScreen(new LoginScreen());
            }
        });

        guestButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parallaxBackground.move(300, 4, Interpolation.smoother);
                rootTable.addAction(Actions.sequence(
                    Actions.moveBy(0, -300, 4, Interpolation.smoother),
                    Actions.run(()->{
                        MainMenuScreen mainMenuScreen = new MainMenuScreen();
                        mainMenuScreen.enterAnim();
                        ClientGame.getInstance().setScreen(mainMenuScreen);
                    })
                ));
            }
        });

        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game game = new Game();
                App.setActiveGame(game);

                Account[] accounts = {App.getUserByUsername("parsa"), App.getUserByUsername("ali"), App.getUserByUsername("ilia")};

                game.initGame(new GameStartingDetails(true, "asd", accounts, null, null, null));
                ClientGame.getInstance().setScreen(new GameScreen());
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



    public void enterAnim(){
        registerButton.addAction(
            Actions.sequence(
                Actions.alpha(0),
                Actions.delay(0.1f),
                Actions.alpha(1, 0.5f, Interpolation.smooth)
            )
        );
        loginButton.addAction(
            Actions.sequence(
                Actions.alpha(0),
                Actions.delay(0.2f),
                Actions.alpha(1, 0.5f, Interpolation.smooth)
            )
        );
        guestButton.addAction(
            Actions.sequence(
                Actions.alpha(0),
                Actions.delay(0.3f),
                Actions.alpha(1, 0.5f, Interpolation.smooth)
            )
        );
        play.addAction(
            Actions.sequence(
                Actions.alpha(0),
                Actions.delay(0.4f),
                Actions.alpha(1, 0.5f, Interpolation.smooth)
            )
        );
    }
    public void exitAnim(){

    }
}
