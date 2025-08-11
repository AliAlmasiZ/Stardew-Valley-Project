package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnection;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.dto.JSONMessage;
import com.badlogic.gdx.utils.Json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AudioServerController {

    public static JSONMessage handleMessage(JSONMessage message, ClientConnection client) {
        if (message.getType() == JSONMessage.Type.audio_packet) {
            return handleUpload(message, client);
        }
        throw new UnsupportedOperationException();
    }

    private static JSONMessage handleUpload(JSONMessage packet, ClientConnection client) {
        var response = new JSONMessage(JSONMessage.Type.response);
        try {
            String fileName = packet.getFromBody("file_name");
            var buffer = client.uploadBuffers.computeIfAbsent(fileName, k -> new ByteArrayOutputStream());
            buffer.write(packet.getFromBody("chunk_data", byte[].class));
            if(packet.getFromBody("is_last_chunk")) {
                DatabaseManager.saveAudioFile(client.getCurrentAccount().getUsername(), fileName, buffer.toByteArray());
            }
            var result = new Result(true, "");
            response.put("result", result);
        } catch (IOException e) {
            var result = new Result(false, e.getMessage());
            response.put("result", result);
        }
        return response;
    }

}
