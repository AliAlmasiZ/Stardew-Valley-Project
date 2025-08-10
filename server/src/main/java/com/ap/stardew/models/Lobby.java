package com.ap.stardew.models;

import com.ap.stardew.models.dto.AccountInfo;

import java.io.Serial;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ap.stardew.models.LobbyInfo;
import com.ap.stardew.models.dto.SavedGameDetails;

/**
 * Represents a Lobby and its users
 * */
public class Lobby implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;
    private static CopyOnWriteArrayList<Lobby> allLobbies = new CopyOnWriteArrayList<>();

    public enum Type{
        NEW_GAME,
        SAVED_GAME
    }

    private Type type;
    private String lobbyId;
    private String lobbyName;
    private String hostUsername;
    private List<AccountInfo> players;
    private int maxPlayers;
    private SavedGameDetails savedGameDetails;
    private String password;
    private boolean isVisible;

    /**
     * Generates a random alphanumeric string of a given length.
     * @param length The length of the ID to generate.
     * @return A random, short ID.
     */
    private static String generateShortId(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /*
    * Empty constructor for Deserialization
    */
    public Lobby() {}

    /**
     * Creates a new game Lobby
     * @param lobbyName
     * @param accountInfo
     * @param maxPlayers
     * @param password
     * @param isVisible
     */
    public Lobby(String lobbyName, AccountInfo accountInfo, int maxPlayers, String password, boolean isVisible) {
        do this.lobbyId = generateShortId(6);
        while (getLobbyById(lobbyId) != null);

        this.lobbyName = lobbyName;
        this.hostUsername = accountInfo.getUsername();

        this.maxPlayers = maxPlayers;
        this.password = password;
        this.isVisible = isVisible;
        this.players = new ArrayList<>();

        players.add(accountInfo);
        allLobbies.add(this);
    }

    /***
     * Creates a saved game lobby
     * @param lobbyName
     * @param accountInfo
     * @param savedGameDetails
     */
    public Lobby(String lobbyName, AccountInfo accountInfo, SavedGameDetails savedGameDetails) {
        do this.lobbyId = generateShortId(6);
        while (getLobbyById(lobbyId) != null);

        this.lobbyName = lobbyName;
        this.hostUsername = accountInfo.getUsername();

        this.maxPlayers = savedGameDetails.players.size();
        this.password = "";
        this.isVisible = true;
        this.players = new ArrayList<>();
        this.savedGameDetails = savedGameDetails;

        accountInfo.setSelectedMapRegion(savedGameDetails.farms.get(hostUsername));

        players.add(accountInfo);
        allLobbies.add(this);
    }


    public String getLobbyName() {
        return lobbyName;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public List<AccountInfo> getPlayers() {
        return players;
    }

    synchronized public int getCurrentPlayers() {
        return players.size();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public String getPassword() {
        return password;
    }

    public static ArrayList<Lobby> getAllLobbies() {
        return new ArrayList<>(allLobbies);
    }

    public static Lobby getLobbyById(String id) {
        if(id == null) return null;
        for (Lobby lobby : allLobbies) {
            if(lobby != null && id.equals(lobby.lobbyId))
                return lobby;
        }
        return null;
    }

    public boolean isPrivate() {
        return password != null && !password.isEmpty() ;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void addAccountInfo(AccountInfo accountInfo) {
        players.add(accountInfo);
    }

    public LobbyInfo getLobbyInfo(){
        LobbyInfo lobbyInfo = new LobbyInfo();
        lobbyInfo.setLobbyId(lobbyId); ;
        lobbyInfo.setLobbyName(lobbyName);
        lobbyInfo.setHostUsername(hostUsername);
        lobbyInfo.setAccounts(players);
        lobbyInfo.setMaxPlayers(maxPlayers);
        lobbyInfo.setPrivate(isPrivate());
        lobbyInfo.setSavedGameDetails(savedGameDetails);

        return lobbyInfo;
    }

    public void close(){
        allLobbies.remove(this);
    }

    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }

    public SavedGameDetails getSavedGameDetails() {
        return savedGameDetails;
    }
}
