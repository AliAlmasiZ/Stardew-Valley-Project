package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnectionThread;
import com.ap.stardew.app.ServerApp;
import com.ap.stardew.models.*;
import com.ap.stardew.models.dto.AccountInfo;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.InteriorComponent;
import com.ap.stardew.models.entities.components.PositionComponent;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.enums.Weather;
import com.ap.stardew.models.gameMap.MapRegion;
import com.ap.stardew.models.gameMap.WorldMap;
import com.ap.stardew.models.player.Gift;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.friendship.PlayerFriendship;
import com.ap.stardew.utils.TiledMapUtils;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    public static GameSession createGame(List<AccountInfo> accountInfos){
        WorldMap worldMap = TiledMapUtils.loadWorldMapFromFile("./Content(unpacked)/Maps/untitled.tmx");

        Game game = new Game();
        game.setTodayWeather(Weather.SUNNY);
        game.setTomorrowWeather(Weather.SUNNY);
        game.setMainMap(worldMap);

        GameSession gameSession = new GameSession(game);

        for (AccountInfo accountInfo : accountInfos) {
            Player player = new Player(accountInfo.getUsername());
            player.initPlayer();
            player.setPosition(112, 112);
            game.addPlayer(player);

            String regionName = accountInfo.getSelectedMapRegion();
            MapRegion region = worldMap.getRegion(regionName);
            player.addRegion(region, worldMap);
            player.setHouse(worldMap.getFarmsDetail().get(region).farmHouse);

            EntityPlacementSystem.placeOnMap(player, new Position(5, 5),
                player.getHouse().getComponent(InteriorComponent.class).getMap());

            gameSession.addUserToSession(accountInfo.getUsername(), player);
        }

        game.initGame(null);
        initialCheats(game);



        return gameSession;
    }

    private static void initialCheats(Game game) {
        for (Player player : game.getPlayers()) {
            GameStaticController.cheatGiveItem(game, player,"Hay", 500);
            GameStaticController.cheatGiveItem(game, player,"Wood", 50);
            GameStaticController.cheatGiveItem(game, player,"Stone", 40);
            GameStaticController.cheatGiveItem(game, player,"Coal", 8);
            GameStaticController.cheatGiveItem(game, player,"Axe", 1);
            GameStaticController.cheatGiveItem(game, player,"vegetable", 1);
            GameStaticController.cheatGiveItem(game, player,"Cherry", 10);
        }
    }


    public static Gift giveGift(Game game,String receiverName, String senderName, String itemName, int amount) {
        Player sender = game.findPlayer(senderName);
        Player giftedPlayer = game.findPlayer(receiverName);
        Inventory inventory = sender.getComponent(Inventory.class);

        Entity item = inventory.takeFromInventory(itemName, amount);

        Gift gift = new Gift(sender, giftedPlayer, item, game.getDate());
        sender.addGiftSent(gift);
        giftedPlayer.receiveGift(gift);

        return gift;
    }

    public static void rateGift(Game game, String senderName, String receiverName, int id, int rating) {
        Player sender = game.findPlayer(senderName);
        Player receiver = game.findPlayer(receiverName);
        Gift gift = sender.findGift(id);

        gift.setRating(rating);

        PlayerFriendship playerFriendship = game.getFriendshipBetween(sender, receiver);
        if (rating < 3) playerFriendship.reduceXp((3 - rating) * 30 - 15);
        else playerFriendship.addXp((rating - 3) * 30 + 15);
    }

    public static void hug(Game game, String senderName, String receiverName) {
        Player currentPlayer = game.getPlayerByUsername(senderName);
        Player huggedPlayer = game.findPlayer(receiverName);

        PlayerFriendship playerFriendship = game.getFriendshipBetween(currentPlayer, huggedPlayer);
        currentPlayer.setAction(Player.Action.HUGGING);
        huggedPlayer.setAction(Player.Action.HUGGING);

        playerFriendship.setHadHuggedToday(true);
        playerFriendship.addXp(60);

    }

    public static void flower(Game game, String SenderName,String receiverName) {
        Player sender = game.getPlayerByUsername(SenderName);
        Player receiver = game.getPlayerByUsername(receiverName);
        Inventory inventory = sender.getComponent(Inventory.class);
        Inventory inventory2 = receiver.getComponent(Inventory.class);

        sender.setAction(Player.Action.GIVING_FLOWER);
        receiver.setAction(Player.Action.RECEIVING_FLOWER);

        PlayerFriendship playerFriendship = game.getFriendshipBetween(sender, receiver);



        if (playerFriendship.getLevel() == 2) {
            playerFriendship.setLevel(3);
            playerFriendship.setXp(0);
        }

        inventory.takeFromInventory("Bouquet", 1);
        inventory2.addItem(App.entityRegistry.makeEntity("Bouquet"), 1);
    }

}
