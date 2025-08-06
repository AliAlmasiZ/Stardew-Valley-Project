package com.ap.stardew.app;

import com.ap.stardew.ClientGame;
import com.ap.stardew.models.App;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.views.GameScreen;
import com.ap.stardew.views.LobbyScreen;
import com.badlogic.gdx.Gdx;

import java.util.Map;

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
}
