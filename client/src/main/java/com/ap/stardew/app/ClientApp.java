package com.ap.stardew.app;

import com.ap.stardew.models.ConnectionThread;
import com.ap.stardew.models.JSONMessage;
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

    private static ServerConnectionThread serverConnectionThread;

    private static boolean exitFlag = false;

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
            System.err.println("client already connected");
            return;
        }
        client = new Client();
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
        JSONMessage response = ClientConnectionController.handleCommand(message);
        if(response == null)
            return false;
        sendTCP(response);
        return true;
    }

    public static boolean handleReceived(Object received) {
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


}
