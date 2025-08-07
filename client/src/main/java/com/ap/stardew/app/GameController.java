package com.ap.stardew.app;

import com.ap.stardew.ClientGame;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.views.GameScreen;
import com.ap.stardew.views.LobbyScreen;
import com.ap.stardew.views.TradeDialog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class GameController {
    public static void startGame(JSONMessage details){
        String lobbyId = details.getFromBody("lobby_id");

        if(!(ClientGame.getInstance().getScreen() instanceof LobbyScreen lobbyScreen)) return;

        if(!lobbyId.equals(lobbyScreen.getCurrentLobby().getLobbyId())) return;

        Game game = details.getFromBody("gameData");

        game.setCurrentPlayer(details.getFromBody("player"));





        ClientApp.setActiveGame(game);

        Gdx.app.postRunnable(() -> {
            ClientGame.getInstance().setScreen(new GameScreen(game));
        });
    }

    // Trade

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
    public static void stopTradeWithPlayer(Player player) {
        Player currentPlayer = ClientApp.getActiveGame().getCurrentPlayer();
        JSONMessage response = new JSONMessage(JSONMessage.Type.trade);
        response.put("command", "stop_trade");
        response.put("sender", currentPlayer.getUsername());
        response.put("receiver", player.getUsername());
        response.put("message", currentPlayer.getUsername() + " stopped the trade!");

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

    public static void acceptTradeStart(Player player) {

    }

    public static void rejectTradeStart(Player player) {

    }

    public static void updateTradeInventory(Player player) {}
}
