package com.ap.stardew.models.gameMap;

import com.ap.stardew.models.Position;
import com.ap.stardew.models.player.Player;
import java.io.Serializable;
import java.util.ArrayList;

public class MapRegion implements Serializable {
    private String name;
    private Player owner;
    private Position center = new Position(0, 0);
    private boolean isFarm = false;
    private int tilesNum = 0;

    public void addTile(Tile tile) {
        this.center.scl(tilesNum).add(tile.getPosition()).scl(1f / (tilesNum + 1));
        tilesNum++;
    }

    public MapRegion(String name, boolean isFarm) {
        this.name = name;
        this.isFarm = isFarm;
    }

    private MapRegion(){

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
