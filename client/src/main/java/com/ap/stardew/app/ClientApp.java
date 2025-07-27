package com.ap.stardew.app;

import java.io.IOException;
import java.net.Socket;

public class ClientApp {
    public static final int TIMEOUT_MILLIS = 500;


    private static ServerConnectionThread serverConnectionThread;

    private static boolean exitFlag = false;

    public static boolean isEnded() {
        return exitFlag;
    }

    public static void endAll() {
        exitFlag = true;
        serverConnectionThread.end();
    }

    public static void connectServer() {
        if(serverConnectionThread != null && !serverConnectionThread.isAlive())
            serverConnectionThread.start();
        else
            System.err.println("server connected already");
    }

    public static void connectServer(String ip, int port) {
        try {
            setServerConnectionThread(ip, port);
            connectServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setServerConnectionThread(String ip, int port) throws IOException {
        serverConnectionThread = new ServerConnectionThread(new Socket(ip, port));
    }


}
