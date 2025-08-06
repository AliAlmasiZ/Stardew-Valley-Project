package com.ap.stardew.models;

import com.ap.stardew.models.NPC.NPC;
import com.ap.stardew.models.NPC.NpcFriendship;
import com.ap.stardew.models.building.Door;
import com.ap.stardew.models.crafting.Ingredient;
import com.ap.stardew.models.crafting.Recipe;
import com.ap.stardew.models.crafting.RecipeType;
import com.ap.stardew.models.dto.AccountInfo;
import com.ap.stardew.models.dto.GameMapInfo;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.dto.PlayerState;
import com.ap.stardew.models.dto.serilizers.GameMapSerializer;
import com.ap.stardew.models.entities.CollisionEvent;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.EntityList;
import com.ap.stardew.models.entities.Renderable;
import com.ap.stardew.models.entities.components.EntityComponent;
import com.ap.stardew.models.entities.components.InteriorComponent;
import com.ap.stardew.models.entities.components.Placeable;
import com.ap.stardew.models.entities.components.PositionComponent;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.components.inventory.InventorySlot;
import com.ap.stardew.models.enums.*;
import com.ap.stardew.models.gameMap.*;
import com.ap.stardew.models.player.*;
import com.ap.stardew.models.player.buff.Buff;
import com.ap.stardew.models.player.friendship.PlayerFriendship;
import com.ap.stardew.utils.JSONUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;

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
    public static final int TCP_PORT = 54555;
    public static final int UDP_PORT = 54777;
    public static final String HOST = "127.0.0.1";
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

    /**
     * Every class here should have empty constructor
     * @param kryo kryo object of connection
     */
    public static void registerClasses(Kryo kryo) {
        kryo.setReferences(true);

        /* java objects */
        kryo.register(HashMap.class);
        kryo.register(ArrayList.class);
        kryo.register(Vector2.class);
        kryo.register(HashSet.class);

        /* DTOs */
        kryo.register(JSONMessage.class);
        kryo.register(JSONMessage.Type.class);
        kryo.register(com.ap.stardew.models.LobbyInfo.class);
        kryo.register(PlayerState.class);
        kryo.register(Player.State.class);
        kryo.register(AccountInfo.class);
        kryo.register(Result.class);

        kryo.register(GameMap.class);
        kryo.register(Date.class);
        kryo.register(Season.class);
        kryo.register(BiomeType.class);
        kryo.register(BiomeType[].class);
        kryo.register(BiomeType[][].class);
        kryo.register(MapRegion.class);
        kryo.register(MapRegion[].class);
        kryo.register(MapRegion[][].class);
        kryo.register(Weather.class);
        kryo.register(WorldMap.class);
        kryo.register(EntityList.class);
        kryo.register(Environment.class);
        kryo.register(Position.class);
        kryo.register(Vector2.class);
        kryo.register(Vec2.class);
        kryo.register(TileType.class);
        kryo.register(TileType[].class);
        kryo.register(TileType[][].class);
        kryo.register(TileType[][].class);
        kryo.register(Player.class);
        kryo.register(Entity.class);
        kryo.register(EntityComponent.class);
        kryo.register(Inventory.class);
        kryo.register(InventorySlot.class);
        kryo.register(PositionComponent.class);
        kryo.register(Renderable.class);
        kryo.register(Energy.class);
        kryo.register(Wallet.class);
        kryo.register(SkillType.class);
        kryo.register(Skill.class);
        kryo.register(NPC.class);
        kryo.register(NpcFriendship.class);
        kryo.register(PlayerFriendship.class);
        kryo.register(Gift.class);
        kryo.register(Message.class);
        kryo.register(TradeOffer.class);
        kryo.register(Rectangle.class);
        kryo.register(Renderable.Statue.class);
        kryo.register(Gender.class);
        kryo.register(EntityTag.class);
        kryo.register(InteriorComponent.class);
        kryo.register(Placeable.class);
        kryo.register(Door.class);
        kryo.register(CollisionEvent.class);
        kryo.register(Recipe.class);
        kryo.register(Ingredient.class);
        kryo.register(RecipeType.class);




        kryo.register(Game.class);
        kryo.register(WorldMap.class);
        kryo.register(Tile[][].class);
        kryo.register(Tile[].class);
        kryo.register(Tile.class);

//        kryo.register(GameMap.class, new GameMapSerializer());
    }
}
