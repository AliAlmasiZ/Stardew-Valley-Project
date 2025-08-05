package com.ap.stardew.app;

import com.ap.stardew.models.App;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.player.Player;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.FrameworkMessage;

public class ClientConnectionController {
    public static JSONMessage handleCommand(JSONMessage message) {
        if(message.getType() == JSONMessage.Type.update) {
            String update = message.getFromBody("update");
            switch (update) {
                case "player_move" -> { //players movement update
                    String username = message.getFromBody("username");
                    float delta = message.getFromBody("delta_time");
                    Vector2 direction = message.getFromBody("direction");

                    if(ClientApp.getUsername().equals(username)) {
                        return null;
                    }
                    Player player = App.getActiveGame().getPlayerByUsername(username);
                    player.move(direction, delta);
                }
                case "update_players" -> {

                }
            }
        }
        throw new UnsupportedOperationException(); // for messages cant handle
    }
}
