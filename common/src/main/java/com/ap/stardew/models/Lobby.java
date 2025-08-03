package com.ap.stardew.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents a Lobby and its users
 * */
public class Lobby implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;
    private static int lobbiesCount = 0;
    private static ArrayList<Lobby> allLobbies = new ArrayList<>();

    private int lobbyId;
    private String lobbyName;
    private String hostUsername;
    private List<PlayerInfo> players;
    private int maxPlayers;

    /*
    * Empty constructor for Deserialization
    */
    public Lobby() {}

    public Lobby(String lobbyName, String hostUsername, int maxPlayers) {
        lobbiesCount++;
        this.lobbyId = lobbiesCount;
        this.lobbyName = lobbyName;
        this.hostUsername = hostUsername;
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
        allLobbies.add(this);
    }


    public String getLobbyName() {
        return lobbyName;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }

    public int getCurrentPlayers() {
        return players.size();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getID() {
        return lobbyId;
    }

    public static class PlayerInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String username;
        private boolean isReady;

        /*
        * Empty constructor for Deserialization
        */
        public PlayerInfo() {}

        public PlayerInfo(String username) {
            this.username = username;
            this.isReady = false;
        }

        // Getters and setters
        public String getUsername() { return username; }
        public boolean isReady() { return isReady; }
        public void setReady(boolean ready) { isReady = ready; }
    }
}
