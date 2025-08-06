package com.ap.stardew.models.dto.serilizers;


import com.ap.stardew.models.dto.GameMapInfo;
import com.ap.stardew.models.gameMap.GameMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class GameMapSerializer extends Serializer<GameMap> {
    @Override
    public void write(Kryo kryo, Output output, GameMap gameMap) {
        GameMapInfo info = new GameMapInfo(gameMap);
        kryo.writeObject(output, info);
    }

    @Override
    public GameMap read(Kryo kryo, Input input, Class<GameMap> aClass) {
        GameMapInfo info = kryo.readObject(input, GameMapInfo.class);
        return info.toGameMap();
    }
}
