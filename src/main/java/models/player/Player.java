package models.player;

import models.Account;
import models.App;
import models.Position;
import models.NPC.NPC;
import models.entities.EntityList;
import models.entities.components.*;
import models.enums.Weather;
import models.gameMap.GameMap;
import models.gameMap.Tile;
import models.animal.Animal;
import models.crafting.Recipe;
import models.entities.Entity;
import models.entities.components.inventory.Inventory;
import models.entities.components.inventory.InventorySlot;
import models.enums.SkillType;
import models.gameMap.MapRegion;
import models.NPC.NpcFriendship;
import models.gameMap.WorldMap;
import models.player.buff.Buff;
import models.player.friendship.PlayerFriendship;
import views.inGame.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class    Player extends Entity {
    private Energy energy = new Energy();
    private Wallet wallet = new Wallet();
    private final Map<SkillType, Skill> skills = new HashMap<>();
    private int trashcanLevel;
    private Map<NPC, NpcFriendship> npcFriendships = new HashMap<>();
    private final Map<Player, PlayerFriendship> playerFriendships = null;
    private HashMap<Player, Entity> suitors = new HashMap<>();
    private Player spouse;
    private ArrayList<Gift> giftLog = new ArrayList<>();
    private int giftId = 1;
    private ArrayList<Message> messageLog = new ArrayList<>();
    private final ArrayList<Recipe> unlockedRecipes;
    private ArrayList<TradeOffer> trades = new ArrayList<>();
    private final Account account;
    private InventorySlot activeSlot;
    private final ArrayList<MapRegion> ownedRegions = new ArrayList<>();
    private ArrayList<Animal> animals = new ArrayList<>();
    private final EntityList ownedBuildings = new EntityList();
    private Entity house;
    private Entity refrigerator;
    private Entity greenHouse;
    private Entity trashcan;
    private Buff activeBuff;


    private transient ArrayList<Tile> ownedTiles = null;


    // boolean for messages
    private boolean haveNewMessage = false;
    private boolean haveNewGift = false;
    private boolean haveNewTrade = false;
    private boolean haveNewSuitor = false;

    public Player(Account account){
        super("Player", new Inventory(12), new Renderable('@',  new Color(255, 255, 50)), new PositionComponent(0, 0));
        unlockedRecipes = new ArrayList<>(App.recipeRegistry.getUnlockedRecipes());
        for (SkillType s : SkillType.values()) {
            skills.put(s, new Skill());
        }

        this.trashcan = App.entityRegistry.makeEntity("Trashcan");

        this.account = account;
    }

    public GameMap getCurrentMap() {
        return getPosition().getMap();
    }

    public void setRefrigerator(Entity refrigerator) {
        this.refrigerator = refrigerator;
    }

    public void setCurrentMap(GameMap currentMap) {
        if(this.getCurrentMap() != null){
            this.getCurrentMap().removeEntity(this);
        }
        this.getPosition().setMap(currentMap);
        if(currentMap != null){
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

    public ArrayList<Gift> getGiftLog() {
        return giftLog;
    }

    public ArrayList<TradeOffer> getTrades() {
        return trades;
    }

    public void setGiftLog(ArrayList<Gift> giftLog) {
        this.giftLog = giftLog;
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

    public void reduceEnergy(double energyCost , Weather weather) {
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

    public void addMessage() {

    }

    public void changePosition(int x, int y) {
        this.getPosition().add(x, y);
    }

    public Position getPosition() {
        return getComponent(PositionComponent.class).get();
    }

    public void setPosition(Position position) {
        this.getPosition().set(position);
    }

    public Account getAccount() {
        return account;
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
            if (message.getReceiver() == this) {
                message.setSeen(true);
            }
        }
    }

    public void receiveGift(Gift gift) {
        giftLog.add(gift);
        gift.setId(giftId);
        giftId++;
        haveNewGift = true;
    }

    public void receiveFlower() {


    }

    public TradeOffer findTradeOffer(int id) {
        for (TradeOffer tradeOffer : trades) {
            if (tradeOffer.getId() == id) {
                return tradeOffer;
            }
        }
        return null;
    }


    public Gift findGift(int giftId) {
        for (Gift gift : giftLog) {
            if (gift.getId() == giftId) {
                return gift;
            }
        }
        return null;
    }

    public String getUsername() {
        return account.getUsername();
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

    public void addRegion(MapRegion region) {
        this.ownedRegions.add(region);
        this.getOwnedTiles();
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
            NPC npc = entry.getKey();
            NpcFriendship npcFriendship = entry.getValue();
            result.append("Name: ").append(npc.getName()).append("\n");
            result.append("Friendship points: ").append(npcFriendship.getXp()).append("\n");
            result.append("Friendship level: ").append(npcFriendship.getLevel()).append("\n");
            result.append("----------------------------------------------------------------\n");
        }

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

    public ArrayList<Tile> getOwnedTiles() {
        if(ownedTiles != null) return ownedTiles;

        WorldMap map = App.activeGame.getMainMap();
        ownedTiles = new ArrayList<>();

        for(Tile[] row : map.getTiles()){
            for(Tile t : row){
                if(ownedRegions.contains(t.getRegion())){
                    ownedTiles.add(t);
                }
            }
        }
        return ownedTiles;
    }

    public ArrayList<Tile> getOwnedPlantedTiles() {
        ArrayList<Tile> ownedTile = getOwnedTiles();
        ArrayList<Tile> plantedTiles = new ArrayList<>();

        for(Tile t : ownedTile){
            if((t.getContent() != null) && (t.getContent().getComponent(Growable.class) != null)) plantedTiles.add(t);
        }
        return plantedTiles;
    }

    public Entity getGreenHouse() {
        return greenHouse;
    }

    public void setGreenHouse(Entity greenHouse) {
        this.greenHouse = greenHouse;
    }

    public boolean isGhashed(){
        return this.energy.isGhashed();
    }
}