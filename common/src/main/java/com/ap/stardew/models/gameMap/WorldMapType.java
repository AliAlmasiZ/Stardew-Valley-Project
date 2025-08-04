package com.ap.stardew.models.gameMap;

import com.ap.stardew.models.App;

public enum WorldMapType {
    DEFAULT("bigMap"),
    BIG("bigMap");
    private final String mapName;

    WorldMapType(String mapName) {
        this.mapName = mapName;
    }

    public MapData getData() {
        return App.mapRegistry.getData(mapName);
    }


}
