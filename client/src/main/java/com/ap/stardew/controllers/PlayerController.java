package com.ap.stardew.controllers;

import com.ap.stardew.models.App;

import com.ap.stardew.models.Position;
import com.ap.stardew.models.entities.CollisionEvent;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.Renderable;
import com.ap.stardew.models.entities.UseFunction;
import com.ap.stardew.models.entities.components.Placeable;
import com.ap.stardew.models.entities.components.Useable;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.components.inventory.InventorySlot;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.views.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PlayerController implements InputProcessor {
    private static final float ZOOM_SPEED = 0.1f;
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 1.5f;

    private final GameMenuController gameMenuController;

    private Player player;
    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    private boolean advanceTime;
    private GameScreen screen;
    private Vector2 direction = new Vector2();

    private Position cursorPos = new Position(0, 0);

    public enum EquippedItemState{
        USEABLE,
        USEABLE_INVALID,
        PLACEABLE,
        PLACEABLE_INVALID,
        NONE
    }

    private EquippedItemState equippedItemState = EquippedItemState.NONE;

    public PlayerController(GameScreen screen, Player player, GameMenuController gameMenuController) {
        this.screen = screen;
        this.player = player;
        this.gameMenuController = gameMenuController;
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
        if((Input.Keys.NUM_1 <= keycode)  && (keycode <= Input.Keys.NUM_9)){
           player.setActiveSlot(player.getComponent(Inventory.class).getSlots().get(keycode - 8));
        }
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
            screen.openJournal();
        if (keycode == Input.Keys.P) //TODO: Temporarily
            screen.startFishing();
        if (keycode == Input.Keys.F1) // TODO: add this to other menu
            screen.showCraftInfoDialog();

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 mouseScreenPos = new Vector3(screenX, screenY, 0);
        screen.getCamera().unproject(mouseScreenPos); // convert to world coordinates

        cursorPos.x = mouseScreenPos.x;
        cursorPos.y = mouseScreenPos.y;

        if (button == Input.Buttons.RIGHT) {
            screen.getController().handleRightClick(cursorPos.x, cursorPos.y, screen);
            return true; // input was handled
        }
        if(button == Input.Buttons.LEFT){
            InventorySlot activeSlot = App.getActiveGame().getCurrentPlayer().getActiveSlot();
            if(activeSlot == null) return false;
            Entity entity = activeSlot.getEntity();
            if(entity == null) return false;
            Tile tile = App.getActiveGame().getActiveMap().getTileByPosition(cursorPos);

            Useable useable = entity.getComponent(Useable.class);
            if(useable != null && (player.getPosition().cpy().convertToInt().sub(cursorPos.cpy().convertToInt()).len() < 1.6f)){

                for (UseFunction function : useable.getFunctions()) {
                    function.use(entity, tile);
                }
            }

            Placeable placeable = entity.getComponent(Placeable.class);
            if(placeable != null){
                activeSlot.setEntity(null);
                EntityPlacementSystem.placeOnTile(entity, tile);
                equippedItemState = EquippedItemState.NONE;
            }
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
        Vector3 mouseScreenPos = new Vector3(screenX, screenY, 0);
        screen.getCamera().unproject(mouseScreenPos); // convert to world coordinates

        cursorPos.x = mouseScreenPos.x;
        cursorPos.y = mouseScreenPos.y;

        equippedItemState = EquippedItemState.NONE;



        InventorySlot activeSlot = App.getActiveGame().getCurrentPlayer().getActiveSlot();
        if(activeSlot == null) return false;
        Entity entity = activeSlot.getEntity();
        if(entity == null) return false;
        Useable useable = entity.getComponent(Useable.class);

        Tile tile = App.getActiveGame().getActiveMap().getTileByPosition(cursorPos);

        if(tile == null) return false;

        if(useable != null){
            if(player.getPosition().cpy().convertToInt().sub(cursorPos.cpy().convertToInt()).len() < 1.6f){
                equippedItemState = EquippedItemState.USEABLE;
            }
        }
        Placeable placeable = entity.getComponent(Placeable.class);
        if(placeable != null){
            System.out.println(tile.isWalkable());
            if(!EntityPlacementSystem.canPlace(tile)){
                equippedItemState = EquippedItemState.PLACEABLE_INVALID;
            }else{
                equippedItemState = EquippedItemState.PLACEABLE;
            }
        }

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
            getTileByPosition(player.getPosition().cpy().add(direction.x, direction.y));

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

    public Position getCursorPos() {
        return cursorPos;
    }

    public EquippedItemState getEquippedItemState() {
        return equippedItemState;
    }
}
