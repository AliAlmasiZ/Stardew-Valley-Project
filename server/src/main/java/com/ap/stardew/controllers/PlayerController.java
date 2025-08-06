package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnectionThread;
import com.ap.stardew.models.App;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.entities.CollisionEvent;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Placeable;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.player.Player;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerController {
    private ClientConnectionThread clientConnectionThread;
    private Vector2 direction = new Vector2();
    private Player player;

    public PlayerController(ClientConnectionThread clientThread) {
        this.clientConnectionThread = clientThread;
        this.player = clientThread.player;

    }



    public JSONMessage handleWalk(JSONMessage request) {

        if(request.getFromBody("left"))
            direction.x -= 1;
        if(request.getFromBody("right"))
            direction.x += 1;
        if(request.getFromBody("up"))
            direction.y += 1;
        if(request.getFromBody("down"))
            direction.y -= 1;


        float delta = clientConnectionThread.gameThread.getDeltaTime();
        //Todo: that walkable check i wrote is ass
        Tile destTile = App.getActiveGame().getActiveMap().
            getTileByPosition(player.getPosition().cpy().add(direction.x, direction.y));


        boolean canWalk = destTile.isWalkable();
        Entity entity = null;
        if ((entity = destTile.getContent()) != null) {
            Placeable placeable = entity.getComponent(Placeable.class);
            if (placeable.isWalkable() && destTile.isWalkable()) {
                for (CollisionEvent c : placeable.getCollisionEvents()) {
                    c.onEnter(player);
                }
            } else {
                canWalk = false;
                for (CollisionEvent c : placeable.getCollisionEvents()) {
                    c.onCollision(player);
                }
            }
        }
        if (canWalk) {
            player.move(direction, delta);
            player.setState(Player.State.WALKING);
            JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.update);
            updateMessage.put("command", "player_move");
            updateMessage.put("delta_time", delta);
            updateMessage.put("direction", direction);
            clientConnectionThread.gameThread.sendAllTCP(updateMessage);
        } else {
            player.setState(Player.State.IDLE);
        }

        return null;

    }








    public void update(float delta) {

    }

}
