package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnectionThread;
import com.ap.stardew.app.GameThread;
import com.ap.stardew.app.ServerApp;
import com.ap.stardew.models.*;
import com.ap.stardew.models.dto.AccountInfo;
import com.ap.stardew.models.dto.SavedGameDetails;
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
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameController {
    public static GameSession createGame(List<AccountInfo> accountInfos){
        WorldMap worldMap = TiledMapUtils.loadWorldMapFromFile("./Content(unpacked)/Maps/untitled.tmx");

        Game game = new Game();
        game.setTodayWeather(Weather.SUNNY);
        game.setTomorrowWeather(Weather.SUNNY);
        game.setMainMap(worldMap);


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
        }

        game.initGame(null);
        initialCheats(game);



        return new GameSession(game);
    }

    public static JSONMessage handleGameReconnectRequest(JSONMessage req){
        JSONMessage response = new JSONMessage(JSONMessage.Type.response);

        String username = ServerApp.getUsername(req.getFromBody("token"));
        if (username == null){
            Result result = new Result(false, "Invalid token");
            response.put("result", result);
            return response;
        }

        boolean accepted = req.getFromBody("accepted");

        GameThread gameThread = null;
        for (GameThread game : ServerApp.getGames()) {
            for (String disconnectedClient : game.getDisconnectedClients()) {
                if (disconnectedClient.equals(username)) {
                    gameThread = game;
                    break;
                }
            }
        }
        if (gameThread == null){
            Result result = new Result(false, "No game to join to");
            response.put("result", result);
            return response;
        }

        return gameThread.handleReconnectRequest(username, accepted);
    }

    public static String generateSavePath(String baseDir) throws IOException {
        Files.createDirectories(Path.of(baseDir));

        String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Path folderPath = Path.of(baseDir, dateFolder);
        Files.createDirectories(folderPath);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String randomToken = UUID.randomUUID().toString().substring(0, 8);

        String fileName = "game_" + timestamp + "_" + randomToken + ".sav";
        return folderPath.resolve(fileName).toString();
    }

    public static void saveGame(GameSession gameSession) throws IOException, SQLException{
        String savePath = generateSavePath("./games");

        try (Output output = new Output(new FileOutputStream(savePath))) {
            ServerApp.getServer().getKryo().writeObject(output, gameSession.getGame());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save game session file", e);
        }

        DatabaseManager.saveGame(savePath, gameSession.getGame(), new ArrayList<>(gameSession.getUserPlayerMap().keySet()));
    }

    public static JSONMessage getSavedGames(JSONMessage req){
        JSONMessage response = new JSONMessage(JSONMessage.Type.response);

        String username = ServerApp.getUsername(req.getFromBody("token"));
        if (username == null){
            Result result = new Result(false, "Invalid token");
            response.put("result", result);
            return response;
        }

        List<SavedGameDetails> gamesByUser;
        try {
            gamesByUser = DatabaseManager.findGamesByUser(username);
        } catch (SQLException e) {
            Result result = new Result(false, "failed to retrieve saved games from the database: " + e);
            response.put("result", result);
            return response;
        }

        Result result = new Result(true, "found the games");
        response.put("result", result);
        response.put("games", gamesByUser);
        return response;
    }

    public static JSONMessage startInGameVote(JSONMessage message){
        return new JSONMessage(JSONMessage.Type.command);
    }

    public static JSONMessage handleInGameVote(JSONMessage message){
        return new JSONMessage(JSONMessage.Type.command);
    }

    public static GameSession loadGame(int gameId) throws SQLException, FileNotFoundException {
        String savedGamePath = DatabaseManager.getSavedGamePath(gameId);

        if(savedGamePath == null) return null;

        try (Input input = new Input(new FileInputStream(savedGamePath))) {
            GameSession gameSession = new GameSession(ServerApp.getServer().getKryo().readObject(input, Game.class));

            DatabaseManager.deleteGame(gameId);

            return gameSession;
        }
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
