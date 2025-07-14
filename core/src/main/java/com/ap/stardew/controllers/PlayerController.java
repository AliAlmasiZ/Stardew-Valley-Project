package com.ap.stardew.controllers;

import com.ap.stardew.models.player.Player;
import com.ap.stardew.views.GameScreen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;

public class PlayerController implements InputProcessor {
    private Player player;
    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    private GameScreen screen;


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
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
        return false;
    }

    public void update(float delta) {
        if(left) {
            player.move(Vector2.X.scl(-1), delta);
        }
        if(right) {
            player.move(Vector2.X, delta);
        }
        if(up) {
            player.move(Vector2.Y, delta);
        }
        if(down) {
            player.move(Vector2.Y.scl(-1), delta);
        }

        if(!up && !down && !right && !left) {
            player.setState(Player.State.IDLE);
        } else {
            player.setState(Player.State.WALKING);
        }

    }

}
