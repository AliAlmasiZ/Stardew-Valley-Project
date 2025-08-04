package com.ap.stardew.controllers;

import com.ap.stardew.GameServer;
import com.ap.stardew.app.ClientConnectionThread;
import com.ap.stardew.app.ServerApp;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.ConnectionThread;
import com.ap.stardew.models.JSONMessage;
import com.ap.stardew.models.Result;
import com.badlogic.gdx.utils.Json;
import io.jsonwebtoken.*;

import java.util.Date;

public class ServerConnectionController {
    public static Object handleCommand(JSONMessage message, ClientConnectionThread connectionThread) {
        if(message.getType() == JSONMessage.Type.player_input_command) {
            //TODO
        }
        if(message.getType() == JSONMessage.Type.lobby_command) {
            String command =  message.getFromBody("command");
            switch (command) {
                case "fetch" -> {
                    return LobbyController.fetch();
                }
                case "host" -> {
                    return LobbyController.createLobby(message);
                }
                case "join" -> {
                    return LobbyController.joinLobby(message);
                }
                case "leave_lobby" -> {

                }
                default -> {
                    return null;
                }
            }
        }
        if(message.getType() == JSONMessage.Type.command){
            JSONMessage response = new JSONMessage(JSONMessage.Type.response);

            switch ((String) message.getFromBody("command")){
                case "login" -> {
                    return login(message.getFromBody("username"), message.getFromBody("password"), connectionThread);
                }
                case "getUsername" -> {
                    return getUsername(message.getFromBody("token"));
                }
            }

            return response;
        }
        return null;
    }

    public static JSONMessage login(String username, String password, ClientConnectionThread connectionThread){
        Account account = ServerApp.getAccountByUsername(username);
        JSONMessage response = new JSONMessage(JSONMessage.Type.response);

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

    public static JSONMessage getUsername(String token){
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

        System.out.println(payload.getSubject());

        return response;
    }
}
