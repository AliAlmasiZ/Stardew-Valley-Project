package com.ap.stardew.app;

import java.util.ArrayList;
import java.util.List;

import static com.ap.stardew.GameServer.PORT;

public class ServerApp {
    public static final int TIMEOUT_MILLIS = 500;
    private static final ArrayList<ClientConnectionThread> connections = new ArrayList<>();
    private static boolean exitFlag = false;
    private static ListenerThread listenerThread;

    public static ClientConnectionThread getConnectionByIpPort(String ip, int port) {
        for (ClientConnectionThread connection : connections) {
            if(connection.getOtherSideIP().equals(ip) && connection.getOtherSidePort() == port) {
                return connection;
            }
        }
        return null;
    }

    public static boolean isEnded() {
        return exitFlag;
    }

    public static void setListenerThread(ListenerThread listenerThread) {
        ServerApp.listenerThread = listenerThread;
    }

    public static List<ClientConnectionThread> getConnections() {
        return List.copyOf(ServerApp.connections);
    }

    public static void startListening() {
        if (listenerThread != null && !listenerThread.isAlive()) {
            listenerThread.start();

        } else {
            throw new IllegalStateException("Listener thread is already running or not set.");
        }
    }

    public static void endAll() {
        exitFlag = true;
        for (ClientConnectionThread connection : connections)
            connection.end();
        connections.clear();
    }

    public static void removeClientConnection(ClientConnectionThread clientConnectionThread) {
        if (clientConnectionThread != null) {
            connections.remove(clientConnectionThread);
            clientConnectionThread.end();
        }
    }

    public static void addClientConnection(ClientConnectionThread clientConnectionThread) {
        if (clientConnectionThread != null && !connections.contains(clientConnectionThread)) {
            connections.add(clientConnectionThread);
        }
    }
}
