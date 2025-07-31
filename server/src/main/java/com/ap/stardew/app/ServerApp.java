package com.ap.stardew.app;

import com.ap.stardew.models.Account;
import com.ap.stardew.models.enums.Gender;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.JsonParserSequence;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ap.stardew.GameServer.PORT;
import static com.ap.stardew.GameServer.main;

public class ServerApp {
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
}
