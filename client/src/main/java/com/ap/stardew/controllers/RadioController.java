package com.ap.stardew.controllers;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.dto.JSONMessage;
import com.badlogic.gdx.Gdx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

import static com.ap.stardew.utils.NetworkUtils.BUFFER_SIZE;

public class RadioController {

    public static JSONMessage handleMessage(JSONMessage message) {
        if(message.getType() == JSONMessage.Type.audio_packet) {
            playAudio(message);
            return null;
        }
        if(message.getType() == JSONMessage.Type.audio_command) {
            return handleAudioCommand(message);
        }
        throw new UnsupportedOperationException();
    }

    private static JSONMessage handleAudioCommand(JSONMessage message) {
        String command = message.getFromBody("command");
        switch (command) {
            case "stop_playing" -> {
                ClientGame.getInstance().audioQueue.clear();
                return null;
            }
        }


        return null;

    }


    private static synchronized void playAudio(JSONMessage packet) {
        ClientGame app = ClientGame.getInstance();
        if(app.audioDevice == null) {
            app.audioDevice = Gdx.audio.newAudioDevice(
                packet.getFromBody("sample_rate"),
                packet.getFromBody("channels", int.class) == 1
            );
        }
        app.audioQueue.offer(packet.getFromBody("data"));
    }


    public static synchronized void uploadFile(String localPath, String fileName, Consumer<Float> onProgress) {
        try (var fis = new FileInputStream(localPath)) {
            long totalBytes = new File(localPath).length();
            long bytesSent = 0;

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead, chunkIndex = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] chunk = Arrays.copyOf(buffer, bytesRead);
                JSONMessage uploadMessage = new JSONMessage(JSONMessage.Type.upload_packet);
                uploadMessage.put("token", ClientApp.getToken());
                uploadMessage.put("file_name", fileName);
                uploadMessage.put("chunk_data", chunk);
                uploadMessage.put("chunk_index", chunkIndex++);
                uploadMessage.put("is_last_chunk", bytesRead < BUFFER_SIZE);
                var res = ClientApp.sendAndWaitForAudioResponse(uploadMessage, ClientApp.TIMEOUT_MILLIS);

                Result result = res.getFromBody("result");
                if(!result.isSuccessful()) {
                    throw new Error(result.message());
                }

                bytesSent += bytesRead;
                if(onProgress != null) {
                    onProgress.accept(bytesSent / (float)totalBytes);
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("File doesn't exist: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
