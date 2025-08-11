package com.ap.stardew.app;

import com.ap.stardew.controllers.PlayerController;
import com.ap.stardew.controllers.ServerConnectionController;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.player.Player;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientConnection {
    ExecutorService gameExecutor = Executors.newSingleThreadExecutor();
    ExecutorService audioExecutor = Executors.newSingleThreadExecutor();
    protected final BlockingQueue<Object> receivedObjectsQueue;
    private Connection gameConnection;
    private Connection audioConnection;
    private Account currentAccount = null;

    public PlayerController playerController;
    public Player player;
    public GameThread gameThread;


    public ClientConnection(Connection gameConnection) throws IOException {
        this.receivedObjectsQueue = new LinkedBlockingQueue<>();
        this.gameConnection = gameConnection;


    }

    public boolean initialHandshake() {
        ServerApp.addClientConnection(this);
        return true;
    }


    protected boolean handleMessage(JSONMessage message) {
        try {

            Object response = ServerConnectionController.handleCommand(message, this);
            //for Socket
    //        sendMessage(response);
            if(response != null)
                sendTCP(response);
            return true;
        } catch (UnsupportedOperationException notHandled) {
            return false;
        }
    }


    public void startGameConnection() {
        gameConnection.addListener(new Listener(){
            @Override
            public void received(Connection connection, Object object) {
                gameExecutor.submit(() -> {
                    boolean handled = handleReceived(object);
                    if(!handled) try {
                        receivedObjectsQueue.put(object);
                    } catch (InterruptedException e) {
                        System.err.println("Error occurred in add object message to queue :");
                        System.err.println(e.getMessage());
                    }
                });
            }
        });

    }

    private boolean handleReceived(Object received) {
        if(received instanceof JSONMessage) {
            return handleMessage((JSONMessage) received);
        }
        if(received instanceof FrameworkMessage.KeepAlive) {
            return true;
        }
        return false;
    }

    public synchronized void sendTCP(Object object) {
        gameConnection.sendTCP(object);
    }
    public synchronized void sendUDP(Object object) {
        gameConnection.sendUDP(object);
    }

    public void update(float delta) {
        playerController.update(delta);
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }



    public Connection getGameConnection() {
        return gameConnection;
    }

    public Connection getAudioConnection() {
        return audioConnection;
    }

    public void setAudioConnection(Connection audioConnection) {
        this.audioConnection = audioConnection;
    }

    public void setAudioConnectionListener() {
        if (audioConnection == null)
            throw new IllegalStateException("You should set a audioConnection first");
        audioConnection.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                audioExecutor.submit(() -> {
                    // TODO : handle audio commands
                });
            }
        });
    }

    public void end() {
        gameConnection.close();
        audioConnection.close();
        gameExecutor.shutdown();
        audioExecutor.shutdown();
    }

}
