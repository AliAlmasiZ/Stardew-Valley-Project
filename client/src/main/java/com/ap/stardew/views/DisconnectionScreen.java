package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.views.widgets.WrapperWithBackground;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.io.IOException;
import java.util.List;

public class DisconnectionScreen extends AbstractMenuScreen{
    private final GameScreen gameScreen;
    private final boolean thisUserHasDisconnected; //sorry
    private float timeLeft;
    private final Label messageLabel;
    private final Label countDownLabel;
    private Table disconnectedUsernamesBox;
    private Button backButton;

    public DisconnectionScreen(GameScreen gameScreen, List<String> usernames, final float timeLeft) {
        this.gameScreen = gameScreen;
        this.timeLeft = timeLeft;
        thisUserHasDisconnected = usernames.get(0).equals(ClientApp.getUsername());

        Table mainBox = new Table();

        messageLabel = new Label("", customSkin);
        messageLabel.setWrap(true);
        messageLabel.setColor(0, 0, 0, 1);

        mainBox.add(new WrapperWithBackground(messageLabel, customSkin.getDrawable("1222"))).width(250).grow();

        countDownLabel = new Label("2:00", customSkin, "big");
        countDownLabel.setColor(Color.BLACK);
        mainBox.add(new WrapperWithBackground(countDownLabel, customSkin.getDrawable("0110")).padLeft(1)).width(35).growY().left().row();

        if(usernames.get(0).equals(ClientApp.getUsername())){
            messageLabel.setText("You got disconnected from server. The game will automatically save after two minutes");
            backButton = new Button(customSkin, "back");

            backButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    dispose();
                    ClientGame.getInstance().setScreen(new MainScreen());
                }
            });

            Table wrapper = new Table();
            wrapper.setFillParent(true);
            uiStage.addActor(wrapper);
            wrapper.bottom().right();
            wrapper.pad(3);
            wrapper.add(backButton);
        }else{
            messageLabel.setText(
                "These players have disconnected from the game. The game will automatically save after two minutes");

            disconnectedUsernamesBox = new Table();
            disconnectedUsernamesBox.setBackground(customSkin.getDrawable("0011"));

            mainBox.row();
            mainBox.add(disconnectedUsernamesBox).growX().row();

            updateDisconnectedPlayers(usernames);
        }

        rootTable.add(mainBox);
    }

    public void updateDisconnectedPlayers(List<String> usernames){
        if(disconnectedUsernamesBox == null) return;

        disconnectedUsernamesBox.clearChildren();

        for (String username : usernames) {
            Label label = new Label(username, customSkin, "withBackground");
            label.setAlignment(Align.center);
            disconnectedUsernamesBox.add(label).spaceBottom(1).growX().row();
        }
    }

    public void resumeGame(){
        ClientGame.getInstance().setScreen(gameScreen);
    }

    @Override
    public void render(float delta) {
        if(!thisUserHasDisconnected){
            if(!ClientApp.isConnected()){
                dispose();

                DisconnectionScreen disconnectionScreen = new DisconnectionScreen(gameScreen, List.of(ClientApp.getUsername()), timeLeft);
                ClientGame.getInstance().setScreen(disconnectionScreen);
            }
        }

        if (timeLeft > 0) {
            timeLeft -= delta;
            if (timeLeft < 0) timeLeft = 0;

            int totalSeconds = (int) timeLeft;
            int minute = totalSeconds / 60;
            int sec = totalSeconds % 60;

            countDownLabel.setText(String.format("%d:%02d", minute, sec));

            if (timeLeft == 0) {
                onTimerFinished();
            }
        }

        super.render(delta);
    }

    private void onTimerFinished(){
        gameScreen.dispose();
        ClientGame.getInstance().setScreen(new MultiplayerScreen());
    }
}
