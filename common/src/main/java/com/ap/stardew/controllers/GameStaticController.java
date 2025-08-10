package com.ap.stardew.controllers;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Pickable;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.player.Gift;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.friendship.PlayerFriendship;

public class GameStaticController {
    public static void giftRate(int giftNumber, int rating, Game game, Player giftRater) {
        Gift gift = giftRater.findGift(giftNumber);


        gift.setRating(rating);

        PlayerFriendship playerFriendship = game.getFriendshipBetween(gift.getSender(), gift.getReceiver());
        if (rating < 3) playerFriendship.reduceXp((3 - rating) * 30 - 15);
        else playerFriendship.addXp((rating - 3) * 30 + 15);
    }

    public static Result cheatGiveItem(Game game, Player currentPlayer, String name, int quantity) {
        if (quantity <= 0) {
            return new Result(false, "You should enter positive number!");
        }
        if (!App.entityRegistry.doesEntityExist(name)) {
            return new Result(false, "Entity doesnt exist");
        }
        Entity entity = App.entityRegistry.makeEntity(name);
        if (!currentPlayer.getComponent(Inventory.class).canAddItem(entity, quantity))
            return new Result(false, "Your inventory doesn't have enough size");
        if (entity.getComponent(Pickable.class) == null) {
            return new Result(false, "Entity isn't pickable");
        }
        entity.getComponent(Pickable.class).changeStackSize(quantity);
        currentPlayer.getComponent(Inventory.class).addItem(entity);
        return new Result(true, quantity + " " + name + (quantity > 1 ? "s" : "") +
            " were given to " + currentPlayer.getNickname());
    }


}
