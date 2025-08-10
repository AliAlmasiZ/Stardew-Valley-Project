package com.ap.stardew.controllers;

import com.ap.stardew.models.Game;
import com.ap.stardew.models.player.Gift;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.friendship.PlayerFriendship;

public class GameStaticController {
    public static void giftRate(int giftNumber, int rating, Game game, Player giftRater) {
        Gift gift = giftRater.findGift(giftNumber);


        gift.setRating(rating);

        PlayerFriendship playerFriendship = game.getFriendshipWith(gift.getSender());
        if (rating < 3) playerFriendship.reduceXp((3 - rating) * 30 - 15);
        else playerFriendship.addXp((rating - 3) * 30 + 15);
    }

}
