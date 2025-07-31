package com.ap.stardew.controllers;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.entities.components.Pickable;
import com.ap.stardew.models.enums.Menu;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.TradeOffer;
public class TradeMenuController implements Controller {
    public Result trade(String username, String type, String itemName, int amount, boolean isBuyByMoney, double price, String targetItemName, int targetItemAmount) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Player targetPlayer = game.findPlayer(username);

        if (targetPlayer == null || targetPlayer == currentPlayer) {
            return new Result(false, "Not valid player");
        }

        if (!type.equalsIgnoreCase("offer") && !type.equalsIgnoreCase("request")) {
            return new Result(false, "Not valid type");
        }

        if (!App.entityRegistry.doesEntityExist(itemName)) {
            return new Result(false, "dont use imaginary item!");
        } else if (App.entityRegistry.makeEntity(itemName).getComponent(Pickable.class) == null) {
            return new Result(false, "this item cant be trade!");
        }

        if (amount <= 0) {
            return new Result(false, "amount must be greater than 0");
        }

        if (!isBuyByMoney) {
            if (!App.entityRegistry.doesEntityExist(targetItemName)) {
                return new Result(false, "dont use imaginary item!");
            } else if (App.entityRegistry.makeEntity(targetItemName).getComponent(Pickable.class) == null) {
                return new Result(false, "this item cant be trade!");
            }

            if (targetItemAmount <= 0) {
                return new Result(false, "amount must be greater than 0");
            }
        }

        TradeOffer tradeOffer = null;

        if (type.equalsIgnoreCase("offer") && isBuyByMoney) {
            tradeOffer = new TradeOffer(currentPlayer, targetPlayer, 2, price,
                    null, 0, itemName, amount, game.getDate(), game.getTradeId());
        } else if (type.equalsIgnoreCase("request") && isBuyByMoney) {
            tradeOffer = new TradeOffer(currentPlayer, targetPlayer, 3, price,
                    itemName, amount, null, 0, game.getDate(), game.getTradeId());
        } else if (!isBuyByMoney) {
            tradeOffer = new TradeOffer(currentPlayer, targetPlayer, 1, 0,
                    targetItemName, targetItemAmount, itemName, amount, game.getDate(), game.getTradeId());
        }

        addTradeOffer(tradeOffer);

        return new Result(true, "trade request sent successfully");
    }

    public Result tradeRespond(String response, int id) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();

        TradeOffer tradeOffer = currentPlayer.findTradeOffer(id);
        if (tradeOffer == null) {
            return new Result(false, "No trade offer found");
        }
        if (tradeOffer.getReceiver() != currentPlayer) {
            return new Result(false, "You cant decide on this trade");
        }
        if (tradeOffer.isDecided()) {
            return new Result(false, "Trade is already decided");
        }

        if (response.equalsIgnoreCase("accept")) {
            if(tradeOffer.accept()) {
                return new Result(true, "Trade accepted successfully!");
            } else {
                return new Result(false, "Trade accept failed due to not enough resources.");
            }

        } else if (response.equalsIgnoreCase("reject")) {
            tradeOffer.reject();
            return new Result(true, "Trade rejected successfully!");

        } else {
            return new Result(false, "Invalid response! (Use \"accept\" or \"reject\")");
        }

    }

    public Result tradeList() {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();

        StringBuilder message = new StringBuilder("Your not decided trade requests:\n\n");

        for (TradeOffer tradeOffer : currentPlayer.getTrades()) {
            if (tradeOffer.getReceiver() == currentPlayer && !tradeOffer.isDecided()) {
                message.append(tradeOffer.infoMessage(false)).append("\n");
            }
        }

        return new Result(true, message.toString());
    }

    public Result tradeHistory() {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();

        StringBuilder message = new StringBuilder("Your trade history:\n\n");

        for (TradeOffer tradeOffer : currentPlayer.getTrades()) {
            if (tradeOffer.isDecided()) {
                message.append(tradeOffer.infoMessage(true)).append("\n");
            }
        }

        return new Result(true, message.toString());
    }

    public Result back() {
        App.setCurrentMenu(Menu.GAME_MENU);
        return new Result(true, "You are now in GameMenu");
    }

    private void addTradeOffer(TradeOffer tradeOffer) {
        Player sender = tradeOffer.getSender();
        Player receiver = tradeOffer.getReceiver();

        sender.getTrades().add(tradeOffer);
        receiver.getTrades().add(tradeOffer);
        receiver.setHaveNewTrade(true);
    }


    @Override
    public Result changeMenu(String menuName) {
        return null;
    }
}
