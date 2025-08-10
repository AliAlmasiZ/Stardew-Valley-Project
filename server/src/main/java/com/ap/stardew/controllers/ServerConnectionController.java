package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnectionThread;
import com.ap.stardew.app.ServerApp;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.dto.JSONMessage;
import io.jsonwebtoken.*;

public class ServerConnectionController {
    public static JSONMessage handleCommand(JSONMessage message, ClientConnectionThread connectionThread) {
        if (message.getType() == JSONMessage.Type.player_input_command) {
            String command = message.getFromBody("command");
            switch (command) {
                case "player_move" -> {
                    return connectionThread.playerController.handleWalk(message);
                }
                case "update_player_action" -> {
                    connectionThread.playerController.handleChangeAction(message);
                }

            }
        }
        if (message.getType() == JSONMessage.Type.lobby_command) {
            String command = message.getFromBody("command");
            switch (command) {
                case "fetch" -> {
                    return LobbyController.fetch();
                }
                case "host" -> {
                    return LobbyController.createLobby(message);
                }
                case "hostSavedGame" -> {
                    return LobbyController.createSavedGameLobby(message);
                }
                case "join" -> {
                    return LobbyController.joinLobby(message);
                }
                case "leave_lobby" -> {
                    return LobbyController.leaveLobby(message);
                }
                case "startGame" -> {
                    return LobbyController.startGame(message);
                }
                case "toggleReady" -> {
                    return LobbyController.toggleReady(message);
                }
                case "chooseMapRegion" -> {
                    return LobbyController.chooseMapRegion(message);
                }

                default -> {
                    return null;
                }
            }
        }
        if (message.getType() == JSONMessage.Type.command) {
            JSONMessage response = new JSONMessage(JSONMessage.Type.response);

            switch ((String) message.getFromBody("command")) {
                case "login" -> {
                    return login(message, connectionThread);
                }
                case "getUsername" -> {
                    return getUsernameCommand(message.getFromBody("token"));
                }
                case "game_reconnect_request" -> {
                    return GameController.handleGameReconnectRequest(message);
                }
                case "getSavedGames" -> {
                    return GameController.getSavedGames(message);
                }
                case "startInGameVote" -> {
                    return GameController.startInGameVote(message);
                }
                case "inGameVote" -> {
                    return GameController.handleInGameVote(message);
                }
            }

            return response;
        }
        if (message.getType() == JSONMessage.Type.trade) {
            String command = message.getFromBody("command");
            switch (command) {
                case "do_trade" -> {
                    connectionThread.playerController.doTrade(message);
                }
                default -> {
                    connectionThread.gameThread.sendTCP(message, message.getFromBody("receiver"));
                    return null;
                }
            }
        }
            throw new UnsupportedOperationException("didn't handle");
    }

    public static JSONMessage login(JSONMessage message, ClientConnectionThread connectionThread) {
        JSONMessage response = new JSONMessage(JSONMessage.Type.response);

        if(message.getFromBody("token") != null){
            String username = ServerApp.getUsername(message.getFromBody("token"));
            if(username == null){
                response.put("success", false);
                response.put("message", "failed to login with token");
                return response;
            }

            Account account = ServerApp.getAccountByUsername(username);

            if (account == null) {
                response.put("success", false);
                response.put("message", "username doesn't exist");
                return response;
            }

            connectionThread.setCurrentAccount(account);

            response.put("success", true);
            response.put("username", username);
            return response;
        }

        String username = message.getFromBody("username");
        String password = message.getFromBody("password");
        Account account = ServerApp.getAccountByUsername(username);

        if (account == null) {
            response.put("success", false);
            response.put("message", "username doesn't exist");
            return response;
        }

        if (!account.isPasswordCorrect(password)) {
            response.put("success", false);
            response.put("message", "incorrect password");
            return response;
        }

        String token = Jwts.builder().subject(username).signWith(ServerApp.key).compact();

        response.put("success", true);
        response.put("token", token);

        connectionThread.setCurrentAccount(account);

        return response;
    }

    private static JSONMessage getUsernameCommand(String token) {
        JSONMessage response = new JSONMessage(JSONMessage.Type.response);
        Claims payload;
        try {
            payload = ServerApp.jwtParser.parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            System.out.println(e);
            response.put("success", false);
            return response;
        }

        response.put("success", true);
        response.put("username", payload.getSubject());

        return response;
    }
}
