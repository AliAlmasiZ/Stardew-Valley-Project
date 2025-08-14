package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.models.LobbyInfo;
import com.ap.stardew.models.dto.AccountInfo;
import com.ap.stardew.models.gameMap.MapRegion;
import com.ap.stardew.models.gameMap.WorldMap;
import com.ap.stardew.utils.TiledMapUtils;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.managers.TransitionManager;
import com.ap.stardew.views.widgets.MapActor;
import com.ap.stardew.views.widgets.PopUpMessage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

import java.util.ArrayList;


public class LobbyScreen extends AbstractMenuScreen{
    private LobbyInfo currentLobby;
    private boolean isHost;

    private Label lobbyNameLabel;
    private TextButton lobbyIdButton;
    private Label copyFeedbackLabel;
    private Table playerList;
    private ScrollPane scrollPane;
    private TextButton startGameBtn, readyBtn, leaveBtn;
    private MapActor mapActor;

    private final Class<? extends AbstractMenuScreen> prevScreen;

    public LobbyScreen(LobbyInfo lobby, boolean isHost, Class<? extends AbstractMenuScreen> prevScreen) {
        super();
        this.currentLobby = lobby;
        this.isHost = isHost;
        this.prevScreen = prevScreen;

        setupUI();
        updatePlayerList();
    }

    private void setupUI() {
        Table headerTable  = new Table();
        playerList = new Table();
        lobbyNameLabel = new Label("Lobby: " + currentLobby.getLobbyName(), customSkin);
        headerTable.add(lobbyNameLabel).expandX().left();

        lobbyIdButton = new TextButton("ID: " + currentLobby.getLobbyId(), customSkin);
        copyFeedbackLabel = new Label("Copied!", customSkin);
        copyFeedbackLabel.setVisible(false);

        headerTable.add(lobbyIdButton).padLeft(20);
        headerTable.add(copyFeedbackLabel).padLeft(10);

        rootTable.add(headerTable).padBottom(20).colspan(3).row();

        scrollPane = new ScrollPane(playerList, customSkin);

        startGameBtn = new TextButton("Start Game", customSkin);
        readyBtn = new TextButton("Ready", customSkin);
        leaveBtn = new TextButton("Leave Lobby", customSkin);

        Table mainBox = new Table();
        Table leftPanel = new Table();
        Table rightPanel = new Table();

        rightPanel.top();
        leftPanel.top();

        leftPanel.add(new Table(){
            {
                setBackground(customSkin.getDrawable("scrollBackgroundNinePatch"));
                Label label = new Label("Players", customSkin, "big");
                label.setColor(0, 0, 0, 1);
                add(label);
            }
        }).spaceBottom(5).row();
        leftPanel.add(scrollPane).left().grow();

        GameAssetManager.getInstance().miniMap.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        rightPanel.add(new Table(){
            {
                setBackground(customSkin.getDrawable("scrollBackgroundNinePatch"));
                Label label = new Label("Farm Selection", customSkin, "big");
                label.setColor(0, 0, 0, 1);
                add(label);
            }
        }).spaceBottom(5).row();

        WorldMap worldMap = TiledMapUtils.getRegionData("./Content(unpacked)/Maps/untitled.tmx");
        mapActor = new MapActor(worldMap, GameAssetManager.getInstance().miniMap){
            @Override
            public void regionClicked(MapRegion mapRegion) {
                if(currentLobby.getSavedGameDetails() != null){
                    new PopUpMessage("can't change the farms").show(AbstractScreen.getFrontStage());
                    return;
                }
                JSONMessage req = new JSONMessage(JSONMessage.Type.lobby_command);

                req.put("command", "chooseMapRegion");
                req.put("token", ClientApp.getToken());
                req.put("lobby_id", currentLobby.getLobbyId());
                req.put("mapRegion", mapRegion.getName());

                JSONMessage res = ClientApp.sendAndWaitForResponse(req, 500);

                if(res == null) return;

                if(res.getFromBody("result", Result.class).message().contains("map region already selected by")){
                    PopUpMessage popUpMessage = new PopUpMessage();
                    popUpMessage.add(new Label(res.getFromBody("result", Result.class).message(), customSkin));
                    popUpMessage.show(uiStage);
                }
            }
        };
        rightPanel.add(mapActor).width(200).height(200f * GameAssetManager.getInstance().miniMap.getHeight() /
            GameAssetManager.getInstance().miniMap.getWidth());

        mainBox.add(leftPanel).width(100).grow().pad(5).padLeft(2.5f);
        mainBox.add(rightPanel).pad(5).padRight(2.5f);

        mainBox.setBackground(customSkin.getDrawable("frameNinePatch2"));

        mainBox.pack();

        rootTable.add(mainBox).colspan(3).row();
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

                JSONMessage message1 = ClientApp.sendAndWaitForResponse(message, 5000);

                if(message1 == null) return;

                System.out.println(message1.getBody());

                Result result = message1.getFromBody("result");
                if(!result.isSuccessful()){
                    PopUpMessage popUpMessage = new PopUpMessage();
                    popUpMessage.add(new Label(result.message(), customSkin));
                    popUpMessage.show(uiStage);
                }
            }
        });
        readyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                JSONMessage req = new JSONMessage(JSONMessage.Type.lobby_command);
                req.put("command", "toggleReady");
                req.put("lobby_id", currentLobby.getLobbyId());
                req.put("token", ClientApp.getToken());

                JSONMessage message = ClientApp.sendAndWaitForResponse(req, 500);

                if(message == null || !message.getFromBody("result", Result.class).isSuccessful()) return;

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

                if(prevScreen == MultiplayerScreen.class){
                    TransitionManager.horizontalTransition(new MultiplayerScreen(), LobbyScreen.this, true, 1.5f, Interpolation.smoother);
                }else if(prevScreen == NewGameScreen.class){
                    TransitionManager.verticalTransition(new NewGameScreen(), LobbyScreen.this, -500, 4f, Interpolation.smoother);
                }else if(prevScreen == LoadGameScreen.class){
                    JSONMessage request = new JSONMessage(JSONMessage.Type.command);
                    request.put("command", "getSavedGames");
                    request.put("token", ClientApp.getToken());

                    JSONMessage respond = ClientApp.sendAndWaitForResponse(request, 2000);

                    if(respond == null || respond.getFromBody("result") == null || respond.getFromBody("games") == null){
                        new PopUpMessage("failed to retrieve saved games").show(AbstractMenuScreen.getFrontStage());
                        TransitionManager.verticalTransition(new MainMenuScreen(), LobbyScreen.this, -500, 4f, Interpolation.smoother);
                    }
                    if(!respond.getFromBody("result", Result.class).isSuccessful()){
                        new PopUpMessage(respond.getFromBody("result", Result.class).message()).show(AbstractMenuScreen.getFrontStage());
                        TransitionManager.verticalTransition(new MainMenuScreen(), LobbyScreen.this, -500, 4f, Interpolation.smoother);
                    }
                    if(respond.getFromBody("games", ArrayList.class).isEmpty()){
                        new PopUpMessage("you have no saved games").show(AbstractMenuScreen.getFrontStage());
                        TransitionManager.verticalTransition(new MainMenuScreen(), LobbyScreen.this, -500, 4f, Interpolation.smoother);
                    }
                    TransitionManager.verticalTransition(new LoadGameScreen(respond.getFromBody("games")), LobbyScreen.this, -500, 4f, Interpolation.smoother);
                }
            }
        });
    }

    /** This method would be called whenever the client receives an update about the lobby state
     *
     * @param updatedLobby
     * @param message
     */
    public void updateLobbyState(LobbyInfo updatedLobby, String message) {
        this.currentLobby = updatedLobby;
        lobbyNameLabel.setText("Lobby: " + currentLobby.getLobbyName());
        lobbyIdButton.setText("ID: " + currentLobby.getLobbyId());
        updatePlayerList();

        if(message != null){
            PopUpMessage popUpMessage = new PopUpMessage();
            popUpMessage.add(new Label(message, customSkin));
            popUpMessage.show(uiStage);
        }
    }

    private void updatePlayerList() {
        playerList.clearChildren();
        playerList.top();
        playerList.pad(3);

        mapActor.updateOwners(currentLobby.getAccounts());

        for (AccountInfo player : currentLobby.getAccounts()) {
            Table table = new Table();

            Label user = new Label(player.getUsername(), customSkin);
            user.setColor(0, 0, 0, 1);
            table.add(user).left().growX();

            Label status = new Label("", customSkin);
            if(player.getUsername().equals(currentLobby.getHostUsername())){
                status.setText("Host");
                status.setColor(Color.YELLOW);
            }else if (player.isReady()){
                status.setText("ready");
                status.setColor(ColorPalette.green);
            }else {
                status.setText("not ready");
                status.setColor(ColorPalette.red);
            }
            table.add(status).right();

            playerList.add(table).growX().row();
        }
        if(currentLobby.getSavedGameDetails()!= null){
            outerLoop:
            for (String player : currentLobby.getSavedGameDetails().players){
                for (AccountInfo account : currentLobby.getAccounts()) {
                    if(account.getUsername().equals(player)){
                        continue outerLoop;
                    }
                }
                Table table = new Table();

                Label user = new Label(player, customSkin);
                user.setColor(0, 0, 0, 1);
                table.add(user).left().growX();

                Label status = new Label("not joined", customSkin);
                status.setColor(ColorPalette.red);
                table.add(status).right();

                playerList.add(table).growX().row();
            }
        }
    }

    public LobbyInfo getCurrentLobby() {
        return currentLobby;
    }
}
