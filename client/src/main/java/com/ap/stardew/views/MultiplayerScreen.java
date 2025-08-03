package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.controllers.validators.NonEmptyValidator;
import com.ap.stardew.models.App;
import com.ap.stardew.models.JSONMessage;
import com.ap.stardew.models.Lobby;
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
    private Button hostBtn, joinBtn, refreshBtn, backBtn;
    /* dialog for hosting */
    private Dialog dialog;
    private SelectBox<Integer> maxPlayersSelectBox;
    private ValidatedTextField lobbyNameTextField;
    private TextButton createBtn, closeBtn;

    private ArrayList<Lobby> availableLobbies = new ArrayList<>();

    public MultiplayerScreen() {
        super();
        setupUI();

        fetchLobbies();
    }

    public void setupUI() {
        rootTable.add(new Label("Available Lobbies", customSkin)).padBottom(20).colspan(4).row();

        dialog = new Dialog("Host Game", customSkin);
        lobbyList = new List<>(customSkin);
        scrollPane = new ScrollPane(lobbyList, customSkin);
        scrollPane.setFadeScrollBars(false);

        hostBtn = new TextButton("Host New Game", customSkin);
        joinBtn = new TextButton("Join Game", customSkin);
        refreshBtn = new TextButton("Refresh", customSkin);
        backBtn = new Button(customSkin, "back");


        rootTable.add(scrollPane).colspan(4).grow().pad(10).row();
        rootTable.add(hostBtn).pad(10);
        rootTable.add(joinBtn).pad(10);
        rootTable.add(refreshBtn).pad(10);
        rootTable.add(backBtn).pad(10);


        /* --- setup dialog --- */
        Table dialogTable = dialog.getContentTable();
        lobbyNameTextField = new ValidatedTextField(customSkin, new NonEmptyValidator());
        lobbyNameTextField.setMessageText("Lobby Name");

        createBtn = new TextButton("Create Game", customSkin);
        closeBtn = new TextButton("Close", customSkin);

        maxPlayersSelectBox = new SelectBox<>(customSkin);
        maxPlayersSelectBox.setItems(2, 3, 4);
        maxPlayersSelectBox.setSelected(2);
        dialogTable.add(lobbyNameTextField).colspan(2);
        dialogTable.row();
        dialogTable.add(new Label("Max Players : ", customSkin));
        dialogTable.add(maxPlayersSelectBox);
        dialogTable.row();
        dialogTable.add(createBtn);
        dialogTable.add(closeBtn);


        /* --- Listeners --- */
        hostBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.show(uiStage);
            }
        });
        joinBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int selectedIndex = lobbyList.getSelectedIndex();
                if(selectedIndex != -1) {
                    Lobby selectedLobby = availableLobbies.get(selectedIndex);
                    JSONMessage message = new JSONMessage(JSONMessage.Type.lobby_command);
                    message.put("command", "join");
                    message.put("username" , App.getLoggedInAccount().getUsername());
                    message.put("lobby_id", selectedLobby.getID());
                    JSONMessage response = (JSONMessage) ClientApp.sendAndWaitForResponse(message, 5000);
                    //TODO
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

        createBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                JSONMessage message = new JSONMessage(JSONMessage.Type.lobby_command);
                message.put("command", "host");
                message.put("lobby_name", lobbyNameTextField.getText());
                message.put("host_username", App.getLoggedInAccount().getUsername());
                message.put("max_players", maxPlayersSelectBox.getSelected());
                JSONMessage response = (JSONMessage) ClientApp.sendAndWaitForResponse(message, 5000);
                //TODO
            }
        });
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });




    }

    public void fetchLobbies() {
        availableLobbies.clear();

        JSONMessage message = new JSONMessage(JSONMessage.Type.lobby_command);
        message.put("command", "fetch");
        ArrayList<Lobby> arrayList = (ArrayList<Lobby>) ClientApp.sendAndWaitForResponse(message, 5000);


        availableLobbies.addAll(arrayList);



        updateLobbyList();

    }

    private void updateLobbyList() {
        Array<String> lobbyNames = new Array<>();
        for (Lobby lobby : availableLobbies) {
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
