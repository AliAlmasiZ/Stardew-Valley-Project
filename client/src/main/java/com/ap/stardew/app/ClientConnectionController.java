package com.ap.stardew.app;

import com.ap.stardew.models.Game;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.dto.PlayerState;
import com.ap.stardew.models.player.Player;
import com.badlogic.gdx.math.Vector2;
import com.ap.stardew.ClientGame;
import com.ap.stardew.views.LobbyScreen;

import java.util.ArrayList;

public class ClientConnectionController {
    public static JSONMessage handleCommand(JSONMessage message) {
        if(message.getType() == JSONMessage.Type.update) {
            String command = message.getFromBody("command");
            switch (command) {
                case "updateLobby" -> {
                    if (ClientGame.getInstance().getScreen() instanceof LobbyScreen lobbyScreen){
                        lobbyScreen.updateLobbyState(message.getFromBody("lobby_info"));
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
                    player.move(direction, delta);
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
            }
        }
        if (message.getType() == JSONMessage.Type.trade) {
            String command = message.getFromBody("command");
            switch (command) {
                case "request_start" -> {
                    GameController.startTradeRequest(message);
                }
                case "stop_trade" -> {
                    GameController.stopTrade(message);
                }
                case "accept_trade" -> {
                    GameController.acceptTradeRequest();
                }
                case "update_trade" -> {
                    GameController.updateTradeInventory(message);
                }
                case "confirm" -> {
                    GameController.confirmTrade();
                }
                case "reject_trade_offer" -> {
                    GameController.rejectTradeOffer();
                }
                case "finish_trade" -> {
                    GameController.finishTrade(message);
                }
            }
        }
        throw new UnsupportedOperationException(); // for messages cant handle
    }
}
