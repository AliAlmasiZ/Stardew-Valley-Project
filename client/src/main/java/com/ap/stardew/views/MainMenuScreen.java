package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.views.managers.ActorAnimManager;
import com.ap.stardew.views.managers.TransitionManager;
import com.ap.stardew.views.widgets.PopUpMessage;
import com.ap.stardew.views.widgets.TransformWidgetWrapper;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import java.util.ArrayList;

public class MainMenuScreen extends AbstractMenuScreen {
    TextButton profileMenuBtn;
    TransformWidgetWrapper<Button> logoutBtnWrapper, newGameButtonWrapper, loadGameButtonWrapper, coopButtonWrapper, exitButtonWrapper, infoButtonWrapper;
    Image logo;
    Label menuTitle;

    public MainMenuScreen() {
        super();
        setupUI();
    }

    private void setupUI() {
        rootTable.bottom();
        newGameButtonWrapper    = new TransformWidgetWrapper<>(new Button(customSkin, "newGame"));
        loadGameButtonWrapper   = new TransformWidgetWrapper<>(new Button(customSkin, "loadGame"));
        coopButtonWrapper       = new TransformWidgetWrapper<>(new Button(customSkin, "coop"));
        exitButtonWrapper       = new TransformWidgetWrapper<>(new Button(customSkin, "exit"));
        logoutBtnWrapper        = new TransformWidgetWrapper<>(new Button(customSkin, "logoutDown"));
        infoButtonWrapper       = new TransformWidgetWrapper<>(new Button(customSkin, "info"));

        rootTable.add(newGameButtonWrapper).pad(5);
        rootTable.add(loadGameButtonWrapper).pad(5);
        rootTable.add(coopButtonWrapper).pad(5);
        rootTable.add(exitButtonWrapper).pad(5);

        Table logoutButtonTable = new Table();
        logoutButtonTable.setFillParent(true);
        uiStage.addActor(logoutButtonTable);
        logoutButtonTable.bottom().right().pad(5);
        logoutButtonTable.add(logoutBtnWrapper);

        Table infoButtonTable = new Table();
        infoButtonTable.setFillParent(true);
        infoButtonTable.pad(5).bottom().left();
        infoButtonTable.add(infoButtonWrapper);
        uiStage.addActor(infoButtonTable);

        if(ClientApp.getUsername() != null){
            profileMenuBtn = new TextButton(ClientApp.getUsername(), customSkin, "big");

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
        logo.setScaling(Scaling.fit);
        logo.setAlign(Align.top);
        logoTable.add(logo).width(300).top();
        uiStage.addActor(logoTable);

        ActorAnimManager.addRotateAction(newGameButtonWrapper, 3);
        ActorAnimManager.addRotateAction(loadGameButtonWrapper, 3);
        ActorAnimManager.addRotateAction(coopButtonWrapper, 3);
        ActorAnimManager.addRotateAction(exitButtonWrapper, 3);
        ActorAnimManager.addVerticalElastic(logoutBtnWrapper, true);
        ActorAnimManager.addWiggle(infoButtonWrapper);

        newGameButtonWrapper.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiStage.addAction(
                    Actions.sequence(
                        Actions.run(() -> exitAnim(false, true)),
                        Actions.delay(0.7f),
                        Actions.run(()->TransitionManager.horizontalTransition(new NewGameScreen(), MainMenuScreen.this, false, 1.5f, Interpolation.smoother))
                    )
                );
            }
        });
        loadGameButtonWrapper.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!ClientApp.isConnected()){
                    new PopUpMessage("You should be connected").show(AbstractMenuScreen.getFrontStage());
                    return;
                }

                JSONMessage request = new JSONMessage(JSONMessage.Type.command);
                request.put("command", "getSavedGames");
                request.put("token", ClientApp.getToken());

                JSONMessage respond = ClientApp.sendAndWaitForResponse(request, 2000);

                if(respond == null || respond.getFromBody("result") == null || respond.getFromBody("games") == null){
                    new PopUpMessage("failed to retrieve saved games").show(AbstractMenuScreen.getFrontStage());
                    return;
                }
                if(!respond.getFromBody("result", Result.class).isSuccessful()){
                    new PopUpMessage(respond.getFromBody("result", Result.class).message()).show(AbstractMenuScreen.getFrontStage());
                    return;
                }
                if(respond.getFromBody("games", ArrayList.class).isEmpty()){
                    new PopUpMessage("you have no saved games").show(AbstractMenuScreen.getFrontStage());
                    return;
                }
                uiStage.addAction(
                    Actions.sequence(
                        Actions.run(() -> exitAnim(false, false)),
                        Actions.delay(0.7f),
                        Actions.run(()->TransitionManager.horizontalTransition(new LoadGameScreen(respond.getFromBody("games")), MainMenuScreen.this, true, 1.5f, Interpolation.smoother))
                    )
                );
            }
        });
        coopButtonWrapper.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiStage.addAction(
                    Actions.sequence(
                        Actions.run(() -> MainMenuScreen.this.exitAnim(false, false)),
                        Actions.delay(0.8f),
                        Actions.run(()->{
                            TransitionManager.verticalTransition(new MultiplayerScreen(), MainMenuScreen.this, 500, 4, Interpolation.smoother);
                        })
                    )
                );
            }
        });
        logoutBtnWrapper.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiStage.addAction(
                    Actions.sequence(
                        Actions.run(() -> MainMenuScreen.this.exitAnim(true, true)),
                        Actions.delay(0.8f),
                        Actions.run(()->{
                            TransitionManager.verticalTransition(new MainScreen(), MainMenuScreen.this, -300, 4, Interpolation.smoother);
                        })
                    )
                );
            }
        });
        infoButtonWrapper.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiStage.addAction(
                    Actions.sequence(
                        Actions.run(() -> exitAnim(false, true)),
                        Actions.delay(0.7f),
                        Actions.run(()->TransitionManager.horizontalTransition(new InfoScreen(), MainMenuScreen.this, false, 1.5f, Interpolation.smoother))
                    )
                );
            }
        });
    }
    public void prepareForAnim(boolean hideLogo){
        newGameButtonWrapper.setVisible(false);
        loadGameButtonWrapper.setVisible(false);
        exitButtonWrapper.setVisible(false);
        logoutBtnWrapper.setVisible(false);
        if(hideLogo){
            logo.setVisible(false);
        }
        coopButtonWrapper.setVisible(false);
        infoButtonWrapper.setVisible(false);
        if(profileMenuBtn != null) profileMenuBtn.setVisible(false);
    }
    public void enterAnim(boolean hideLogo, boolean toRight){
        newGameButtonWrapper.setVisible(true);
        loadGameButtonWrapper.setVisible(true);
        exitButtonWrapper.setVisible(true);
        logoutBtnWrapper.setVisible(true);
        coopButtonWrapper.setVisible(true);
        infoButtonWrapper.setVisible(true);
        logo.setVisible(true);
        if(profileMenuBtn != null) profileMenuBtn.setVisible(true);
        float backgroundDuration = 3f;
        if(!hideLogo) backgroundDuration = 0;
        if(hideLogo){
            logo.addAction(
                Actions.sequence(
                    Actions.alpha(0),
                    Actions.delay(0.3f),
                    Actions.alpha(1, 5f, Interpolation.smoother)
                )
            );
        }
        if(toRight){
            newGameButtonWrapper.addAction(
                Actions.sequence(
                    Actions.moveBy(0, -newGameButtonWrapper.getPrefHeight() - 20),
                    Actions.delay(backgroundDuration + 0.1f),
                    Actions.moveBy(0, newGameButtonWrapper.getPrefHeight() + 20, 0.5f, Interpolation.swingOut)
                )
            );
            loadGameButtonWrapper.addAction(
                Actions.sequence(
                    Actions.moveBy(0, -loadGameButtonWrapper.getPrefHeight() - 20),
                    Actions.delay(backgroundDuration + 0.2f),
                    Actions.moveBy(0, loadGameButtonWrapper.getPrefHeight() + 20, 0.5f, Interpolation.swingOut)
                )
            );
            coopButtonWrapper.addAction(
                Actions.sequence(
                    Actions.moveBy(0, -coopButtonWrapper.getPrefHeight() - 20),
                    Actions.delay(backgroundDuration + 0.3f),
                    Actions.moveBy(0, coopButtonWrapper.getPrefHeight() + 20, 0.5f, Interpolation.swingOut)
                )
            );
            exitButtonWrapper.addAction(
                Actions.sequence(
                    Actions.moveBy(0, -exitButtonWrapper.getPrefHeight() - 20),
                    Actions.delay(backgroundDuration + 0.4f),
                    Actions.moveBy(0, exitButtonWrapper.getPrefHeight() + 20, 0.5f, Interpolation.swingOut)
                )
            );
        }else {
            newGameButtonWrapper.addAction(
                Actions.sequence(
                    Actions.moveBy(0, -newGameButtonWrapper.getPrefHeight() - 20),
                    Actions.delay(backgroundDuration + 0.4f),
                    Actions.moveBy(0, newGameButtonWrapper.getPrefHeight() + 20, 0.5f, Interpolation.swingOut)
                )
            );
            loadGameButtonWrapper.addAction(
                Actions.sequence(
                    Actions.moveBy(0, -loadGameButtonWrapper.getPrefHeight() - 20),
                    Actions.delay(backgroundDuration + 0.3f),
                    Actions.moveBy(0, loadGameButtonWrapper.getPrefHeight() + 20, 0.5f, Interpolation.swingOut)
                )
            );
            coopButtonWrapper.addAction(
                Actions.sequence(
                    Actions.moveBy(0, -coopButtonWrapper.getPrefHeight() - 20),
                    Actions.delay(backgroundDuration + 0.2f),
                    Actions.moveBy(0, coopButtonWrapper.getPrefHeight() + 20, 0.5f, Interpolation.swingOut)
                )
            );
            exitButtonWrapper.addAction(
                Actions.sequence(
                    Actions.moveBy(0, -exitButtonWrapper.getPrefHeight() - 20),
                    Actions.delay(backgroundDuration + 0.1f),
                    Actions.moveBy(0, exitButtonWrapper.getPrefHeight() + 20, 0.5f, Interpolation.swingOut)
                )
            );
        }
        if(toRight){
            logoutBtnWrapper.addAction(
                Actions.sequence(
                    Actions.moveBy(logoutBtnWrapper.getPrefWidth() + 20, 0),
                    Actions.delay(backgroundDuration + 0.7f),
                    Actions.moveBy(-logoutBtnWrapper.getPrefWidth() - 20, 0, 0.5f, Interpolation.swingOut)
                )
            );
        }else {
            logoutBtnWrapper.addAction(
                Actions.sequence(
                    Actions.moveBy(0, -logoutBtnWrapper.getPrefHeight() - 20),
                    Actions.delay(backgroundDuration + 0.3f),
                    Actions.moveBy(0, logoutBtnWrapper.getPrefHeight() + 20, 0.3f, Interpolation.swingOut)
                )
            );
        }
        if(profileMenuBtn != null){
            profileMenuBtn.addAction(
                Actions.sequence(
                    Actions.moveBy(0, profileMenuBtn.getPrefHeight() + 20),
                    Actions.delay(backgroundDuration + 0.6f),
                    Actions.moveBy(0, -profileMenuBtn.getPrefHeight() - 20, 0.5f, Interpolation.swingOut)
                )
            );
        }
        infoButtonWrapper.addAction(
            Actions.sequence(
                Actions.moveBy(0, -infoButtonWrapper.getPrefHeight() - 20),
                Actions.delay(backgroundDuration + 0.6f),
                Actions.moveBy(0, infoButtonWrapper.getPrefHeight() + 20, 0.5f, Interpolation.swingOut)
            )
        );
    }
    public void exitAnim(boolean hideLogo, boolean toRight){
        float backgroundDuration = 4;
        if(toRight){
            newGameButtonWrapper.addAction(
                Actions.sequence(
                    Actions.delay(0.1f),
                    Actions.moveBy(0, -newGameButtonWrapper.getPrefHeight() - 20, 0.5f, Interpolation.swingIn),
                    Actions.visible(false)
                )
            );
            loadGameButtonWrapper.addAction(
                Actions.sequence(
                    Actions.delay(0.2f),
                    Actions.moveBy(0, -loadGameButtonWrapper.getPrefHeight() - 20, 0.5f, Interpolation.swingIn),
                    Actions.visible(false)
                )
            );
            coopButtonWrapper.addAction(
                Actions.sequence(
                    Actions.delay(0.3f),
                    Actions.moveBy(0, -coopButtonWrapper.getPrefHeight() - 20, 0.5f, Interpolation.swingIn),
                    Actions.visible(false)
                )
            );
            exitButtonWrapper.addAction(
                Actions.sequence(
                    Actions.delay(0.4f),
                    Actions.moveBy(0, -exitButtonWrapper.getPrefHeight() - 20, 0.5f, Interpolation.swingIn),
                    Actions.visible(false)
                )
            );
        }else{
            newGameButtonWrapper.addAction(
                Actions.sequence(
                    Actions.delay(0.4f),
                    Actions.moveBy(0, -newGameButtonWrapper.getPrefHeight() - 20, 0.5f, Interpolation.swingIn),
                    Actions.visible(false)
                )
            );
            loadGameButtonWrapper.addAction(
                Actions.sequence(
                    Actions.delay(0.3f),
                    Actions.moveBy(0, -loadGameButtonWrapper.getPrefHeight() - 20, 0.5f, Interpolation.swingIn),
                    Actions.visible(false)
                )
            );
            coopButtonWrapper.addAction(
                Actions.sequence(
                    Actions.delay(0.2f),
                    Actions.moveBy(0, -coopButtonWrapper.getPrefHeight() - 20, 0.5f, Interpolation.swingIn),
                    Actions.visible(false)
                )
            );
            exitButtonWrapper.addAction(
                Actions.sequence(
                    Actions.delay(0.1f),
                    Actions.moveBy(0, -exitButtonWrapper.getPrefHeight() - 20, 0.5f, Interpolation.swingIn),
                    Actions.visible(false)
                )
            );
        }

        if(toRight){
            logoutBtnWrapper.addAction(
                Actions.sequence(
                    Actions.delay(0.6f),
                    Actions.moveBy(logoutBtnWrapper.getPrefWidth() + 20, 0, 0.5f, Interpolation.swingIn)
                )
            );
        }else {
            logoutBtnWrapper.addAction(
                Actions.sequence(
                    Actions.moveBy(0, -logoutBtnWrapper.getPrefHeight() - 20, 0.3f, Interpolation.swingIn)
                )
            );
        }

        if(profileMenuBtn != null){
            profileMenuBtn.addAction(
                Actions.sequence(
                    Actions.delay(0.5f),
                    Actions.moveBy(0, profileMenuBtn.getPrefHeight() + 20, 0.5f, Interpolation.swingIn),
                    Actions.visible(false)
                )
            );
        }
        infoButtonWrapper.addAction(
            Actions.sequence(
                Actions.delay(0.5f),
                Actions.moveBy(0, -infoButtonWrapper.getPrefHeight() - 20, 0.5f, Interpolation.swingIn),
                Actions.visible(false)
            )
        );
        if(hideLogo){
            logo.addAction(
                Actions.sequence(
                    Actions.alpha(1),
                    Actions.delay(0.8f),
                    Actions.alpha(0, 3f, Interpolation.smoother)
                )
            );
        }

    }
}
