package com.ap.stardew.models;

import java.io.Serializable;

public class Result implements Serializable {
    private boolean isSuccessful;
    private String message;

    public Result(){}

    public Result(boolean isSuccessful, String message) {
        this.isSuccessful = isSuccessful;
        this.message = message;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
