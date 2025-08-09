package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnectionThread;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.building.Door;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Pickable;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.player.Gift;
import com.ap.stardew.models.player.Message;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.TradeHistoryItem;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;

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

    //Maybe this should be at GameController
    public void doTrade(JSONMessage message) {
        Player receiver = clientConnectionThread.player;
        String senderName = message.getFromBody("receiver");
        Player sender = clientConnectionThread.gameThread.getClientByUsername(senderName).player;
        Game game = clientConnectionThread.gameThread.getGame();

        Inventory receiverInventory = message.getFromBody("receiverInventory");
        Inventory senderInventory = message.getFromBody("senderInventory");

        System.out.println(receiverInventory);
        System.out.println(senderInventory);

        sender.getComponent(Inventory.class).tradeEntities(senderInventory, receiverInventory);

        System.out.println(receiverInventory);
        System.out.println(senderInventory);

        receiver.getComponent(Inventory.class).tradeEntities(receiverInventory, senderInventory);

        System.out.println(receiverInventory);
        System.out.println(senderInventory);

        TradeHistoryItem tradeHistoryItem = new TradeHistoryItem(sender,
            receiver, senderInventory, receiverInventory, game.getDate(), game.getTradeId(), true);

        sender.addTradeHistory(tradeHistoryItem);
        receiver.addTradeHistory(tradeHistoryItem);

        updatePlayerAfterTrade(sender, tradeHistoryItem);
        updatePlayerAfterTrade(receiver, tradeHistoryItem);
    }

    public void updatePlayer(Player player, JSONMessage.Type type, String command) {
        JSONMessage updateMessage = new JSONMessage(type);
        updateMessage.put("command", command);
        updateMessage.put("username", player.getUsername());
        updateMessage.put("player", player);

        clientConnectionThread.gameThread.sendTCP(updateMessage, player.getUsername());
    }

    public void updatePlayer(Player player) {
        updatePlayer(player, JSONMessage.Type.update, "update_player");
    }

    private void updatePlayerAfterTrade(Player player, TradeHistoryItem tradeHistoryItem) {
        JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.trade);
        updateMessage.put("command", "finish_trade");
        updateMessage.put("inventory", player.getComponent(Inventory.class));
        updateMessage.put("trade_history", tradeHistoryItem);

        clientConnectionThread.gameThread.sendTCP(updateMessage, player.getUsername());
    }

    /***************************************** Chat **********************************************/
    public void sendPrivateMessage(JSONMessage jsonMessage) {
        String receiverName = jsonMessage.getFromBody("receiver");
        clientConnectionThread.gameThread.getClientByUsername(receiverName).player.addMessage(jsonMessage.getFromBody("message"));

        clientConnectionThread.gameThread.sendTCP(jsonMessage,jsonMessage.getFromBody("receiver"));
    }

    public void sendPublicMessage(JSONMessage jsonMessage) {
        Message message = jsonMessage.getFromBody("message");
        clientConnectionThread.gameThread.getGame().addPublicMessage(message);

        clientConnectionThread.gameThread.sendAllTCP(jsonMessage);
    }

    /***************************************** **** **********************************************/

    public void giftPlayer(JSONMessage jsonMessage) {
        Gift gift = jsonMessage.getFromBody("gift");

        Player sender = gift.getSender();
        Player receiver = gift.getReceiver();
        Entity entity = gift.getContent();

        sender.getComponent(Inventory.class).takeFromInventory(entity, entity.getComponent(Pickable.class).getStackSize());
        sender.addGiftSent(gift);
        receiver.receiveGift(gift);

        clientConnectionThread.gameThread.sendTCP(jsonMessage, jsonMessage.getFromBody("receiver"));
    }

}
