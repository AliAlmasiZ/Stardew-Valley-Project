package com.ap.stardew.app;

import com.ap.stardew.models.App;
import com.ap.stardew.models.building.Door;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.dto.PlayerState;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.player.Player;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.ap.stardew.ClientGame;
import com.ap.stardew.models.Result;
import com.ap.stardew.views.LobbyScreen;
import com.ap.stardew.views.MultiplayerScreen;

import java.util.ArrayList;

public class ClientConnectionController {
    public static JSONMessage handleCommand(JSONMessage message) {
        if(message.getType() == JSONMessage.Type.update) {
            String command = message.getFromBody("command");
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
                    player.setState(Player.State.WALKING);

                    System.out.println(player.getPosition().getMap());
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
        throw new UnsupportedOperationException(); // for messages cant handle
    }
}
