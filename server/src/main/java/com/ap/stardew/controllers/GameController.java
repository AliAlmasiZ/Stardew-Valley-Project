package com.ap.stardew.controllers;

import com.ap.stardew.models.Game;
import com.ap.stardew.models.GameSession;
import com.ap.stardew.models.dto.AccountInfo;
import com.ap.stardew.models.enums.Weather;
import com.ap.stardew.models.gameMap.WorldMap;
import com.ap.stardew.models.player.Player;
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

        int i = 0;
        for (AccountInfo accountInfo : accountInfos) {
            Player player = new Player();
            game.addPlayer(player);

            gameSession.addUserToSession(accountInfo.getUsername(), player);

            player.setPosition(55 * 16 + i * 32, 86 * 16);
            player.setCurrentMap(game.getMainMap());
            i++;
        }

        return gameSession;
    }
}
