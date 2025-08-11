package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnectionThread;
import com.ap.stardew.app.ServerApp;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.App;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.player.Player;
import io.jsonwebtoken.*;

import java.util.ArrayList;
import java.util.Random;

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
                case "suggest_username" -> {
                    return suggestUsername(message.getFromBody("username"));
                }
                case "login" -> {
                    return login(message, connectionThread);
                }
                case "register" -> {
                    ArrayList<Account> accounts = new ArrayList<>();
                    accounts.add(message.getFromBody("account"));
                    ServerApp.saveAccounts(accounts);
                    connectionThread.setCurrentAccount(accounts.get(0));
                    return null;
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
                    return null;
                }
                default -> {
                    connectionThread.gameThread.sendTCP(message, message.getFromBody("receiver"));
                    return null;
                }
            }
        }
        if (message.getType() == JSONMessage.Type.chat) {
            String command = message.getFromBody("command");
            switch (command) {
                case "send_private_message" -> {
                    connectionThread.playerController.sendPrivateMessage(message);
                    return null;
                }
                case "send_public_message" -> {
                    connectionThread.playerController.sendPublicMessage(message);
                    return null;
                }
            }
        }
        if (message.getType() == JSONMessage.Type.update) {
            String command = message.getFromBody("command");
            switch (command) {
                case "gift_player" -> {
                    connectionThread.playerController.giftPlayer(message);
                    return null;
                }
                case "rate_gift" -> {
                    connectionThread.playerController.rateGift(message);
                    return null;
                }
                case "meet_npc" -> {
                    GameController.meetNPC(connectionThread.gameThread.getGame(), connectionThread.player, message.getFromBody("npc_name"));
                }
            }
        }
        if (message.getType() == JSONMessage.Type.request) {
            switch ((String) message.getFromBody("command")) {
                case "hug" -> {
                    connectionThread.playerController.hug(message);
                    return null;
                }
                case "flower" -> {
                    connectionThread.playerController.flower(message);
                    return null;
                }
                case "ask_marriage" -> {
                    connectionThread.playerController.askMarriage(message);
                    return null;
                }
                case "accept_marriage" -> {
                    connectionThread.playerController.acceptMarriage(message);
                    return null;
                }
                case "reject_marriage" -> {
                    connectionThread.playerController.rejectMarriage(message);
                    return null;
                }
            }
        }
        if (message.getType() == JSONMessage.Type.cheat) {
            switch ((String) message.getFromBody("command")) {
                case "energy" -> {
                    Game game = connectionThread.gameThread.getGame();
                    Player player = game.getPlayerByUsername(message.getFromBody("sender"));

                    player.getEnergy().addEnergy(message.getFromBody("amount"));

                    JSONMessage response = new JSONMessage(JSONMessage.Type.update);
                    response.put("command", "update_players_fields");
                    response.put("energy", player.getEnergy());
                    response.put("username", player.getUsername());

                    connectionThread.gameThread.sendTCP(message, player.getUsername());
                    return null;
                }
                case "friendship" -> {
                    connectionThread.playerController.cheatSetFriendship(message);
                    return null;
                }
                case "give_item" -> {
                    GameStaticController.cheatGiveItem(connectionThread.gameThread.getGame(),
                        connectionThread.player,
                        message.getFromBody("name"),
                        message.getIntFromBody("amount"));
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

    /******************************** for signup menu *******************************************/
    private static JSONMessage suggestUsername(String username) {
        JSONMessage response = new JSONMessage(JSONMessage.Type.response);
        if (ServerApp.getAccountByUsername(username) == null) {
            response.put("success", true);
            return response;
        }

        StringBuilder newUsername = new StringBuilder(username);
        Random rand = new Random();
        while (ServerApp.getAccountByUsername(newUsername.toString()) != null) {
            int randomNumber = rand.nextInt() % 11;
            if (randomNumber == 10) {
                newUsername.append("-");
            } else {
                newUsername.append(randomNumber);
            }
        }

        response.put("success", false);
        response.put("username", newUsername.toString());

        return response;
    }
}
