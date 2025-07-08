package com.ap.stardew.records;

import com.ap.stardew.models.Account;
import com.ap.stardew.models.gameMap.MapRegion;

import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;

public record GameStartingDetails(boolean success, String message,
                                  Account[] accounts,
                                  Queue<Account> notChosen,
                                  Map<Account, MapRegion> selections,
                                  ArrayList<MapRegion> availableRegions){

    public GameStartingDetails(boolean success, String message) {
        this(success, message, null, null, null, null);
    }
}
