package com.ap.stardew.controllers;

import com.ap.stardew.models.App;
import com.ap.stardew.models.entities.CollisionEvent;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Placeable;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.views.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Map;

public class PlayerController implements InputProcessor {
    private static final float ZOOM_SPEED = 0.1f;
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 1.5f;
    private Player player;
    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    private boolean advanceTime;
    private GameScreen screen;
    private Vector2 direction = new Vector2();
    private final Map<Integer, Boolean> keysState = new HashMap<>();

    public PlayerController(GameScreen screen, Player player) {
        this.screen = screen;
        this.player = player;
        left = false;
        right = false;
        up = false;
        down = false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.LEFT || keycode == Input.Keys.A)
            this.left = true;
        if(keycode == Input.Keys.UP || keycode == Input.Keys.W)
            this.up = true;
        if(keycode == Input.Keys.DOWN || keycode == Input.Keys.S)
            this.down = true;
        if(keycode == Input.Keys.RIGHT || keycode == Input.Keys.D)
            this.right = true;
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.LEFT || keycode == Input.Keys.A)
            this.left = false;
        if(keycode == Input.Keys.UP || keycode == Input.Keys.W)
            this.up = false;
        if(keycode == Input.Keys.DOWN || keycode == Input.Keys.S)
            this.down = false;
        if(keycode == Input.Keys.RIGHT || keycode == Input.Keys.D)
            this.right = false;
        if (keycode == Input.Keys.T)
            this.advanceTime = true;
        if (keycode == Input.Keys.TAB)
            screen.openTestDialog();
        if (keycode == Input.Keys.P) //Temporarily
            screen.startFishing();

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            Vector3 mouseScreenPos = new Vector3(screenX, screenY, 0);
            screen.getCamera().unproject(mouseScreenPos); // convert to world coordinates

            float x = mouseScreenPos.x;
            float y = mouseScreenPos.y;
            screen.getController().handleRightClick(x, y, screen);
            return true; // input was handled
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
            OrthographicCamera cam = screen.camera;
            cam.zoom += amountY * ZOOM_SPEED;
            cam.zoom = Math.max(MIN_ZOOM, cam.zoom);
            cam.zoom = Math.min(MAX_ZOOM, cam.zoom);
            return true;
        }
        return false;
    }

    public void update(float delta) {
        processInput(delta);
        player.update(delta);
    }

    private void processInput(float delta) {
        direction.setZero();

        if(left) {
            direction.x -= 1;
        }
        if(right) {
            direction.x += 1;
        }
        if(up) {
            direction.y += 1;
        }
        if(down) {
            direction.y -= 1;
        }


        //Todo: that walkable check i wrote is ass
        Tile destTile = App.getActiveGame().getActiveMap().
            getTileByPosition(player.getPosition().copy().add(direction.x, direction.y));

        if((!up && !down && !right && !left) || destTile == null) {
            player.setState(Player.State.IDLE);
        }else {
            boolean canWalk = destTile.isWalkable();
            Entity entity = null;
            if ((entity = destTile.getContent()) != null) {
                Placeable placeable = entity.getComponent(Placeable.class);
                if(placeable.isWalkable() && destTile.isWalkable()){
                    for (CollisionEvent c : placeable.getCollisionEvents()) {
                        c.onEnter(player);
                    }
                }else{
                    canWalk = false;
                    for (CollisionEvent c : placeable.getCollisionEvents()) {
                        c.onCollision(player);
                    }
                }
            }
            if(canWalk){
                player.move(direction, delta);
                player.setState(Player.State.WALKING);
            }else{
                player.setState(Player.State.IDLE);
            }
        }
    }
}
