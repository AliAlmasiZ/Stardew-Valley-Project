package com.ap.stardew.app;

import com.ap.stardew.models.Result;
import com.ap.stardew.models.building.Door;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.dto.PlayerState;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.*;
import com.ap.stardew.views.widgets.PopUpMessage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ap.stardew.ClientGame;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

public class ClientConnectionController {
    public static JSONMessage handleCommand(JSONMessage message) {
        if(message.getType() == JSONMessage.Type.update) {
            String command = message.getFromBody("command");
//            System.out.println(command);
            if(command == null) return null;
            switch (command) {
                case "updateLobby" -> {
                    if (ClientGame.getInstance().getScreen() instanceof LobbyScreen lobbyScreen){
                        lobbyScreen.updateLobbyState(message.getFromBody("lobby_info"), message.getFromBody("message"));
                    }
                    return null;
                }
                case "startGame" ->{
                    GameController.startGame(message);
                    return null;
                }
                case "players_update" -> { //players movement update
                    String username = message.getFromBody("username");
                    float delta = message.getFromBody("delta_time");
                    Vector2 direction = message.getFromBody("direction");

                    if(ClientApp.getUsername().equals(username)) {
                        return null;
                    }
                    Player player = ClientApp.getActiveGame().getPlayerByUsername(username);
                    Tile destTile = player.getCurrentMap().
                        getTileByPosition(player.getPosition().cpy().add(direction.x, direction.y));
                    Entity entity;
                    if ((entity = destTile.getContent()) != null) {
                        if(entity instanceof Door door){
                            EntityPlacementSystem.placeOnMap(player, door.getDestination(), door.getDestination().getMap());
                        }
                    }

                    player.move(direction, delta);
                    player.setAction(Player.Action.WALKING);

                    return null;
                }
                case "update_player_action" -> {
                    String username = message.getFromBody("username");
                    Player.Action action = message.getFromBody("action");

                    if(ClientApp.getUsername().equals(username)) {
                        return null;
                    }
                    Player player = ClientApp.getActiveGame().getPlayerByUsername(username);
                    player.setAction(action);

                    return null;
                }
                case "update_players" -> {
                    ArrayList<PlayerState> playerStates = message.getFromBody("player_states");
                    for (PlayerState playerState : playerStates) {
                        Player player = ClientApp.getActiveGame().getPlayerByUsername(playerState.username);
                        player.loadFromState(playerState);
                    }
                    return null;
                }
                case "player_disconnected" -> {
                    GameController.handleGamePauseForDisconnection(message);
                    return null;
                }
                case "resume_game" -> {
                    if(ClientGame.getInstance().getScreen() instanceof DisconnectionScreen disconnectionScreen){
                        Gdx.app.postRunnable(()->{
                            disconnectionScreen.resumeGame();
                        });
                    }
                    return null;
                }
                case "gameSaved" -> {
                    if(ClientApp.getActiveGame() != null){
                        Gdx.app.postRunnable(()->{
                            if(message.getFromBody("message") != null){
                                new PopUpMessage(message.getFromBody("message")).show(AbstractScreen.getFrontStage());
                            }else {
                                new PopUpMessage("Player reconnection timed out. The game was saved.").show(AbstractScreen.getFrontStage());
                            }
                            GameController.endGame();
                            ClientGame.getInstance().getScreen().dispose();
                            ClientGame.getInstance().setScreen(new MultiplayerScreen());
                        });
                    }
                    return null;
                }
                case "update_player_gift" -> {
                    GameController.receiveGiftUpdate(message);
                    return null;
                }
                case "update_rate_gift" -> {
                    GameController.receiveGiftRateUpdate(message);
                    return null;
                }
                case "update_hug" -> {
                    GameController.hugUpdate(message);
                }
                case "update_flower" -> {
                    GameController.flowerUpdate(message);
                }
                case "update_ask_marriage" -> {
                    GameController.askMarriageUpdate(message);
                }
                case "update_accept_marriage" -> {
                    GameController.acceptMarriageUpdate(message);
                }
                case "update_reject_marriage" -> {
                    GameController.rejectMarriageUpdate(message);
                }
                case "update_players_fields" -> {
                    GameController.updatePlayer(message.getFromBody("username"), message);
                }
                case "update_friendships" -> {
                    GameController.updateFriendships(message);
                }
            }
        }
        if (message.getType() == JSONMessage.Type.trade) {
            String command = message.getFromBody("command");
            switch (command) {
                case "request_start" -> {
                    GameController.startTradeRequest(message);
                    return null;
                }
                case "stop_trade" -> {
                    GameController.stopTrade(message);
                    return null;
                }
                case "accept_trade" -> {
                    GameController.acceptTradeRequest();
                    return null;
                }
                case "update_trade" -> {
                    GameController.updateTradeInventory(message);
                    return null;
                }
                case "confirm" -> {
                    GameController.confirmTrade();
                    return null;
                }
                case "reject_trade_offer" -> {
                    GameController.rejectTradeOffer();
                    return null;
                }
                case "finish_trade" -> {
                    GameController.finishTrade(message);
                    return null;
                }
            }
        }
        if(message.getType() == JSONMessage.Type.command){
            String command = message.getFromBody("command");
            switch (command){
                case "game_reconnect_request" -> {
                    GameController.handleGameReconnectionRequest(message);
                    return null;
                }
            }
        }
        if (message.getType() == JSONMessage.Type.chat) {
            String command = message.getFromBody("command");
            switch (command) {
                case "send_private_message" -> {
                    GameController.receivePrivateMessage(message);
                    return null;
                }
                case "send_public_message" -> {
                    GameController.receivePublicMessage(message);
                    return null;
                }
            }
        }
        throw new UnsupportedOperationException(); // for messages cant handle
    }
}
