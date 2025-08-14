package com.ap.stardew.views;

import com.ap.stardew.ClientGame;

public class TransitionScreen extends AbstractMenuScreen {
    private final AbstractScreen prevScreen;
    private final AbstractScreen newScreen;

    public TransitionScreen(AbstractScreen prevScreen, AbstractScreen newScreen) {
        this.prevScreen = prevScreen;
        this.newScreen = newScreen;
    }

    @Override
    public void render(float delta) {
        prevScreen.uiStage.act();
        newScreen.uiStage.act();
        frontStage.act();
        backgroundStage.act();
        uiStage.act();

        backgroundStage.draw();
        prevScreen.uiStage.draw();
        newScreen.uiStage.draw();
        uiStage.draw();
        frontStage.draw();
    }

    public void finish(){
        prevScreen.dispose();
        ClientGame.getInstance().setScreen(newScreen);
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
