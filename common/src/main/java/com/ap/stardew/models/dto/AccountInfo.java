package com.ap.stardew.models.dto;

import com.badlogic.gdx.graphics.Color;

import java.io.Serial;
import java.io.Serializable;

public class AccountInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String username;
    private boolean isReady;
    private String selectedMapRegion;

    /*
     * Empty constructor for Deserialization
     */
    public AccountInfo() {}

    public AccountInfo(String username) {
        this.username = username;
        this.isReady = false;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public boolean isReady() { return isReady; }
    public void setReady(boolean ready) { isReady = ready; }

    public String getSelectedMapRegion() {
        return selectedMapRegion;
    }

    public void setSelectedMapRegion(String selectedMapRegion) {
        this.selectedMapRegion = selectedMapRegion;
    }
}
