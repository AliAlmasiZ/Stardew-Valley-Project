package com.ap.stardew.app;

import com.ap.stardew.ClientGame;
import com.ap.stardew.models.ConnectionThread;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.LobbyInfo;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.AbstractScreen;
import com.ap.stardew.views.ColorPalette;
import com.ap.stardew.views.LoginScreen;
import com.ap.stardew.views.MultiplayerScreen;
import com.ap.stardew.views.widgets.PopUpMessage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ap.stardew.utils.NetworkUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.ap.stardew.models.ConnectionThread.*;
import static com.ap.stardew.views.AbstractScreen.updateNetworkStatus;

public class ClientApp {
    private static Client client;
    private static BlockingQueue<JSONMessage> receivedMessageQueue = new LinkedBlockingQueue<>();
    public static final int TIMEOUT_MILLIS = 5000;

    private static boolean exitFlag = false;

    private static Game activeGame;
    private static String token;
    private static String username;

    public static boolean isEnded() {
        return exitFlag;
    }

    public static void endAll() {
        exitFlag = true;
//        serverConnectionThread.end();
    }

    public static void connectServer(String host, int tcpPort, int udpPort) throws IOException {
        client.addListener(new Listener(){
            @Override
            public void connected(Connection connection) {
                Gdx.app.postRunnable(()->{
                    updateNetworkStatus("connected");
                });
            }

            @Override
            public void disconnected(Connection connection) {
                Gdx.app.postRunnable(()->{
                    updateNetworkStatus("disconnected");
                });
            }

            @Override
            public void received(Connection connection, Object object) {
//                System.out.println("new message received in class : " + object.getClass());
                boolean handled = handleReceived(object);
                if(!handled) try {
                    receivedMessageQueue.put((JSONMessage) object); // other objects must be handled
                } catch (InterruptedException e) {
                    System.err.println("Error occurred in add object message to queue :");
                    System.err.println(e.getMessage());
                }
            }
        });
        try {
            client.connect(TIMEOUT_MILLIS, host, tcpPort, udpPort);
        }catch (IOException e){
            Gdx.app.postRunnable(()->{
                updateNetworkStatus("disconnected");
            });
        }
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
        NetworkUtils.registerClasses(client.getKryo());
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
    public static void reconnect(){
        try {
            ClientApp.getClient().reconnect(2000);

            if(token != null){
                JSONMessage loginRequest = new JSONMessage(JSONMessage.Type.command);
                loginRequest.put("command", "login");
                loginRequest.put("token", token);

                JSONMessage jsonMessage = sendAndWaitForResponse(loginRequest, 3000);
                System.out.println(jsonMessage);
                if(jsonMessage == null || !jsonMessage.getFromBody("success", boolean.class)){
                    PopUpMessage popUpMessage = new PopUpMessage(jsonMessage != null ? jsonMessage.getFromBody("message") : "failed to log in");
                    popUpMessage.show(AbstractScreen.getFrontStage());
                    ClientGame.getInstance().setScreen(new LoginScreen());
                    return;
                }

                ClientApp.setUsername(jsonMessage.getFromBody("username"));
                PopUpMessage popUpMessage = new PopUpMessage("logged in as " + username);
                popUpMessage.show(AbstractScreen.getFrontStage());

                ClientGame.getInstance().setScreen(new MultiplayerScreen());
            }
        } catch (IOException e) {
            updateNetworkStatus("disconnected");
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
        return client.isConnected() ;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        ClientApp.token = token;
    }

    public static String getUsername(){
        return username;
    }

    public static void setUsername(String username) {
        ClientApp.username = username;
    }

    public static Game getActiveGame() {
        return activeGame;
    }

    public static void setActiveGame(Game activeGame) {
        ClientApp.activeGame = activeGame;
    }


}
