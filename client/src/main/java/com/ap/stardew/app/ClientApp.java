package com.ap.stardew.app;

import com.ap.stardew.models.ConnectionThread;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.LobbyInfo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.ap.stardew.models.ConnectionThread.*;

public class ClientApp {
    private static Client client;
    private static BlockingQueue<JSONMessage> receivedMessageQueue = new LinkedBlockingQueue<>();
    public static final int TIMEOUT_MILLIS = 5000;

    private static boolean exitFlag = false;

    private static Game activeGame;
    private static String token;

    public static boolean isEnded() {
        return exitFlag;
    }

    public static void endAll() {
        exitFlag = true;
//        serverConnectionThread.end();
    }

    public static void connectServer(String host, int tcpPort, int udpPort) throws IOException {
        client.connect(TIMEOUT_MILLIS, host, tcpPort, udpPort);
        client.addListener(new Listener(){
            @Override
            public void connected(Connection connection) {
                System.out.println("connected");
            }

            @Override
            public void disconnected(Connection connection) {
                System.err.println("disconnected");
            }

            @Override
            public void received(Connection connection, Object object) {
                System.out.println("new message received in class : " + object.getClass());
                boolean handled = handleReceived(object);
                if(!handled) try {
                    receivedMessageQueue.put((JSONMessage) object); // other objects must be handled
                } catch (InterruptedException e) {
                    System.err.println("Error occurred in add object message to queue :");
                    System.err.println(e.getMessage());
                }
            }
        });
    }

    public static void connectServer() {

        try {
            connectServer(HOST, TCP_PORT, UDP_PORT);
        } catch (IOException e) {
            System.err.println("Error : can not connect to server :");
            System.err.println(e.getMessage());
        }
    }

    public static void startClient() {
        if(client != null) {
            System.err.println("client already started");
            return;
        }
        client = new Client(1024 * 1024 * 10, 1024 * 1024 * 10);
        client.start();
        registerClasses();
    }

    private static void registerClasses() {
        ConnectionThread.registerClasses(client.getKryo());
    }

    public static void sendTCP(Object o) {
        client.sendTCP(o);
    }

    public static void sendUDP(Object o) {
        client.sendUDP(o);
    }

    private static boolean handleMessage(JSONMessage message) {
        try {
            JSONMessage response = ClientConnectionController.handleCommand(message);

            if(response != null)
                sendTCP(response);
            return true;
        } catch (UnsupportedOperationException notHandled) {
            return false;
        }
    }

    public static boolean handleReceived(Object received) {
        if(received == null) {

        }
        if(received instanceof JSONMessage) {
            return handleMessage((JSONMessage) received);
        }
        if(received instanceof FrameworkMessage.KeepAlive) {
            return true;
        }
        return false;
    }
    public static JSONMessage sendAndWaitForResponse(JSONMessage message, int timeoutMilli) {
        sendTCP(message);
        try {
            return receivedMessageQueue.poll(timeoutMilli, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            System.err.println("Request Timed out.");
            return null;
        }
    }

    public static Client getClient() {
        return client;
    }

    //    public static void connectServer() {
//        if(serverConnectionThread != null && !serverConnectionThread.isAlive())
//            serverConnectionThread.start();
//        else
//            System.err.println("server connected already");
//    }

//    public static void connectServer(String ip, int port) {
//        try {
//            setServerConnectionThread(ip, port);
//            connectServer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void setServerConnectionThread(String ip, int port) throws IOException {
//        //for Socket connection
//        serverConnectionThread = new ServerConnectionThread(new Socket(ip, port));
//
//    }

//    public static ServerConnectionThread getServerConnectionThread() {
//        return serverConnectionThread;
//    }

    public static boolean isConnected(){
//        return serverConnectionThread != null && serverConnectionThread.isAlive();

        return client.isConnected() ;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        ClientApp.token = token;
    }

    public static String getUsername(){
        if (activeGame != null) {
            return activeGame.getCurrentPlayer().getUsername();
        }

        if(token == null) return null;

        JSONMessage request = new JSONMessage(JSONMessage.Type.command);

        request.put("command", "getUsername");
        request.put("token", token);

        JSONMessage response = sendAndWaitForResponse(request, 5000);


        System.out.println(response.toString());


        if(!response.getFromBody("success", boolean.class)) return null;

        System.out.println(response.getFromBody("username", String.class));

        return response.getFromBody("username");
    }

    public static Game getActiveGame() {
        return activeGame;
    }

    public static void setActiveGame(Game activeGame) {
        ClientApp.activeGame = activeGame;
    }


}
