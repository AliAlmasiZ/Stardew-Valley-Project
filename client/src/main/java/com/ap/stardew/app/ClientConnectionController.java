package com.ap.stardew.app;

import com.ap.stardew.ClientGame;
import com.ap.stardew.models.JSONMessage;
import com.ap.stardew.models.Result;
import com.ap.stardew.views.LobbyScreen;
import com.ap.stardew.views.MultiplayerScreen;

public class ClientConnectionController {
    public static JSONMessage handleCommand(JSONMessage message) {
        System.out.println(message.getBody());
        if(message.getType() == JSONMessage.Type.update){
            switch (message.getFromBody("command", String.class)){
                case "updateLobby" -> {
                    if (ClientGame.getInstance().getScreen() instanceof LobbyScreen lobbyScreen){
                        lobbyScreen.updateLobbyState(message.getFromBody("lobby_info"));
                    }
                }
            }
        }
        return null;
    }
}
