package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnectionThread;
import com.ap.stardew.app.ServerApp;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.GameSession;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.Result;
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

        return gameSession;
    }




}
