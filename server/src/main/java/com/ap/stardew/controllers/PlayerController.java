package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnection;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.animal.Animal;
import com.ap.stardew.models.building.Door;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.Renderable;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.player.Message;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.TradeHistoryItem;
import com.ap.stardew.models.player.friendship.PlayerFriendship;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class PlayerController {
    private ClientConnection ClientConnection;
    private Vector2 direction = new Vector2();
    private Player player;

    public PlayerController(ClientConnection clientThread) {
        this.ClientConnection = clientThread;
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
        ClientConnection.gameThread.sendAllTCP(updateMessage);

        return null;
    }

    public void handleChangeAction(JSONMessage message){
        Player.Action action = message.getFromBody("action");

        player.setAction(action);

        JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.update);
        updateMessage.put("command", "update_player_action");
        updateMessage.put("username", player.getUsername());
        updateMessage.put("action", action);
        ClientConnection.gameThread.sendAllTCP(updateMessage);
    }


    public void update(float delta) {

    }

    public void updatePlayer() {
        this.player = ClientConnection.player;
    }

    //Maybe this should be at GameController
    public void doTrade(JSONMessage message) {
        Player receiver = ClientConnection.player;
        String senderName = message.getFromBody("receiver");
        Player sender = ClientConnection.gameThread.getClientByUsername(senderName).player;
        Game game = ClientConnection.gameThread.getGame();

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

        ClientConnection.gameThread.sendTCP(updateMessage, player.getUsername());
    }

    public void updatePlayer(Player player) {
        updatePlayer(player, JSONMessage.Type.update, "update_player");
    }

    private void updatePlayerAfterTrade(Player player, TradeHistoryItem tradeHistoryItem) {
        JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.trade);
        updateMessage.put("command", "finish_trade");
        updateMessage.put("inventory", player.getComponent(Inventory.class));
        updateMessage.put("trade_history", tradeHistoryItem);

        ClientConnection.gameThread.sendTCP(updateMessage, player.getUsername());
    }

    /***************************************** Chat **********************************************/
    public void sendPrivateMessage(JSONMessage jsonMessage) {
        String receiverName = jsonMessage.getFromBody("receiver");
        Message message = jsonMessage.getFromBody("message");
        ClientConnection.gameThread.getClientByUsername(receiverName).player.addMessage(jsonMessage.getFromBody("message"));

        PlayerFriendship playerFriendship = ClientConnection.gameThread.getGame()
            .getFriendshipBetween(message.getSender(), message.getReceiver());

        if (!playerFriendship.isHadMessageToday()) {
            playerFriendship.setHadMessageToday(true);
            playerFriendship.addXp(20);
        }

        ClientConnection.gameThread.sendTCP(jsonMessage,jsonMessage.getFromBody("receiver"));
    }

    public void sendPublicMessage(JSONMessage jsonMessage) {
        Message message = jsonMessage.getFromBody("message");
        ClientConnection.gameThread.getGame().addPublicMessage(message);

        ClientConnection.gameThread.sendAllTCP(jsonMessage);
    }

    /***************************************** **** **********************************************/


    /********************************** players interactions **********************************************/

    public void giftPlayer(JSONMessage jsonMessage) {
        Game game = ClientConnection.gameThread.getGame();
        String receiverName = jsonMessage.getFromBody("receiver");
        String senderName = jsonMessage.getFromBody("sender");
        String entityName = jsonMessage.getFromBody("entity");
        int amount = jsonMessage.getFromBody("amount");

        Player sender = ClientConnection.gameThread.getClientByUsername(senderName).player;
        Player receiver = ClientConnection.gameThread.getClientByUsername(receiverName).player;

        // Do the logic
        GameController.giveGift(game, receiverName, senderName, entityName, amount);


        // Send to client
        JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.update);
        updateMessage.put("command", "update_player_gift");
        updateMessage.put("sender", senderName);
        updateMessage.put("receiver", receiverName);
        updateMessage.put("friendships", game.getPlayerFriendships());

        HashMap<String, JSONMessage> playerUpdateMessages = new HashMap<>();
        // update for sender:
        JSONMessage senderUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        senderUpdateMessage.put("inventory", sender.getComponent(Inventory.class));
        senderUpdateMessage.put("gift_received", sender.getGiftReceived());
        senderUpdateMessage.put("gift_sent", sender.getGiftSent());
        playerUpdateMessages.put(senderName, senderUpdateMessage);

        JSONMessage receiverUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        receiverUpdateMessage.put("inventory", receiver.getComponent(Inventory.class));
        receiverUpdateMessage.put("gift_received", receiver.getGiftReceived());
        receiverUpdateMessage.put("gift_sent", receiver.getGiftSent());
        playerUpdateMessages.put(receiverName, receiverUpdateMessage);

        updateMessage.put("players_update", playerUpdateMessages);

        ClientConnection.gameThread.sendTCP(updateMessage, receiverName, senderName);
    }

    public void rateGift(JSONMessage jsonMessage) {
        Game game = ClientConnection.gameThread.getGame();
        int id = jsonMessage.getFromBody("id");
        int rating = jsonMessage.getFromBody("rating");
        String senderName = jsonMessage.getFromBody("sender");
        String receiverName = jsonMessage.getFromBody("receiver");
        Player sender = ClientConnection.gameThread.getClientByUsername(senderName).player;
        Player receiver = ClientConnection.gameThread.getClientByUsername(receiverName).player;

        GameController.rateGift(game, senderName, receiverName, id, rating);

        // Send to client
        JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.update);
        updateMessage.put("command", "update_rate_gift");
        updateMessage.put("sender", senderName);
        updateMessage.put("receiver", receiverName);
        updateMessage.put("friendships", game.getPlayerFriendships());

        HashMap<String, JSONMessage> playerUpdateMessages = new HashMap<>();
        // update for sender:
        JSONMessage senderUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        senderUpdateMessage.put("gift_received", sender.getGiftReceived());
        senderUpdateMessage.put("gift_sent", sender.getGiftSent());
        playerUpdateMessages.put(senderName, senderUpdateMessage);

        JSONMessage receiverUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        receiverUpdateMessage.put("gift_received", receiver.getGiftReceived());
        receiverUpdateMessage.put("gift_sent", receiver.getGiftSent());
        playerUpdateMessages.put(receiverName, receiverUpdateMessage);

        updateMessage.put("players_update", playerUpdateMessages);

        ClientConnection.gameThread.sendTCP(updateMessage, receiver.getUsername(), sender.getUsername());
    }

    public void hug(JSONMessage jsonMessage) {
        Game game = ClientConnection.gameThread.getGame();
        String senderName = jsonMessage.getFromBody("sender");
        String receiverName = jsonMessage.getFromBody("receiver");
        Player sender = ClientConnection.gameThread.getClientByUsername(senderName).player;
        Player receiver = ClientConnection.gameThread.getClientByUsername(receiverName).player;

        GameController.hug(game, senderName, receiverName);

        // Send to clients
        JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.update);
        updateMessage.put("command", "update_hug");
        updateMessage.put("sender", senderName);
        updateMessage.put("receiver", receiverName);
        updateMessage.put("friendships", game.getPlayerFriendships());

        HashMap<String, JSONMessage> playerUpdateMessages = new HashMap<>();
        // update for sender:
        JSONMessage senderUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        senderUpdateMessage.put("action", sender.getAction());
        playerUpdateMessages.put(senderName, senderUpdateMessage);

        JSONMessage receiverUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        receiverUpdateMessage.put("action", receiver.getAction());
        playerUpdateMessages.put(receiverName, receiverUpdateMessage);

        updateMessage.put("players_update", playerUpdateMessages);

        ClientConnection.gameThread.sendAllTCP(updateMessage);
    }

    public void flower(JSONMessage jsonMessage) {
        Game game = ClientConnection.gameThread.getGame();
        String senderName = jsonMessage.getFromBody("sender");
        String receiverName = jsonMessage.getFromBody("receiver");
        Player sender = ClientConnection.gameThread.getClientByUsername(senderName).player;
        Player receiver = ClientConnection.gameThread.getClientByUsername(receiverName).player;

        GameController.flower(game, senderName, receiverName);

        // Send to clients
        JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.update);
        updateMessage.put("command", "update_flower");
        updateMessage.put("sender", senderName);
        updateMessage.put("receiver", receiverName);
        updateMessage.put("friendships", game.getPlayerFriendships());

        HashMap<String, JSONMessage> playerUpdateMessages = new HashMap<>();
        // update for sender:
        JSONMessage senderUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        senderUpdateMessage.put("action", sender.getAction());
        senderUpdateMessage.put("inventory", sender.getComponent(Inventory.class));
        playerUpdateMessages.put(senderName, senderUpdateMessage);

        JSONMessage receiverUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        receiverUpdateMessage.put("action", receiver.getAction());
        receiverUpdateMessage.put("inventory", receiver.getComponent(Inventory.class));
        playerUpdateMessages.put(receiverName, receiverUpdateMessage);

        updateMessage.put("players_update", playerUpdateMessages);

        ClientConnection.gameThread.sendAllTCP(updateMessage);
    }

    public void askMarriage(JSONMessage jsonMessage) {
        Game game = ClientConnection.gameThread.getGame();
        String senderName = jsonMessage.getFromBody("sender");
        String receiverName = jsonMessage.getFromBody("receiver");
        String ringName = jsonMessage.getFromBody("ring_name");
        Player sender = ClientConnection.gameThread.getClientByUsername(senderName).player;
        Player receiver = ClientConnection.gameThread.getClientByUsername(receiverName).player;

        GameController.askMarriage(game, senderName, receiverName, ringName);



        // Send to clients
        JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.update);
        updateMessage.put("command", "update_ask_marriage");
        updateMessage.put("sender", senderName);
        updateMessage.put("receiver", receiverName);
        HashMap<String, JSONMessage> playerUpdateMessages = new HashMap<>();

        // update for sender:
        JSONMessage senderUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        senderUpdateMessage.put("inventory", sender.getComponent(Inventory.class));
        playerUpdateMessages.put(senderName, senderUpdateMessage);

        JSONMessage receiverUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        receiverUpdateMessage.put("suitors", receiver.getSuitors());
        receiverUpdateMessage.put("inventory", receiver.getComponent(Inventory.class));
        playerUpdateMessages.put(receiverName, receiverUpdateMessage);

        updateMessage.put("players_update", playerUpdateMessages);

        ClientConnection.gameThread.sendTCP(updateMessage,senderName, receiverName);
    }

    public void acceptMarriage(JSONMessage jsonMessage) {
        Game game = ClientConnection.gameThread.getGame();
        String senderName = jsonMessage.getFromBody("sender");
        String receiverName = jsonMessage.getFromBody("receiver");
        Player sender = ClientConnection.gameThread.getClientByUsername(senderName).player;
        Player receiver = ClientConnection.gameThread.getClientByUsername(receiverName).player;

        game.marry(sender, receiver);
        receiver.getComponent(Inventory.class).addItem(receiver.getSuitors().get(sender).clone());
        sender.setAction(Player.Action.MARRIED);
        receiver.setAction(Player.Action.MARRIED);

        // Send to clients
        JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.update);
        updateMessage.put("command", "update_accept_marriage");
        updateMessage.put("sender", senderName);
        updateMessage.put("receiver", receiverName);
        updateMessage.put("friendships", game.getPlayerFriendships());
        HashMap<String, JSONMessage> playerUpdateMessages = new HashMap<>();

        // update for sender:
        JSONMessage senderUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        senderUpdateMessage.put("action", sender.getAction());
        senderUpdateMessage.put("wallet", sender.getWallet());
        playerUpdateMessages.put(senderName, senderUpdateMessage);

        JSONMessage receiverUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        senderUpdateMessage.put("action", receiver.getAction());
        receiverUpdateMessage.put("suitors", receiver.getSuitors());
        receiverUpdateMessage.put("wallet", receiver.getWallet());
        receiverUpdateMessage.put("inventory", receiver.getComponent(Inventory.class));
        playerUpdateMessages.put(receiverName, receiverUpdateMessage);

        updateMessage.put("players_update", playerUpdateMessages);

        ClientConnection.gameThread.sendAllTCP(updateMessage);

    }

    public void rejectMarriage(JSONMessage jsonMessage) {
        Game game = ClientConnection.gameThread.getGame();
        String senderName = jsonMessage.getFromBody("sender");
        String receiverName = jsonMessage.getFromBody("receiver");
        Player sender = ClientConnection.gameThread.getClientByUsername(senderName).player;
        Player receiver = ClientConnection.gameThread.getClientByUsername(receiverName).player;

        GameController.rejectMarriage(game, senderName, receiverName);

        // Send to clients
        JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.update);
        updateMessage.put("command", "update_reject_marriage");
        updateMessage.put("sender", senderName);
        updateMessage.put("receiver", receiverName);
        updateMessage.put("friendships", game.getPlayerFriendships());
        HashMap<String, JSONMessage> playerUpdateMessages = new HashMap<>();

        // update for sender:
        JSONMessage senderUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        senderUpdateMessage.put("action", sender.getAction());
        senderUpdateMessage.put("inventory", sender.getComponent(Inventory.class));
        senderUpdateMessage.put("energy", sender.getEnergy());
        playerUpdateMessages.put(senderName, senderUpdateMessage);

        JSONMessage receiverUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        receiverUpdateMessage.put("suitors", receiver.getSuitors());
        receiverUpdateMessage.put("inventory", receiver.getComponent(Inventory.class));
        playerUpdateMessages.put(receiverName, receiverUpdateMessage);

        updateMessage.put("players_update", playerUpdateMessages);

        ClientConnection.gameThread.sendAllTCP(updateMessage);
    }

    public void cheatSetFriendship(JSONMessage jsonMessage) {
        Game game = ClientConnection.gameThread.getGame();
        String senderName = jsonMessage.getFromBody("sender");
        String receiverName = jsonMessage.getFromBody("receiver");
        int level =  jsonMessage.getFromBody("level");
        int xp = jsonMessage.getFromBody("xp");

        GameController.cheatFriendship(game, senderName, receiverName, level, xp);

        // Send to clients
        JSONMessage updateMessage = new JSONMessage(JSONMessage.Type.update);
        updateMessage.put("command", "update_friendships");
        updateMessage.put("friendships", game.getPlayerFriendships());

        ClientConnection.gameThread.sendTCP(updateMessage, receiverName, senderName);
    }

    /*******************************************************************************************/

    /***************************************** animal **********************************************/
    public void feedAnimal(JSONMessage jsonMessage) {
        Game game = ClientConnection.gameThread.getGame();
        String senderName = jsonMessage.getFromBody("sender");
        String animalName = jsonMessage.getFromBody("animal");

        Player sender = game.getPlayerByUsername(senderName);
        Animal animal = sender.findAnimal(animalName);

        Inventory inventory = sender.getComponent(Inventory.class);

        inventory.takeFromInventory("Hay", 1);
        animal.setFedToday(true);
        animal.getComponent(Renderable.class).setStatue(Renderable.Statue.EATING, 5);

        // Sending to clients
        JSONMessage senderUpdateMessage = new JSONMessage(JSONMessage.Type.update);
        senderUpdateMessage.put("command", "update_player_fields");
        senderUpdateMessage.put("username", senderName);
        senderUpdateMessage.put("inventory", inventory);
        JSONMessage animalUpdate = new JSONMessage(JSONMessage.Type.update);
            animalUpdate.put("name", animalName);
            animalUpdate.put("fed_today", true);
            animalUpdate.put("statue", Renderable.Statue.EATING);
            animalUpdate.put("statue_time", 5);
        senderUpdateMessage.put("animal", animalUpdate);



        ClientConnection.gameThread.sendAllTCP(senderUpdateMessage);
    }


}
