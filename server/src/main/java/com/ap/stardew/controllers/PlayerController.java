package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnectionThread;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.TradeHistoryItem;
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

    //Maybe this should be at GameController
    public void doTrade(JSONMessage message) {
        Player receiver = clientConnectionThread.player;
        String senderName = message.getFromBody("receiver");
        Player sender = clientConnectionThread.gameThread.getClientByUsername(senderName).player;
        Game game = clientConnectionThread.gameThread.getGame();

        Inventory receiverInventory = message.getFromBody("receiverInventory");
        Inventory senderInventory = message.getFromBody("senderInventory");

        sender.getComponent(Inventory.class).tradeEntities(senderInventory, receiverInventory);
        receiver.getComponent(Inventory.class).tradeEntities(receiverInventory, senderInventory);

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

}
