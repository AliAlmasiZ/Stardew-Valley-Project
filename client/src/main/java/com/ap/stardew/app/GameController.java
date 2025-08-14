package com.ap.stardew.app;

import com.ap.stardew.ClientGame;
import com.ap.stardew.models.*;
import com.ap.stardew.models.animal.Animal;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.entities.Renderable;
import com.ap.stardew.models.entities.components.PositionComponent;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.player.Message;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.friendship.PlayerFriendship;
import com.ap.stardew.views.GameScreen;
import com.ap.stardew.views.dialogs.TradeDialog;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.*;
import com.ap.stardew.views.widgets.PopUpMessage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameController {
    public static Game makeGame(JSONMessage details) {
        Game game = details.getFromBody("gameData");

        game.setCurrentPlayer(details.getFromBody("player"));

        for (Player player : game.getPlayers()) {
            player.setSprite(new Sprite());
        }

        game.getCurrentPlayer().getComponent(Inventory.class).addItem(App.entityRegistry.makeEntity("Axe"));

        return game;
    }

    public static void startGame(JSONMessage details) {
        Game game = makeGame(details);

        ClientApp.setActiveGame(game);

        Gdx.app.postRunnable(() -> {
            ClientGame.getInstance().setScreen(new GameScreen(game));
        });
    }

    public static void updatePlayers(HashMap<String, JSONMessage> playerUpdateMessages) {
        for (Map.Entry<String, JSONMessage> entry : playerUpdateMessages.entrySet()) {
            updatePlayer(entry.getKey(), entry.getValue());
        }
    }

    public static void updatePlayer(String username, JSONMessage message) {
        Game game = ClientApp.getActiveGame();
        Player player = game.getPlayerByUsername(username);

        if (message.containsKey("gift_received")) {
            player.setGiftReceived(message.getFromBody("gift_received"));
        }
        if (message.containsKey("gift_sent")) {
            player.setGiftSent(message.getFromBody("gift_sent"));
        }
        if (message.containsKey("inventory")) {
            player.getComponent(Inventory.class).empty();
            player.getComponent(Inventory.class).addItems(message.getFromBody("inventory"));
        }
        if (message.containsKey("action")) {
            player.setAction(message.getFromBody("action"));
        }
        if (message.containsKey("suitors")) {
            player.setSuitors(message.getFromBody("suitors"));
        }
        if (message.containsKey("wallet")) {
            player.setWallet(message.getFromBody("wallet"));
        }
        if (message.containsKey("energy")) {
            player.setEnergy(message.getFromBody("energy"));
        }
        if (message.containsKey("animal")) {
            updateAnimal(username, message.getFromBody("animal"));
        }
        if (message.containsKey("remove_animal")) {
            player.getAnimals().remove(message.getFromBody("animal"));
        }
    }

    public static void updateAnimal( String playerName, JSONMessage message) {
        Game game = ClientApp.getActiveGame();
        Player player = game.getPlayerByUsername(playerName);
        String animalName = message.getFromBody("name");
        Animal animal = player.findAnimal(animalName);

        if (message.containsKey("statue")) {
            Renderable.Statue statue = message.getFromBody("statue");
            float statueTime = 0;
            switch (statue) {
                case RIGHT_WALKING, LEFT_WALKING -> {
                    statueTime = 1000;
                }
                case EATING -> {
                    statueTime = 5;
                }
                case PET -> {
                    statueTime = 3;
                }

            }
            animal.getComponent(Renderable.class).setStatue(statue, statueTime);
        }
        if (message.containsKey("fed_today")) {
            animal.setFedToday(message.getFromBody("fed_today"));
        }
        if (message.containsKey("pet_today")) {
            animal.setPetToday(message.getFromBody("pet_today"));
        }
        if (message.containsKey("destination_x")) {
            animal.setDestinationX(message.getFromBody("destination_x"));
        }
        if (message.containsKey("destination_y")) {
            animal.setDestinationY(message.getFromBody("destination_y"));
        }
        if (message.containsKey("time_left_to_move")) {
            animal.setTimeLeftToMove(message.getFromBody("time_left_to_move"));
        }
        if (message.containsKey("friendship")) {
            animal.setFriendshipLevel(message.getFromBody("friendship"));
        }


    }

    public static void updateFriendships(JSONMessage jsonMessage) {
        Game game = ClientApp.getActiveGame();
        if (!jsonMessage.containsKey("friendships")) return;
        game.setPlayerFriendships(jsonMessage.getFromBody("friendships"));
    }

    /***************************************** Trade **********************************************/

    /**
     * SEND
     * show the waiting dialog, send the message
     *
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
     *
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
     *
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
     *
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
     *
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
     *
     * @param player   player to send
     * @param item     item that is given
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
     *
     * @param message update info
     */
    public static void updateTradeInventory(JSONMessage message) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        gameScreen.tradeDialog.updateInventory(message.getFromBody("item"), message.getFromBody("isSender"));
    }

    /**
     * SEND
     * finish putting items and wait for response
     *
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
     *
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
     *
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

    public static Result sendReconnectRequestResponse(boolean accept) {
        JSONMessage req = new JSONMessage(JSONMessage.Type.command);
        req.put("command", "game_reconnect_request");
        req.put("token", ClientApp.getToken());
        req.put("accepted", accept);

        JSONMessage response = ClientApp.sendAndWaitForResponse(req, 3000);

        if (response == null) return new Result(false, "failed to reconnect");

        Result result = response.getFromBody("result", Result.class);
        if (result == null) return new Result(false, result.message());
        if (!result.isSuccessful()) return new Result(false, result.message());


        final Game game = makeGame(response);
        Gdx.app.postRunnable(() -> {
            ClientApp.setActiveGame(game);
            GameScreen gameScreen = new GameScreen(game);
            ClientGame.getInstance().setScreen(gameScreen);
            if (response.getFromBody("gamePaused")) {
                handleGamePauseForDisconnection(response);
            }
        });

        return new Result(true, "reconnected");
    }

    public static void handleGameDisconnection(GameScreen gameScreen) {
        gameScreen.camera = null;
        gameScreen.tradeDialog = null;
        gameScreen.dispose();
        ClientApp.setActiveGame(null);

        ClientGame.getInstance().setScreen(new DisconnectionScreen(null, List.of(ClientApp.getUsername()), 120f));
    }

    public static void handleGamePauseForDisconnection(JSONMessage message) {
        final ArrayList<String> usernames = message.getFromBody("usernames");

        if (ClientGame.getInstance().getScreen() instanceof GameScreen gameScreen) {
            Game activeGame = ClientApp.getActiveGame();
            for (String username : usernames) {
                if (activeGame.getPlayerByUsername(username) == null) return;
            }
            Gdx.app.postRunnable(() -> {
                DisconnectionScreen disconnectionScreen = new DisconnectionScreen(gameScreen, usernames, message.getFromBody("timeLeft"));
                ClientGame.getInstance().setScreen(disconnectionScreen);
            });
        } else if (ClientGame.getInstance().getScreen() instanceof DisconnectionScreen disconnectionScreen) {
            Gdx.app.postRunnable(() -> {
                disconnectionScreen.updateDisconnectedPlayers(usernames);
            });
        }
    }

    public static void endGame() {
        ClientApp.setActiveGame(null);
    }

    public static void handleGameReconnectionRequest(JSONMessage message) {
        Gdx.app.postRunnable(() -> {
            PopUpMessage popUpMessage = new PopUpMessage();
            popUpMessage.add(new Label("You were disconnected from a session. Do you want to rejoin?",
                GameAssetManager.getInstance().getCustomSkin()) {{
                setWrap(true);
            }}).grow().width(200).spaceBottom(2).row();
            Button acceptButton = new Button(GameAssetManager.getInstance().getCustomSkin(), "accept");
            Button rejectButton = new Button(GameAssetManager.getInstance().getCustomSkin(), "reject");

            popUpMessage.add(acceptButton);
            popUpMessage.add(rejectButton).expandX().left();
            popUpMessage.add().growX();

            popUpMessage.show(AbstractScreen.getFrontStage());

            acceptButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Result result = GameController.sendReconnectRequestResponse(true);
                    PopUpMessage popUpMessage = new PopUpMessage();
                    popUpMessage.add(new Label(result.message(),
                        GameAssetManager.getInstance().getCustomSkin())).row();
                    popUpMessage.show(AbstractScreen.getFrontStage());
                }
            });

            rejectButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Result result = GameController.sendReconnectRequestResponse(false);
                    PopUpMessage popUpMessage = new PopUpMessage();
                    popUpMessage.add(new Label(result.message(),
                        GameAssetManager.getInstance().getCustomSkin())).row();
                    popUpMessage.show(AbstractScreen.getFrontStage());
                }
            });
        });
    }

    /*************************************************************************************************/

    /***************************************** Chat **********************************************/

    /**
     * SEND
     *
     * @param username to send
     * @param text     message text
     */
    public static void sendPrivateMessage(String username, String text) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();

        Player player = ClientApp.getActiveGame().getPlayerByUsername(username);
        JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.chat);
        jsonMessage.put("command", "send_private_message");
        jsonMessage.put("receiver", player.getUsername());
        Message message = new Message(ClientApp.getActiveGame().getDate(), text, player, ClientApp.getActiveGame().getCurrentPlayer());
        jsonMessage.put("message", message);

        PlayerFriendship playerFriendship = ClientApp.getActiveGame().getFriendshipBetween(username, ClientApp.getActiveGame().getCurrentPlayer().getUsername());

        if (!playerFriendship.isHadMessageToday()) {
            playerFriendship.setHadMessageToday(true);
            playerFriendship.addXp(20);
        }

        gameScreen.chatDialog.updateMessage(message.getReceiver(), message);

        ClientApp.sendTCP(jsonMessage);
    }

    /**
     * HANDLE
     *
     * @param jsonMessage to handle
     */
    public static void receivePrivateMessage(JSONMessage jsonMessage) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        Message message = jsonMessage.getFromBody("message");
        ClientApp.getActiveGame().getCurrentPlayer().addMessage(message);

        gameScreen.chatDialog.updateMessage(message.getSender(), message);

        PlayerFriendship playerFriendship = ClientApp.getActiveGame().getFriendshipBetween(message.getSender(), message.getReceiver());

        if (!playerFriendship.isHadMessageToday()) {
            playerFriendship.setHadMessageToday(true);
            playerFriendship.addXp(20);
        }

        if (isTagged(message.getMessage()) && !(gameScreen.chatDialog.isOpen && gameScreen.chatDialog.isPublic()))
            gameScreen.showTemporaryMessage(
                "\"" + message.getSender() + "\" sent you a message!",
                5, Color.GREEN);
    }

    /**
     * SEND
     *
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
     *
     * @param jsonMessage message
     */
    public static void receivePublicMessage(JSONMessage jsonMessage) {
//        if (!(ClientGame.getInstance().getScreen() instanceof GameScreen)) return;
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();

        Message message = jsonMessage.getFromBody("message");
        ClientApp.getActiveGame().addPublicMessage(message);

        gameScreen.chatDialog.updateMessage(null, message);

        if (isTagged(message.getMessage()) && !(gameScreen.chatDialog.isOpen && gameScreen.chatDialog.isPublic()))
            gameScreen.showTemporaryMessage(
                "\"" + message.getSender() + "\" tagged you in public chat",
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

    /**
     * SEND
     * after check that its possible to send a gift it is sent to server to do it
     *
     * @param player     the player to give gift
     * @param entityName name of the gift
     * @param amount     amount of the gift
     */
    public static void giftPlayer(Player player, String entityName, int amount) {
        JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.update);
        jsonMessage.put("command", "gift_player");
        jsonMessage.put("receiver", player.getUsername());
        jsonMessage.put("sender", ClientApp.getActiveGame().getCurrentPlayer().getUsername());
        jsonMessage.put("entity", entityName);
        jsonMessage.put("amount", amount);

        ClientApp.sendTCP(jsonMessage);
    }

    /**
     * HANDLE
     * after the server done it, it will send the updates
     *
     * @param jsonMessage details
     */
    public static void receiveGiftUpdate(JSONMessage jsonMessage) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        Game game = ClientApp.getActiveGame();
        Player currentPlayer = ClientApp.getActiveGame().getCurrentPlayer();

        String senderName = jsonMessage.getFromBody("sender");
        String receiverName = jsonMessage.getFromBody("receiver");
        game.setPlayerFriendships(jsonMessage.getFromBody("friendships"));
        updatePlayers(jsonMessage.getFromBody("players_update"));


        if (currentPlayer.getUsername().equals(receiverName)) {
            gameScreen.showTemporaryMessage(jsonMessage.getFromBody("sender") + " sent you a gift", 5, Color.CYAN);
        } else if (currentPlayer.getUsername().equals(senderName)) {
            gameScreen.showTemporaryMessage("You sent gift to \"" + jsonMessage.getFromBody("receiver") + "\"!", 5, Color.CYAN);

        }
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

    public static void receiveGiftRateUpdate(JSONMessage jsonMessage) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        Game game = ClientApp.getActiveGame();
        String senderName = jsonMessage.getFromBody("sender");
        String receiverName = jsonMessage.getFromBody("receiver");
        game.setPlayerFriendships(jsonMessage.getFromBody("friendships"));
        updatePlayers(jsonMessage.getFromBody("players_update"));


        if (ClientApp.getActiveGame().getCurrentPlayer().getUsername().equals(receiverName)) {
            gameScreen.showTemporaryMessage("Your gift has been rated by \"" + senderName + "\"", 5, Color.CYAN);
        }
    }

    public static void sendReactionUpdate(Player player){
        JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.command);
        jsonMessage.put("command", "update_player_reaction");
        jsonMessage.put("reaction", player.getCurrentReaction());

        ClientApp.sendTCP(jsonMessage);
    }

    public static void sendActionUpdate(Player player){
        JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.command);
        jsonMessage.put("command", "update_player_action");
        jsonMessage.put("action", player.getAction());

        if(player.getActionItem() != null){
            Entity newItem = player.getActionItem().clone();
            newItem.removeComponent(PositionComponent.class);
            jsonMessage.put("actionItem", newItem);
        }
        jsonMessage.put("stateTime", player.getStateTime());
        jsonMessage.put("lastDir", player.getLastDir());

        ClientApp.sendTCP(jsonMessage);
    }

    /******************************************* Player Interactions **********************************************/

    public static void hugUpdate(JSONMessage jsonMessage) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        Game game = ClientApp.getActiveGame();
        updatePlayers(jsonMessage.getFromBody("players_update"));
    }

    public static void flowerUpdate(JSONMessage jsonMessage) {
        updatePlayers(jsonMessage.getFromBody("players_update"));
    }

    public static void askMarriageUpdate(JSONMessage jsonMessage) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        Game game = ClientApp.getActiveGame();
        Player currentPlayer = ClientApp.getActiveGame().getCurrentPlayer();

        String senderName = jsonMessage.getFromBody("sender");
        Player suitor = game.getPlayerByUsername(senderName);

        updatePlayers(jsonMessage.getFromBody("players_update"));

        if (currentPlayer.getUsername().equals(jsonMessage.getFromBody("receiver"))) {
            gameScreen.openAskMarriageDialog(suitor);
        }
    }

    public static void acceptMarriageUpdate(JSONMessage jsonMessage) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        Player currentPlayer = ClientApp.getActiveGame().getCurrentPlayer();
        String receiverName = jsonMessage.getFromBody("receiver");
        String senderName = jsonMessage.getFromBody("sender");

        updatePlayers(jsonMessage.getFromBody("players_update"));
        updateFriendships(jsonMessage);

        if (currentPlayer.getUsername().equals(senderName) || currentPlayer.getUsername().equals(receiverName)) {
            gameScreen.showTemporaryMessage("Lalala mobarak bada!", 5, Color.CYAN);
        }
    }

    public static void rejectMarriageUpdate(JSONMessage jsonMessage) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        Player currentPlayer = ClientApp.getActiveGame().getCurrentPlayer();
        String receiverName = jsonMessage.getFromBody("receiver");
        String senderName = jsonMessage.getFromBody("sender");

        updatePlayers(jsonMessage.getFromBody("players_update"));
        updateFriendships(jsonMessage);

        if (currentPlayer.getUsername().equals(senderName)) {
            gameScreen.showTemporaryMessage("hala fekr kardi ki hasti??", 5, Color.CYAN);
        } else if (currentPlayer.getUsername().equals(receiverName)) {
            gameScreen.showTemporaryMessage("Ghamet Nabashe Mard...", 5, Color.RED);

        }
    }


    /*************************************************************************************************/

    public static void initialAddAnimal(JSONMessage jsonMessage) {
        Game game = ClientApp.getActiveGame();
        Player owner = game.getPlayerByUsername(jsonMessage.getFromBody("owner"));
        Animal animal = jsonMessage.getFromBody("animal");

        owner.getAnimals().add(animal);
        EntityPlacementSystem.placeEntity(animal, animal.getComponent(PositionComponent.class).get(), game.getMainMap());
        System.out.println("animal added");
    }

    public static void sellAnimalUpdate(JSONMessage jsonMessage) {
        Game game = ClientApp.getActiveGame();
        String animalName = jsonMessage.getFromBody("animal_name");
        String sender = jsonMessage.getFromBody("sender");
        Player player = game.getPlayerByUsername(sender);

        Animal animal = player.findAnimal(animalName);
        player.getAnimals().remove(animal);
        EntityPlacementSystem.removeFromMap(animal);

        updatePlayers(jsonMessage.getFromBody("players_update"));
    }

    public static void advanceOneHourUpdate(JSONMessage jsonMessage) {
        GameScreen gameScreen = (GameScreen) ClientGame.getInstance().getScreen();
        Game game = ClientApp.getActiveGame();

        game.setDate(jsonMessage.getFromBody("date"));

        gameScreen.initNPCDialogs();

        updatePlayers(jsonMessage.getFromBody("players_update"));


    }


    public static void sendSelectedEmojiUpdate(Player player){
        JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.command);
        jsonMessage.put("command", "update_player_emojis");
        jsonMessage.put("emojis", player.getEmojis());

        ClientApp.sendTCP(jsonMessage);
    }
}
