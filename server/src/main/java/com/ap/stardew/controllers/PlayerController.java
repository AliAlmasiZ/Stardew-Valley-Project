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

import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerController {
    private ClientConnectionThread clientConnectionThread;
    private Vector2 direction = new Vector2();
    private Player player;
    private AtomicBoolean left;
    private AtomicBoolean right;
    private AtomicBoolean up;
    private AtomicBoolean down;

    public PlayerController(ClientConnectionThread clientThread) {
        this.clientConnectionThread = clientThread;
        this.player = clientThread.player;
        left = new AtomicBoolean();
        right = new AtomicBoolean();
        up = new AtomicBoolean();
        down = new AtomicBoolean();
    }

    public void handleInput(JSONMessage req) {
        String command = req.getFromBody("command");
        switch (command) {
            case "key_down" -> {
                int keycode = req.getFromBody("keycode");
                keyDown(keycode);
            }
            case "key_up" -> {
                int keycode = req.getFromBody("keycode");
                keyUp(keycode);
            }
        }
    }

    private boolean keyDown(int keycode) {
        if(keycode == Input.Keys.LEFT || keycode == Input.Keys.A)
            this.left.set(true);
        if(keycode == Input.Keys.UP || keycode == Input.Keys.W)
            this.up.set(true);
        if(keycode == Input.Keys.DOWN || keycode == Input.Keys.S)
            this.down.set(true);
        if(keycode == Input.Keys.RIGHT || keycode == Input.Keys.D)
            this.right.set(true);
        if((Input.Keys.NUM_1 <= keycode)  && (keycode <= Input.Keys.NUM_9)){
            player.setActiveSlot(player.getComponent(Inventory.class).getSlots().get(keycode - 8));
        }

        return true;
    }

    private boolean keyUp(int keycode) {
        if(keycode == Input.Keys.LEFT || keycode == Input.Keys.A)
            this.left.set(false);
        if(keycode == Input.Keys.UP || keycode == Input.Keys.W)
            this.up.set(false);
        if(keycode == Input.Keys.DOWN || keycode == Input.Keys.S)
            this.down.set(false);
        if(keycode == Input.Keys.RIGHT || keycode == Input.Keys.D)
            this.right.set(false);

        return true;
    }


    public void update(float delta) {
        processInput(delta);

    }

    private void processInput(float delta) {
        direction.setZero();

        if (left.get()) {
            direction.x -= 1;
        }
        if (right.get()) {
            direction.x += 1;
        }
        if (up.get()) {
            direction.y += 1;
        }
        if (down.get()) {
            direction.y -= 1;
        }

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
            updateMessage.put("update", "player_move");
            updateMessage.put("delta_time", delta);
            updateMessage.put("direction", direction);
            clientConnectionThread.gameThread.sendAllUDP(updateMessage);
        } else {
            player.setState(Player.State.IDLE);
        }
    }
}
