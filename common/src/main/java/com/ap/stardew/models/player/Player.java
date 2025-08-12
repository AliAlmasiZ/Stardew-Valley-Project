package com.ap.stardew.models.player;

import com.ap.stardew.models.App;
import com.ap.stardew.models.NPC.NPC;
import com.ap.stardew.models.NPC.NpcFriendship;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.animal.Animal;
import com.ap.stardew.models.crafting.Recipe;
import com.ap.stardew.models.dto.PlayerState;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.EntityList;
import com.ap.stardew.models.entities.Renderable;
import com.ap.stardew.models.entities.components.*;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.components.inventory.InventorySlot;
import com.ap.stardew.models.enums.Gender;
import com.ap.stardew.models.enums.SkillType;
import com.ap.stardew.models.enums.Weather;
import com.ap.stardew.models.gameMap.GameMap;
import com.ap.stardew.models.gameMap.MapRegion;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.gameMap.WorldMap;
import com.ap.stardew.models.player.buff.Buff;
import com.ap.stardew.models.player.friendship.PlayerFriendship;
import com.ap.stardew.models.player.reaction.Emoji;
import com.ap.stardew.models.player.reaction.Reaction;
import com.ap.stardew.view.GameAssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends Entity implements Serializable {
    private Energy energy = new Energy();
    private Wallet wallet = new Wallet();
    private final Map<SkillType, Skill> skills = new HashMap<>();
    private int trashcanLevel;
    private Map<NPC, NpcFriendship> npcFriendships = new HashMap<>();
    private final Map<Player, PlayerFriendship> playerFriendships = null;
    private HashMap<Player, Entity> suitors = new HashMap<>();
    private Player spouse;
    private ArrayList<Gift> giftReceived = new ArrayList<>();
    private ArrayList<Gift> giftSent = new ArrayList<>();
    private int giftId = 1;
    private ArrayList<Message> messageLog = new ArrayList<>();
    private ArrayList<Recipe> unlockedRecipes;
    private ArrayList<TradeOffer> tradeOffers = new ArrayList<>(); //for phase one
    private ArrayList<TradeHistoryItem> trades = new ArrayList<>();
    private String accountUsername;
    private String nickname;
    private Gender gender;
    private InventorySlot activeSlot;
    private final ArrayList<MapRegion> ownedRegions = new ArrayList<>();
    private ArrayList<Animal> animals = new ArrayList<>();
    private final EntityList ownedBuildings = new EntityList();
    private Entity house;
    private Entity refrigerator;
    private Entity greenHouse;
    private Entity trashcan;
    private Buff activeBuff;
    //for graphic
    private Sprite sprite;
    private float stateTime = 0f;
    private Rectangle bounds;
    private float speed = 200f;
    private Action action = Action.IDLE;
    private Vector2 lastDir = new Vector2(0, -1);
    private ArrayList<Emoji> emojis = new ArrayList<>();
    private Reaction currentReaction = null;
    private Entity actionItem = null;

    private transient ArrayList<Tile> ownedTiles = null;

    // boolean for messages
    private boolean haveNewMessage = false;
    private boolean haveNewGift = false;
    private boolean haveNewTrade = false;
    private boolean haveNewSuitor = false;

    public Player(String username) {
        super("Player", new PositionComponent(0, 0));
        this.accountUsername = username;
    }
    private Player(){

    }

    public void initPlayer(){
        addComponent(new Inventory(30));

        action = Action.IDLE;
        unlockedRecipes = new ArrayList<>(App.recipeRegistry.getUnlockedRecipes());
        for (SkillType s : SkillType.values()) {
            skills.put(s, new Skill());
        }
        setActiveSlot(getComponent(Inventory.class).getSlots().get(0));
        Renderable renderable = new Renderable();
        addComponent(renderable);

        emojis.addAll(List.of(Emoji.SAD, Emoji.HEART, Emoji.YES, Emoji.NO, Emoji.ANGRY, Emoji.LAUGH));
    }

    public GameMap getCurrentMap() {
        return getPosition().getMap();
    }

    public void setRefrigerator(Entity refrigerator) {
        this.refrigerator = refrigerator;
    }

    public void setCurrentMap(GameMap currentMap) {
        if(this.getCurrentMap() == currentMap) return;

        if (this.getCurrentMap() != null) {
            this.getCurrentMap().removeEntity(this);
        }
        this.getPosition().setMap(currentMap);
        if (currentMap != null) {
            currentMap.addEntity(this);
        }
    }

    public int getTrashcanLevel() {
        return trashcanLevel;
    }

    public Entity getRefrigerator() {
        return refrigerator;
    }

    public void addTrashcanLevel(int trashcanLevel) {
        //TODO
    }

    public ArrayList<Gift> getGiftSent() {
        return giftSent;
    }

    public void addGiftSent(Gift gift) {
        giftSent.add(gift);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAccountUsername(String accountUsername) {
        this.accountUsername = accountUsername;
    }

    public ArrayList<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(ArrayList<Animal> animals) {
        this.animals = animals;
    }

    public HashMap<Player, Entity> getSuitors() {
        return suitors;
    }

    public void setSuitors(HashMap<Player, Entity> suitors) {
        this.suitors = suitors;
    }

    public ArrayList<Gift> getGiftReceived() {
        return giftReceived;
    }

    public void setTrashcanLevel(int trashcanLevel) {
        this.trashcanLevel = trashcanLevel;
    }

    public Map<Player, PlayerFriendship> getPlayerFriendships() {
        return playerFriendships;
    }

    public void setGiftSent(ArrayList<Gift> giftSent) {
        this.giftSent = giftSent;
    }

    public int getGiftId() {
        return giftId;
    }

    public void setGiftId(int giftId) {
        this.giftId = giftId;
    }

    public void setUnlockedRecipes(ArrayList<Recipe> unlockedRecipes) {
        this.unlockedRecipes = unlockedRecipes;
    }

    public void setTradeOffers(ArrayList<TradeOffer> tradeOffers) {
        this.tradeOffers = tradeOffers;
    }

    public ArrayList<TradeHistoryItem> getTrades() {
        return trades;
    }

    public void setTrades(ArrayList<TradeHistoryItem> trades) {
        this.trades = trades;
    }

    public String getAccountUsername() {
        return accountUsername;
    }

    public ArrayList<MapRegion> getOwnedRegions() {
        return ownedRegions;
    }

    public Entity getTrashcan() {
        return trashcan;
    }

    public void setTrashcan(Entity trashcan) {
        this.trashcan = trashcan;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Vector2 getLastDir() {
        return lastDir;
    }

    public void setLastDir(Vector2 lastDir) {
        if (lastDir.isZero()) return;
        this.lastDir = lastDir;
    }

    public ArrayList<Tile> getOwnedTiles() {
        return ownedTiles;
    }

    public void setOwnedTiles(ArrayList<Tile> ownedTiles) {
        this.ownedTiles = ownedTiles;
    }

    public boolean isHaveNewSuitor() {
        return haveNewSuitor;
    }

    public void setHaveNewSuitor(boolean haveNewSuitor) {
        this.haveNewSuitor = haveNewSuitor;
    }

    public ArrayList<TradeOffer> getTradeOffers() {
        return tradeOffers;
    }

    public ArrayList<TradeHistoryItem> getTradeHistoryWith(Player player) {
        ArrayList<TradeHistoryItem> tradeHistory = new ArrayList<>();
        for (TradeHistoryItem tradeHistoryItem : trades) {
            if (tradeHistoryItem.hasPlayer(player)) tradeHistory.add(tradeHistoryItem);
        }
        return tradeHistory;
    }

    public ArrayList<TradeHistoryItem> getTradeHistory() {
        return trades;
    }

    public void setGiftReceived(ArrayList<Gift> giftReceived) {
        this.giftReceived = giftReceived;
    }

    public Entity getHouse() {
        return house;
    }

    public void setHouse(Entity house) {
        this.house = house;
    }

    public boolean isHaveNewGift() {
        return haveNewGift;
    }

    public void setHaveNewGift(boolean haveNewGift) {
        this.haveNewGift = haveNewGift;
    }

    public void trashItem(Entity item) {

    }

    public Energy getEnergy() {
        return energy;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void setEnergy(Energy energy) {
        this.energy = energy;
    }

    public void reduceEnergy(double energyCost) {
        this.energy.reduceEnergy(energyCost);
    }

    public void reduceEnergy(double energyCost, Weather weather) {
        this.energy.reduceEnergy(energyCost * weather.getEnergyEffect());
    }

    public Wallet getWallet() {
        return this.wallet;
    }

    public Player getSpouse() {
        return spouse;
    }

    public void setSpouse(Player spouse) {
        this.spouse = spouse;
    }

    public Skill getSkill(SkillType type) {
        return this.skills.get(type);
    }

    public Map<SkillType, Skill> getSkills() {
        return skills;
    }

    public void addExperince(SkillType type, int amount) {
        Skill skill = getSkill(type);
        int levelUp = skill.addExperience(amount);


    }

    public void addSuitor(Player suitor, Entity ring) {
        this.suitors.put(suitor, ring);
        haveNewSuitor = true;
    }

    public void addQuest() {

    }

    public void addTradeOffer() {

    }

    public void addGift() {

    }

    public void addMessage(Message message) {
        messageLog.add(message);
    }

    public void changePosition(int x, int y) {
        this.getPosition().add(x, y);
    }

    public Position getPosition() {
        return getComponent(PositionComponent.class).get();
    }

    public void setPosition(Position position) {
        position.set(position);
        if(position.getMap() != null) setCurrentMap(position.getMap());
    }

    public void setPosition(float x, float y) {
        this.getPosition().set(x, y);
    }

    public InventorySlot getActiveSlot() {
        return activeSlot;
    }

    public void setActiveSlot(InventorySlot activeSlot) {
        this.activeSlot = activeSlot;
    }

    public ArrayList<Message> getMessageLog() {
        return messageLog;
    }

    public void setMessageLog(ArrayList<Message> messageLog) {
        this.messageLog = messageLog;
    }

    public boolean doesHaveNewMessage() {
        return haveNewMessage;
    }

    public boolean isHaveNewMessage() {
        return haveNewMessage;
    }

    public void setHaveNewMessage(boolean haveNewMessage) {
        this.haveNewMessage = haveNewMessage;
    }

    public boolean isHaveNewTrade() {
        return haveNewTrade;
    }

    public void setHaveNewTrade(boolean haveNewTrade) {
        this.haveNewTrade = haveNewTrade;
    }

    public void makeMessagesSeen() {
        for (Message message : messageLog) {
            if (this.getUsername().equals(message.getReceiver())) {
                message.setSeen(true);
            }
        }
    }

    public void receiveGift(Gift gift) {
        giftReceived.add(gift);
        gift.setId(giftId);
        giftId++;
        haveNewGift = true;

        this.getComponent(Inventory.class).addItem(gift.getContent().clone());
    }

    public void receiveFlower() {


    }

    public TradeOffer findTradeOffer(int id) {
        for (TradeOffer tradeOffer : tradeOffers) {
            if (tradeOffer.getId() == id) {
                return tradeOffer;
            }
        }
        return null;
    }

    public void addTradeHistory(TradeHistoryItem tradeHistoryItem) {
        trades.add(tradeHistoryItem);
    }


    public Gift findGift(int giftId) {
        for (Gift gift : giftReceived) {
            if (gift.getId() == giftId) {
                return gift;
            }
        }
        return null;
    }

    public String getUsername() {
        return accountUsername;
    }

    public ArrayList<Recipe> getUnlockedRecipes() {
        return new ArrayList<>(unlockedRecipes);
    }

    public boolean hasRecipe(String name) {
        return hasRecipe(App.recipeRegistry.getRecipe(name));
    }

    public boolean hasRecipe(Recipe recipe) {
        return unlockedRecipes.contains(recipe);
    }

    public void addRecipe(String recipeName) {
        addRecipe(App.recipeRegistry.getRecipe(recipeName));
    }

    public void addRecipe(Recipe recipe) {
        unlockedRecipes.add(recipe);
    }

    public String newMessages() {
        StringBuilder result = new StringBuilder();
        if (haveNewMessage) {
            result.append("You have new Messages!\n");
            haveNewMessage = false;
        }
        if (haveNewTrade) {
            result.append("You have new Trade offers!\n");
            haveNewTrade = false;
        }
        if (haveNewGift) {
            result.append("You have new Gift!\n");
            haveNewGift = false;
        }
        if (haveNewSuitor) {
            result.append("You have new Suitor! Your suitors:");
            haveNewSuitor = false;
            for (Map.Entry<Player, Entity> entry : suitors.entrySet()) {
                result.append("Player: ").append(entry.getKey().getUsername());
                result.append("Ring: ").append(entry.getValue()).append("\n");
            }
        }

        return result.toString();
    }

    public boolean doesOwnTile(Tile tile) {
        if (tile.getRegion() == null) return true;

        Player tileOwner = tile.getOwner();

        return tileOwner == null || tileOwner == this || (this.spouse != null && tileOwner == this.spouse);
    }

    public void addRegion(MapRegion region, WorldMap worldMap) {
        ownedRegions.add(region);
        updateOwnedTiles(worldMap);
        region.setOwner(this);
    }

    public void removeRegion(MapRegion region) {
        this.ownedRegions.remove(region);
    }

    public void updatePerDay() {
        getEnergy().updatePerDay();
        for (Map.Entry<NPC, NpcFriendship> npcFriendship : npcFriendships.entrySet()) {
            npcFriendship.getValue().updatePerDay();
            if (npcFriendship.getValue().getLevel() >= 3) {
                NPC npc = npcFriendship.getKey();
                String randomGift = npc.getRandomGift();
                if (randomGift != null) {
                    if (!App.entityRegistry.doesEntityExist(randomGift)) continue;
                    Entity gift = App.entityRegistry.makeEntity(randomGift);
                    gift.getComponent(Pickable.class).setStackSize(1);
                    this.getComponent(Inventory.class).addItem(gift);
                    System.out.println("gifted");
                }

            }
        }

        for (Animal animal : animals) {
            animal.updatePerDay();
        }

    }

    public void updatePerHour() {
        this.getEnergy().updatePerHour();
    }

    //NPC functions
    public Map<NPC, NpcFriendship> getNpcFriendships() {
        return npcFriendships;
    }

    public void setNpcFriendships(Map<NPC, NpcFriendship> npcFriendships) {
        this.npcFriendships = npcFriendships;
    }

    public void addFriendshipByGift(NPC npc, Entity gift) {
        NpcFriendship npcFriendship = npcFriendships.get(npc);
        if (!npcFriendship.isWasGiftedToday()) {
            if (npc.getFavorites().contains(gift.getEntityName())) {
                npcFriendship.addXp(200);
            } else {
                npcFriendship.addXp(50);
            }
            npcFriendship.setWasGiftedToday(true);
        }
    }


    public String npcFriendshipDetails() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<NPC, NpcFriendship> entry : npcFriendships.entrySet()) {
            result.append(npcFriendshipDetails(entry.getKey()));
            result.append("----------------------------------------------------------------\n");
        }

        return result.toString();
    }

    public String npcFriendshipDetails(NPC npc) {
        StringBuilder result = new StringBuilder();

        NpcFriendship npcFriendship = npcFriendships.get(npc);
        result.append("Name: ").append(npc.getName()).append("\n");
        result.append("Friendship points: ").append(npcFriendship.getXp()).append("\n");
        result.append("Friendship level: ").append(npcFriendship.getLevel()).append("\n");


        return result.toString();
    }

    public Animal findAnimal(String animalName) {
        for (Animal animal : animals) {
            if (animal.getName().equals(animalName)) {
                return animal;
            }
        }
        return null;
    }

    public void addOwnedBuilding(Entity building) {
        this.ownedBuildings.add(building);
    }

    public EntityList getOwnedBuildings() {
        return ownedBuildings;
    }

    public AnimalHouse findAnimalHouse(String animalHouseName) {
        for (Entity building : ownedBuildings) {
            AnimalHouse animalHouse = building.getComponent(AnimalHouse.class);
            if (animalHouse != null && animalHouse.getName().equals(animalHouseName.trim())) {
                return animalHouse;
            }
        }
        return null;
    }

    public Buff getActiveBuff() {
        return activeBuff;
    }

    public void setActiveBuff(Buff activeBuff) {
        this.activeBuff = activeBuff;
    }

    public ArrayList<Tile> getOwnedTiles(WorldMap worldMap) {
        if (ownedTiles != null) return ownedTiles;
        updateOwnedTiles(worldMap);
        return ownedTiles;
    }
    public void updateOwnedTiles(WorldMap worldMap){
        if(ownedTiles != null) ownedTiles.clear();
        ownedTiles = new ArrayList<>();

        for(Tile[] row : worldMap.getTiles()){
            for (Tile t : row) {
                if(t == null) continue;
                if (ownedRegions.contains(t.getRegion())) {
                    ownedTiles.add(t);
                }
            }
        }
    }

    public ArrayList<Tile> getOwnedPlantedTiles(WorldMap worldMap) {
        ArrayList<Tile> ownedTile = getOwnedTiles(worldMap);
        ArrayList<Tile> plantedTiles = new ArrayList<>();

        for (Tile t : ownedTile) {
            if ((t.getContent() != null) && (t.getContent().getComponent(Growable.class) != null)) plantedTiles.add(t);
        }
        return plantedTiles;
    }

    public Entity getGreenHouse() {
        return greenHouse;
    }

    public void setGreenHouse(Entity greenHouse) {
        this.greenHouse = greenHouse;
    }

    public boolean isGhashed() {
        return this.energy.isGhashed();
    }

    public void move(Vector2 direction, float delta) {
        if (direction.isZero()) return;
        lastDir = direction.cpy();
        getComponent(PositionComponent.class).move(direction, delta * speed);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public enum Action {
        IDLE,
        WALKING,
        HARVESTING,
        USING_TOOL,
        USING_SCYTHE,
        PASSING_OUT,
        WATERING
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public boolean update(float delta) {
        boolean actionChanged = false;
        switch (action){
            case IDLE -> {
                stateTime = 0;
            }
            case WALKING -> {
                stateTime += delta;
            }
            default -> {
                stateTime += delta;
                if(stateTime > GameAssetManager.getInstance().characterSpriteManager.getAnimationDuration(lastDir, action)){
                    setAction(Action.IDLE);
                    actionItem = null;
                    actionChanged = true;
                }
            }
        }

        if(currentReaction != null){
            currentReaction.timeLeft -= delta;
            if(currentReaction.timeLeft <= 0){
                currentReaction = null;
            }
        }

        return actionChanged;
    }

    public PlayerState getPlayerState() {
        PlayerState state = new PlayerState();
        state.energy = getEnergy().getAmount();
        state.position = getPosition();
        state.action = this.action; //WTF this piece of shit -> fixed :3
        state.username = this.getUsername();
        state.reaction = this.currentReaction;
        //TODO

        return state;
    }

    public void loadFromState(PlayerState state) {
        this.getEnergy().setAmount(state.energy);
        this.setPosition(state.position.x, state.position.y);
        this.action = state.action;
        this.currentReaction = state.reaction;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Action getAction() {
        return action;
    }

    public ArrayList<Emoji> getEmojis() {
        return emojis;
    }

    public Reaction getCurrentReaction() {
        return currentReaction;
    }

    public void setCurrentReaction(Reaction currentReaction) {
        this.currentReaction = currentReaction;
    }

    public void setEmojis(ArrayList<Emoji> emojis) {
        this.emojis = emojis;
    }

    public Entity getActionItem() {
        return actionItem;
    }

    public void setActionItem(Entity actionItem) {
        this.actionItem = actionItem;
    }
}
