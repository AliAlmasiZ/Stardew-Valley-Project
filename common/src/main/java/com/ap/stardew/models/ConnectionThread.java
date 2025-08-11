package com.ap.stardew.models;

import com.ap.stardew.models.NPC.NPC;
import com.ap.stardew.models.NPC.NpcFriendship;
import com.ap.stardew.models.building.Door;
import com.ap.stardew.models.crafting.Ingredient;
import com.ap.stardew.models.crafting.Recipe;
import com.ap.stardew.models.crafting.RecipeType;
import com.ap.stardew.models.dto.AccountInfo;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.dto.PlayerState;
import com.ap.stardew.models.entities.*;
import com.ap.stardew.models.entities.components.*;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.components.inventory.InventorySlot;
import com.ap.stardew.models.enums.*;
import com.ap.stardew.models.gameMap.*;
import com.ap.stardew.models.player.*;
import com.ap.stardew.models.player.friendship.PlayerFriendship;
import com.ap.stardew.models.shop.*;
import com.ap.stardew.utils.JSONUtils;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import org.tiledreader.TiledMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

abstract public class ConnectionThread extends Thread {
    protected final DataInputStream dataInputStream;
    protected final DataOutputStream dataOutputStream;
    //this is for Socket connection
    protected final BlockingQueue<JSONMessage> receivedMessagesQueue;
    protected final BlockingQueue<Object> receivedObjectsQueue;
    protected String otherSideIP;
    protected int otherSidePort;
    protected Socket socket;
    protected Connection connection;
    protected AtomicBoolean end;
    protected boolean initialized = false;

    protected ConnectionThread(Socket socket) throws IOException {
        this.socket = socket;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.receivedMessagesQueue = new LinkedBlockingQueue<>();
        this.receivedObjectsQueue = null;
        this.end = new AtomicBoolean(false);
    }

    protected ConnectionThread(Connection connection) throws IOException {// kryonet
        this.dataInputStream = null;
        this.dataOutputStream = null;
        this.receivedMessagesQueue = null;
        this.connection = connection;
        this.receivedObjectsQueue = new LinkedBlockingQueue<>();
        this.end = new AtomicBoolean(false);
    }

    public JSONMessage sendAndWaitForResponse(JSONMessage message, int timeoutMilli) {
        sendTCP(message);
        try {
            if (initialized) return receivedMessagesQueue.poll(timeoutMilli, TimeUnit.MILLISECONDS);
            socket.setSoTimeout(timeoutMilli);
            var result = JSONUtils.fromJson(dataInputStream.readUTF());
            socket.setSoTimeout(0);
            return result;
        } catch (Exception e) {
            System.err.println("Request Timed out.");
            return null;
        }
    }

    abstract public boolean initialHandshake();

    abstract protected boolean handleMessage(JSONMessage message);

    public synchronized void sendTCP(Object object) {
        connection.sendTCP(object);
    }

    public synchronized void sendUDP(Object object) {
        connection.sendUDP(object);
    }

    @Override
    public void run() {
        initialized = false;
        if (!initialHandshake()) {
            System.err.println("Inital HandShake failed with remote device.");
            end();
            return;
        }

        initialized = true;
        while (!end.get()) {
            try {
                String receivedStr = dataInputStream.readUTF();
                JSONMessage message = JSONUtils.fromJson(receivedStr);
                boolean handled = handleMessage(message);
                if (!handled) try {
                    receivedMessagesQueue.put(message);
                } catch (InterruptedException e) {}
            } catch (Exception e) {
                System.out.println(e);
                break;
            }
        }

        end();
    }

    public String getOtherSideIP() {
        return otherSideIP;
    }

    public void setOtherSideIP(String otherSideIP) {
        this.otherSideIP = otherSideIP;
    }

    public int getOtherSidePort() {
        return otherSidePort;
    }

    public void setOtherSidePort(int otherSidePort) {
        this.otherSidePort = otherSidePort;
    }

    public void end() {
        end.set(true);
        connection.close();
        try {
            socket.close();
        } catch (Exception ignored) {}
    }


    public Connection getConnection() {
        return connection;
    }

}
