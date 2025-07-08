package models.gameMap;

import models.Position;
import models.entities.Entity;
import models.entities.EntityObserver;
import models.enums.TileType;
import models.player.Player;
import views.inGame.Color;

import java.io.Serializable;

public class Tile implements Serializable, EntityObserver {    private TileType type;
    final private Position position;
    private Entity content;
    private final GameMap map;

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
}
