package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.models.LobbyInfo;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.views.managers.ActorAnimManager;
import com.ap.stardew.views.managers.TransitionManager;
import com.ap.stardew.views.widgets.LabelMessage;
import com.ap.stardew.views.widgets.TransformWidgetWrapper;
import com.ap.stardew.views.widgets.WrapperWithBackground;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class NewGameScreen extends AbstractMenuScreen{
    private int maxPlayers = 3;

    public NewGameScreen(){
        Table mainBox = new Table();
        Table dialog = new Table();

        mainBox.defaults().spaceBottom(5);

        TextField nameField = new TextField("", customSkin);
        TextField passwordField = new TextField("", customSkin);
        nameField.setMessageText("Lobby Name");
        passwordField.setMessageText("Password (optional)");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        CheckBox visibleCheckbox = new CheckBox("", customSkin);
        visibleCheckbox.setChecked(true);

        Label maxPlayersLabel = new Label("3", customSkin, "inventoryQuantity");
        maxPlayersLabel.setFontScale(1.15f);
        maxPlayersLabel.setColor(ColorPalette.yellow);
        maxPlayersLabel.setAlignment(Align.center);

        TransformWidgetWrapper<Button> minusButtonWrapper       = new TransformWidgetWrapper<>(new Button(customSkin, "smallMinus"));
        TransformWidgetWrapper<Button> plusButtonWrapper        = new TransformWidgetWrapper <>(new Button(customSkin, "smallPlus"));
        TransformWidgetWrapper<Button> backButtonWrapper        = new TransformWidgetWrapper<>(new Button(customSkin, "back"));
        TransformWidgetWrapper<TextButton> submitButtonWrapper  = new TransformWidgetWrapper<>(new TextButton("Create Lobby", customSkin, "big"));

        Table selectTable = new Table();
        selectTable.add(minusButtonWrapper);
        selectTable.add(new WrapperWithBackground(maxPlayersLabel, customSkin.getDrawable("borderLessTextField"))).maxHeight(11);
        selectTable.add(plusButtonWrapper);

        dialog.add(nameField).growX().colspan(2).spaceBottom(2).row();
        dialog.add(passwordField).growX().colspan(2).row();
        dialog.add(new Label("max players:", customSkin){{setColor(Color.BLACK);}}).expandX().padLeft(3).left();
        dialog.add(selectTable).expandX().right().row();
        dialog.add(new Label("visible:", customSkin){{setColor(Color.BLACK);}}).expandX().padLeft(3).left();
        dialog.add(visibleCheckbox).right().row();

        mainBox.add(backButtonWrapper).expandX().right().row();
        mainBox.add(dialog).row();
        mainBox.add(submitButtonWrapper).growX().pad(0, 3, 0, 3).row();

        ActorAnimManager.addHorizontalElastic(backButtonWrapper, true);
        ActorAnimManager.addHorizontalDrop(minusButtonWrapper, false, 2);
        ActorAnimManager.addHorizontalDrop(plusButtonWrapper, true, 2);
        ActorAnimManager.addRotateAction(submitButtonWrapper, 2);

        backButtonWrapper.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MainMenuScreen mainMenuScreen = new MainMenuScreen();
                uiStage.addAction(
                    Actions.sequence(
                        Actions.run(() -> mainMenuScreen.prepareForAnim(false)),
                        Actions.run(() -> TransitionManager.horizontalTransition(mainMenuScreen, NewGameScreen.this, true, 1.5f, Interpolation.smoother)),
                        Actions.delay(0.5f),
                        Actions.run(() -> mainMenuScreen.enterAnim(false, true))
                    )
                );
            }
        });

        minusButtonWrapper.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                minusButtonWrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.moveBy(2, 0, 0.2f, Interpolation.swingOut),
                        Actions.moveBy(-2, 0, 0.2f, Interpolation.swingOut)
                    )
                );
                if(maxPlayers == 2){
                    new LabelMessage(maxPlayersLabel, "at least 2", customSkin){{setColor(ColorPalette.red);}}.show();
                    return;
                }
                maxPlayers--;
                maxPlayersLabel.setText(maxPlayers);
            }
        });
        plusButtonWrapper.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                plusButtonWrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.moveBy(-2, 0, 0.2f, Interpolation.swingOut),
                        Actions.moveBy(2, 0, 0.2f, Interpolation.swingOut)
                    )
                );
                if(maxPlayers == 4){
                    new LabelMessage(maxPlayersLabel, "at most 4", customSkin){{setColor(ColorPalette.red);}}.show();
                    return;
                }
                maxPlayers++;
                maxPlayersLabel.setText(maxPlayers);
            }
        });

        submitButtonWrapper.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = nameField.getText();
                String password = passwordField.getText();
                boolean isVisible = visibleCheckbox.isChecked();

                if (!name.isEmpty()) {
                    JSONMessage message = new JSONMessage(JSONMessage.Type.lobby_command);
                    message.put("command", "host");
                    message.put("lobby_name", name);
                    message.put("host_username", ClientApp.getUsername());
                    message.put("max_players", maxPlayers);
                    message.put("password", password);
                    message.put("is_visible", isVisible);

                    JSONMessage response = ClientApp.sendAndWaitForResponse(message, 5000);
                    LobbyInfo lobbyInfo = response.getFromBody("lobby_info");
                    if(lobbyInfo == null)
                        return;

                    TransitionManager.verticalTransition(new LobbyScreen(lobbyInfo, true, NewGameScreen.class), NewGameScreen.this, 500, 4, Interpolation.smoother);
                }else {
                    new LabelMessage(nameField, "should not be empty", customSkin){{setColor(ColorPalette.red);}}.show();
                }
            }
        });
        dialog.setBackground(customSkin.getDrawable("frameNinePatch2"));


        rootTable.add(mainBox);
    }
}
