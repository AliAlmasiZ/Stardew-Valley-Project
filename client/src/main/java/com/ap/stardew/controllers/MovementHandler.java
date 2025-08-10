package com.ap.stardew.controllers;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.building.Door;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Placeable;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.gameMap.GameMap;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.player.Player;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.math.Vector2;

public class MovementHandler {

    public static class MoveResult {
        public Tile destination;
        public Vector2 adjustedDirection;
        public boolean canMove;

        public MoveResult(Tile destination, Vector2 adjustedDirection, boolean canMove) {
            this.destination = destination;
            this.adjustedDirection = adjustedDirection;
            this.canMove = canMove;
        }
    }

    public static MoveResult tryMove(Player player, Vector2 direction) {
        GameMap map = ClientApp.getActiveGame().getActiveMap();
        Position playerPos = player.getPosition().cpy();

        Vector2[] directions = {
            direction.cpy(),
            new Vector2(0, direction.y),
            new Vector2(direction.x, 0)
        };

        for (Vector2 dir : directions) {
            Tile destTile = map.getTileByPosition(playerPos.cpy().add(dir));
            if(destTile == null)
                continue;


            // Check Tile Placeable
            Entity contentEntity = destTile.getContent();
            if(contentEntity != null) {
                Placeable placeable = contentEntity.getComponent(Placeable.class);
                if(!placeable.isWalkable())
                    continue;

                if(contentEntity instanceof Door door) {
                    EntityPlacementSystem.placeOnMap(player, door.getDestination(), door.getDestination().getMap());
                    return new MoveResult(destTile, dir, true);
                }
            }

            if(!destTile.isWalkable())
                continue;
            //Can move
            return new MoveResult(destTile, dir, true);
        }
        // Can not move in this direction
        return new MoveResult(null, direction.cpy(), false);
    }
}
