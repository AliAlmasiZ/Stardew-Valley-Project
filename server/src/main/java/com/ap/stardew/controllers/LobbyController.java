package com.ap.stardew.controllers;

import com.ap.stardew.models.JSONMessage;
import com.ap.stardew.models.Lobby;

import java.util.ArrayList;

public class LobbyController {
    static public ArrayList<Lobby> fetch() {
        return Lobby.getAllLobbies();
    }

    //TODO
}
