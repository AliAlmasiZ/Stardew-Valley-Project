package com.ap.stardew.app;

import com.ap.stardew.models.Account;
import com.ap.stardew.models.JSONMessage;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerApp {
    private static Server server;
    public static final int TIMEOUT_MILLIS = 500;
    private static final ArrayList<ClientConnectionThread> connections = new ArrayList<>();
    private static boolean exitFlag = false;
    private static ListenerThread listenerThread;

    static {
//        JsonWriter mapper = new JsonWriter(new FileWriter(new File("./data/test.json")));
//        JsonMapper mapper1 = new JsonMapper();
//        accounts.add(new Account(Gender.MALE, "parsa", "Parsa@1145", "Parsa@1145", "parsa"));
//        accounts.add(new Account(Gender.MALE, "asd", "asd2", "asd d", "asdsad"));
//        accounts.add(new Account(Gender.MALE, "asd", "asd2", "asd d", "asdsad"));
//        accounts.add(new Account(Gender.MALE, "asd", "asd2", "asd d", "asdsad"));
//
//        saveAccounts(accounts);


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

    public static ArrayList<Account> loadAccounts(){
        JsonMapper mapper = new JsonMapper();
        try {
            JsonNode jsonNode = mapper.readTree(new File("./accounts.json"));
            return new ArrayList<>(Arrays.asList(mapper.treeToValue(jsonNode, Account[].class)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void saveAccounts(ArrayList<Account> accounts){
        JsonMapper mapper = new JsonMapper();
        try {
            mapper.writeValue(new File("./accounts.json"), accounts);
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

    private static void registerClasses() {
        Kryo kryo = server.getKryo();

        kryo.register(JSONMessage.class);
    }

    public static void startServer(int tcpPort, int udpPort) throws IOException {
        if(server != null) server.dispose();
        server = new Server();
        server.start();
        server.bind(tcpPort, udpPort);
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

    private static void removeClientConnection(Connection connection) {
        for (ClientConnectionThread connectionThread : connections) {
            if (connectionThread.getConnection().equals(connection)) {
                removeClientConnection(connectionThread);
                return;
            }
        }
    }

}
