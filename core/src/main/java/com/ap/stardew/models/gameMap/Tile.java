package com.ap.stardew.models.gameMap;

import com.ap.stardew.models.Position;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.EntityObserver;
import com.ap.stardew.models.enums.TileType;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.views.old.inGame.Color;

import java.io.Serializable;

public class Tile implements Serializable, EntityObserver {    private TileType type;
    final private Position position;
    private Entity content;
    private final GameMap map;
    private boolean isWalkable = true;

    public Tile(Position position, TileType type/*, MapRegion region*/, GameMap map) {
        this.position = position;
        this.type = type;
        this.map = map;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public Entity getContent() {
        return content;
    }

    public void setContent(Entity content) {
        if (this.content != null) {
            this.content.removeObserveer(this);
        }
        this.content = content;
        if (this.content != null) {
            this.content.addObserver(this);
        }
    }

    public Position getPosition() {
        return position;
    }

    public int getRow() {
        return position.getRow();
    }

    public int getCol() {
        return position.getCol();
    }

    public Color getColor() {
        return this.type.color;
    }

    public char getCharacter() {
        try {
            return this.type.character;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public MapRegion getRegion() {
        if (map instanceof WorldMap) {
            return ((WorldMap) map).getRegion(this);
        } else return null;
    }

    public Player getOwner() {
        if (map instanceof WorldMap) {
            return ((WorldMap) map).getRegion(this).getOwner();
        } else return null;
    }

    public GameMap getMap() {
        return map;
    }

    @Override
    public void onDelete(Entity entity) {
        this.content = null;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    public void setWalkable(boolean walkable) {
        isWalkable = walkable;
    }
}
