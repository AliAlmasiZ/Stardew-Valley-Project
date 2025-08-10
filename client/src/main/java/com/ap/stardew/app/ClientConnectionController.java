package com.ap.stardew.app;

import com.ap.stardew.models.building.Door;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.dto.PlayerState;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.player.Player;
import com.badlogic.gdx.math.Vector2;
import com.ap.stardew.ClientGame;
import com.ap.stardew.views.LobbyScreen;

import java.util.ArrayList;

public class ClientConnectionController {
    public static JSONMessage handleCommand(JSONMessage message) {
        if(message.getType() == JSONMessage.Type.update) {
            String command = message.getFromBody("command");
//            System.out.println(command);
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
                case "gift_player" -> {
                    GameController.receiveGift(message);
                }
                case "rate_gift" -> {
                    GameController.receiveGiftRate(message);
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
        if (message.getType() == JSONMessage.Type.chat) {
            String command = message.getFromBody("command");
            switch (command) {
                case "send_private_message" -> {
                    GameController.receivePrivateMessage(message);
                }
                case "send_public_message" -> {
                    GameController.receivePublicMessage(message);
                }
            }
        }
        throw new UnsupportedOperationException(); // for messages cant handle
    }
}
