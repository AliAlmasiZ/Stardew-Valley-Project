package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnection;
import com.ap.stardew.app.ClientConnection;
import com.ap.stardew.app.GameThread;
import com.ap.stardew.app.ServerApp;
import com.ap.stardew.models.*;
import com.ap.stardew.models.LobbyInfo;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.dto.AccountInfo;
import com.ap.stardew.models.dto.PlayerState;
import com.ap.stardew.models.dto.SavedGameDetails;
import com.ap.stardew.models.player.Player;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
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
        AccountInfo accountInfo = new AccountInfo(hostUsername);
        Lobby lobby = new Lobby(name, accountInfo, maxPlayers, password, isVisible);

        ClientConnection connection = ServerApp.getConnectionByUsername(hostUsername);

        connection.getGameConnection().addListener(new Listener(){
            @Override
            public void disconnected(Connection connection) {
                leaveLobby(lobby, accountInfo);
                connection.removeListener(this);
            }
        });

        JSONMessage response  = new JSONMessage(JSONMessage.Type.response);
        response.put("lobby_info", lobby.getLobbyInfo());

        return response;
    }
    static public JSONMessage createSavedGameLobby(JSONMessage request) {
        JSONMessage response = new JSONMessage(JSONMessage.Type.response);

        String hostUsername = request.getFromBody("host_username");
        int gameId = request.getFromBody("saved_game_id");

        SavedGameDetails savedGameDetails = null;
        try {
            savedGameDetails = DatabaseManager.getSavedGameDetails(gameId);
        } catch (SQLException e) {
            Result result = new Result(false, "faild to retrieve game data from server");
            response.put("result", result);
            return response;
        }

        AccountInfo accountInfo = new AccountInfo(hostUsername);
        Lobby lobby = new Lobby(hostUsername + "'s saved game", accountInfo, savedGameDetails);

        ClientConnection connection = ServerApp.getConnectionByUsername(hostUsername);

        if(connection == null){
            Result result = new Result(false, "wtf");
            response.put("result", result);
            return response;
        }

        connection.getGameConnection().addListener(new Listener(){
            @Override
            public void disconnected(Connection connection) {
                leaveLobby(lobby, accountInfo);
                connection.removeListener(this);
            }
        });

        response.put("lobby_info", lobby.getLobbyInfo());
        Result result = new Result(true, "created lobby");
        response.put("result", result);

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
        if(lobby.getSavedGameDetails() != null){
            if(!lobby.getSavedGameDetails().players.contains(username)){
                Result result = new Result(false, "This lobby is hosting a saved game which you didn't participate in.");
                response.put("result", result);
                return response;
            }
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

        AccountInfo accountInfo = new AccountInfo(username);
        if(lobby.getLobbyInfo().getSavedGameDetails() != null){
            accountInfo.setSelectedMapRegion(lobby.getSavedGameDetails().farms.get(username));
        }
        lobby.addAccountInfo(accountInfo);
        Result result = new Result(true, "you joined lobby " + lobbyId);
        response.put("result", result);
        response.put("lobby_info", lobby.getLobbyInfo());

        ClientConnection connection = ServerApp.getConnectionByUsername(username);

        connection.getGameConnection().addListener(new Listener(){
            @Override
            public void disconnected(Connection connection) {
                leaveLobby(lobby, accountInfo);
                connection.removeListener(this);
            }
        });

        updateClients(lobby, accountInfo.getUsername() + " joined the lobby");

        return response;
    }

    static public JSONMessage leaveLobby(JSONMessage request){
        JSONMessage response = new JSONMessage(JSONMessage.Type.response);

        String username = ServerApp.getUsername(request.getFromBody("token"));
        if (username == null){
            Result result = new Result(false, "Invalid token");
            response.put("result", result);
            return response;
        }

        String lobbyId = request.getFromBody("lobby_id");

        Lobby lobby = Lobby.getLobbyById(lobbyId);
        if(lobby == null) {
            Result result = new Result(false, "There is no lobby with this ID");
            response.put("result", result);
            return response;
        }

        AccountInfo requestedPLayer = null;
        for (AccountInfo player : lobby.getPlayers()) {
            if(player.getUsername().equals(username)){
                requestedPLayer = player;
                break;
            }
        }
        if(requestedPLayer == null){
            Result result = new Result(false, "No player with that id is in the lobby");
            response.put("result", result);
            return response;
        }

        return leaveLobby(lobby, requestedPLayer);
    }
    static public JSONMessage leaveLobby(Lobby lobby, AccountInfo requestedPLayer) {
        JSONMessage response = new JSONMessage(JSONMessage.Type.response);

        if(!lobby.getPlayers().contains(requestedPLayer)){
            Result result = new Result(true, "invalid");
            response.put("result", result);
            return response;
        }

        if(lobby.getHostUsername().equals(requestedPLayer.getUsername())){
            if(lobby.getPlayers().size() > 1){
                lobby.setHostUsername(lobby.getPlayers().get(1).getUsername());
            }else{
                lobby.close();
            }
        }

        lobby.getPlayers().remove(requestedPLayer);
        updateClients(lobby, requestedPLayer.getUsername() + " left the lobby");

        Result result = new Result(true, "left the lobby");
        response.put("result", result);
        return response;
    }

    static public void updateClients(Lobby lobby, String message){
        JSONMessage command = new JSONMessage(JSONMessage.Type.update);
        command.put("command", "updateLobby");
        command.put("lobby_info", lobby.getLobbyInfo());
        command.put("message", message);

        for (AccountInfo account : lobby.getPlayers()) {
            ClientConnection connection = ServerApp.getConnectionByUsername(account.getUsername());
            if(connection != null){
                connection.sendGameTCP(command);
            }
        }
    }

    static public JSONMessage toggleReady(JSONMessage req){
        JSONMessage response = new JSONMessage(JSONMessage.Type.response);

        String username = ServerApp.getUsername(req.getFromBody("token"));
        if (username == null){
            Result result = new Result(false, "Invalid token");
            response.put("result", result);
            return response;
        }

        String lobbyId = req.getFromBody("lobby_id");

        Lobby lobby = Lobby.getLobbyById(lobbyId);
        if(lobby == null) {
            Result result = new Result(false, "There is no lobby with this ID");
            response.put("result", result);
            return response;
        }

        AccountInfo requestedPLayer = null;
        for (AccountInfo player : lobby.getPlayers()) {
            if(player.getUsername().equals(username)){
                requestedPLayer = player;
                break;
            }
        }
        if(requestedPLayer == null){
            Result result = new Result(false, "No player with that id is in the lobby");
            response.put("result", result);
            return response;
        }

        requestedPLayer.setReady(!requestedPLayer.isReady());

        updateClients(lobby, null);

        Result result = new Result(true, "success");
        response.put("result", result);
        return response;
    }

    static public JSONMessage chooseMapRegion(JSONMessage req){
        JSONMessage response = new JSONMessage(JSONMessage.Type.response);

        String username = ServerApp.getUsername(req.getFromBody("token"));
        if (username == null){
            Result result = new Result(false, "Invalid token");
            response.put("result", result);
            return response;
        }

        String lobbyId = req.getFromBody("lobby_id");

        Lobby lobby = Lobby.getLobbyById(lobbyId);
        if(lobby == null) {
            Result result = new Result(false, "There is no lobby with this ID");
            response.put("result", result);
            return response;
        }

        AccountInfo requestedPLayer = null;
        for (AccountInfo player : lobby.getPlayers()) {
            if(player.getUsername().equals(username)){
                requestedPLayer = player;
                break;
            }
        }
        if(requestedPLayer == null){
            Result result = new Result(false, "No player with that id is in the lobby");
            response.put("result", result);
            return response;
        }

        String selectedMapRegion = req.getFromBody("mapRegion");

        for (AccountInfo player : lobby.getPlayers()) {
            if(player.getSelectedMapRegion() != null && player.getSelectedMapRegion().equals(selectedMapRegion)){
                if(player == requestedPLayer){
                    player.setSelectedMapRegion(null);
                    Result result = new Result(true, "de selected the farm");
                    response.put("result", result);

                    updateClients(lobby, null);
                }else{
                    Result result = new Result(false, "map region already selected by " + player.getUsername());
                    response.put("result", result);
                }
                return response;
            }
        }

        requestedPLayer.setSelectedMapRegion(selectedMapRegion);
        Result result = new Result(true, "selected the farm");
        response.put("result", result);

        updateClients(lobby, null);

        return response;
    }

    static public JSONMessage startGame(JSONMessage req) {
        JSONMessage res = new JSONMessage(JSONMessage.Type.response);
        System.out.println("starting the game");

        String username = ServerApp.getUsername(req.getFromBody("token"));
        if(username == null){
            Result result = new Result(false, "invalid token");
            res.put("result", result);
            return res;
        }

        Lobby lobby = Lobby.getLobbyById(req.getFromBody("lobby_id"));
        if(lobby == null){
            Result result = new Result(false, "lobby doesn't exist");
            res.put("result", result);
            return res;
        }

        if(!lobby.getHostUsername().equals(username)){
            Result result = new Result(false, "only the host can startGameConnection the game");
            res.put("result", result);
            return res;
        }

        List<AccountInfo> accounts = lobby.getPlayers();
        for (AccountInfo account : accounts) {
            if(!account.getUsername().equals(lobby.getHostUsername()) && !account.isReady()){
                Result result = new Result(false, "all players should be ready!");
                res.put("result", result);
                return res;
            }
            if(account.getSelectedMapRegion() == null){
                Result result = new Result(false, "all players should select a farm!");
                res.put("result", result);
                return res;
            }
        }

        GameSession session;
        if(lobby.getSavedGameDetails() == null){
            session = GameController.createGame(accounts);
        }else{
            try {
                session = GameController.loadGame(lobby.getSavedGameDetails().gameId);

                if(session == null) throw new RuntimeException();
            } catch (Exception e){
                System.out.println(e);
                Result result = new Result(false, "failed to load the game");
                res.put("result", result);
                return res;
            }
        }

        GameThread gameThread = new GameThread(session);

        for (ClientConnection client : gameThread.getClients()) {
            client.player = session.getUserPlayerMap().get(client.getCurrentAccount().getUsername());
            client.playerController = new PlayerController(client);

            JSONMessage gameStartDetails = new JSONMessage(JSONMessage.Type.update);
            gameStartDetails.put("command", "startGame");
            gameStartDetails.put("lobby_id", lobby.getLobbyId());
            gameStartDetails.put("gameData", session.getGame());
            gameStartDetails.put("player", session.getUserPlayerMap().get(client.getCurrentAccount().getUsername()));
            client.sendGameTCP(gameStartDetails);
        }

        ServerApp.addGameThread(gameThread);
        gameThread.start();

        res.put("result", new Result(true, "game started"));
        return res;
    }
}
