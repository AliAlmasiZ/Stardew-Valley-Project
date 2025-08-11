package com.ap.stardew.models.dto;

import com.ap.stardew.utils.JSONUtils;

import java.util.HashMap;
import java.util.Map;

public class JSONMessage {
    private Type type;
    private Map<String, Object> body;

    /*
     * Empty constructor needed for JSON Serialization/Deserialization
     */
    public JSONMessage() {

    }
    public JSONMessage(Map<String, Object> body, Type type) {
        this.body = body;
        this.type = type;
    }

    public JSONMessage(Type type) {
        this.body = new HashMap<>();
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public <T> T getFromBody(String fieldName) {
        return (T) body.get(fieldName);
    }

    public <T> T getFromBody(String fieldName, Class<T> tClass) {
        return (T) body.get(fieldName);
    }

    public Object put(String key, Object object) {
        return body.put(key, object);
    }


    public int getIntFromBody(String fieldName) {
        return (int) ((double) ((Double) body.get(fieldName)));
    }

    public enum Type {
        command,
        lobby_command,
        response,
        player_input_command,
        update,
        request,
        trade,
        chat,
        cheat,
        audio_packet,
        upload_packet,
        files_list_request,
        audio_auth,
    }

    @Override
    public String toString() {
        return JSONUtils.toJson(this);
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public boolean containsKey(String key) {
        return body.containsKey(key);
    }
}
