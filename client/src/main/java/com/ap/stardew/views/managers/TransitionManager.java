package com.ap.stardew.views.managers;

import com.ap.stardew.ClientGame;
import com.ap.stardew.views.AbstractMenuScreen;
import com.ap.stardew.views.TransitionScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.security.SecureRandom;

public class TransitionManager{
    private static SecureRandom random = new SecureRandom();
    public static void verticalTransition(AbstractMenuScreen newScreen,
                                          AbstractMenuScreen prevScreen,
                                          float movement,
                                          float time,
                                          Interpolation interpolation){

        TransitionScreen tmpScreen = new TransitionScreen(prevScreen, newScreen);

        newScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        newScreen.getUiStage().act();

        float dir = movement > 0 ? 1 : -1;

        prevScreen.getUiStage().addAction(
            Actions.moveBy(0, dir * -prevScreen.getUiStage().getViewport().getWorldHeight(), time, interpolation)
        );
        newScreen.getUiStage().addAction(
            Actions.sequence(
                Actions.moveBy(0, dir * newScreen.getUiStage().getViewport().getWorldHeight()),
                Actions.moveBy(0, dir * -newScreen.getUiStage().getViewport().getWorldHeight(), time, interpolation),
                Actions.run(tmpScreen::finish)
            )
        );
        AbstractMenuScreen.getParallaxBackground().move(movement, time, interpolation);

        ClientGame.getInstance().setScreen(tmpScreen);
    }
    public static void horizontalTransition(AbstractMenuScreen newScreen,
                                          AbstractMenuScreen prevScreen,
                                          boolean toRight,
                                          float time,
                                          Interpolation interpolation){

        TransitionScreen tmpScreen = new TransitionScreen(prevScreen, newScreen);

        newScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        tmpScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        newScreen.getUiStage().act();

        float dir = toRight ? -1 : 1;

        prevScreen.getUiStage().addAction(
            Actions.sequence(
                Actions.moveBy(dir * prevScreen.getUiStage().getViewport().getWorldWidth(), 0, time, interpolation),
                Actions.run(tmpScreen::finish)
            )
        );
        newScreen.getUiStage().addAction(
            Actions.sequence(
                Actions.moveBy(dir * -(newScreen.getUiStage().getViewport().getWorldWidth()),0),
                Actions.moveBy(dir * (newScreen.getUiStage().getViewport().getWorldWidth()), 0, time, interpolation)
            )
        );

        ClientGame.getInstance().setScreen(tmpScreen);
    }
}
