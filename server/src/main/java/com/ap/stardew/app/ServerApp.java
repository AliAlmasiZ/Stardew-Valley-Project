package com.ap.stardew.app;

import com.ap.stardew.controllers.GameController;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.ConnectionThread;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.GameSession;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.enums.Gender;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.gson.stream.JsonWriter;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ServerApp {
    private static Server server;
    public static final int TIMEOUT_MILLIS = 500;
    private static final ArrayList<ClientConnectionThread> connections = new ArrayList<>();
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
        accounts.add(new Account(Gender.MALE, "asd", "a", "a", "d"));
        accounts.add(new Account(Gender.MALE, "asd", "a", "a", "f"));

        saveAccounts(accounts);



    }

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


    public static List<ClientConnectionThread> getConnections() {
        return List.copyOf(ServerApp.connections);
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

    public static void addGameThread(GameThread gameThread) {
        if(gameThread != null && !games.contains(gameThread)) {
            games.add(gameThread);
        }
    }

    public static ArrayList<Account> loadAccounts(){
        JsonMapper mapper = new JsonMapper();
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
        }
    }
    public static void saveAccounts(ArrayList<Account> accounts){
        JsonMapper mapper = new JsonMapper();
        try {
            FileOutputStream file = new FileOutputStream("./accounts.json");
            mapper.writeValue(file, accounts);
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    private static void registerClasses() { //to register any classes register it in ConnectionThread registerClasses function
        ConnectionThread.registerClasses(server.getKryo());
    }

    public static void startServer(int tcpPort, int udpPort) throws IOException {
        if(server != null) server.dispose();
        server = new Server(1024 * 1024 * 10, 1024 * 1024 * 10);
        server.start();
        server.bind(tcpPort, udpPort);
        registerClasses();
    }

    public static void initializeServerListener() {
        server.addListener(new Listener(){
            @Override
            public void connected(Connection connection) {
                try { // make a connection thread for handle every player in a different thread
                    ClientConnectionThread connectionThread = new ClientConnectionThread(connection);
                    if (!connectionThread.initialHandshake()) {
                        System.err.println("Inital HandShake failed with remote device.");
                        connectionThread.end();
                        return;
                    }
                    connectionThread.start();
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
    }

    public static Server getServer() {
        return server;
    }

    private static ClientConnectionThread getConnectionThread(int connectionId) {
        for (ClientConnectionThread connectionThread : connections) {
            if (connectionThread.getConnection().getID() == connectionId) {
                return connectionThread;
            }
        }
        return null;
    }

    private static ClientConnectionThread getConnectionThread(Connection connection) {
        return getConnectionThread(connection.getID());
    }

    private static void removeClientConnection(Connection connection) {
        ClientConnectionThread connectionThread = getConnectionThread(connection);
        if(connections.contains(connectionThread)) {
            connections.remove(connectionThread);
            connectionThread.end();
        }
    }

    public static ClientConnectionThread getConnectionByUsername(String username){
        for (ClientConnectionThread connection : connections) {
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
}
