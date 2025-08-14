package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientApp;

import com.ap.stardew.app.GameController;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.building.Door;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.Renderable;
import com.ap.stardew.models.entities.UseFunction;
import com.ap.stardew.models.entities.components.*;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.components.inventory.InventorySlot;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.enums.Direction;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.views.GameScreen;
import com.ap.stardew.models.entities.RenderFunction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
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

    private Entity hoveredEntity = null;

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
            this.advanceTime = false;
        if (keycode == Input.Keys.TAB)
            screen.openJournal();
        if (keycode == Input.Keys.P) //TODO: TemporarilyBoo
            screen.startFishing();
        if (keycode == Input.Keys.B)
            screen.openCraftingMenu();
        if (keycode == Input.Keys.C)
            screen.openCookingMenu();
        if (keycode == Input.Keys.F1)
            screen.openCheatMenu();
        if (keycode == Input.Keys.F2)
            screen.scoreBoardDialog.show();
        if (keycode == Input.Keys.F3) {
            JSONMessage message = new JSONMessage(JSONMessage.Type.request);
            message.put("command", "initial_add_animal");
            System.out.println("sent from client initial_add_animal"); //TODO
            ClientApp.sendTCP(message);
        }
        if (keycode == Input.Keys.Q)
            screen.openReactionMenu(player);


        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        //TODO : make these request to server to handle logic on map
        Vector3 mouseScreenPos = new Vector3(screenX, screenY, 0);
        screen.getCamera().unproject(mouseScreenPos); // convert to world coordinates

        cursorPos.x = mouseScreenPos.x;
        cursorPos.y = mouseScreenPos.y;

        if (button == Input.Buttons.RIGHT) {
            screen.getController().handleRightClick(cursorPos.x, cursorPos.y, screen);
            return true; // input was handled
        }
        if(button == Input.Buttons.LEFT){
            Entity hoveredEntity1 = getHoveredEntity(cursorPos);
            if(hoveredEntity1 != null){
                if(hoveredEntity1.getComponent(Pickable.class) != null){
                    gameMenuController.pickupItem(hoveredEntity1);
                    player.setAction(Player.Action.HARVESTING);
                    player.setActionItem(hoveredEntity1);
                    player.setLastDir(cursorPos.cpy().sub(player.getPosition().cpy()));
                    GameController.sendActionUpdate(player);
                    return true;
                }
            }

            InventorySlot activeSlot = ClientApp.getActiveGame().getCurrentPlayer().getActiveSlot();
            if(activeSlot == null) return false;
            Entity entity = activeSlot.getEntity();
            if(entity == null) return false;
            Tile tile = ClientApp.getActiveGame().getActiveMap().getTileByPosition(cursorPos);

            Useable useable = entity.getComponent(Useable.class);
            if(useable != null && (player.getPosition().cpy().convertToInt().sub(cursorPos.cpy().convertToInt()).len() < 5f)){

                for (UseFunction function : useable.getFunctions()) {
                    function.use(player, entity, ClientApp.getActiveGame(), tile, null);
                }

                if(entity.getEntityName().equals("Watering can")){
                    player.setAction(Player.Action.WATERING);
                }else if(entity.getEntityName().equals("Scythe")){
                    player.setAction(Player.Action.USING_SCYTHE);
                }else {
                    player.setAction(Player.Action.USING_TOOL);
                }
                player.setActionItem(entity);
                player.setLastDir(cursorPos.cpy().sub(player.getPosition().cpy()));
                GameController.sendActionUpdate(player);

            }

            Placeable placeable = entity.getComponent(Placeable.class);
            if(placeable != null){
                activeSlot.setEntity(null);
                EntityPlacementSystem.placeOnTile(entity, tile);
                equippedItemState = EquippedItemState.NONE;
            }

            Edible edible = entity.getComponent(Edible.class);
            if(edible != null){
                edible.setBuff(player, ClientApp.getActiveGame());
                player.reduceEnergy(-edible.getEnergy());
                entity.getComponent(Pickable.class).changeStackSize(-1);
                player.setAction(Player.Action.HARVESTING);
                player.setActionItem(entity);
                GameController.sendActionUpdate(player);
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

        Entity hoveredEntity1 = getHoveredEntity(cursorPos);

        if(hoveredEntity != null){
            Renderable renderable1 = hoveredEntity.getComponent(Renderable.class);
            if(renderable1 != null) renderable1.getState().remove("hovered");
        }
        if(hoveredEntity1 != null){
            Renderable renderable = hoveredEntity1.getComponent(Renderable.class);
            if(renderable != null){
                renderable.getState().put("hovered", true);
                renderable.getState().put("cursorPos", cursorPos);
            }
        }
        hoveredEntity = hoveredEntity1;
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

//        player.update(delta);

        equippedItemState = EquippedItemState.NONE;

//        if(hoveredEntity != null){
//            Renderable renderable1 = hoveredEntity.getComponent(Renderable.class);
//            if(renderable1 != null) renderable1.getState().remove("hovered");
//        }

        InventorySlot activeSlot = ClientApp.getActiveGame().getCurrentPlayer().getActiveSlot();
        if(activeSlot == null) return;
        Entity activeItem = activeSlot.getEntity();
        if(activeItem == null) return;
        Useable useable = activeItem.getComponent(Useable.class);

        Tile tile = ClientApp.getActiveGame().getActiveMap().getTileByPosition(cursorPos);

        if(tile == null) return;

        if(useable != null){
            if(player.getPosition().cpy().convertToInt().sub(cursorPos.cpy().convertToInt()).len() < 1.6f){
                equippedItemState = EquippedItemState.USEABLE;
            }
        }
        Placeable placeable = activeItem.getComponent(Placeable.class);
        if(placeable != null){
            System.out.println(tile.isWalkable());
            if(!EntityPlacementSystem.canPlace(tile)){
                equippedItemState = EquippedItemState.PLACEABLE_INVALID;
            }else{
                equippedItemState = EquippedItemState.PLACEABLE;
            }
        }
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


        if((!up && !down && !right && !left)) {
            if(player.getAction() == Player.Action.WALKING){
                player.setAction(Player.Action.IDLE);
                GameController.sendActionUpdate(player);
            }
        }else {
            MovementHandler.MoveResult moveResult = MovementHandler.tryMove(player, direction);
            direction.set(moveResult.adjustedDirection);

            if(moveResult.canMove){
                JSONMessage message = new JSONMessage(JSONMessage.Type.player_input_command);
                message.put("command", "player_move");
                message.put("direction", direction);
                message.put("delta", delta);
                ClientApp.sendTCP(message);

                player.move(direction, delta);
                player.setAction(Player.Action.WALKING);
            }else{
                if(player.getAction() == Player.Action.WALKING){
                    player.setAction(Player.Action.IDLE);
                    GameController.sendActionUpdate(player);
                }
            }
        }
    }

    private Entity getHoveredEntity(Vector2 cursor){
        for (Entity entity : ClientApp.getActiveGame().getActiveMap().getEntities()){
            Renderable renderable = entity.getComponent(Renderable.class);
            Position position = entity.getComponent(PositionComponent.class).get();

            if(renderable != null){
                RenderFunction renderFunction = renderable.getRenderFunction();
                if(renderFunction != null){
                    Texture texture = renderFunction.getCurrentTexture(entity);

                    if(new Rectangle(position.x, position.y, texture.getWidth(), texture.getHeight())
                        .contains(cursor.x, cursor.y)){
                        return entity;
                    }
                }
            }
        }
        return null;
    }

    public Position getCursorPos() {
        return cursorPos;
    }

    public EquippedItemState getEquippedItemState() {
        return equippedItemState;
    }
}
