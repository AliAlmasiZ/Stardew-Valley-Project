package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnectionThread;
import com.ap.stardew.models.building.Door;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.player.Message;
import com.ap.stardew.models.player.Player;
import com.badlogic.gdx.math.Vector2;

public class PlayerController {
    private ClientConnectionThread clientConnectionThread;
    private Vector2 direction = new Vector2();
    private Player player;

    public PlayerController(ClientConnectionThread clientThread) {
        this.clientConnectionThread = clientThread;
        player = clientThread.player;
    }

    public JSONMessage handleWalk(JSONMessage request) {
        direction = request.getFromBody("direction");
        float delta = request.getFromBody("delta");

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

        JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.update);
        updateMessage.put("command", "players_update");
        updateMessage.put("username", player.getUsername());
        updateMessage.put("delta_time", delta);
        updateMessage.put("direction", direction);
        clientConnectionThread.gameThread.sendAllTCP(updateMessage);

        return null;
    }

    public void handleChangeAction(JSONMessage message){
        Player.Action action = message.getFromBody("action");

        player.setAction(action);

        JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.update);
        updateMessage.put("command", "update_player_action");
        updateMessage.put("username", player.getUsername());
        updateMessage.put("action", action);
        clientConnectionThread.gameThread.sendAllTCP(updateMessage);
    }






    public void update(float delta) {

    }

    public void updatePlayer() {
        this.player = clientConnectionThread.player;
    }

}
