package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientApp;
import com.ap.stardew.models.dto.JSONMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

import static com.ap.stardew.utils.NetworkUtils.CHUNK_SIZE;

public class RadioController {



    public static synchronized void uploadFile(String localPath, String fileName, Consumer<Float> onProgress) {
        try (var fis = new FileInputStream(localPath)) {
            long totalBytes = new File(localPath).length();
            long bytesSent = 0;

            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead, chunkIndex = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] chunk = Arrays.copyOf(buffer, bytesRead);
                JSONMessage uploadMessage = new JSONMessage(JSONMessage.Type.upload_packet);
                uploadMessage.put("token", ClientApp.getToken());
                uploadMessage.put("file_name", fileName);
                uploadMessage.put("chunk_data", chunk);
                uploadMessage.put("chunk_index", chunkIndex++);
                uploadMessage.put("is_last_chunk", bytesRead < CHUNK_SIZE);
                ClientApp.getAudioClient().sendTCP(uploadMessage);

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
