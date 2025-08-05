package com.ap.stardew.app;

import com.ap.stardew.models.Account;
import com.ap.stardew.models.ConnectionThread;
import com.ap.stardew.models.enums.Gender;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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
//        JsonWriter mapper = new JsonWriter(new FileWriter(new File("./data/test.json")));
//        JsonMapper mapper1 = new JsonMapper();

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(new Account(Gender.MALE, "parsa", "Parsa@1145", "Parsa@1145", "parsa"));
        accounts.add(new Account(Gender.MALE, "asd", "asd2", "asd d", "asdsad"));
        accounts.add(new Account(Gender.MALE, "asd", "asd2", "asd d", "asdsad"));
        accounts.add(new Account(Gender.MALE, "asd", "asd2", "asd d", "asdsad"));

        saveAccounts(accounts);


//        FileWriter fileWriter = null;
//        try {
//            fileWriter = new FileWriter("./accounts.json");
//            fileWriter.write(mapper1.valueToTree(accounts).toString());
//            fileWriter.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
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
        server = new Server();
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

}
