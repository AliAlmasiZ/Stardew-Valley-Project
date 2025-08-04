package com.ap.stardew.models;

import com.ap.stardew.models.dto.AccountInfo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * A Data Transfer Object (DTO) that represents the public-facing information of a lobby.
 */
public class LobbyInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String lobbyId;
    private String lobbyName;
    private String hostUsername;
    private List<AccountInfo> accounts;
    private int maxPlayers;
    private boolean isPrivate;

    /**
     * Empty constructor for Deserialization
     * */
    public LobbyInfo(){}

    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }

    public List<AccountInfo> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountInfo> accounts) {
        this.accounts = accounts;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public int getCurrentPlayers() {
        return accounts.size();
    }
}
