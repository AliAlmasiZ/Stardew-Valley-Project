package com.ap.stardew.models;

import com.ap.stardew.app.ClientConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Radio {
    public String ownerUsername;
    public String currentFile;
    public AtomicBoolean isPlaying = new AtomicBoolean(false);
    public List<ClientConnection> listeners = new CopyOnWriteArrayList<>();
}
