package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnectionThread;
import com.ap.stardew.app.GameThread;
import com.ap.stardew.app.ServerApp;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.Lobby;
import com.ap.stardew.models.LobbyInfo;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.dto.AccountInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    static public JSONMessage startGame(JSONMessage req) {
        JSONMessage res = new JSONMessage(JSONMessage.Type.response);
        String lobbyId =  req.getFromBody("lobby_id");//TODO
        Lobby lobby = Lobby.getLobbyById(lobbyId);
        List<AccountInfo> accounts = lobby.getPlayers();
        //...


        List<ClientConnectionThread> clients = new ArrayList<>();
        for (AccountInfo account : accounts) {
            // TODO
            // add this account thread to List
        }
        GameThread gameThread = new GameThread(clients);
        gameThread.start();
        ServerApp.addGameThread(gameThread);


        // TODO: set game for all players
        return res;
    }
}
