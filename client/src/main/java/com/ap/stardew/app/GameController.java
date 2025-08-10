package com.ap.stardew.app;

import com.ap.stardew.ClientGame;
import com.ap.stardew.controllers.GameMenuController;
import com.ap.stardew.controllers.GameStaticController;
import com.ap.stardew.models.App;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.player.Gift;
import com.ap.stardew.models.player.Message;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.views.GameScreen;
import com.ap.stardew.views.LobbyScreen;
import com.ap.stardew.views.TradeDialog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;

import com.badlogic.gdx.graphics.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameController {
    public static void startGame(JSONMessage details){
        String lobbyId = details.getFromBody("lobby_id");

        if(!(ClientGame.getInstance().getScreen() instanceof LobbyScreen lobbyScreen)) return;

        if(!lobbyId.equals(lobbyScreen.getCurrentLobby().getLobbyId())) return;

        Game game = details.getFromBody("gameData");

        game.setCurrentPlayer(details.getFromBody("player"));

        for (Player player : game.getPlayers()) {
            player.setSprite(new Sprite());
        }

        GameMenuController gameMenuController = new GameMenuController();


        game.getCurrentPlayer().getComponent(Inventory.class).addItem(App.entityRegistry.makeEntity("Axe"));

        ClientApp.setActiveGame(game);

        Gdx.app.postRunnable(() -> {
            ClientGame.getInstance().setScreen(new GameScreen(game));
        });
    }

    /***************************************** Trade **********************************************/

    /**
     * SEND
     * show the waiting dialog, send the message
     * @param player the player that the trade begin with
     */
    public static void startTradeWithPlayer(Player player) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();

        gameScreen.tradeDialog = new TradeDialog(gameScreen.getUiStage(), player);
        gameScreen.tradeDialog.openAsSender();

        JSONMessage message = new JSONMessage(JSONMessage.Type.trade);
        message.put("command", "request_start");
        message.put("sender", ClientApp.getActiveGame().getCurrentPlayer().getUsername());
        message.put("receiver", player.getUsername());

        ClientApp.sendTCP(message);
    }

    /**
     * HANDLE
     * when a startTradeWithPlayer message receives it will handle here
     * @param message the coming message from server
     */
    public static void startTradeRequest(JSONMessage message) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        Player currentPlayer = ClientApp.getActiveGame().getCurrentPlayer();
        String playerToTradeUsername = message.getFromBody("sender");

        if (gameScreen.tradeDialog != null) {
            JSONMessage response = new JSONMessage(JSONMessage.Type.trade);
            response.put("command", "stop_trade");
            response.put("sender", currentPlayer.getUsername());
            response.put("receiver", message.getFromBody("sender"));
            response.put("message", currentPlayer.getUsername() + " is in another trade now!");

            ClientApp.sendTCP(response);
            return;
        }

        Player playerToTrade = ClientApp.getActiveGame().getPlayerByUsername(playerToTradeUsername);
        gameScreen.tradeDialog = new TradeDialog(gameScreen.getUiStage(), playerToTrade);
        gameScreen.tradeDialog.openAsReceiver();
    }

    /**
     * SEND
     * after closing the menu
     * @param player player to trade
     */
    public static void stopTradeWithPlayer(Player player, String message) {
        Player currentPlayer = ClientApp.getActiveGame().getCurrentPlayer();
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        gameScreen.tradeDialog = null;


        JSONMessage response = new JSONMessage(JSONMessage.Type.trade);
        response.put("command", "stop_trade");
        response.put("sender", currentPlayer.getUsername());
        response.put("receiver", player.getUsername());
        response.put("message", currentPlayer.getUsername() + message);

        ClientApp.sendTCP(response);
    }

    /**
     * HANDLE
     * @param message received from server
     */
    public static void stopTrade(JSONMessage message) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();

        gameScreen.showTemporaryMessage(message.getFromBody("message"), 5, Color.RED);
        if (gameScreen.tradeDialog != null) {
            gameScreen.tradeDialog.hide();
            gameScreen.tradeDialog = null;
        }
    }

    /**
     * SEND
     * after clicking yes
     * @param player player to send
     */
    public static void acceptTradeStart(Player player) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        gameScreen.tradeDialog.openMainTradeAsReceiver();

        JSONMessage message = new JSONMessage(JSONMessage.Type.trade);
        message.put("command", "accept_trade");
        message.put("receiver", player.getUsername());

        ClientApp.sendTCP(message);
    }

    /**
     * HANDLE
     */
    public static void acceptTradeRequest() {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        gameScreen.tradeDialog.openMainTradeAsSender();
    }

    /**
     * SEND
     * update the inventory
     * @param player player to send
     * @param item item that is given
     * @param isSender if it is added to sender inventory or not
     */
    public static void updateTradeInventory(Player player, Entity item, boolean isSender) {
        JSONMessage message = new JSONMessage(JSONMessage.Type.trade);
        message.put("command", "update_trade");
        message.put("receiver", player.getUsername());
        message.put("item", item);
        message.put("isSender", isSender);

        ClientApp.sendTCP(message);
    }

    /**
     * HANDLE
     * @param message update info
     */
    public static void updateTradeInventory(JSONMessage message) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        gameScreen.tradeDialog.updateInventory(message.getFromBody("item"), message.getFromBody("isSender"));
    }

    /**
     * SEND
     * finish putting items and wait for response
     * @param player to send
     */
    public static void confirmTrade(Player player) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        gameScreen.tradeDialog.openFinalTradeAsSender();
        JSONMessage message = new JSONMessage(JSONMessage.Type.trade);
        message.put("command", "confirm");
        message.put("receiver", player.getUsername());

        ClientApp.sendTCP(message);
    }

    /**
     * HANDLE
     */
    public static void confirmTrade() {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        gameScreen.tradeDialog.openFinalTradeAsReceiver();
    }

    /**
     * SEND
     * jic jic jic
     * @param player have trade with
     */
    public static void doTrade(Player player) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();

        JSONMessage message = new JSONMessage(JSONMessage.Type.trade);
        message.put("command", "do_trade");
        message.put("receiver", player.getUsername());
        message.put("senderInventory", gameScreen.tradeDialog.getSenderInventory());
        message.put("receiverInventory", gameScreen.tradeDialog.getReceiverInventory());

        ClientApp.sendTCP(message);
    }

    public static void finishTrade(JSONMessage message) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        Player currentPlayer = ClientApp.getActiveGame().getCurrentPlayer();
        currentPlayer.getComponent(Inventory.class).empty();
        currentPlayer.getComponent(Inventory.class).addItems(message.getFromBody("inventory"));
        currentPlayer.addTradeHistory(message.getFromBody("trade_history"));

        gameScreen.showTemporaryMessage("Trade has done successfully!", 5, Color.GREEN);

        gameScreen.tradeDialog.hide();
        gameScreen.tradeDialog = null;
    }

    /**
     * SEND
     * @param player to send
     */
    public static void rejectTradeOffer(Player player) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        gameScreen.tradeDialog.openMainTradeAsReceiver();

        gameScreen.tradeDialog.getReceiverInventory().empty();
        gameScreen.tradeDialog.getSenderInventory().empty();

        JSONMessage message = new JSONMessage(JSONMessage.Type.trade);
        message.put("command", "reject_trade_offer");
        message.put("receiver", player.getUsername());

        ClientApp.sendTCP(message);
    }

    /**
     * HANDLE
     */
    public static void rejectTradeOffer() {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        gameScreen.tradeDialog.openMainTradeAsSender();
        gameScreen.tradeDialog.errorLabel.setVisible(true);
        gameScreen.tradeDialog.errorLabel.setText("Your offer has been rejected...");
        gameScreen.tradeDialog.getReceiverInventory().empty();
        gameScreen.tradeDialog.getSenderInventory().empty();
    }

    /*************************************************************************************************/

    /***************************************** Chat **********************************************/

    /**
     * SEND
     * @param username to send
     * @param text message text
     */
    public static void sendPrivateMessage(String username, String text) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();

        Player player = ClientApp.getActiveGame().getPlayerByUsername(username);
        JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.chat);
        jsonMessage.put("command", "send_private_message");
        jsonMessage.put("receiver", player.getUsername());
        Message message = new Message(ClientApp.getActiveGame().getDate(), text, player, ClientApp.getActiveGame().getCurrentPlayer());
        jsonMessage.put("message", message);

        gameScreen.chatDialog.updateMessage(message.getReceiver(), message);

        ClientApp.sendTCP(jsonMessage);
    }

    /**
     * HANDLE
     * @param jsonMessage to handle
     */
    public static void receivePrivateMessage(JSONMessage jsonMessage) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        Message message = jsonMessage.getFromBody("message");
        ClientApp.getActiveGame().getCurrentPlayer().addMessage(message);

        gameScreen.chatDialog.updateMessage(message.getSender(), message);

        if (isTagged(message.getMessage()) && !(gameScreen.chatDialog.isOpen && gameScreen.chatDialog.isPublic()))  gameScreen.showTemporaryMessage(
            "\"" + message.getSender() + "\" sent you a message!" ,
            5, Color.GREEN);
    }

    /**
     * SEND
     * @param text message text
     */
    public static void sendPublicMessage(String text) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        Game game = ClientApp.getActiveGame();
        Message message = new Message(game.getDate(), text, null, game.getCurrentPlayer());

        JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.chat);
        jsonMessage.put("command", "send_public_message");
        jsonMessage.put("message", message);

        ClientApp.sendTCP(jsonMessage);
    }

    /**
     * HANDLE
     * the logic after receiving
     * @param jsonMessage message
     */
    public static void receivePublicMessage(JSONMessage jsonMessage) {
//        if (!(ClientGame.getInstance().getScreen() instanceof GameScreen)) return;
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();

        Message message = jsonMessage.getFromBody("message");
        ClientApp.getActiveGame().addPublicMessage(message);

        gameScreen.chatDialog.updateMessage(null, message);

        if (isTagged(message.getMessage()) && !(gameScreen.chatDialog.isOpen && gameScreen.chatDialog.isPublic()))  gameScreen.showTemporaryMessage(
            "\"" + message.getSender() + "\" tagged you in public chat" ,
            5, Color.GREEN);
    }

    private static boolean isTagged(String text) {
        String username = ClientApp.getActiveGame().getCurrentPlayer().getUsername();

        Pattern pattern = Pattern.compile("(?<!\\\\S)@[A-Za-z0-9_-]+");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            if (matcher.group().substring(1).equals(username)) return true;
        }

        return false;
    }

    /*************************************************************************************************/

    public static void giftPlayer(Player player, Gift gift) {
        JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.update);
        jsonMessage.put("command", "gift_player");
        jsonMessage.put("receiver", player.getUsername());
        jsonMessage.put("gift", gift);
    }

    public static void receiveGift(JSONMessage jsonMessage) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        Gift gift = jsonMessage.getFromBody("gift");

        ClientApp.getActiveGame().getCurrentPlayer().receiveGift(gift);

        gameScreen.showTemporaryMessage(jsonMessage.getFromBody("sender"), 5, Color.CYAN);
    }

    public static void rateGift(int id, int rating, Player player) {
        JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.update);
        jsonMessage.put("command", "rate_gift");
        jsonMessage.put("sender", ClientApp.getActiveGame().getCurrentPlayer().getUsername());
        jsonMessage.put("receiver", player.getUsername());
        jsonMessage.put("id", id);
        jsonMessage.put("rating", rating);

        ClientApp.sendTCP(jsonMessage);
    }

    public static void receiveGiftRate(JSONMessage jsonMessage) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        int id = jsonMessage.getFromBody("id");
        int rating = jsonMessage.getFromBody("rating");
        Game game = ClientApp.getActiveGame();
        Player giftRater = ClientApp.getActiveGame().getCurrentPlayer();

        GameStaticController.giftRate(id, rating, game, giftRater);

        gameScreen.showTemporaryMessage("Your gift has been rated by \"" + giftRater.getUsername() + "\"" , 5, Color.CYAN);
    }


}
