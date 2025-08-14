package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.LobbyInfo;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.dto.SavedGameDetails;
import com.ap.stardew.views.managers.ActorAnimManager;
import com.ap.stardew.views.managers.TransitionManager;
import com.ap.stardew.views.widgets.InGameDialog;
import com.ap.stardew.views.widgets.PopUpMessage;
import com.ap.stardew.views.widgets.TransformWidgetWrapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import javax.swing.plaf.ToolBarUI;
import java.util.ArrayList;

public class MultiplayerScreen extends AbstractMenuScreen {
    private Table lobbyList;
    private ScrollPane scrollPane;

    private ArrayList<LobbyInfo> availableLobbies = new ArrayList<>();

    public MultiplayerScreen() {
        super();
        setupUI();

//        fetchLobbies();
    }

    public void setupUI() {
        Table mainTable = new Table();
        Table lobbiesTable = new Table();
        lobbyList = new Table();
        scrollPane = new ScrollPane(lobbyList);
        scrollPane.setFadeScrollBars(false);

        TextButton joinByIdBtn  = new TextButton("Join", customSkin);
        TextButton refreshBtn   = new TextButton("Refresh", customSkin);
        TransformWidgetWrapper<Button> backBtn          = new TransformWidgetWrapper<>(new Button(customSkin, "backDown"));

        TextField lobbyIdField = new TextField("", customSkin);
        TextField lobbyPasswordField = new TextField("", customSkin);
        lobbyIdField.setMessageText("Lobby id");
        lobbyPasswordField.setMessageText("Lobby password");
        lobbyPasswordField.setPasswordCharacter('*');
        lobbyPasswordField.setPasswordMode(true);

        Table leftTable = new Table();
        Table rightTable = new Table();

        leftTable.setBackground(customSkin.getDrawable("1221"));
        rightTable.setBackground(customSkin.getDrawable("0110"));

        mainTable.defaults().spaceBottom(5);
        mainTable.add(lobbiesTable).row();
        lobbiesTable.add(leftTable).growY();
        lobbiesTable.add(rightTable).growY();

        leftTable.add(refreshBtn).expandX().left().spaceBottom(2 ).row();
        leftTable.add(scrollPane).grow().size(200, 150);

        rightTable.defaults().pad(2);
        rightTable.top();
        rightTable.add(lobbyIdField).width(60).row();
        rightTable.add(lobbyPasswordField).width(60).row();
        rightTable.add(joinByIdBtn).growX().row();
        mainTable.add(backBtn);

        rootTable.add(mainTable);

        ActorAnimManager.addVerticalElastic(backBtn, true);

        joinByIdBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String lobbyId = lobbyIdField.getText();
                if (!lobbyId.trim().isEmpty()) {
                    JSONMessage response = joinLobby(lobbyId, lobbyPasswordField.getText());
                    Result result = response.getFromBody("result");
                    if(result.isSuccessful()) {
                        ClientGame.getInstance().setScreen(new LobbyScreen(response.getFromBody("lobby_info"), false, MultiplayerScreen.class));
                        dispose();
                    } else {
                        //TODO : show Error ressult.message
                    }
                }
            }
        });
        refreshBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fetchLobbies();
            }
        });
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MainMenuScreen mainMenuScreen = new MainMenuScreen();
                mainMenuScreen.getUiStage().addAction(
                    Actions.sequence(
                        Actions.run(() -> mainMenuScreen.prepareForAnim(false)),
                        Actions.delay(4),
                        Actions.run(() -> mainMenuScreen.enterAnim(false, false))
                    )
                );
                TransitionManager.verticalTransition(mainMenuScreen, MultiplayerScreen.this, -500, 4, Interpolation.smoother);
                return;
            }
        });
    }

    private void showJoinPrivateLobbyDialog(LobbyInfo lobby) {
        Dialog dialog = new Dialog("Enter Password", customSkin);
        Table contentTable = dialog.getContentTable();

        Label infoLabel = new Label("Joining: " + lobby.getLobbyName(), customSkin);
        TextField passwordField = new TextField("", customSkin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        TextButton confirmButton = new TextButton("Join", customSkin);
        TextButton cancelButton = new TextButton("Cancel", customSkin);

        contentTable.pad(20);
        contentTable.add(infoLabel).padBottom(10).row();
        contentTable.add(passwordField).growX().padBottom(20).row();

        dialog.getButtonTable().add(confirmButton).pad(10);
        dialog.getButtonTable().add(cancelButton).pad(10);

        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String password = passwordField.getText();
                JSONMessage response = joinLobby(lobby.getLobbyId(), password);

                Result result = response.getFromBody("result");
                if(result.isSuccessful()) {
                    ClientGame.getInstance().setScreen(new LobbyScreen(lobby, false, MultiplayerScreen.class));
                } else {
                    //TODO : show error
                }
                dialog.hide();
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        dialog.show(uiStage);
    }

    private void showJoinByIdDialog() {
        Dialog dialog = new Dialog("Join by ID", customSkin);
        Table contentTable = dialog.getContentTable();

        TextField idField = new TextField("", customSkin);
        idField.setMessageText("Enter Lobby ID");

        TextField passField = new TextField("", customSkin);
        passField.setMessageText("Password (if its public, leave this blank)");

        TextButton confirmButton = new TextButton("Join", customSkin);
        TextButton cancelButton = new TextButton("Cancel", customSkin);

        contentTable.pad(20);
        contentTable.add(idField).growX().padBottom(20).row();
        contentTable.add(passField).growX().padBottom(20).row();

        dialog.getButtonTable().add(confirmButton).pad(10);
        dialog.getButtonTable().add(cancelButton).pad(10);

        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String lobbyId = idField.getText();
                if (!lobbyId.trim().isEmpty()) {
                    JSONMessage response = joinLobby(lobbyId, passField.getText());
                    Result result = response.getFromBody("result");
                    if(result.isSuccessful()) {
                        ClientGame.getInstance().setScreen(new LobbyScreen(response.getFromBody("lobby_info"), false, MultiplayerScreen.class));
                        dispose();
                    } else {
                        //TODO : show Error ressult.message
                    }
                    dialog.hide();
                }
            }
        });
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        dialog.show(uiStage);
    }

    private JSONMessage joinLobby (String lobbyId, String password) {
        JSONMessage message = new JSONMessage(JSONMessage.Type.lobby_command);
        message.put("command", "join");
        message.put("lobby_id", lobbyId);
        message.put("password", password);
        message.put("username", ClientApp.getUsername());

        return ClientApp.sendAndWaitForResponse(message, 5000);
    }

    public void fetchLobbies() {
        availableLobbies.clear();

        JSONMessage message = new JSONMessage(JSONMessage.Type.lobby_command);
        message.put("command", "fetch");

        JSONMessage response = ClientApp.sendAndWaitForResponse(message, 5000);
        if(response == null || response.getFromBody("lobby_infos") == null) {
            new PopUpMessage("couldn't refresh the list").show(AbstractMenuScreen.getFrontStage());
            return;
        }
        ArrayList<LobbyInfo> arrayList = response.getFromBody("lobby_infos");

        availableLobbies.addAll(arrayList);

        updateLobbyList();

    }

    private void updateLobbyList() {
        lobbyList.clearChildren();
        lobbyList.top();
        for (LobbyInfo lobby : availableLobbies) {
            Table lobbyTable = new Table();
            lobbyTable.setBackground(customSkin.getDrawable("savedGameBox"));

            Label hostName = new Label(lobby.getHostUsername(), customSkin);
            Label lobbyName = new Label(lobby.getLobbyName(), customSkin, "big");
            Label players = new Label(lobby.getCurrentPlayers() + "/" + lobby.getMaxPlayers(), customSkin);
            TransformWidgetWrapper<Button> playButton = new TransformWidgetWrapper<>(new Button(customSkin, "play"));

            hostName.setColor(ColorPalette.green);
            lobbyName.setColor(ColorPalette.red);
            players.setColor(Color.BLACK);

            lobbyTable.add(lobbyName).expandX().left();
            lobbyTable.add(hostName).expandX();
            lobbyTable.add(players).expandX();
            lobbyTable.add(playButton).expandX().right();

            playButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(lobby.isPrivate()) {
                        showJoinPrivateLobbyDialog(lobby);
                        return;
                    }

                    JSONMessage response = joinLobby(lobby.getLobbyId(), null);
                    Result result = response.getFromBody("result");
                    if(result.isSuccessful()) {
                        LobbyScreen lobbyScreen = new LobbyScreen(response.getFromBody("lobby_info"), false, MultiplayerScreen.class);
                        boolean toRight = false;
                        if(lobby.getSavedGameDetails() != null) toRight = true;
                        TransitionManager.horizontalTransition(lobbyScreen, MultiplayerScreen.this, toRight, 1.5f, Interpolation.smoother);
                    }
                    else {
                        //TODO : show error
                    }

                }
            });

            lobbyList.add(lobbyTable).growX().spaceBottom(2).row();
        }
    }
}
