package com.ap.stardew.models;

public record Result(boolean isSuccessful, String message) {
    @Override
    public String toString() {
        return this.message;
    }
}
