package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnectionThread;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.player.Player;
import com.badlogic.gdx.math.Vector2;

public class PlayerController {
    private ClientConnectionThread clientConnectionThread;
    private Vector2 direction = new Vector2();
    private Player player;

    public PlayerController(ClientConnectionThread clientThread) {
        this.clientConnectionThread = clientThread;
    }



    public JSONMessage handleWalk(JSONMessage request) {





        direction = request.getFromBody("direction");
        float delta = request.getFromBody("delta");
        player.move(direction, delta);
        player.setState(Player.State.WALKING);
        JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.update);
        updateMessage.put("command", "players_update");
        updateMessage.put("username", player.getUsername());
        updateMessage.put("delta_time", delta);
        updateMessage.put("direction", direction);
        System.out.println("player " + player.getUsername() + " moved to " + player.getPosition().x + ", " + player.getPosition().y);
        clientConnectionThread.gameThread.sendAllTCP(updateMessage);

        return null;

    }


    public void update(float delta) {

    }

    public void updatePlayer() {
        this.player = clientConnectionThread.player;
    }

    // Trade

}
