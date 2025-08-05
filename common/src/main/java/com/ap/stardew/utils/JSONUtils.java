package com.ap.stardew.utils;

import com.ap.stardew.models.dto.JSONMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONUtils {
    private static final GsonBuilder gsonBuilder = new GsonBuilder();
    private static final Gson gson;

    static {
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }

    public synchronized static String toJson(Object message) {
        return gson.toJson(message);
    }

    public synchronized static JSONMessage fromJson(String json) {
        return gson.fromJson(json, JSONMessage.class);
    }
    public synchronized static <T> T fromJson(String json, Class<T> tClass) {
        return gson.fromJson(json,tClass);
    }
}
