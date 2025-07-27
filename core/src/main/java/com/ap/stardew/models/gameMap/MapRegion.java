package com.ap.stardew.models.gameMap;

import com.ap.stardew.models.Position;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.views.old.inGame.Color;

import java.io.Serializable;
import java.util.ArrayList;

public class MapRegion implements Serializable {
    private String name;
    private Player owner;
    private ArrayList<Tile> tiles = new ArrayList<>();
    private Color color;
    private Position center = new Position(0, 0);
    private boolean isFarm = false;

    public void addTile(Tile tile) {
        this.center.scl(this.tiles.size()).add(tile.getPosition()).scl(1f / (this.tiles.size() + 1));
        tiles.add(tile);
    }

    public boolean hasTile(Tile tile) {
        return this.tiles.contains(tile);
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public MapRegion(String name, Color color, boolean isFarm) {
        this.name = name;
        this.color = color;
        this.isFarm = isFarm;
    }

    public Color getColor() {
        return color;
    }

    public Position getCenter() {
        return center;
    }

    public String getName() {
        return name;
    }

    public void setOwner(Player player) {
        this.owner = player;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean isFarm() {
        return isFarm;
    }
}
