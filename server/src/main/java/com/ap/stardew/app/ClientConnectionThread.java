package com.ap.stardew.app;

import com.ap.stardew.controllers.ServerConnectionController;
import com.ap.stardew.models.ConnectionThread;
import com.ap.stardew.models.JSONMessage;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.net.Socket;

public class ClientConnectionThread extends ConnectionThread {


    protected ClientConnectionThread(Socket socket) throws IOException {
        super(socket);
    }

    public ClientConnectionThread(Connection connection) throws IOException {
        super(connection);
    }

    @Override
    public boolean initialHandshake() {
        ServerApp.addClientConnection(this);
        return true;
    }

    @Override
    protected boolean handleMessage(JSONMessage message) {
        JSONMessage response = ServerConnectionController.handleCommand(message);
        if(response == null)
            return false;
        sendMessage(response);
        return true;
    }

    @Override
    public void run() {
        // this is for socket
//        super.run();

        connection.addListener(new Listener(){
            @Override
            public void received(Connection connection, Object object) {
                boolean handled = handleReceived(object);
                if(!handled) try {
                    receivedObjectsQueue.put(object);
                } catch (InterruptedException e) {
                    System.err.println("Error occurred in add object message to queue :");
                    System.err.println(e.getMessage());
                }
            }
        });

        while (!end.get()) {} // to keep thread alive
        ServerApp.removeClientConnection(this);
    }

    private boolean handleReceived(Object received) {
        if(received instanceof JSONMessage) {
            return handleMessage((JSONMessage) received);
        }
        return false;
    }
}
