package models.gameMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.json.JsonMapper;
import models.enums.TileType;
import views.inGame.Color;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class ObjectPropertyDeserializer extends JsonDeserializer<MapData.ObjectProperty> {
    @Override
    public MapData.ObjectProperty deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);

        MapData.ObjectProperty result = new MapData.ObjectProperty();
        result.name = node.get("name").asText();
        result.type = node.get("type").asText();

        switch (result.type) {
            case "int" -> {
                result.asInt = node.get("value").asInt();
            }
            case "double" -> {
                result.asDouble = node.get("value").asDouble();
            }
            case "string" -> {
                result.asString = node.get("value").asText();
            }
            case "bool" -> {
                result.asBoolean = node.get("value").asBoolean();
            }
        }
        return result;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class MapObject {
    @JsonProperty("gid")
    int gid;
    @JsonProperty("id")
    int id;
    @JsonProperty("x")
    int x;
    @JsonProperty("y")
    int y;
    @JsonProperty("width")
    int width;
    @JsonProperty("height")
    int height;
    @JsonProperty("properties")
    ArrayList<MapData.ObjectProperty> properties;
    @JsonProperty("type")
    String type;
    TileData data;
}

enum LayerType {
    tilelayer,
    objectgroup
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = MapLayerDeserializer.class)
class MapLayer {
    public String name;
    public int width;
    public int height;
    private int[][] data2d;
    public TileData[][] data;
    public ArrayList<MapObject> objects = new ArrayList<>();
    public LayerType type;

    public MapLayer(String name, int width, int height, int[] data) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.type = LayerType.tilelayer;

        this.data2d = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.data2d[i][j] = data[i * width + j];
            }
        }
    }

    public MapLayer(String name, MapObject[] objects) {
        this.objects.addAll(Arrays.asList(objects));
        this.name = name;
        this.type = LayerType.objectgroup;
    }

    public void parseData(Map<Integer, TileData> tileMap) {
        switch (type) {
            case tilelayer -> {
                this.data = new TileData[height][width];
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        data[i][j] = tileMap.get(data2d[i][j]);
                    }
                }
            }
            case objectgroup -> {
                for (MapObject o : objects) {
                    o.data = tileMap.get(o.gid);
                }
            }
        }
    }
}

class MapLayerDeserializer extends JsonDeserializer<MapLayer> {
    @Override
    public MapLayer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);

        String name = node.get("name").asText();
        LayerType type = LayerType.valueOf(node.get("type").asText());

        switch (type) {
            case tilelayer -> {
                int width = node.get("width").asInt();
                int height = node.get("height").asInt();
                JsonNode dataNode = node.get("data");
                int[] data1d = new int[dataNode.size()];
                for (int i = 0; i < dataNode.size(); i++) {
                    data1d[i] = dataNode.get(i).asInt();
                }
                return new MapLayer(name, width, height, data1d);
            }
            case objectgroup -> {
                JsonNode objectsNode = node.get("objects");
                MapObject[] objects = p.getCodec().treeToValue(objectsNode, MapObject[].class);

                return new MapLayer(name, objects);
            }
            default -> throw new IllegalStateException("Unexpected layer type in map json: " + type);
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapData {
    public int width, height;
    private Map<String, MapLayer> layers = new HashMap<>();
    private MapLayerData<TileType> mainLayer = null;
    private MapLayerData<MapRegion> regionsLayer = null;
    private MapLayerData<BiomeType> biomeLayer = null;
    private MapLayerData<String> buildingLayer = null;
    private MapLayerData<String> doorLayer = null;
    private MapLayerData<String> entityLayer = null;
    private MapLayerData<String> npcLayer = null;
    private Map<String, TileSet> tileSets = new HashMap<>();
    private ArrayList<MapRegion> regions = new ArrayList<>();
    private Map<Integer, TileData> tileMap = new HashMap<>();
    public String name;

    @JsonCreator
    MapData(@JsonProperty("layers") MapLayer[] layersArray, @JsonProperty("tilesets") TileSetReference[] tileSetReferences) {
        for (MapLayer m : layersArray) {
            this.layers.putIfAbsent(m.name, m);
        }
        if ((layers.get("ground")) == null) {
            throw new RuntimeException("no layer with the name \"ground\" was found in the layers of . the map needs a ground layer.");
        }

        this.width = layers.get("ground").width;
        this.height = layers.get("ground").height;

        JsonMapper mapper = new JsonMapper();
        for (TileSetReference t : tileSetReferences) {
            try {
                TileSet tileset = mapper.readValue(new File(t.path.substring(t.path.lastIndexOf("../") + 3)), TileSet.class);
                tileset.firstgid = t.firstgid;
                this.tileSets.putIfAbsent(tileset.name, tileset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (tileSets.get("ground") == null) {
            throw new RuntimeException("no tileset with the name \"ground\" was found in the tilesets. the map needs a ground tileset.");
        }
        for (TileSet t : tileSets.values()) {
            for (TileData d : t.tiles) {
                d.globalId = d.id + t.firstgid;
                tileMap.putIfAbsent(d.globalId, d);
            }
        }
        tileMap.putIfAbsent(0, new TileData());

        for (MapLayer l : layers.values()) {
            l.parseData(tileMap);
            switch (l.name) {
                case "ground" -> {
                    mainLayer = new MapLayerData<>(TileType.class, layers.get("ground"), tileSets.get("ground"));
                    for (TileData t : mainLayer.tileSet.tiles) {
                        try {
                            TileType type = TileType.valueOf(t.type);
                            mainLayer.dataMap.putIfAbsent(t.globalId, type);
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException("tile type \"" + t.type + "\" doesn't exist");
                        }
                    }
                    mainLayer.populateData();
                }
                case "region" -> {
                    regionsLayer = new MapLayerData<>(MapRegion.class, layers.get("region"), tileSets.get("region"));
                    for (TileData t : regionsLayer.tileSet.tiles) {
                        MapRegion region = new MapRegion(t.type, new Color(Math.random(), Math.random(), Math.random()), t.getProperty("isFarm") != null);
                        regions.add(region);
                        regionsLayer.dataMap.putIfAbsent(t.globalId, region);
                    }
                    regionsLayer.populateData();
                }
                case "biome" -> {
                    biomeLayer = new MapLayerData<>(BiomeType.class, layers.get("biome"), tileSets.get("biome"));
                    for (TileData t : biomeLayer.tileSet.tiles) {
                        try {
                            BiomeType type = BiomeType.valueOf(t.type);
                            biomeLayer.dataMap.putIfAbsent(t.globalId, type);
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException("tile type \"" + t.type + "\" doesn't exist");
                        }
                    }
                    biomeLayer.populateData();
                }
                case "building" -> {
                    buildingLayer = new MapLayerData<>(String.class, layers.get("building"), tileSets.get("buildings"));
                    for (TileData t : buildingLayer.tileSet.tiles) {
                        buildingLayer.dataMap.putIfAbsent(t.globalId, t.type);
                    }
                    buildingLayer.populateData();
                }
                case "door" -> {
                    doorLayer = new MapLayerData<>(String.class, layers.get("door"), tileSets.get("objects"));
                    for (TileData t : doorLayer.tileSet.tiles) {
                        doorLayer.dataMap.putIfAbsent(t.globalId, t.type);
                    }
                    doorLayer.populateData();
                }
                case "entity" -> {
                    entityLayer = new MapLayerData<>(String.class, layers.get("entity"), tileSets.get("objects"));
                    for (TileData t : entityLayer.tileSet.tiles) {
                        entityLayer.dataMap.putIfAbsent(t.globalId, t.type);
                    }
                    entityLayer.populateData();
                }
                case "npc" -> {
                    npcLayer = new MapLayerData<>(String.class, layers.get("npc"), null);
                    npcLayer.populateData();
                }
            }
        }
    }

    public TileType[][] getTypeMap() {
        return mainLayer.getDataArray();
    }

    public MapRegion[][] getRegionMap() {
        if (regionsLayer == null) return null;
        return regionsLayer.getDataArray();
    }

    public ArrayList<MapRegion> getRegions(){
        return this.regions;
    }

    public BiomeType[][] getBiomeMap() {
        if (biomeLayer == null) return null;
        return biomeLayer.getDataArray();
    }

    public ArrayList<MapLayerData<String>.ObjectData> getBuildings() {
        if (buildingLayer == null) return null;
        return buildingLayer.getObjectArray();
    }

    public ArrayList<MapLayerData<String>.ObjectData> getDoors() {
        if (doorLayer == null) return null;
        return doorLayer.getObjectArray();
    }
    public ArrayList<MapLayerData<String>.ObjectData> getEntities() {
        if (entityLayer == null) return null;
        return entityLayer.getObjectArray();
    }
    public ArrayList<MapLayerData<String>.ObjectData> getNpcs() {
        if (npcLayer == null) return null;
        return npcLayer.getObjectArray();
    }

    public static MapData parse(String name, String path) {
        Path file = Paths.get(path);

        JsonMapper mapper = new JsonMapper();
        MapData data;
        try {
            data = mapper.treeToValue(mapper.readTree(file.toFile()), MapData.class);
            data.name = name;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    @JsonDeserialize(using = ObjectPropertyDeserializer.class)
    public static class ObjectProperty {
        public String name;
        public String type;
        public int asInt;
        public double asDouble;
        public String asString;
        public boolean asBoolean;
    }

    static public class MapLayerData<T>{
        public class ObjectData{
            public T value;
            public String type;
            public int x, y;
            public ArrayList<ObjectProperty> properties = new ArrayList<>();

            public ObjectData(T value, int x, int y, ArrayList<ObjectProperty> properties) {
                this.value = value;
                this.x = x ;
                this.y = y;
                this.properties.addAll(properties);
            }

            public ObjectData(int x, int y, ArrayList<ObjectProperty> properties, String type) {
                this.type = type;
                this.x = x;
                this.y = y;
                this.properties = properties;
            }

            public ObjectProperty getProperty(String name) {
                for (ObjectProperty p : properties) {
                    if (p.name.equals(name)) {
                        return p;
                    }
                }
                return null;
            }
        }

        public MapLayer layer;
        public TileSet tileSet;
        public Map<Integer, T> dataMap = new HashMap<>();
        private T[][] dataArray;
        private ArrayList<ObjectData> objectArray = new ArrayList<>();
        private final Class<T> type;


        public T[][] getDataArray() {
            return dataArray;
        }

        public MapLayerData(Class<T> type, MapLayer layer, TileSet tileSet) {
            this.layer = layer;
            this.tileSet = tileSet;
            this.type = type;
        }

        public ArrayList<ObjectData> getObjectArray() {
            return objectArray;
        }

        public void populateData() {
            switch (layer.type) {
                case tilelayer -> {
                    @SuppressWarnings("unchecked")
                    T[][] arr = (T[][]) Array.newInstance(type, layer.height, layer.width);
                    this.dataArray = arr;
                    for (int i = 0; i < layer.height; i++) {
                        for (int j = 0; j < layer.width; j++) {
                            dataArray[i][j] = dataMap.get(layer.data[i][j] != null ? layer.data[i][j].globalId : 0);
                        }
                    }
                }
                case objectgroup -> {
                        for (MapObject o : layer.objects) {
                            if(!o.type.isEmpty()){
                                objectArray.add(this.new ObjectData(o.x, o.y - (o.height - 1)
                                        , o.properties != null ? o.properties : new ArrayList<>(), o.type));
                            }else{
                            objectArray.add(this.new ObjectData(dataMap.get(o.gid), o.x, o.y - (o.height - 1)
                                    , o.properties != null ? o.properties : new ArrayList<>()));
                            }
                        }
                }
            }
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class TileData {
    @JsonProperty("id")
    public int id;
    @JsonProperty("type")
    public String type;
    @JsonProperty("properties")
    public ArrayList<MapData.ObjectProperty> properties;
    public int globalId;
    public MapData.ObjectProperty getProperty(String name) {
        if(this.properties == null) return null;
        for (MapData.ObjectProperty p : properties) {
            if (p.name.equals(name)) {
                return p;
            }
        }
        return null;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class TileSet {
    @JsonProperty("tiles")
    public TileData[] tiles;
    @JsonProperty("name")
    public String name;

    public int firstgid;
}

@JsonIgnoreProperties(ignoreUnknown = true)
class TileSetReference {
    @JsonProperty("firstgid")
    public int firstgid;
    @JsonProperty("source")
    public String path;
}


