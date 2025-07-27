package com.ap.stardew.app;

import com.ap.stardew.controllers.ServerConnectionController;
import com.ap.stardew.models.ConnectionThread;
import com.ap.stardew.models.JSONMessage;

import java.io.IOException;
import java.net.Socket;

public class ClientConnectionThread extends ConnectionThread {


    protected ClientConnectionThread(Socket socket) throws IOException {
        super(socket);
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
        super.run();
        ServerApp.removeClientConnection(this);
    }
}
