package com.ap.stardew.models.player.friendship;

import java.io.Serializable;

public class AnimalFriendship extends Friendship implements Serializable {
    boolean wasPetToday;
    boolean wasFedToday;

    @Override
    void changeAmount(int amount) {

    }
}
