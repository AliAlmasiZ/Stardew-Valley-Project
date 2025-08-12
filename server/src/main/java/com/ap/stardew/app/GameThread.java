package com.ap.stardew.app;

import com.ap.stardew.controllers.DatabaseManager;
import com.ap.stardew.controllers.GameController;
import com.ap.stardew.controllers.PlayerController;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.GameSession;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.dto.PlayerState;
import com.ap.stardew.models.player.Player;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameThread extends Thread{
    static public final int UPDATE_FREQUENCY = 30;
    private float lastUpdateSent = 0;
    private float stateTime = 0;
    private float deltaTime;
    private ArrayList<ClientConnection> clients = new ArrayList<>();
    private final List<String> disconnectedClients = new ArrayList<>();
    private final List<String> playersWithSentReconnectionRequests = new ArrayList<>(); // sorry (*^ω^*)
    private AtomicBoolean end = new AtomicBoolean(false);
    private final GameSession gameSession;
    private float timeLeftToTermination;

    private AtomicBoolean gamePaused = new AtomicBoolean(false);

    public GameThread(GameSession gameSession) {
        this.gameSession = gameSession;

        for (Map.Entry<String, Player> entry : gameSession.getUserPlayerMap().entrySet()) {
            ClientConnection clientConnection = ServerApp.getConnectionByUsername(entry.getKey());
            clients.add(clientConnection);

            System.out.println("client " + entry.getKey() + " added to game");
        }
    }

    @Override
    public void start() {
        for (ClientConnection client : clients) {
            client.gameThread = this;
        }
        super.start();
    }

    @Override
    public void run() {
        long lastTime = System.currentTimeMillis();
        while (!end.get()) {
            long currentTime = System.currentTimeMillis();
            deltaTime = (currentTime - lastTime) / 1000f; // Convert to seconds
            lastTime = currentTime;
            stateTime += deltaTime;

            update(deltaTime);
        }
    }

    private void update(float delta) {
        if(gamePaused.get()){
            if(stateTime - lastUpdateSent < 0.2f){
                return;
            }
            lastUpdateSent = stateTime;
            for (int i = clients.size() - 1; i >= 0; i--) {
                ClientConnection client = clients.get(i);
                if(!client.getGameConnection().isConnected()){
                    handleUserDisconnection(client);
                }
            }
            System.out.println("game is paused");
            synchronized (disconnectedClients) {
                for (String disconnectedClient : disconnectedClients) {
                    ClientConnection connection = ServerApp.getConnectionByUsername(disconnectedClient);
                    if(connection != null && connection.getGameConnection().isConnected()){
                        if(playersWithSentReconnectionRequests.contains(disconnectedClient)) continue;

                        JSONMessage req = new JSONMessage(JSONMessage.Type.command);
                        req.put("command", "game_reconnect_request");
                        connection.sendGameTCP(req);

                        playersWithSentReconnectionRequests.add(disconnectedClient);
                    }
                }
            }
            timeLeftToTermination -= 0.2f;
            if(timeLeftToTermination <= 0){
                saveGame();
                endGame();

                JSONMessage gameSavedMessage = new JSONMessage(JSONMessage.Type.update);
                gameSavedMessage.put("command", "gameSaved");
                gameSavedMessage.put("message", "the game was saved");
                sendAllTCP(gameSavedMessage);

                return;
            }
        }else {
            for (int i = clients.size() - 1; i >= 0; i--) {
                ClientConnection client = clients.get(i);
                if(!client.getGameConnection().isConnected()){
                    handleUserDisconnection(client);
                    gamePaused.set(true);
                    timeLeftToTermination = 120f;
                    return;
                }
                client.update(delta);
            }
            sendUpdates();
        }
    }

    private void handleUserDisconnection(ClientConnection disconnectedClient){
        clients.remove(disconnectedClient);

        JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.update);
        jsonMessage.put("command", "player_disconnected");
        synchronized (disconnectedClients) {
            disconnectedClients.add(disconnectedClient.getCurrentAccount().getUsername());
            jsonMessage.put("usernames", new ArrayList<>(disconnectedClients));
        }
        jsonMessage.put("timeLeft", timeLeftToTermination);
        sendAllTCP(jsonMessage);
    }

    private void sendUpdates() {

        if(stateTime - lastUpdateSent < 2)
            return;
        lastUpdateSent = stateTime;


//        JSONMessage message = new JSONMessage(JSONMessage.Type.update);
//
//        message.put("command", "update_players");
//        message.put("player_states", getPlayerStates());
//        sendAllTCP(message);
        //update sent to all
    }

    private ArrayList<PlayerState> getPlayerStates() {
        ArrayList<PlayerState> states = new ArrayList<>();
        for (ClientConnection client : clients) {
            states.add(client.player.getPlayerState());
        }
        return states;
    }

    public void sendAllTCP(Object object) {
        for (ClientConnection client : clients) {
            client.sendGameTCP(object);
        }
    }

    public void sendTCP(JSONMessage message, String ... username) {
        for (ClientConnection client : clients) {
            for (String user : username) {
                if (client.player.getUsername().equals(user)) {
                    client.sendGameTCP(message);
                }
            }
        }
    }

    public void sendAllUDP(Object object) {
        for (ClientConnection client : clients) {
            client.sendGameUDP(object);
        }
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public ArrayList<ClientConnection> getClients() {
        return clients;
    }

    public ClientConnection getClientByUsername(String username) {
        for (ClientConnection client : clients) {
            if (client.player.getUsername().equals(username)) return client;
        }
        return null;
    }

    public Game getGame() {
        return gameSession.getGame();
    }

    public List<String> getDisconnectedClients() {
        return disconnectedClients;
    }

    public JSONMessage handleReconnectRequest(String username, boolean accepted){
        if(!accepted){
            saveGame();
            endGame();

            JSONMessage gameSavedMessage = new JSONMessage(JSONMessage.Type.update);
            gameSavedMessage.put("command", "gameSaved");
            gameSavedMessage.put("message", username + "didn't connect. the game was saved");
            sendAllTCP(gameSavedMessage);

            JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.response);
            jsonMessage.put("result", new Result(false, "Game was saved"));
            return jsonMessage;
        }

        ClientConnection connection = ServerApp.getConnectionByUsername(username);

        if(connection == null){
            JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.response);
            jsonMessage.put("result", new Result(false, "wtf"));
            return jsonMessage;
        }

        //clean up the disconnected users information
        synchronized (disconnectedClients){
            for (int i = disconnectedClients.size() - 1; i >= 0; i--) {
                if(disconnectedClients.get(i).equals(username)){
                    disconnectedClients.remove(i);
                    break;
                }
            }
            playersWithSentReconnectionRequests.remove(username);

            //check whether the game should resume
            JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.update);
            if(disconnectedClients.isEmpty()){
                jsonMessage.put("command", "resume_game");
                gamePaused.set(false);
            }else {
                jsonMessage.put("command", "player_disconnected");
                jsonMessage.put("usernames", disconnectedClients);
            }
            sendAllTCP(jsonMessage);
            //add the client to the game
            clients.add(connection);
            connection.gameThread = this;
            connection.player = gameSession.getUserPlayerMap().get(username);
            connection.playerController = new PlayerController(connection);

            JSONMessage gameStartDetails = new JSONMessage(JSONMessage.Type.response);
            gameStartDetails.put("result", new Result(true, "reconnecting"));
            gameStartDetails.put("gameData", gameSession.getGame());
            gameStartDetails.put("player", gameSession.getUserPlayerMap().get(username));
            gameStartDetails.put("gamePaused", gamePaused.get());
            synchronized (disconnectedClients){
                if(gamePaused.get()){
                    gameStartDetails.put("usernames", disconnectedClients);
                    gameStartDetails.put("timeLeft", timeLeftToTermination);
                }
            }

            return gameStartDetails;
        }
    }

    public void saveGame(){
        try {
            GameController.saveGame(gameSession);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void endGame(){
        end.set(true);

        for (ClientConnection client : clients) {
            client.gameThread = null;
            client.playerController = null;
            client.player = null;
        }
    }

    public void end() {
        end.set(true);
    }
}
