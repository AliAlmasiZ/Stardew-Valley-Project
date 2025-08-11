package com.ap.stardew.app;

import com.ap.stardew.controllers.DatabaseManager;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.enums.Gender;
import com.ap.stardew.utils.NetworkUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.ap.stardew.utils.NetworkUtils.*;

public class ServerApp {
    private static Server gameServer;
    private static Server audioServer;
    public static final int TIMEOUT_MILLIS = 500;
    private static final ArrayList<ClientConnection> connections = new ArrayList<>();
    private static final ArrayList<GameThread> games = new ArrayList<>();
    private static boolean exitFlag = false;

    // shitty doc:
    private static final String SECRET = "yOZpBdp+vYUn6p+m4rU5hAeQb4YFw7WjQbHbZ3P4fhw=";
    public static final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    public static final JwtParser jwtParser = Jwts.parser().verifyWith(ServerApp.key).build();

    static {
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(new Account(Gender.MALE, "parsa", "a", "a", "a"));
        accounts.add(new Account(Gender.MALE, "asd", "a", "a", "s"));
        accounts.add(new Account(Gender.MALE, "asd2", "a", "a", "d"));
        accounts.add(new Account(Gender.MALE, "asd3", "a", "a", "f"));

        saveAccounts(accounts);

    }


    public static boolean isEnded() {
        return exitFlag;
    }


    public static List<ClientConnection> getConnections() {
        return List.copyOf(ServerApp.connections);
    }

    public static void endAll() {
        exitFlag = true;
        for (ClientConnection connection : connections)
            connection.end();
        for (GameThread game : games) {
            game.end();
        }
        connections.clear();
        games.clear();
    }

    public static void removeClientConnection(ClientConnection ClientConnection) {
        if (ClientConnection != null) {
            connections.remove(ClientConnection);
            ClientConnection.end();
        }
    }

    public static void addClientConnection(ClientConnection ClientConnection) {
        if (ClientConnection != null && !connections.contains(ClientConnection)) {
            connections.add(ClientConnection);
        }
    }

    public static void addGameThread(GameThread gameThread) {
        if(gameThread != null && !games.contains(gameThread)) {
            games.add(gameThread);
        }
    }

    public static ArrayList<GameThread> getGames(){
        return games;
    }

    public static ArrayList<Account> loadAccounts(){
        /*JsonMapper mapper = new JsonMapper();
        try {
            FileInputStream file;
            try {
                file = new FileInputStream("./accounts.json");
            }catch (FileNotFoundException e){
                saveAccounts(new ArrayList<>());
                return new ArrayList<>();
            }
            JsonNode jsonNode = mapper.readTree(file);
            file.close();
            return new ArrayList<>(Arrays.asList(mapper.treeToValue(jsonNode, Account[].class)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        return new ArrayList<>(DatabaseManager.findAllAccounts());
    }
    public static void saveAccounts(ArrayList<Account> accounts){
        /*JsonMapper mapper = new JsonMapper();
        try {
            FileOutputStream file = new FileOutputStream("./accounts.json");
            mapper.writeValue(file, accounts);
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        //bad! should insert on account creation or update
        for (Account account : accounts) {
            DatabaseManager.saveAccount(account);
        }
    }
    public static Account getAccountByUsername(String username){
        for (Account account : loadAccounts()) {
            if(account.getUsername().equals(username)){
                return account;
            }
        }
        return null;
    }

    /**
     * Register any classes with {@link NetworkUtils#registerClasses(Kryo)}
     * */
    private static void registerClasses() {
        NetworkUtils.registerClasses(gameServer.getKryo());
        NetworkUtils.registerClasses(audioServer.getKryo());
    }

    public static void startServer() throws IOException {
        if(gameServer != null) gameServer.dispose();
        if(audioServer != null) audioServer.dispose();

        gameServer = new Server(1024 * 1024 * 10, 1024 * 1024 * 10);
        audioServer = new Server(1024 * 1024 * 10, 1024 * 1024 * 10);

        gameServer.start();
        audioServer.start();

        gameServer.bind(TCP_PORT, UDP_PORT);
        audioServer.bind(AUDIO_CHANNEL_TCP, AUDIO_CHANNEL_UDP);


        registerClasses();
    }

    public static void initializeServerListener() {
        gameServer.addListener(new Listener(){
            @Override
            public void connected(Connection connection) {
                try { // make a connection thread for handle every player in a different thread
                    ClientConnection connectionThread = new ClientConnection(connection);
                    if (!connectionThread.initialHandshake()) {
                        System.err.println("Inital HandShake failed with remote device.");
                        connectionThread.end();
                        return;
                    }
                    connectionThread.startGameConnection();
                    System.out.println("new client connected : " + connection.getID());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void disconnected(Connection connection) {
                removeClientConnection(connection);
                connection.close();
                System.out.println("client disconnected : " + connection.getID());
            }

        });

        audioServer.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof JSONMessage message) {
                    if(message.getType() == JSONMessage.Type.audio_auth) {
                        String username = ServerApp.getUsername(message.getFromBody("token"));
                        var clientConnection = ServerApp.getConnectionByUsername(username);
                        clientConnection.setAudioConnection(connection);
                        clientConnection.setAudioConnectionListener();
                    }
                }
            }
        });
    }

    public static Server getGameServer() {
        return gameServer;
    }

    private static ClientConnection getConnectionThread(int connectionId) {
        for (ClientConnection connectionThread : connections) {
            if (connectionThread.getGameConnection().getID() == connectionId) {
                return connectionThread;
            }
        }
        return null;
    }

    private static ClientConnection getConnectionThread(Connection connection) {
        return getConnectionThread(connection.getID());
    }

    private static void removeClientConnection(Connection connection) {
        ClientConnection connectionThread = getConnectionThread(connection);
        if(connections.contains(connectionThread)) {
            connections.remove(connectionThread);
            connectionThread.end();
        }
    }

    public static ClientConnection getConnectionByUsername(String username){
        for (ClientConnection connection : connections) {
            if(connection.getCurrentAccount() != null && connection.getCurrentAccount().getUsername().equals(username)){
                return connection;
            }
        }
        return null;
    }

    public static String getUsername(String token){
        Claims payload;
        try {
            payload = jwtParser.parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            System.out.println(e);
            return null;
        }
        return payload.getSubject();
    }

    public static List<GameThread> getGameThreads() {
        return new ArrayList<>(games);
    }
}
