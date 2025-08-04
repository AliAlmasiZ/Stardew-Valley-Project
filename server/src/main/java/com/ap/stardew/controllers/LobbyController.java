package com.ap.stardew.controllers;

import com.ap.stardew.models.JSONMessage;
import com.ap.stardew.models.Lobby;
import com.ap.stardew.models.LobbyInfo;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.dto.AccountInfo;

import java.util.ArrayList;

public class LobbyController {
    static public JSONMessage fetch() {
        ArrayList<LobbyInfo> lobbyInfos = new ArrayList<>();
        for (Lobby lobby : Lobby.getAllLobbies()) {
            if(lobby.isVisible())
                lobbyInfos.add(lobby.getLobbyInfo());
        }
        JSONMessage response = new JSONMessage(JSONMessage.Type.response);
        response.put("lobby_infos", lobbyInfos);
        return response;
    }

    static public JSONMessage createLobby(JSONMessage request) {
        String name = request.getFromBody("lobby_name");
        String hostUsername = request.getFromBody("host_username");
        int maxPlayers = request.getFromBody("max_players");
        String password = request.getFromBody("password");
        boolean isVisible = request.getFromBody("is_visible");
        Lobby lobby = new Lobby(name, hostUsername, maxPlayers, password, isVisible);

        JSONMessage response  = new JSONMessage(JSONMessage.Type.response);
        response.put("lobby_info", lobby.getLobbyInfo());

        return response;
    }

    static public JSONMessage joinLobby(JSONMessage request) {
        JSONMessage response = new JSONMessage(JSONMessage.Type.response);

        String lobbyId = request.getFromBody("lobby_id");
        String password = request.getFromBody("password");
        String username = request.getFromBody("username");

        Lobby lobby = Lobby.getLobbyById(lobbyId);
        if(lobby == null) {
            Result result = new Result(false, "There is no lobby with this ID");
            response.put("result", result);
            return response;
        }
        else if(lobby.getCurrentPlayers() >= lobby.getMaxPlayers()) {
            Result result = new Result(false, "Lobby is full");
            response.put("result", result);
            return response;
        }
        else if(lobby.isPrivate()) {
            if(password == null || !password.equals(lobby.getPassword())) {
                Result result = new Result(false, "Password is incorrect");
                response.put("result", result);
                return response;
            }
        }
        lobby.addAccountInfo(new AccountInfo(username));
        Result result = new Result(true, "you joined lobby " + lobbyId);
        response.put("result", result);
        response.put("lobby_info", lobby.getLobbyInfo());

        return response;
    }
}
