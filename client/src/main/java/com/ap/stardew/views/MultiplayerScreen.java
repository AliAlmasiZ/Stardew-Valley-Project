package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.controllers.validators.NonEmptyValidator;
import com.ap.stardew.models.App;
import com.ap.stardew.models.JSONMessage;
import com.ap.stardew.models.LobbyInfo;
import com.ap.stardew.models.Result;
import com.ap.stardew.views.widgets.ValidatedTextField;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.jsonbeans.Json;

import java.util.ArrayList;

public class MultiplayerScreen extends AbstractMenuScreen {
    private List<String> lobbyList; //LibGdx list
    private ScrollPane scrollPane;
    private Button hostBtn, joinBtn, joinByIdBtn,  refreshBtn, backBtn;

    private ArrayList<LobbyInfo> availableLobbies = new ArrayList<>();

    public MultiplayerScreen() {
        super();
        setupUI();

        fetchLobbies();
    }

    public void setupUI() {
        rootTable.add(new Label("Available Lobbies", customSkin)).padBottom(20).colspan(4).row();

        lobbyList = new List<>(customSkin);
        scrollPane = new ScrollPane(lobbyList, customSkin);
        scrollPane.setFadeScrollBars(false);

        hostBtn = new TextButton("Host New Game", customSkin);
        joinBtn = new TextButton("Join Game", customSkin);
        joinByIdBtn = new TextButton("Join by ID", customSkin);
        refreshBtn = new TextButton("Refresh", customSkin);
        backBtn = new Button(customSkin, "back");


        rootTable.add(scrollPane).colspan(5).grow().pad(10).row();
        rootTable.add(hostBtn).pad(10);
        rootTable.add(joinBtn).pad(10);
        rootTable.add(joinByIdBtn).pad(10);
        rootTable.add(refreshBtn).pad(10);
        rootTable.add(backBtn).pad(10);



        /* --- Listeners --- */
        hostBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showCreateLobbyDialog();
            }
        });
        joinBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int selectedIndex = lobbyList.getSelectedIndex();
                if(selectedIndex != -1) {
                    LobbyInfo selectedLobby = availableLobbies.get(selectedIndex);
                    if(selectedLobby.isPrivate()) {
                        showJoinPrivateLobbyDialog(selectedLobby);
                    }

                    JSONMessage message = new JSONMessage(JSONMessage.Type.lobby_command);
                    message.put("command", "join");
                    message.put("username" , App.getLoggedInAccount().getUsername());
                    message.put("lobby_id", selectedLobby.getLobbyId());
                    JSONMessage response = ClientApp.sendAndWaitForResponse(message, 5000);
                    //TODO
                }
            }
        });
        joinByIdBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showJoinByIdDialog();
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
                parallaxBackground.move(300, 4, Interpolation.smoother);
                rootTable.addAction(Actions.sequence(
                    Actions.moveBy(0, -300, 4, Interpolation.smoother),
                    Actions.run(()->{
                        MainMenuScreen mainMenuScreen = new MainMenuScreen();
                        mainMenuScreen.enterAnim();
                        ClientGame.getInstance().setScreen(mainMenuScreen);
                    })
                ));
                dispose();
            }
        });
    }

    private void showCreateLobbyDialog() {
        Dialog dialog = new Dialog("Create Lobby", customSkin);
        Table contentTable = dialog.getContentTable();

        TextField nameField = new TextField("", customSkin);
        nameField.setMessageText("Lobby Name");
        TextField passwordField = new TextField("", customSkin);
        passwordField.setMessageText("Password (optional)");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        CheckBox visibleCheckbox = new CheckBox(" Visible to others", skin); // add to custom skin
        visibleCheckbox.setChecked(true);

        Label maxPlayersLabel = new Label("Max Players", customSkin);
        SelectBox<Integer> maxPlayersSelectBox = new SelectBox<>(customSkin);
        maxPlayersSelectBox.setItems(2,3,4);
        maxPlayersSelectBox.setSelected(2);


        TextButton confirmButton = new TextButton("Create", customSkin);
        TextButton cancelButton = new TextButton("Cancel", customSkin);

        contentTable.pad(20);
        contentTable.add(nameField).colspan(3).growX().padBottom(10).row();
        contentTable.add(passwordField).colspan(3).growX().padBottom(10).row();
        contentTable.add(maxPlayersLabel).pad(10);
        contentTable.add(maxPlayersSelectBox).pad(10);
        contentTable.add(visibleCheckbox).right().padBottom(20).row();

        dialog.getButtonTable().add(confirmButton).pad(10);
        dialog.getButtonTable().add(cancelButton).pad(10);

        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = nameField.getText();
                String password = passwordField.getText();
                boolean isVisible = visibleCheckbox.isChecked();

                if (!name.isEmpty()) {
                    JSONMessage message = new JSONMessage(JSONMessage.Type.lobby_command);
                    message.put("command", "host");
                    message.put("lobby_name", name);
                    message.put("host_username", App.getLoggedInAccount().getUsername());
                    message.put("max_players", maxPlayersSelectBox.getSelected().intValue());
                    message.put("password", password);
                    message.put("is_visible", isVisible);

                    JSONMessage response = ClientApp.sendAndWaitForResponse(message, 5000);
                    LobbyInfo lobbyInfo = response.getFromBody("lobby_info");
                    if(lobbyInfo == null)
                        return;

                    ClientGame.getInstance().setScreen(new LobbyScreen(lobbyInfo, true));

                    dialog.hide();
                    dispose();
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

                } else {
                    //TODO
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

        TextButton confirmButton = new TextButton("Join", customSkin);
        TextButton cancelButton = new TextButton("Cancel", customSkin);

        contentTable.pad(20);
        contentTable.add(idField).growX().padBottom(20).row();

        dialog.getButtonTable().add(confirmButton).pad(10);
        dialog.getButtonTable().add(cancelButton).pad(10);

        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String lobbyId = idField.getText();
                if (!lobbyId.trim().isEmpty()) {
                    // TODO: Send "Join Lobby" request to the server with this ID.
                    // The server will have to check if this lobby requires a password
                    // and could respond by asking the client to show the password dialog.
                    dialog.hide();
                }
            }
        });
    }

    private JSONMessage joinLobby (String lobbyId, String password) {
        JSONMessage message = new JSONMessage(JSONMessage.Type.lobby_command);
        message.put("command", "join");
        message.put("lobby_id", lobbyId);
        message.put("password", password);
        message.put("username", App.getLoggedInAccount().getUsername());



        return ClientApp.sendAndWaitForResponse(message, 5000);
    }

    public void fetchLobbies() {
        availableLobbies.clear();

        JSONMessage message = new JSONMessage(JSONMessage.Type.lobby_command);
        message.put("command", "fetch");

        JSONMessage response = ClientApp.sendAndWaitForResponse(message, 5000);
        if(response == null) { // TODO: show timeout error
            response = new JSONMessage(JSONMessage.Type.response);
            response.put("lobby_infos", new ArrayList<LobbyInfo>());
        }
        ArrayList<LobbyInfo> arrayList = response.getFromBody("lobby_infos");


        availableLobbies.addAll(arrayList);



        updateLobbyList();

    }

    private void updateLobbyList() {
        Array<String> lobbyNames = new Array<>();
        for (LobbyInfo lobby : availableLobbies) {
            lobbyNames.add(String.format("%s (%d/%d) - Host: %s",
                lobby.getLobbyName(),
                lobby.getCurrentPlayers(),
                lobby.getMaxPlayers(),
                lobby.getHostUsername()
            ));
        }
        lobbyList.setItems(lobbyNames);
    }
}
