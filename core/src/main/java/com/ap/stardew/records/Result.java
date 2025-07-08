package com.ap.stardew.records;

public record Result(boolean isSuccessful, String message) {
    @Override
    public String toString() {
        return this.message;
    }
}
