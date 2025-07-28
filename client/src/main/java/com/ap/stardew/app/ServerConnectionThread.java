package com.ap.stardew.app;

import com.ap.stardew.models.ConnectionThread;
import com.ap.stardew.models.JSONMessage;

import java.io.IOException;
import java.net.Socket;

import static com.ap.stardew.app.ClientApp.TIMEOUT_MILLIS;

public class ServerConnectionThread extends ConnectionThread {
    protected ServerConnectionThread(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public boolean initialHandshake() {
        try {
            socket.setSoTimeout(TIMEOUT_MILLIS);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    protected boolean handleMessage(JSONMessage message) {
        JSONMessage response = ClientConnectionController.handleCommand(message);
        if(response == null)
            return false;
        sendMessage(response);
        return true;
    }
}
