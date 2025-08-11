package com.ap.stardew.app;

import com.ap.stardew.models.Game;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.utils.NetworkUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.concurrent.*;

import static com.ap.stardew.utils.NetworkUtils.*;

public class ClientApp {
    private static Client gameClient;
    private static Client audioClient;
    private static BlockingQueue<JSONMessage> receivedMessageQueue = new LinkedBlockingQueue<>();
    private static BlockingQueue<JSONMessage> receivedAudioMessageQueue = new LinkedBlockingQueue<>();
    public static final int TIMEOUT_MILLIS = 5000;

    private static Game activeGame;
    private static String token;
    private static String username;

    public static void connectAudioClient(String host, int tcp, int udp) throws IOException {
        if(audioClient == null)
            throw new IllegalStateException("You should start audioClient before connect that");
        audioClient.connect(TIMEOUT_MILLIS, host, tcp, udp);
        audioClient.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                System.out.println("Audio Connected");
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("Audio Disconnected");
            }

            @Override
            public void received(Connection connection, Object object) {

            }
        });
    }

    public static void connectGameClient(String host, int tcpPort, int udpPort) throws IOException {
        if(gameClient == null)
            throw new IllegalStateException("You should start gameClient before connect that");
        gameClient.connect(TIMEOUT_MILLIS, host, tcpPort, udpPort);
        gameClient.addListener(new Listener(){
            @Override
            public void connected(Connection connection) {
                System.out.println("Game Connected");
            }

            @Override
            public void disconnected(Connection connection) {
                System.err.println("Game Disconnected");
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

    public static void connectClients() {

        try {
            connectGameClient(HOST, TCP_PORT, UDP_PORT);
            connectAudioClient(HOST, AUDIO_CHANNEL_TCP, AUDIO_CHANNEL_UDP);
        } catch (IOException e) {
            System.err.println("Error : can not connect to server :");
            System.err.println(e.getMessage());
        }
    }

    public static void startClients() {
        if(gameClient != null || audioClient != null) {
            System.err.println("client already started");
            return;
        }
        gameClient = new Client(1024 * 1024 * 10, 1024 * 1024 * 10);
        gameClient.start();
        audioClient = new Client();
        audioClient.start();

        registerClasses();
    }

    /**
     * Register any classes with {@link NetworkUtils#registerClasses(Kryo)}
     * */
    private static void registerClasses() {
        NetworkUtils.registerClasses(gameClient.getKryo());
        NetworkUtils.registerClasses(audioClient.getKryo());
    }

    public static void sendTCP(Object o) {
        gameClient.sendTCP(o);
    }

    public static void sendUDP(Object o) {
        gameClient.sendUDP(o);
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

    public static JSONMessage sendAndWaitForAudioResponse(JSONMessage message, int timeoutMilli) {
        audioClient.sendTCP(message);
        try {
            return receivedAudioMessageQueue.poll(timeoutMilli, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            System.err.println("Request Timed out.");
            return null;
        }
    }

    public static Client getGameClient() {
        return gameClient;
    }

    public static Client getAudioClient() {
        return audioClient;
    }


    public static boolean isConnected(){
        return gameClient.isConnected();
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
