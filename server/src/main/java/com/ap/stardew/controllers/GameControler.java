package com.ap.stardew.controllers;

import com.ap.stardew.models.Game;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.gameMap.WorldMap;

public class GameControler {
    public JSONMessage createGame(){
        WorldMap worldMap = new WorldMap("./Content(unpacked)/Maps/untitled.tmx");

        Game game = new Game();
        game.setMainMap(worldMap);


        return null;
    }
}
