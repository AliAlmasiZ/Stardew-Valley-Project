package com.ap.stardew.controllers;

import com.ap.stardew.models.JSONMessage;

public class ServerConnectionController {
    public static JSONMessage handleCommand(JSONMessage message) {
        if(message.getType() == JSONMessage.Type.player_input_command) {
            //TODO
        }
        return null;
    }
}
