package com.ap.stardew.models.shop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ap.stardew.models.entities.Registry;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;

public class ShopRegistry extends Registry<ShopData> implements Serializable {

    @Override
    public void loadJson(JsonNode jsonRoot, ObjectMapper mapper, Path path) throws IOException {
        ShopData[] shopDatas = mapper.treeToValue(jsonRoot, ShopData[].class);
        for (ShopData shopData : shopDatas) {
            registry.putIfAbsent(shopData.name, shopData);
        }
    }

    @Override
    public ShopData getData(String name) {
        return registry.get(name);
    }
}
