package com.ap.stardew.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Lobby implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;

    private int lobbyId;
    private String lobbyName;
    private String hostUsername;
    private List<PlayerInfo> players;
    private int maxPlayers;

    /*
    * Empty constructor for Deserialization
    */
    public Lobby() {}

    public Lobby(int lobbyId, String lobbyName, String hostUsername, int maxPlayers) {
        this.lobbyId = lobbyId;
        this.lobbyName = lobbyName;
        this.hostUsername = hostUsername;
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();

    }

    public class PlayerInfo implements Serializable {
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
