package com.ap.stardew.models;

import java.io.Serializable;

public record Result(boolean isSuccessful, String message) implements Serializable {
    @Override
    public String toString() {
        return this.message;
    }
}
