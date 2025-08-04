package com.ap.stardew.models.dto;

import java.io.Serial;
import java.io.Serializable;

public class AccountInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String username;
    private boolean isReady;

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
}
