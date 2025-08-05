package com.ap.stardew.app;

import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.dto.PlayerState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameThread extends Thread{
    static public final int UPDATE_FREQUENCY = 30;
    private float lastUpdateSent = 0;
    private float stateTime = 0;
    private ArrayList<ClientConnectionThread> clients;
    private AtomicBoolean end = new AtomicBoolean(false);


    public GameThread(ClientConnectionThread... clients) {
        this.clients = new ArrayList<>(Arrays.asList(clients));
    }

    public GameThread(List<ClientConnectionThread> clients) {
        this.clients = new ArrayList<>(clients);
    }


    @Override
    public void run() {
        long lastTime = System.nanoTime();
        while (!end.get()) {
            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastTime) / 1_000_000_000f; // Convert to seconds
            lastTime = currentTime;
            stateTime += deltaTime;



            update(deltaTime);


        }
    }



    private void update(float delta) {
        for (ClientConnectionThread client : clients) {
            client.update(delta);
        }
        sendUpdates(delta);
    }

    private void sendUpdates(float delta) {
        if(lastUpdateSent + delta < (float) 60 / UPDATE_FREQUENCY)
            return;
        lastUpdateSent = stateTime;

        JSONMessage message = new JSONMessage(JSONMessage.Type.update);
        message.put("update", "update_players");
        message.put("player_states", getPlayerStates());
        sendAllUDP(message);
    }

    private ArrayList<PlayerState> getPlayerStates() {
        ArrayList<PlayerState> states = new ArrayList<>();
        for (ClientConnectionThread client : clients) {
            states.add(client.player.getPlayerState());
        }
        return states;
    }

    public void sendAllTCP(Object object) {
        for (ClientConnectionThread client : clients) {
            client.sendTCP(object);
        }
    }

    public void sendAllUDP(Object object) {
        for (ClientConnectionThread client : clients) {
            client.sendUDP(object);
        }
    }
}
