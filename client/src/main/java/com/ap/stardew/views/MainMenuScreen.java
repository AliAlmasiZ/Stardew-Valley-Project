package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.App;
import com.ap.stardew.models.Game;
import com.ap.stardew.records.GameStartingDetails;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen extends AbstractMenuScreen {
    TextButton profileMenuBtn;
    Button logoutBtn, newGameButton, loadGameButton, exitButton;
    Image logo;
    Label menuTitle;

    public MainMenuScreen() {
        super();
        setupUI();
    }

    private void setupUI() {
        rootTable.bottom();


        newGameButton = new Button(customSkin, "newGame");
        loadGameButton = new Button(customSkin, "loadGame");
        exitButton = new Button(customSkin, "exit");

        if(App.getLoggedInAccount() != null){
            logoutBtn = new Button(customSkin, "logout");
        }else {
            logoutBtn = new Button(customSkin, "back");
        }

        rootTable.add(newGameButton).pad(5);
        rootTable.add(loadGameButton).pad(5);
        rootTable.add(exitButton).pad(5);

        Table logoutButtonTable = new Table();
        logoutButtonTable.setFillParent(true);
        uiStage.addActor(logoutButtonTable);
        logoutButtonTable.bottom().right().pad(5);
        logoutButtonTable.add(logoutBtn);

        if(App.getLoggedInAccount() != null){
            profileMenuBtn = new TextButton(App.getLoggedInAccount().getUsername(), customSkin, "big");

            Table profileButtonTable = new Table();
            profileButtonTable.setFillParent(true);
            uiStage.addActor(profileButtonTable);
            profileButtonTable.top().left().pad(5);
            profileButtonTable.add(profileMenuBtn);

            profileMenuBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    ClientGame.getInstance().setScreen(new ProfileScreen());
                    dispose();
                }
            });
        }

        logo = new Image(customSkin.getDrawable("titleLogo"));
        Table logoTable = new Table();
        logoTable.setFillParent(true);
        logoTable.top();
        logoTable.pad(20);
        logoTable.add(logo);
        uiStage.addActor(logoTable);

        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game game = new Game();
                App.setActiveGame(game);

                Account[] accounts = {App.getUserByUsername("parsa"), App.getUserByUsername("ali"), App.getUserByUsername("ilia")};

                game.initGame(new GameStartingDetails(true, "asd", accounts, null, null, null));
                ClientGame.getInstance().setScreen(new GameScreen());
            }
        });
        logoutBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                exitAnim();
            }
        });
    }
    public void enterAnim(){
        float backgroundDuration = 0;
        newGameButton.addAction(
            Actions.sequence(
                Actions.moveBy(0, -newGameButton.getPrefHeight() - 20),
                Actions.delay(backgroundDuration + 0.1f),
                Actions.moveBy(0, newGameButton.getPrefHeight() + 20, 0.5f, Interpolation.swingOut)
            )
        );
        loadGameButton.addAction(
            Actions.sequence(
                Actions.moveBy(0, -loadGameButton.getPrefHeight() - 20),
                Actions.delay(backgroundDuration + 0.2f),
                Actions.moveBy(0, loadGameButton.getPrefHeight() + 20, 0.5f, Interpolation.swingOut)
            )
        );
        exitButton.addAction(
            Actions.sequence(
                Actions.moveBy(0, -exitButton.getPrefHeight() - 20),
                Actions.delay(backgroundDuration + 0.3f),
                Actions.moveBy(0, exitButton.getPrefHeight() + 20, 0.5f, Interpolation.swingOut)
            )
        );

        logoutBtn.addAction(
            Actions.sequence(
                Actions.moveBy(logoutBtn.getPrefWidth() + 20, 0),
                Actions.delay(backgroundDuration + 0.7f),
                Actions.moveBy(-logoutBtn.getPrefWidth() - 20, 0, 0.5f, Interpolation.swingOut)
            )
        );
        logo.addAction(
            Actions.sequence(
                Actions.alpha(0),
                Actions.delay(backgroundDuration + 1),
                Actions.alpha(1, 3f, Interpolation.smoother)
            )
        );
        if(profileMenuBtn != null){
            profileMenuBtn.addAction(
                Actions.sequence(
                    Actions.moveBy(0, profileMenuBtn.getPrefHeight() + 20),
                    Actions.delay(backgroundDuration + 2f),
                    Actions.moveBy(0, -profileMenuBtn.getPrefHeight() - 20, 0.5f, Interpolation.swingOut)
                )
            );
        }
    }
    public void exitAnim(){
        float backgroundDuration = 4;
        newGameButton.addAction(
            Actions.sequence(
                Actions.delay(0.1f),
                Actions.moveBy(0, -newGameButton.getPrefHeight() - 20, 0.5f, Interpolation.swingIn),
                Actions.visible(false)
            )
        );
        loadGameButton.addAction(
            Actions.sequence(
                Actions.delay(0.2f),
                Actions.moveBy(0, -loadGameButton.getPrefHeight() - 20, 0.5f, Interpolation.swingIn),
                Actions.visible(false)
            )
        );
        exitButton.addAction(
            Actions.sequence(
                Actions.delay(0.3f),
                Actions.moveBy(0, -exitButton.getPrefHeight() - 20, 0.5f, Interpolation.swingIn),
                Actions.visible(false)
            )
        );

        logoutBtn.addAction(
            Actions.sequence(
                Actions.delay(0.7f),
                Actions.moveBy(logoutBtn.getPrefWidth() + 20, 0, 0.5f, Interpolation.swingIn)
            )
        );

        if(profileMenuBtn != null){
            profileMenuBtn.addAction(
                Actions.sequence(
                    Actions.delay(2f),
                    Actions.moveBy(0, profileMenuBtn.getPrefHeight() + 20, 0.5f, Interpolation.swingIn)
                )
            );
        }

        logo.addAction(
            Actions.sequence(
                Actions.alpha(1),
                Actions.delay(0.8f),
                Actions.alpha(0, 3f, Interpolation.smoother)
            )
        );

        uiStage.addAction(
            Actions.sequence(
                Actions.delay(0.8f),
                Actions.parallel(
                    Actions.run(()->{
                        parallaxBackground.move(-300, 4f, Interpolation.smoother);
                    }),
                    Actions.moveBy(0, 300, 4f, Interpolation.smoother)
                )
            )
        );


        backgroundStage.addAction(
            Actions.sequence(
                Actions.delay(backgroundDuration + 0.8f),
                Actions.run(()->{
                    MainScreen mainScreen = new MainScreen();
                    mainScreen.enterAnim();
                    ClientGame.getInstance().setScreen(mainScreen);
                    dispose();
                })
            )
        );
    }
}
