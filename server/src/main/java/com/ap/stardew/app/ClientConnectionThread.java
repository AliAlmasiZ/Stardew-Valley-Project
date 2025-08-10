package com.ap.stardew.app;

import com.ap.stardew.controllers.PlayerController;
import com.ap.stardew.controllers.ServerConnectionController;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.ConnectionThread;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.player.Player;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.net.Socket;

public class ClientConnectionThread extends ConnectionThread {
    private Account currentAccount = null;

    public PlayerController playerController;
    public Player player;
    public GameThread gameThread;

    public ClientConnectionThread(Connection connection) throws IOException {
        super(connection);
    }

    @Override
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

    @Override
    public void run() {
        // this is for socket
        super.run();


        ServerApp.removeClientConnection(this);
    }

    @Override
    protected boolean handleReceived(Object received) {
        if(received instanceof JSONMessage) {
            return handleMessage((JSONMessage) received);
        }
        if(received instanceof FrameworkMessage.KeepAlive) {
            return true;
        }
        return false;
    }

    public void update(float delta) {
        try {
            playerController.update(delta);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }
}
