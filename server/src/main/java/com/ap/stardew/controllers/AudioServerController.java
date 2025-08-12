package com.ap.stardew.controllers;

import com.ap.stardew.app.ClientConnection;
import com.ap.stardew.models.Radio;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.dto.JSONMessage;
import com.badlogic.gdx.utils.Json;
import javazoom.jl.decoder.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ap.stardew.utils.NetworkUtils.BUFFER_SIZE;

public class AudioServerController {

    public static JSONMessage handleMessage(JSONMessage message, ClientConnection client) {
        if (message.getType() == JSONMessage.Type.upload_packet) {
            return handleUpload(message, client);
        }
        if (message.getType() == JSONMessage.Type.files_list_request) {
            return sendFileList(message);
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

    private static JSONMessage sendFileList(JSONMessage request) {
        List<String> fileNames = DatabaseManager.allUserAudioFiles(request.getFromBody("owner_username"));
        JSONMessage response = new JSONMessage(JSONMessage.Type.response);
        response.put("file_names", fileNames);
        return response;
    }


    private static void streamMusic (Radio radio) {
        byte[] musicBytes = DatabaseManager.loadAudioFile(radio.ownerUsername, radio.currentFile);

        try (var bis = new ByteArrayInputStream(musicBytes)) {
            Bitstream bitstream = new Bitstream(bis);
            Decoder decoder = new Decoder();
            int sequence = 0;
            byte[] pcmBuffer = new byte[BUFFER_SIZE];
            int bufferPos = 0;
            JSONMessage audioPacket = new JSONMessage(JSONMessage.Type.audio_packet);

            while (true) {
                Header frameHeader = bitstream.readFrame();
                if(frameHeader == null || !radio.isPlaying.get()) break;

                if(sequence == 0) {
                    audioPacket.put("bit_depth", 16);
                    audioPacket.put("sample_rate", frameHeader.frequency());
                    audioPacket.put("channels", frameHeader.mode() == Header.SINGLE_CHANNEL ? 1 : 2);
                }

                // Decode to PCM
                SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
                short[] pcmSamples = output.getBuffer();
                int sampleCount = output.getBufferLength();

                for (int i = 0; i < sampleCount; i++) {
                    if (bufferPos >= pcmBuffer.length) {
                        //Buffer full
                        audioPacket.put("sequence", sequence++);
                        byte[] data = new byte[bufferPos];
                        System.arraycopy(pcmBuffer, 0, data, 0, bufferPos);
                        audioPacket.put("data", data);
                        for (ClientConnection client : radio.listeners) {
                            client.getAudioConnection().sendTCP(audioPacket);
                        }
                        bufferPos = 0;
                        Thread.sleep(10);
                    }
                    // Write PCM sample (little-endian)
                    pcmBuffer[bufferPos++] = (byte) (pcmSamples[i] & 0xff);
                    pcmBuffer[bufferPos++] = (byte) (pcmSamples[i] >> 8);
                }
                bitstream.closeFrame();
            }

            // Send remaining data in buffer
            if (bufferPos > 0) {
                audioPacket.put("sequence", sequence++);
                byte[] data = new byte[bufferPos];
                System.arraycopy(pcmBuffer, 0, data, 0, bufferPos);
                audioPacket.put("data", data);
                for (ClientConnection client : radio.listeners) {
                    client.getAudioConnection().sendTCP(audioPacket);
                }
            }
        } catch (IOException | BitstreamException | DecoderException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
