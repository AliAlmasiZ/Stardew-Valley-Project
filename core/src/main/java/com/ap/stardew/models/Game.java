package com.ap.stardew.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ap.stardew.models.NPC.Dialogue;
import com.ap.stardew.models.NPC.NPC;
import com.ap.stardew.models.NPC.NpcFriendship;
import com.ap.stardew.models.NPC.Quest;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Growable;
import com.ap.stardew.models.entities.components.InteriorComponent;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.entities.systems.ForageSpawnSystem;
import com.ap.stardew.models.entities.systems.GrowthSystem;
import com.ap.stardew.models.entities.systems.ShopSystem;
import com.ap.stardew.models.enums.EntityTag;
import com.ap.stardew.models.enums.Season;
import com.ap.stardew.models.enums.TileType;
import com.ap.stardew.models.enums.Weather;
import com.ap.stardew.models.gameMap.*;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.Skill;
import com.ap.stardew.models.player.Wallet;
import com.ap.stardew.models.player.friendship.PlayerFriendship;
import com.ap.stardew.models.utils.StringUtils;
import com.ap.stardew.records.GameStartingDetails;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Game implements Serializable {
    private Weather todayWeather;
    private Weather tomorrowWeather;
    private Date date = new Date();
    private WorldMap mainMap;
    private ArrayList<Player> players = new ArrayList<>();
    private Player currentPlayer;
    private ArrayList<PlayerFriendship> playerFriendships = new ArrayList<>();
    private ArrayList<NPC> gameNPCs = new ArrayList<>();
    private boolean mapVisible = true;
    private ArrayList<Quest> quests = new ArrayList<>();
    private int tradeId = 1000;

    public Game() {

    }

    public void setMainMap(WorldMap mainMap) {
        this.mainMap = mainMap;
    }

    public void initGame(GameStartingDetails details) {
        for (Account account : details.accounts()) {
            addPlayer(new Player(account));
        }
        setCurrentPlayer(players.get(0));
        players.get(0).setPosition(new Position(10, 10));

        mainMap = new WorldMap("./Content(unpacked)/Maps/TestMap.tmx");
        setActiveMap(mainMap);
//        setActiveMap(mainMap);
//
        this.todayWeather = Weather.SUNNY;
        this.tomorrowWeather = Weather.SUNNY;
//
//        // init player's friendships
//        for (int i = 0; i < players.size(); i++) {
//            for (int j = i + 1; j < players.size(); j++) {
//                this.playerFriendships.add(new PlayerFriendship(players.get(i), players.get(j)));
//            }
//        }

        //player farms
//        Map<MapRegion, FarmDetails> farmsDetails = mainMap.getFarmsDetail();
//
//        for(Player player : players){
//            MapRegion region = details.selections().get(player.getAccount());
//            player.addRegion(region);
//            player.setCurrentMap(mainMap);
//
//            MapData.MapLayerData<String>.ObjectData houseDetails = farmsDetails.get(region).cottage;
//            MapData.MapLayerData<String>.ObjectData greenHouseDetails = farmsDetails.get(region).abandonedGreenhouse;
//
//            player.setHouse(App.entityRegistry.makeEntity(houseDetails.value));
//            EntityPlacementSystem.placeEntity(player.getHouse(), new Vec2(houseDetails.x, houseDetails.y), mainMap);
//            player.setGreenHouse(App.entityRegistry.makeEntity(greenHouseDetails.value));
//            EntityPlacementSystem.placeEntity(player.getGreenHouse(), new Vec2(greenHouseDetails.x, greenHouseDetails.y), mainMap);
//            for(Entity e : player.getHouse().getComponent(InteriorComponent.class).getMap().getEntities()){
//                if(StringUtils.isNamesEqual(e.getEntityName(), StringUtils.fridge)){
//                    player.setRefrigerator(e);
//                }
//            }
//
//            EntityPlacementSystem.placeOnMap(player, new Position(2, 2), player.getHouse().getComponent(InteriorComponent.class).getMap());
//        }

        //mainMap.initRandomElements();
        initNPCs();
        //currentPlayer.getOwnedTiles();


    }

    public void initNPCs() {
        // create NPC
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<NPC> NPCs = objectMapper.readValue(new File("data/NPC/npcNames.json"),
                    new TypeReference<List<NPC>>() {});

            for (Entity npc : NPCs) {
            NPC realNPC = new NPC(npc.getEntityName());
                gameNPCs.add(realNPC);
                for (Player player : players) {
                    player.getNpcFriendships().put(realNPC, new NpcFriendship());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //init quests
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Quest> quests = mapper.readValue(
                    new File("data/NPC/quests.json"),
                    new TypeReference<List<Quest>>() {}
            );

            for (Quest quest : quests) {
                quest.setNpc(findNPC(quest.getNpcName()));
                this.quests.add(quest);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Initialize favorites
        for (NPC npc : gameNPCs) {
            try {
                ObjectMapper mapper = new ObjectMapper();

                ArrayList<String> list = mapper.readValue(
                        new File("data/NPC/Favorites/" + npc.getEntityName() + ".json"),
                        mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class)
                );

                npc.setFavorites(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        // Initialize dialogs
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Dialogue> dialogues = mapper.readValue(
                    new File("data/NPC/dialogues.json"),
                    new TypeReference<List<Dialogue>>() {}
            );

            for (Dialogue dialogue : dialogues) {
                NPC npc = findNPC(dialogue.getNpcName());
                npc.getDialogues().add(dialogue);
            }

        } catch (IOException e) {
            e.printStackTrace(); // Prints the reason for failure
        }

        //initialize

        //TODO: Put NPC on the Map
//        ArrayList<MapData.MapLayerData<String>.ObjectData> npcDatas = WorldMapType.DEFAULT.getData().getNpcs();
//        for (NPC npc : gameNPCs) {
//            MapData.MapLayerData<String>.ObjectData data = null;
//            for (MapData.MapLayerData<String>.ObjectData d : npcDatas) {
//                if(d.type.equals(npc.getName())){
//                    data = d;
//                }
//            }
//            EntityPlacementSystem.placeOnMap(npc, new Position(data.x, data.y), mainMap);
//        }

    }

    public ArrayList<Quest> getQuests() {
        return quests;
    }

    public void setQuests(ArrayList<Quest> quests) {
        this.quests = quests;
    }

    public NPC findNPC(String npcName) {
        for (NPC npc : gameNPCs) {
            if (npc.getName().equals(npcName)) {
                return npc;
            }
        }
        return null;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void nextTurn(){
        ArrayList<Player> players = getPlayers();
        int index = players.indexOf(getCurrentPlayer());

        if (index == getPlayers().size() - 1) {
            setCurrentPlayer(players.get(0));
            date.addHour(1, this);
        } else {
            setCurrentPlayer(players.get(index + 1));
        }

        if (currentPlayer.isGhashed()) {
            nextTurn();
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public GameMap getActiveMap() {
        return currentPlayer.getCurrentMap();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Weather getTodayWeather() {
        return todayWeather;
    }

    public int getTradeId() {
        tradeId++;
        return tradeId;

    }

    public void setTodayWeather(Weather todayWeather) {
        this.todayWeather = todayWeather;
    }

    public Weather getTomorrowWeather() {
        return tomorrowWeather;
    }

    public void setTomorrowWeather(Weather tomorrowWeather) {
        this.tomorrowWeather = tomorrowWeather;
    }

    public boolean checkPlayerDistance(Player player1, Player player2) {
        Position position1 = player1.getPosition();
        Position position2 = player2.getPosition();

        int distance = (position1.getRow() - position2.getRow()) * (position1.getRow() - position2.getRow())
                + (position1.getCol() - position2.getCol()) * (position1.getCol() - position2.getCol());

        return distance < 3;
    }

    public void crowAttack() {
        for (Player player : players) {
            ArrayList<Tile> tiles = player.getOwnedPlantedTiles();


            for (int i = 0; i < Math.floor((double) tiles.size() / 16); i++) {
                if (Math.random() < 0.75) continue;
                Tile tile = tiles.get((int) (Math.random() * tiles.size()));
                Entity entity = tile.getContent();
                if (entity != null && (entity.hasTag(EntityTag.CROP) || entity.hasTag(EntityTag.FORAGING_CROP))) {
                    entity.delete();
                }
                if (entity != null && entity.hasTag(EntityTag.TREE)) {
                    Growable growable = entity.getComponent(Growable.class);
                    if (growable.getDaysPastFromRegrowth() == growable.getRegrowthTime()) {
                        growable.setRegrowthTime(growable.getRegrowthTime() - 1);
                    }else if (growable.getTotalHarvestTime() == growable.getDaysPastFromPlant()) {
                        growable.setDaysPastFromPlant(growable.getDaysPastFromPlant() - 1);
                    }

                }
            }
        }
    }

    public void dailyThor() {
        if (!getTodayWeather().equals(Weather.STORMY)) {
            return;
        }

        for (Player player : players) {
            ArrayList<Tile> affectedTiles = player.getOwnedTiles();

            for (int i = 0; i < 5; i++) {
                Tile tile = affectedTiles.get((int) (Math.random() * affectedTiles.size()));
                thorTile(tile);
            }

        }
    }



    public void thorTile(Tile tile) {
        if (tile.getContent() != null &&
                (tile.getContent().hasTag(EntityTag.CROP) || tile.getContent().hasTag(EntityTag.FORAGING_CROP))) {
            EntityPlacementSystem.emptyTile(tile);
            tile.setType(TileType.DIRT);
        }

        if (tile.getContent() != null && tile.getContent().hasTag(EntityTag.TREE)) {
            EntityPlacementSystem.emptyTile(tile);
            EntityPlacementSystem.placeOnTile(App.entityRegistry.makeEntity("Burned Tree"), tile);
            tile.setType(TileType.DIRT);
        }

    }

    public void marry(Player girl, Player boy) {
        girl.setSpouse(boy);
        boy.setSpouse(girl);

        // combining wallets
        Wallet boyWallet = boy.getWallet();
        Wallet girlWallet = girl.getWallet();
        Wallet wallet = Wallet.combineWallets(boyWallet, girlWallet);

        boy.setWallet(wallet);
        girl.setWallet(wallet);

        //set friendship
        PlayerFriendship playerFriendship = getFriendshipBetween(girl, boy);
        playerFriendship.setLevel(4);

        //TODO: other effects

    }


    public void setActiveMap(GameMap map) {
        this.currentPlayer.setCurrentMap(map);
    }

    public void toggleMapVisibility(){
        mapVisible = !mapVisible;
    }

    public boolean isMapVisible(){
        return mapVisible;
    }

    public Player findPlayer(String playerName) {
        for (Player player : players) {
            if (player.getAccount().getUsername().equals(playerName)) {
                return player;
            }
        }

        return null;
    }

    public ArrayList<PlayerFriendship> getCurrentPlayerFriendships() {
        ArrayList<PlayerFriendship> friendships = new ArrayList<>();
        for (PlayerFriendship playerFriendships : playerFriendships) {
            if (playerFriendships.getFriends().contains(currentPlayer)) {
                friendships.add(playerFriendships);
            }
        }

        return friendships;
    }

    public PlayerFriendship getFriendshipWith(Player friend) {
        for (PlayerFriendship playerFriendship : playerFriendships) {
            if (playerFriendship.getFriends().contains(friend) &&
            playerFriendship.getFriends().contains(currentPlayer)) {
                return playerFriendship;
            }
        }
        return null;
    }

    public PlayerFriendship getFriendshipBetween(Player friend1, Player friend2) {
        for (PlayerFriendship playerFriendship : playerFriendships) {
            if (playerFriendship.getFriends().contains(friend1) &&
                    playerFriendship.getFriends().contains(friend2)) {
                return playerFriendship;
            }
        }
        return null;
    }

    public Quest findQuest(int questId) {
        for (Quest quest : quests) {
            if (quest.getId() == questId) {
                return quest;
            }
        }

        return null;
    }

    public WorldMap getMainMap() {
        return mainMap;
    }

    /**
      this is the ugliest function of the project, it will take the season and fishing skill
      and return name of available fish for fishing. I have done this to avoid making new classes
      for fish...
     */
    public ArrayList<String> getAvailableFish(Season season, Skill fishingSkill) {
        ArrayList<String> availableFish = new ArrayList<>();
        switch (season) {
            case SPRING -> {
                availableFish.add("Ghostfish");
                availableFish.add("Flounder");
                availableFish.add("Lionfish");
                availableFish.add("Herring");
                if (fishingSkill.getLevel() >= 4) {
                    availableFish.add("Legend");
                }
            }
            case SUMMER -> {
                availableFish.add("Tilapia");
                availableFish.add("Dorado");
                availableFish.add("Sunfish");
                availableFish.add("Rainbow Trout");
                if (fishingSkill.getLevel() >= 4) {
                    availableFish.add("Crimsonfish");
                }

            }
            case FALL -> {
                availableFish.add("Salmon");
                availableFish.add("Sardine");
                availableFish.add("Shad");
                availableFish.add("Blue Discus");
                if (fishingSkill.getLevel() >= 4) {
                    availableFish.add("Angler");
                }
            }
            case WINTER -> {
                availableFish.add("Midnight Carp");
                availableFish.add("Squid");
                availableFish.add("Tuna");
                availableFish.add("Perch");
                if (fishingSkill.getLevel() >= 4) {
                    availableFish.add("Glacierfish");
                }

            }
        }

        return availableFish;
    }

    //TODO: important!! complete them
    public void updateGamePerHour() {
        // this function should update things related to game
        for (Player player : players) {
            player.updatePerHour();
        }

    }

    public void updateGamePerDay() {
        // this function should update things related to game

        // handling weather changes
        todayWeather = tomorrowWeather;
        tomorrowWeather = this.date.getSeason().getWeather();

        for (PlayerFriendship playerFriendship : playerFriendships) {
            playerFriendship.updateDaily();
        }

        GrowthSystem.updatePerDay(this.mainMap);

        ForageSpawnSystem.updatePerDay();

        ShopSystem.updatePerDay();

        for (Player player : players) {
            player.updatePerDay();
        }

        for (Quest quest : quests) {
            quest.reduceDaysToUnlocked();
        }

        dailyThor();

        crowAttack();
    }

}
