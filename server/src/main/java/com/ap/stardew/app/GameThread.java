package com.ap.stardew.app;

import com.ap.stardew.models.Game;
import com.ap.stardew.models.GameSession;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.dto.PlayerState;
import com.ap.stardew.models.player.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameThread extends Thread{
    static public final int UPDATE_FREQUENCY = 30;
    private float lastUpdateSent = 0;
    private float stateTime = 0;
    private float deltaTime;
    private ArrayList<ClientConnection> clients = new ArrayList<>();
    private AtomicBoolean end = new AtomicBoolean(false);
    private final GameSession gameSession;

    public GameThread(GameSession gameSession) {
        this.gameSession = gameSession;

        for (Map.Entry<String, Player> entry : gameSession.getUserPlayerMap().entrySet()) {
            clients.add(ServerApp.getConnectionByUsername(entry.getKey()));
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
        for (ClientConnection client : clients) {
            client.update(delta);
        }
        sendUpdates();
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

    public void sendTCP(JSONMessage message, String username) {
        for (ClientConnection client : clients) {
            if (client.player.getUsername().equals(username)) {
                client.sendGameTCP(message);
                return;
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

    public void end() {
        end.set(true);
    }
}
