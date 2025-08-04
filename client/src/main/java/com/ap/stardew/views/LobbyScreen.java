package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.models.LobbyInfo;
import com.ap.stardew.models.dto.AccountInfo;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;


public class LobbyScreen extends AbstractMenuScreen{

    private LobbyInfo currentLobby;
    private boolean isHost;

    private Label lobbyNameLabel;
    private List<String> playerList;
    private ScrollPane scrollPane;
    private TextButton startGameBtn, readyBtn, leaveBtn;

    public LobbyScreen(LobbyInfo lobby, boolean isHost) {
        super();
        this.currentLobby = lobby;
        this.isHost = isHost;

        setupUI();
        updatePlayerList();
    }

    private void setupUI() {
        lobbyNameLabel = new Label("Lobby: " + currentLobby.getLobbyName(), customSkin);
        rootTable.add(lobbyNameLabel).padBottom(20).colspan(2).row();

        playerList = new List<>(customSkin);
        scrollPane = new ScrollPane(playerList, customSkin);

        startGameBtn = new TextButton("Start Game", customSkin);
        readyBtn = new TextButton("Ready", customSkin);
        leaveBtn = new TextButton("Leave Lobby", customSkin);

        rootTable.add(scrollPane).colspan(2).grow().pad(10).row();
        if (isHost) {
            rootTable.add(startGameBtn).pad(10);
        } else {
            rootTable.add(readyBtn).pad(10);
        }
        rootTable.add(leaveBtn).pad(10);

        /* --- Listeners --- */
        startGameBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Send Start game request to server
                // then server should add all players to game

            }
        });
        readyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: send toggle ready request to server
                readyBtn.setText(readyBtn.isChecked() ? "Not Ready" : "Ready");
            }
        });
        leaveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO : Send Leave lobby request to server
                ClientGame.getInstance().setScreen(new MultiplayerScreen());
                dispose();
            }
        });
    }

    /** This method would be called whenever the client receives an update about the lobby state
     *
     * @param updatedLobby
     */
    public void updateLobbyState(LobbyInfo updatedLobby) {
        this.currentLobby = updatedLobby;
        updatePlayerList();
    }

    private void updatePlayerList() {
        Array<String> players = new Array<>();
        for (AccountInfo player : currentLobby.getAccounts()) {
            String status = player.getUsername().equals(currentLobby.getHostUsername()) ? "[Host]" :
                            player.isReady() ? "[Ready]" : "[Not Ready]";
            players.add(String.format("%s %s", player.getUsername(), status));
        }
        playerList.setItems(players);
    }
}
