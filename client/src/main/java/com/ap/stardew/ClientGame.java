package com.ap.stardew;

import com.ap.stardew.app.ClientApp;
import com.ap.stardew.view.CharacterSpriteManager;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.MainScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

import java.util.concurrent.LinkedBlockingQueue;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ClientGame extends Game {
    private static ClientGame instance;
    public NativeFileChooser fileChooser;
    private SpriteBatch batch;
    //music player
    public AudioDevice audioDevice;
    public LinkedBlockingQueue<byte[]> audioQueue;
    public Thread musicThread;
    private volatile boolean isRunning = true;




    public static ClientGame getInstance() {
        return instance;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public ClientGame(NativeFileChooser fileChooser) {
        super();
        this.fileChooser = fileChooser;
    }

    @Override
    public void create() {
        audioQueue = new LinkedBlockingQueue<>();
        ClientApp.startClients();

        loadDatas();
        instance = this;
        batch = new SpriteBatch();


        setScreen(new MainScreen());
        ClientApp.connectClients();
        musicThread = new Thread(this::playAudio);
        musicThread.start();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0.1f, 0f, 1);
        Gdx.gl.glClear(GL32.GL_COLOR_BUFFER_BIT);
        super.render();
    }

    private static void loadDatas() {
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/Tools"));
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/Crops"));
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/Trees"));
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/Animal"));
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/NPC"));
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/Minerals"));
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/ForagingCrops"));
        GameAssetManager.getInstance().loadTexturesRecursively(Gdx.files.internal("Content/Workstations"));
        GameAssetManager.getInstance().finishLoading();
        GameAssetManager.getInstance().characterSpriteManager = new CharacterSpriteManager();
    }

    public void playAudio() {
        int bufferSize = 4096 * 4; // ~100ms buffer for 44100 Hz, stereo, 16-bit
        byte[] byteBuffer = new byte[bufferSize];
        int bytePos = 0;

        while (isRunning) {
            try {
                byte[] chunk = audioQueue.take(); // Blocking until data arrives
                // Append chunk to buffer
                if (bytePos + chunk.length <= byteBuffer.length) {
                    System.arraycopy(chunk, 0, byteBuffer, bytePos, chunk.length);
                    bytePos += chunk.length;
                }

                // Convert to short[] when buffer is full or enough samples are ready
                if (bytePos >= 4096) { // At least ~23ms of audio
                    int numSamples = bytePos / 2; // 2 bytes per sample
                    short[] samples = new short[numSamples];
                    for (int i = 0; i < numSamples; i++) {
                        // Convert little-endian bytes to short
                        samples[i] = (short) ((byteBuffer[i * 2] & 0xff) | (byteBuffer[i * 2 + 1] << 8));
                    }
                    if(audioDevice == null) {
                        System.err.println("Error: audio device is null");
                        continue;
                    }
                    audioDevice.writeSamples(samples, 0, numSamples);
                    // Shift remaining bytes (if any)
                    int remaining = bytePos - (numSamples * 2);
                    if (remaining > 0) {
                        System.arraycopy(byteBuffer, numSamples * 2, byteBuffer, 0, remaining);
                    }
                    bytePos = remaining;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        if(audioDevice != null)
            audioDevice.dispose();
    }
}
