package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.models.LobbyInfo;
import com.ap.stardew.models.dto.AccountInfo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;


public class LobbyScreen extends AbstractMenuScreen{
    private LobbyInfo currentLobby;
    private boolean isHost;

    private Label lobbyNameLabel;
    private TextButton lobbyIdButton;
    private Label copyFeedbackLabel;
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
        Table headerTable  = new Table();
        lobbyNameLabel = new Label("Lobby: " + currentLobby.getLobbyName(), customSkin);
        headerTable.add(lobbyNameLabel).expandX().left();

        lobbyIdButton = new TextButton("ID: " + currentLobby.getLobbyId(), customSkin);
        copyFeedbackLabel = new Label("Copied!", customSkin);
        copyFeedbackLabel.setVisible(false);

        headerTable.add(lobbyIdButton).padLeft(20);
        headerTable.add(copyFeedbackLabel).padLeft(10);

        rootTable.add(headerTable).padBottom(20).colspan(3).row();


        playerList = new List<>(customSkin);
        scrollPane = new ScrollPane(playerList, customSkin);

        startGameBtn = new TextButton("Start Game", customSkin);
        readyBtn = new TextButton("Ready", customSkin);
        leaveBtn = new TextButton("Leave Lobby", customSkin);

        rootTable.add(scrollPane).colspan(3).grow().pad(10).row();
        if (isHost) {
            rootTable.add(startGameBtn).pad(10);
        } else {
            rootTable.add(readyBtn).pad(10);
        }
        rootTable.add(leaveBtn).pad(10);

        /* --- Listeners --- */
        lobbyIdButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.getClipboard().setContents(currentLobby.getLobbyId());
                copyFeedbackLabel.setVisible(true);
                copyFeedbackLabel.getColor().a = 1f;
                copyFeedbackLabel.addAction(Actions.sequence(
                    Actions.delay(1.0f),
                    Actions.fadeOut(0.5f),
                    Actions.visible(false)
                ));
            }
        });
        startGameBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                JSONMessage message = new JSONMessage(JSONMessage.Type.lobby_command);
                message.put("command", "startGame");
                message.put("lobby_id", currentLobby.getLobbyId());
                message.put("token", ClientApp.getToken());

                JSONMessage message1 = ClientApp.sendAndWaitForResponse(message, 500);

                if(message1 == null) return;

                System.out.println(message1.getBody());

                Result result = message1.getFromBody("result");
                if(!result.isSuccessful()){
                    System.out.println(result.message());
                }
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
                JSONMessage req = new JSONMessage(JSONMessage.Type.lobby_command);
                req.put("command", "leave_lobby");
                req.put("lobby_id", currentLobby.getLobbyId());
                req.put("token", ClientApp.getToken());

                System.out.println(ClientApp.sendAndWaitForResponse(req, 500));
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
        lobbyNameLabel.setText("Lobby: " + currentLobby.getLobbyName());
        lobbyIdButton.setText("ID: " + currentLobby.getLobbyId());
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

    public LobbyInfo getCurrentLobby() {
        return currentLobby;
    }
}
