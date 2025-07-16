package com.ap.stardew.controllers;

import com.ap.stardew.models.*;
import com.ap.stardew.models.Date;
import com.ap.stardew.models.NPC.NPC;
import com.ap.stardew.models.NPC.NpcFriendship;
import com.ap.stardew.models.NPC.Quest;
import com.ap.stardew.models.animal.Animal;
import com.ap.stardew.models.animal.AnimalType;
import com.ap.stardew.models.crafting.Recipe;
import com.ap.stardew.models.crafting.RecipeType;
import com.ap.stardew.models.entities.CollisionEvent;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.*;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.components.inventory.InventorySlot;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.entities.systems.GrowthSystem;
import com.ap.stardew.models.entities.systems.ShopSystem;
import com.ap.stardew.models.entities.workstations.ArtisanComponent;
import com.ap.stardew.models.enums.*;
import com.ap.stardew.models.gameMap.GameMap;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.player.*;
import com.ap.stardew.models.player.friendship.PlayerFriendship;
import com.ap.stardew.models.shop.AnimalShopProduct;
import com.ap.stardew.models.shop.OtherShopProduct;
import com.ap.stardew.models.shop.Shop;
import com.ap.stardew.models.shop.ShopProduct;
import com.ap.stardew.models.utils.StringUtils;
import com.ap.stardew.records.Result;
import com.ap.stardew.records.WalkProposal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.io.*;
import java.util.*;

public class GameMenuController implements Controller {    @Override
    public Result changeMenu(String menuName) {
        return null;
    }

    public Result gameMap() {
        //TODO
        return null;
    }

    public Result exitGame() {
        //TODO
        return null;
    }

    public Result deleteGame() {
        //TODO
        return null;
    }

    public Result nextTurn() {
        Game game = App.getActiveGame();
        game.nextTurn();

        StringBuilder message = new StringBuilder();
        message.append("You are playing as ").append(game.getCurrentPlayer().getAccount().getNickname());
        message.append("\n").append(game.getCurrentPlayer().newMessages());

        return new Result(true, message.toString());
    }

    public Result getTime() {
        Date currentDate = App.getActiveGame().getDate();

        return new Result(true, String.valueOf(currentDate.getHour()));
    }

    public Result getDate() {
        Date currentDate = App.getActiveGame().getDate();

        return new Result(true, String.valueOf(currentDate.getDay()));
    }

    public Result getDateTime() {
        Date currentDate = App.getActiveGame().getDate();
        return new Result(true, "Date: " + currentDate.getDay() +
                "\tHour: " + currentDate.getHour());
    }

    public Result getDayOfTheWeek() {
        Date currentDate = App.getActiveGame().getDate();

        return new Result(true, currentDate.getWeekDay().toString().toLowerCase());
    }

    // dont add large number to this
    public Result advanceTime(int amount) {
        Game game = App.getActiveGame();
        Date date = game.getDate();

        // this function will update data about game.Date
        date.addHour(amount, game);

        return new Result(true, "We've traveled through time for " + amount + " hours!");
    }

    public Result advanceDate(int amount) {
        Game game = App.getActiveGame();
        Date date = game.getDate();

        // this function will update data about game.Date
        date.addDay(amount, game);
        game.setDate(date);
        return new Result(true, "We've traveled through time for " + amount + " days");
    }

    public Result showSeason() {
        Date currentDate = App.getActiveGame().getDate();
        return new Result(true, currentDate.getSeason().name().toLowerCase());
    }

    public void checkTurn() {
        //TODO
        //ghash
    }

    public Result thor(int x, int y) {
        Position position = new Position(x, y);
        Game game = App.getActiveGame();
        Tile tile = game.getActiveMap().getTileByPosition(position);

        game.thorTile(tile);

        return new Result(true, "tile at position " + x + ", " + y + " thord successfully!");
    }

    public Result showWeather() {
        Weather weather = App.getActiveGame().getTodayWeather();
        return new Result(true, weather.toString().toLowerCase());
    }

    public Result weatherForecast() {
        Weather weather = App.getActiveGame().getTomorrowWeather();

        return new Result(true, weather.toString().toLowerCase());
    }

    public Result setWeather(String weatherString) {
        Weather weather = Weather.getweather(weatherString);
        if (weather == null) {
            return new Result(false, "Weather not found");
        }

        Game game = App.getActiveGame();
        game.setTomorrowWeather(weather);
        return new Result(true, "Tomorrow's weather changed to " + weather.name().toLowerCase());
    }

    public WalkProposal proposeWalk(int x, int y) {
        Game game = App.getActiveGame();
        Player player = game.getCurrentPlayer();
        GameMap map = game.getActiveMap();
        Tile start = map.getTileByPosition(
                game.getCurrentPlayer().getPosition()
        );
        Tile goal = map.getTileByPosition(new Position(x, y));
        if (goal == null) {
            return new WalkProposal(false, "tile doesnt exist", 0, x, y);
        }
        if (start == null) {
            return new WalkProposal(false, "you cant be there", 0, x, y);
        }
        if (start.equals(goal))
            return new WalkProposal(
                    false,
                    "you are already in " + goal.getPosition(),
                    0, x, y
            );
        List<Tile> path = shortestPath(goal, start, map.getTiles());
        int changedDir = getChangedDir(path);
        int distance = path.size() - 1;
        double energyCost =  (double) distance / 20+ (double) changedDir / 2;
        if (distance <= 0)
            return new WalkProposal(false, "you can't reach " + goal.getPosition(), 0, x, y);
        if (!player.doesOwnTile(goal))
            return new WalkProposal(false, "you can't go in " + goal.getOwner().getAccount().getNickname() + "'s farm", 0, x, y);
        return new WalkProposal(true, "OK", energyCost, x, y);
    }

    private static int getChangedDir(List<Tile> path) {
        int changedDir = 0;
        if(path.size() > 3) {
            int[] currentDir = {path.get(1).getCol() - path.get(0).getCol(), path.get(1).getRow() - path.get(0).getRow()};
            for(int i = 0; i + 1 < path.size(); i++) {
                int[] newDir = {path.get(i + 1).getCol() - path.get(i).getCol(), path.get(i + 1).getRow() - path.get(i).getRow()};
                if(newDir[0] != currentDir[0] || newDir[1] != currentDir[1]) {
                    changedDir += 1;
                    currentDir = newDir;
                }
            }
        }
        return changedDir;
    }

    public Result executeWalk(WalkProposal p) {
        Player player = App.getActiveGame().getCurrentPlayer();
        if (!p.isAllowed()) {
            return new Result(false, p.message());
        }
        player.setPosition(new Position(p.x(), p.y()));
        player.reduceEnergy(p.energyCost());
        Entity entity = null;
        Tile tile = App.getActiveGame().getActiveMap().getTileByPosition(p.y(), p.x());
        if (tile != null && (entity = tile.getContent()) != null) {
            Placeable placeable = entity.getComponent(Placeable.class);
            for (CollisionEvent c : placeable.getCollisionEvents()) {
                //c.onEnter();
            }
        }
        return new Result(true, "you walked to "
                + player.getPosition()
                + " (-" + p.energyCost() + " energy)");
    }

    private List<Tile> shortestPath(Tile destination, Tile src, Tile[][] tiles) {
        int rows = tiles.length;
        int cols = tiles[0].length;

        Queue<Tile> queue = new LinkedList<>();
        Map<Tile, Tile> cameFrom = new HashMap<>();
        queue.add(src);
        cameFrom.put(src, null);

        int[][] directions = {
                {0, 1}, {1, 0}, {0, -1}, {-1, 0}
//                , {1, -1}, {1, 1}, {-1, 1}, {-1, -1} /* Comment this line to walk vertically and horizontally only*/
        };

        while (!queue.isEmpty()) {
            Tile current = queue.poll();

            if (current.equals(destination))
                break;


            for (int[] dir : directions) {
                int newRow = current.getRow() + dir[0];
                int newCol = current.getCol() + dir[1];

                if (newCol >= 0 && newCol < cols && newRow >= 0 && newRow < rows) {
                    Tile neighbor = tiles[newRow][newCol];
                    if ((neighbor.getContent() == null || neighbor.getContent().getComponent(Placeable.class).isWalkable()) &&
                            (neighbor.getType() != null && neighbor.getType().isWalkable))
                        if (!cameFrom.containsKey(neighbor)) {
                            queue.add(neighbor);
                            cameFrom.put(neighbor, current);
                        }
                }
            }

        }
        if (!cameFrom.containsKey(destination)) {
            return Collections.emptyList();
        }

        List<Tile> path = new ArrayList<>();
        Tile current = destination;
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    public Result helpReadingMap() {
        StringBuilder sb = new StringBuilder();
        sb
                .append("T : tree").append("\n")
                .append("c : foraging crops").append("\n")
                .append("C : crops").append("\n")
                .append("E : Edible").append("\n")
                .append("A : Artisan").append("\n")
                .append("S : Sebastian \n")
                .append("H : Harvey\n")
                .append("L : Lia\n")
                .append("R : Robin");
        return new Result(true, sb.toString());
    }

    public Result energyShow() {
        Game game = App.getActiveGame();
        Player player = game.getCurrentPlayer();
        Energy energy = player.getEnergy();


        return new Result(true, "energy left: " + energy.getAmount());
    }

    public Result energySet(int amount) {
        Game game = App.getActiveGame();
        Player player = game.getCurrentPlayer();
        Energy energy = player.getEnergy();

        energy.setAmount(amount);
        return new Result(true, "energy set to " + energy.getAmount());
    }

    public Result energyUnlimited() {
        Game game = App.getActiveGame();
        Player player = game.getCurrentPlayer();
        Energy energy = player.getEnergy();

        energy.toggleUnlimited();

        return new Result(true, "energy unlimited: " + energy.isUnlimited());
    }

    public Result inventoryTrash() {
        //TODO
        return null;
    }

    /* ---------------------------------- Tools ---------------------------------- */
    public Result toolsEquip(String toolName) {
        Player player = App.getActiveGame().getCurrentPlayer();
        InventorySlot slot = player.getComponent(Inventory.class).getSlot(toolName);
        if (slot == null)
            return new Result(false, "This tool doesn't exist in inventory");
        player.setActiveSlot(slot);
        return new Result(true, "Tool equipped successfully");
    }

    public Result toolsShowCurrent() {
        Player player = App.getActiveGame().getCurrentPlayer();
        Entity active = player.getActiveSlot().getEntity();
        if (active == null || !active.hasTag(EntityTag.TOOL))
            return new Result(false, "This is not a tool");
        return new Result(true, active.getEntityName());

    }

    public Result toolsShowAvailable() {
        Player player = App.getActiveGame().getCurrentPlayer();
        ArrayList<Entity> tools = player.getComponent(Inventory.class).getItemsByTag(EntityTag.TOOL);
        if (tools.isEmpty())
            return new Result(false, "There is no tool in Backpack");
        StringBuilder sb = new StringBuilder("Tools available:");
        for (Entity tool : tools) {
            sb.append("\n").append(tool.getEntityName());
        }
        return new Result(true, sb.toString());

    }

    public Result toolsUpgrade(String toolName) {
        Entity building = App.getActiveGame().getCurrentPlayer().getCurrentMap().getBuilding();
        if(building == null)
            return new Result(false, "You are not in a building");
        Shop shop = building.getComponent(Shop.class);
        if(shop == null)
            return new Result(false, "This building isn't shop");
        if(StringUtils.isNamesEqual(toolName, "Large Pack") || StringUtils.isNamesEqual(toolName, "Deluxe Pack"))
            return ShopSystem.upgradeBackPack(shop, toolName);
        if(!StringUtils.isNamesEqual(shop.getName(), "blacksmith"))
            return new Result(false, "You only can Upgrade in blacksmith");

        return ShopSystem.UpgradeTool(toolName);


    }



    public Result toolsUse(Direction dir) {
        Vec2 playerPosition = App.getActiveGame().getCurrentPlayer().getPosition();
        GameMap map = App.getActiveGame().getActiveMap();
        if (App.getActiveGame().getCurrentPlayer().getActiveSlot() == null) {
            return new Result(false, "nothing equipped");
        }
        Entity tool = App.getActiveGame().getCurrentPlayer().getActiveSlot().getEntity();

        if (
                dir == null
                        || playerPosition.getRow() + dir.dy > map.getHeight() || playerPosition.getRow() + dir.dy < 0
                        || playerPosition.getCol() + dir.dx > map.getWidth() || playerPosition.getCol() + dir.dx < 0
        )
            return new Result(false, "Invalid Direction");
        Position position = new Position(playerPosition.getCol() + dir.dx, playerPosition.getRow() + dir.dy);
        if (tool == null || (!tool.getTags().contains(EntityTag.TOOL)))
            return new Result(false, "You should equip a tool first");
        return tool.getComponent(Useable.class).use(map.getTileByPosition(position));
    }

    public Result useArtisan(String artisanName, String itemName) {
        Game game = App.getActiveGame();
        Player player = game.getCurrentPlayer();
        int x = player.getPosition().getCol();
        int y = player.getPosition().getRow();
        int[][] directions = {{1, 0}, {1, -1}, {1, 1}, {0, 1}, {0, -1}, {-1, 1}, {-1, 0}, {-1, -1}};
        Entity temp = App.entityRegistry.getData(artisanName);
        if(temp == null || !temp.hasTag(EntityTag.ARTISAN))
            return new Result(false, "This artisan doesn't exist!");
        for (int[] dir : directions) {
            Tile tile = game.getActiveMap().getTileByPosition(y + dir[0], x + dir[1]);
            if (tile == null) continue;
            if (tile.getContent() != null && StringUtils.isNamesEqual(tile.getContent().getEntityName(), artisanName)) {
                ArtisanComponent artisan = tile.getContent().getComponent(ArtisanComponent.class);
                if (artisan.isInProcess())/* TODO: should fix in phase2 (what if there is two artisan near player)*/
                    return new Result(false, "another recipe already in process");
                return artisan.addProcess(itemName);
            }
        }
        return new Result(false, "There isn't any " + artisanName + " around you!");
    }

    public Result getArtisan(String artisanName) {
        Game game = App.getActiveGame();
        Player player = game.getCurrentPlayer();
        int x = player.getPosition().getCol();
        int y = player.getPosition().getRow();
        int[][] directions = {{1, 0}, {1, -1}, {1, 1}, {0, 1}, {0, -1}, {-1, 1}, {-1, 0}, {-1, -1}};
        for (int[] dir : directions) {
            Tile tile = game.getActiveMap().getTileByPosition(y + dir[0], x + dir[1]);
            if (tile == null) continue;

            if (tile.getContent() != null && StringUtils.isNamesEqual(tile.getContent().getEntityName(), artisanName)) {
                ArtisanComponent artisan = tile.getContent().getComponent(ArtisanComponent.class);
                if (!artisan.isInProcess())
                    return new Result(false, "This artisan is empty!");
                Inventory inventory = App.getActiveGame().getCurrentPlayer().getComponent(Inventory.class);
                if (!artisan.isProcessFinished())
                    return artisan.remainingTime();
                Entity product = artisan.getProduct();
                inventory.addItem(product);
                return new Result(true, product.getEntityName() + " added to your inventory successfully");
            }
        }
        return new Result(false, "There isn't any " + artisanName + " around you!");
    }
    /* --------------------------------------  -------------------------------------- */

    public Result craftInfo(String name) {
        if (!App.entityRegistry.doesEntityExist(name)) {
            return new Result(false, "There is no crop with name" + name);
        }
        Entity crop = App.entityRegistry.makeEntity(name);
        if (!crop.hasTag(EntityTag.CROP)) {
            return new Result(false, "There is no crop with name" + name);
        }
        Growable growable = crop.getComponent(Growable.class);
        Edible edible = crop.getComponent(Edible.class);
        Sellable sellable = crop.getComponent(Sellable.class);

        StringBuilder message = new StringBuilder();
        message.append("Name: ").append(crop.getEntityName()).append("\n").
                append("Source: ").append(growable.getSeed()).append("\n")
                .append("Stages: ").append(growable.getStages()).append("\n")
                .append("Total Harvest Time: ").append(growable.getTotalHarvestTime()).append("\n")
                .append("One Time: ").append(growable.isOneTime()).append("\n");

        if (growable.getRegrowthTime() > 0) {
            message.append("Regrowth Time: ").append(growable.getRegrowthTime()).append("\n");
        } else {
            message.append("Regrowth Time:\n");
        }

        message.append("Base Sell Price: ").append(sellable.getBasePrice()).append("\n");

        if (edible != null) {
            message.append("""
                    Is Edible: false
                    Base Energy: 0
                    """);
        } else {
            message.append("Is Edible: true\n" + "Base Energy: ").append(edible.getEnergy()).append("\n");
        }

        message.append("Season: ").append(growable.getGrowingSeasons()).append("\n")
                .append("Can Become Giant: ").append(growable.isCanBecomeGiant());


        return new Result(true, message.toString());
    }

    public Result plant(String seedString, String direction) {
        Game game = App.getActiveGame();
        Player player = game.getCurrentPlayer();
        if (!App.entityRegistry.doesEntityExist(seedString)) {
            return new Result(false, "There is no seed with name" + seedString);
        }

        Entity seedDetails = App.entityRegistry.getEntityDetails(seedString);

        if (!seedDetails.hasTag(EntityTag.SEED)) {
            return new Result(false, "There is no seed with name" + seedString);
        }

        Position position = new Position(player.getPosition().getX(), player.getPosition().getY());
        position.changeByDirection(direction);
        if (position == null) {
            return new Result(false, "type a valid direction");
        }
        if (!player.getComponent(Inventory.class).doesHaveItem(seedString)) {
            return new Result(false, "you don't have that seed");
        }


        if(!player.getComponent(Inventory.class).doesHaveItem(seedString)){
            return new Result(false, "you don't have that seed");
        }
        Entity seed = App.entityRegistry.getEntityDetails(seedString);

        Tile tile = game.getActiveMap().getTileByPosition(position);

        if (tile == null || !tile.getType().equals(TileType.PLOWED)) {
            return new Result(false, "tile is unavailable for planting");
        }

        if (tile.getContent() != null || tile.getType().equals(TileType.PLANTED_GROUND)) {
            return new Result(false, "tile isn't empty");
        }
        Entity building = tile.getMap().getBuilding();

        boolean canPlant = true;
        Entity plant = seed.getComponent(SeedComponent.class).getGrowingPlant();

        if(!plant.getComponent(Growable.class).getGrowingSeasons().contains(game.getDate().getSeason())){
            canPlant = false;
        }

        if(building != null && StringUtils.isNamesEqual(building.getEntityName(), "greenhouse")){
            canPlant = true;
        }

        if(!canPlant){
            return new Result(false, "You can't plant a " + plant.getEntityName() + " in this season");
        }

        seed = player.getComponent(Inventory.class).takeFromInventory(seedString, 1);
        tile.setType(TileType.PLANTED_GROUND);
        EntityPlacementSystem.placeOnTile(plant, tile);

        return new Result(true, "planted succusfully");
    }

    public Result showPlant(int col, int row) {
        Position position = new Position(col, row);
        Game game = App.getActiveGame();
        GameMap gameMap = game.getActiveMap();
        Tile tile = gameMap.getTileByPosition(position);

        if (tile == null) {
            return new Result(false, "No tile found");
        }

        Entity plantedEntity = tile.getContent();
        if (plantedEntity == null ||
                !(plantedEntity.hasTag(EntityTag.TREE) || plantedEntity.hasTag(EntityTag.CROP))) {
            return new Result(false, "Tile is not a planted ground");
        }


        StringBuilder message = new StringBuilder();
        message.append("Name: ").append(plantedEntity.getEntityName()).append("\n");
        message.append(plantedEntity.getComponent(Growable.class).getInfo());
        return new Result(true, message.toString());
    }

    public Result fertilize(String fertilizerString, String direction) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();

        // get the fertilizer
        if (!App.entityRegistry.doesEntityExist(fertilizerString)) {
            return new Result(false, "There is no fertilizer with name" + fertilizerString);
        }
        Entity fertilizer = App.entityRegistry.getEntityDetails(fertilizerString);

        if (!fertilizer.hasTag(EntityTag.FERTILIZER)) {
            return new Result(false, "There is no fertilizer with name" + fertilizerString);
        }


        // get the position
        Position position = new Position(currentPlayer.getPosition().getX(), currentPlayer.getPosition().getY());
        position = position.changeByDirection(direction);
        if (position == null) {
            return new Result(false, "type a valid direction");
        }
        Tile tile = game.getActiveMap().getTileByPosition(position);
        if (tile == null) {
            return new Result(false, "No tile found");
        }

        // check existence of a plant
        if (tile.getContent() == null || tile.getContent().getComponent(Growable.class).getInfo() == null) {
            return new Result(false, "Tile is not a planted ground");
        }

        // check player has fertilizer
        Inventory inventory = currentPlayer.getComponent(Inventory.class);
        if (!inventory.doesHaveItem(fertilizer)) {
            return new Result(false, "you don't have this fertilizer");
        }


        // Fertilize!
        inventory.takeFromInventory(fertilizer, 1);
        Entity plantedEntity = tile.getContent();
        FertilizerFunction.fertilize(fertilizerString, plantedEntity.getComponent(Growable.class));
        plantedEntity.getComponent(Growable.class).setFertilized(true);
        return new Result(true, "fertilized successfully!");
    }

    public Result checkWater() {
        //TODO
        return null;
    }

    public Result showRecipes(String recipeType) {
        //TODO: should be in house
        RecipeType type = RecipeType.fromName(recipeType);
        Player activePlayer = App.getActiveGame().getCurrentPlayer();

        ArrayList<Recipe> recipes = activePlayer.getUnlockedRecipes();
        StringBuilder sb = new StringBuilder();
        for (Recipe recipe : recipes) {
            if (recipe.getType().equals(type))
                sb.append(recipe);
        }
        return new Result(true, sb.toString());
    }

    /*
    private Result craftRecipe(String recipeName){
        Player player = App.getActiveGame().getCurrentPlayer();
        Recipe recipe = App.recipeRegistry.getRecipe(recipeName);
        if(recipe == null)
            return new Result(false, "Invalid recipe name");

        if(!player.hasRecipe(recipe))
            return new Result(false, "You didn't unlocked this recipe");
        if(!recipe.canCraft(player.getComponent(Inventory.class)))
            return new Result(false, "you don't have enough items");
        Entity output = recipe.craft(player.getComponent(Inventory.class));
        //TO.DO: if inventory is full drop output on ground
        Result result = player.getComponent(Inventory.class).addItem(output);
        if(!result.isSuccessful())
            return new Result(false, "something wrong happened!");
        return new Result(true, recipeName + " added to your inventory successfully");

    }
    *
     */
    public Result craftingCraft(String recipeName) {
        //TODO: should be in house
        Player player = App.getActiveGame().getCurrentPlayer();
        Recipe recipe = App.recipeRegistry.getRecipe(recipeName);
        if (recipe == null)
            return new Result(false, "Invalid recipe name");
        if (!player.hasRecipe(recipe))
            return new Result(false, "You didn't unlocked this recipe");
        if (!recipe.getType().equals(RecipeType.CRAFTING))
            return new Result(false, "this is not a crafting recipe!");
        if (!recipe.canCraft(player.getComponent(Inventory.class)))
            return new Result(false, "you don't have enough items");
        Entity output = recipe.craft(player.getComponent(Inventory.class));
        //TODO: if inventory is full drop output on ground
        Entity entity = player.getComponent(Inventory.class).addItem(output);
        if (entity != null)
            return new Result(false, "something wrong happened!");

        App.getActiveGame().getCurrentPlayer().reduceEnergy(2);
        return new Result(true, recipeName + " crafted and added to your inventory successfully");
    }

    public Result placeItem() {
        //TODO
        return null;
    }

    public Result addItem() {
        //TODO
        return null;
    }

    public Result refrigerator() {
        //TODO
        return null;
    }

    public Result cheatTakeItem(String itemName, int amount){
        Entity entity = App.getActiveGame().getCurrentPlayer().getComponent(Inventory.class).takeFromInventory(itemName, amount);
        if(entity==null){
            return new Result(true, itemName + " not in inventory");
        }
        return new Result(true, "removed " + entity.getComponent(Pickable.class).getStackSize() + " " + itemName + "from" +
                "inventory");
    }


    public Result cookingPrepare(String recipeName) {

        Player player = App.getActiveGame().getCurrentPlayer();
        Recipe recipe = App.recipeRegistry.getRecipe(recipeName);
        if (recipe == null)
            return new Result(false, "Invalid recipe name");
        if (!player.hasRecipe(recipe))
            return new Result(false, "You didn't unlocked this recipe");
        if (!recipe.getType().equals(RecipeType.COOKING))
            return new Result(false, "this is not a cooking recipe!");
        if (!recipe.canCraft(player.getComponent(Inventory.class)))
            return new Result(false, "you don't have enough items");
        Entity output = recipe.craft(player.getComponent(Inventory.class));
        //TODO: if inventory is full drop output on ground
        Entity entity = player.getComponent(Inventory.class).addItem(output);
        if (entity != null)
            return new Result(false, "something wrong happened!");

        App.getActiveGame().getCurrentPlayer().reduceEnergy(3);
        return new Result(true, recipeName + " cooked and added to your inventory successfully");

    }

    public Result eat(String foodName) {

        Player player = App.getActiveGame().getCurrentPlayer();
        Inventory inventory = player.getComponent(Inventory.class);
        Entity food = inventory.getItem(foodName);
        if (food == null)
            return new Result(false, "You don't have " + foodName);
        Edible edible = food.getComponent(Edible.class);
        if (edible == null)
            return new Result(false, "This is not Edible");
        edible.setBuff();
        player.reduceEnergy(-edible.getEnergy());
        inventory.takeFromInventory(food, 1);
        return new Result(true, "eated " + foodName + " successfully");
    }

    /*-----------------------------------Animal commands----------------------------------------*/

    public Result buildAnimalHouse() {
        //TODO
        return null;
    }

    public Result  buyAnimal(String animalTypeString, String animalName, String animalHouseName) {
        //TODO: move some of these to buySystem later(alan khastam)
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Wallet wallet = currentPlayer.getWallet();

        AnimalType animalType = AnimalType.getAnimalTypeByString(animalTypeString);
        if (animalType == null) {
            return new Result(false, "Invalid animal type");
        }
//
        if(currentPlayer.getPosition().getMap().getBuilding() == null)
            return new Result(false, "you should be in a shop");
        Shop shop = currentPlayer.getPosition().getMap().getBuilding().getComponent(Shop.class);
        if(shop == null)
            return new Result(false, "this building doesn't have shop");
        if(shop.isClosed())
            return new Result(false, "Shop is Closed! it's open between " +shop.startHour + "-" + shop.endHour);
        AnimalShopProduct product = shop.getAnimalShopProduct(animalTypeString);
        if(product == null)
            return new Result(false, "this product doesn't exist");
        if(product.getStock() == 0)
            return new Result(false, "There isn't enough stock! go come tomorrow:)");

//
        if (wallet.getBalance() < animalType.getCost()) {
            return new Result(false, "You don't have enough money");
        }
        AnimalHouse animalHouse = currentPlayer.findAnimalHouse(animalHouseName);
        if (animalHouse == null) {
            return new Result(false, "You don't own " + animalHouseName);
        }

        if (!(animalType.getAnimalHouseType().equals(animalHouse.getType()) &&
                animalType.getHouseLevel().getCapacity() <= animalHouse.getCapacity())) {
            return new Result(false, "This house isn't appropriate for this animal");
        }
//

        product.addSold(1);
//
        Animal animal = new Animal(animalType, animalName);
        currentPlayer.getAnimals().add(animal);
        animalHouse.addAnimal(animal);
        wallet.reduceBalance(animalType.getCost());
        EntityPlacementSystem.placeOnTile(animal, animalHouse.getEntity().getComponent(InteriorComponent.class).getMap().getTileByPosition(2, 2));
        return new Result(true, animalName + " bought and added to your farm successfully");

    }

    public Result showMyAnimalHouses() {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();

        StringBuilder message = new StringBuilder("Your available animal houses:");

        for (Entity building : currentPlayer.getOwnedBuildings()) {
            AnimalHouse animalHouse = building.getComponent(AnimalHouse.class);
            if (animalHouse != null) {
                message.append("\n");
                message.append(animalHouse.getDetail());
            }
        }
        return new Result(true, message.toString());

    }

    //:/
    public Result chooseHouseForAnimal(String animalHouseName) {
//        Game game = App.getActiveGame();
//        Player currentPlayer = game.getCurrentPlayer();
//        Wallet wallet = currentPlayer.getWallet();
//        AnimalType animalType = details.animalType();
//        String animalName = details.animalName();
//
//        AnimalHouse animalHouse = currentPlayer.findAnimalHouse(animalHouseName);
//        if (animalHouse == null) {
//            return new Result(false, "You don't own " + animalHouseName);
//        }
//
//        if (!(animalType.getAnimalHouseType().equals(animalHouse.getType()) &&
//                animalType.getHouseLevel().getCapacity() <= animalHouse.getCapacity() &&
//                animalHouse.getAvailableCapacity() > 0)) {
//            return new Result(false, "This house isn't appropriate for this animal");
//        }
//
//        Animal animal = new Animal(animalType, details.animalName());
//        currentPlayer.getAnimals().add(animal);
//        animalHouse.addAnimal(animal);
//        wallet.reduceBalance(animalType.getCost());
//        EntityPlacementSystem.placeOnMap(animal, new Position(2, 2), animalHouse.getEntity()
//                .getComponent(InteriorComponent.class).getMap());
        return new Result(true, " bought and added to your farm successfully");
    }

    public Result pet(String animalName) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Animal animal = currentPlayer.findAnimal(animalName);
        if (animal == null) {
            return new Result(false, "Animal not found");
        }

        if(currentPlayer.getPosition().getDistance(animal.getComponent(PositionComponent.class).get()) > 2) {
            return new Result(false, "You are too far from this animal!");
        }

        if (!animal.isPetToday()) {
            animal.setPetToday(true);
            animal.addFriendshipLevel(15);
        }

        return new Result(true, animal.getName() + " has pet successfully!");
    }

    public Result setAnimalFriendship(String animalName, int amount) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Animal animal = currentPlayer.findAnimal(animalName);
        if (animal == null) {
            return new Result(false, "Animal not found");
        }

        if (amount < 0 || amount > 1000) {
            return new Result(false, "Invalid amount. Must be between 0 and 1000");
        }

        animal.setFriendshipLevel(amount);

        return new Result(true, "animal friendship set successfully!");
    }

    public Result animals() {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();

        StringBuilder message = new StringBuilder("Your Animals:\n");

        for (Animal animal : currentPlayer.getAnimals()) {
            message.append(animal.getDetail());
        }

        return new Result(true, message.toString());
    }

    public Result shepherdAnimal(String animalName, int x, int y, boolean putInBuilding) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Animal animal = currentPlayer.findAnimal(animalName);

        if(animal == null){
            return new Result(false, "You don't own an animal named:" + animalName);
        }

        Entity building = Animal.getHouse(animal, currentPlayer);
        if(building == null){
            return new Result(false, "RIDI: in current playeri ke dadi owner animal nist");
        }

        if(putInBuilding){
            EntityPlacementSystem.placeOnMap(animal, new Position(2, 2), building.getComponent(InteriorComponent.class)
                    .getMap());

            return new Result(true, animalName + " was put in it's building");
        }

        if(!EntityPlacementSystem.canPlace(x, y)) {
            return new Result(false, "can't place the animal there");
        }

        EntityPlacementSystem.placeOnMap(animal, new Position(x, y), App.getActiveGame().getMainMap());

        if (true /*TODO: check its out of home*/) {
            if (!animal.isFedToday()) {
                animal.setFedToday(true);
                animal.addFriendshipLevel(8);
            }
        }
        return new Result(true, "animal was moved");
    }

    public Result feedHay(String animalName) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Animal animal = currentPlayer.findAnimal(animalName);
        if (animal == null) {
            return new Result(false, "Animal not found");
        }

        Inventory inventory = currentPlayer.getComponent(Inventory.class);
        if (!inventory.doesHaveItem("Hay")) {
            return new Result(false, "You don't have enough hay!");
        }

        inventory.takeFromInventory("Hay", 1);

        animal.setFedToday(true);

        return new Result(true, "animal fed successfully");
    }

    public Result showProduces() {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        StringBuilder message = new StringBuilder("Your available Produces:\n");

        for (Animal animal : currentPlayer.getAnimals()) {
            Entity product = animal.getTodayProduct();
            if (product != null) {
                message.append("Animal ").append(animal.getName()).append("\n");
                message.append("Product ").append(product.getEntityName()).append("\n");
                message.append("Quality: ").append(product.getComponent(Sellable.class).getProductQuality()).append("\n");
                message.append("-------------------------------------------------------------------\n");
            }
        }

        return new Result(true, message.toString());
    }

    public Result collectProduces(String animalName) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Animal animal = currentPlayer.findAnimal(animalName);
        Inventory inventory = currentPlayer.getComponent(Inventory.class);

        if (animal == null) {
            return new Result(false, "Animal not found");
        }

        Entity product = animal.getTodayProduct();

        if (product == null) {
            return new Result(false, "this animal does not have product today!");
        }

        if (animal.getAnimalType().getNeededTool() != null) {
            return new Result(false, "You cant collect this product without a tool!");
        }

        animal.setTodayProduct(null);
        return new Result(inventory.addItem(product) == null, "");

//        return new Result(true, "product collected successfully");
    }

    /* ------------------------------------------- Shop Commands ------------------------------------------- */
    public Result showAllProducts() {
        Entity activeBuilding = App.getActiveGame().getActiveMap().getBuilding();
        if(activeBuilding == null)
            return new Result(false, "You are not in a building.");
        Shop shop = activeBuilding.getComponent(Shop.class);
        if(shop == null)
            return new Result(false, "This building doesn't have shop");
        if(shop.isClosed())
            return new Result(false, "Shop is Closed! it's open between " +shop.startHour + "-" + shop.endHour);
        return new Result(true, printProducts(shop.getAllProducts(), "All Products:"));
    }

    private String printProducts(List<ShopProduct> products, String... startLines) {
        StringBuilder sb = new StringBuilder();
        for (String line : startLines) {
            sb.append(line).append("\n");
        }

        for (ShopProduct product : products) {
            sb.append(product.toString()).append("\n");
        }
        sb.append("--------------------------------------------------");
        return sb.toString();
    }

    public Result showAvailableProducts() {
        Entity activeBuilding = App.getActiveGame().getActiveMap().getBuilding();
        if(activeBuilding == null)
            return new Result(false, "You are not in a building.");
        Shop shop = activeBuilding.getComponent(Shop.class);
        if(shop == null)
            return new Result(false, "This building doesn't have shop");
        if(shop.isClosed())
            return new Result(false, "Shop is Closed! it's open between " +shop.startHour + "-" + shop.endHour);
        return new Result(true, printProducts(shop.getAvailableProducts(), "Available Products:"));
    }

    public Result purchase(String productName, String count) {
        int amount = (count == null) ? 1 : Integer.parseInt(count);
        Entity activeBuilding = App.getActiveGame().getActiveMap().getBuilding();
        if(activeBuilding == null)
            return new Result(false, "You are not in a building.");
        Shop shop = activeBuilding.getComponent(Shop.class);
        if (shop == null)
            return new Result(false, "This building doesn't have shop");
        if(shop.isClosed())
            return new Result(false, "Shop is Closed! it's open between " +shop.startHour + "-" + shop.endHour);
        OtherShopProduct product = shop.getOtherShopProduct(productName);
        if (product == null)
            return new Result(false, "This shop doesn't have this product");
        if (!product.isInSeason(App.getActiveGame().getDate().getSeason()))
            return new Result(false, "This product isn't available in this season");
        return ShopSystem.buyProduct(product, amount);
    }

    public Result buildBuilding(int x, int y, String productName) {
        Entity activeBuilding = App.getActiveGame().getActiveMap().getBuilding();
        if(activeBuilding == null)
            return new Result(false, "You are not in a building.");
        Shop shop = activeBuilding.getComponent(Shop.class);
        if(shop == null)
            return new Result(false, "This building doesn't have shop");
        if(shop.isClosed())
            return new Result(false, "Shop is Closed! it's open between " +shop.startHour + "-" + shop.endHour);
        return ShopSystem.buildPlaceable(shop.getBuildingShopProduct(productName), x, y);
    }

    public Result sellProduct(String productName, String count) {
        if(count == null)
            return sellProduct(productName, -1);
        return sellProduct(productName, Integer.parseInt(count));
    }

    private Result sellProduct(String productName, int count) {
        Game game = App.getActiveGame();
        Player player = game.getCurrentPlayer();
        Inventory inventory = player.getComponent(Inventory.class);
        Entity product = inventory.getItem(productName);
        if(product == null)
            return new Result(false, "you don't have this Item");
        Sellable sellable = product.getComponent(Sellable.class);
        if(sellable == null)
            return new Result(false, "You can't sell this Item");
        if(count == -1)
            count = inventory.getItemCount(productName); // default is all of products
        if(!inventory.doesHaveItem(productName, count))
            return new Result(false, "you don't have enough" + productName);
        int[][] directions = {{1, 0}, {1, -1}, {1, 1}, {0, 1}, {0, -1}, {-1, 1}, {-1, 0}, {-1, -1}};
        int x = player.getPosition().getCol();
        int y = player.getPosition().getRow();
        for (int[] dir : directions) {
            Tile tile = game.getActiveMap().getTileByPosition(y + dir[0], x + dir[1]);
            if (tile == null) continue;
            Entity tileContent = tile.getContent();
            if(tileContent != null && StringUtils.isNamesEqual(tileContent.getEntityName(), "Shipping Bin")) {
                tileContent.getComponent(Inventory.class).addItem(inventory.takeFromInventory(productName, count));
            }
        }
        return new Result(false, "There is no shipping bin around you.");
    }

    /* -------------------------------------------------- -------------------------------------------------- */

    public Result sellAnimal(String animalName) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Animal animal = currentPlayer.findAnimal(animalName);
        if (animal == null) {
            return new Result(false, "Animal not found");
        }


        int price = animal.calculatePrice();
        currentPlayer.getWallet().changeBalance(price);
        currentPlayer.getAnimals().remove(animal);
        //TODO: remove form his house if needed

        return new Result(true, animal.getName() + " sold successfully!");
    }

    public Result fishing(String fishingPole) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Inventory inventory = currentPlayer.getComponent(Inventory.class);

        if (!inventory.doesHaveItem(fishingPole)) {
            return new Result(false, "You don't have this fishing pole!");
        }

        Entity fishingPoleEntity = App.entityRegistry.makeEntity(fishingPole);
        if(fishingPoleEntity == null || fishingPoleEntity.getComponent(FishingPoleComponent.class) == null) {
            return new Result(false, "You can't get fish by " + fishingPole + "!");
        }

        // TODO: check this (parsa) & we can extract it as seperate method
        boolean isCloseToWater = false;
        int x = currentPlayer.getPosition().getCol();
        int y = currentPlayer.getPosition().getRow();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Tile tile = game.getActiveMap().getTileByPosition(x - 1 + j, y - 1 + i);
                if (tile != null && tile.getType().equals(TileType.WATER)) isCloseToWater = true;
            }
        }
        if (!isCloseToWater) {
            return new Result(false, "You are not close to water!");
        }


        Skill skill = currentPlayer.getSkill(SkillType.FISHING);
        double fishNumberDouble = Math.random() * game.getTodayWeather().getFishingEffect();
        fishNumberDouble *= (skill.getLevel() + 2);
        int fishNumber = (int) Math.ceil(fishNumberDouble);
        if (fishNumber > 6) fishNumber = 6;

        ArrayList<Entity> fishes = new ArrayList<>();
        ArrayList<String> availableFish = game.getAvailableFish(game.getDate().getSeason(), skill);
        double poleEffect = fishingPoleEntity.getComponent(FishingPoleComponent.class).getFishingPower();
        currentPlayer.reduceEnergy(fishingPoleEntity.getComponent(FishingPoleComponent.class).getEnergyNeeded(),
                game.getTodayWeather());
        StringBuilder message = new StringBuilder("You got these fishes:\n");

        for (int i = 0; i < fishNumber; i++) {
            int random = (int) (Math.random() * availableFish.size());
            String fishName = availableFish.get(random);
            Entity entity = App.entityRegistry.makeEntity(fishName);

            double qualityDouble = Math.random() * (skill.getLevel() + 2) * poleEffect;
            qualityDouble /= (7 - game.getTodayWeather().getFishingEffect());

            ProductQuality quality = ProductQuality.getQuality(qualityDouble);
            entity.getComponent(Sellable.class).setProductQuality(quality);
            entity.getComponent(Pickable.class).setStackSize(1);
            fishes.add(entity);
            inventory.addItem(entity);

            message.append("Fish: ").append(fishName).append("\t Quality: ").append(quality).append("\n");

        }

        skill.addExperience(10);
        return new Result(true, message.toString());
    }

    /*----------------------------------------------------------------------------------------------*/


    public Result friendship() {
        Game game = App.getActiveGame();
        ArrayList<PlayerFriendship> playerFriendships = game.getCurrentPlayerFriendships();
        String message = PlayerFriendship.buildFriendshipMessage(game.getCurrentPlayer(), playerFriendships);

        return new Result(true, message);
    }

    public Result giveGift(String playerName, String itemName, int amount) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Player giftedPlayer = game.findPlayer(playerName);

        if (giftedPlayer == null) {
            return new Result(false, "Player not found");
        }

        if (giftedPlayer == currentPlayer) {
            return new Result(false, "you can't gift yourself!");
        }

        if (!game.checkPlayerDistance(currentPlayer, giftedPlayer)) {
            return new Result(false, giftedPlayer.getEntityName() + " is out of distance");
        }

        if (!App.entityRegistry.doesEntityExist(itemName)) {
            return new Result(false, "Item not found");
        }

        if (game.getFriendshipWith(giftedPlayer).getLevel() == 0) {
            return new Result(false, "Don't be cousin too soon:)");
        }

        Inventory inventory = currentPlayer.getComponent(Inventory.class);
        if (!currentPlayer.getComponent(Inventory.class).doesHaveItem(itemName, amount)) {
            return new Result(false, "You don't have " + amount + " items!");
        }
        Entity item = inventory.takeFromInventory(itemName, amount);

        Gift gift = new Gift(currentPlayer, giftedPlayer, item, game.getDate());
        giftedPlayer.receiveGift(gift);


        return new Result(true, "You gave gift to " + giftedPlayer.getUsername());
    }

    public Result giftList() {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        StringBuilder message = new StringBuilder("Your gift list:\n");

        for (Gift gift : currentPlayer.getGiftLog()) {
            message.append(gift.toString());
        }

        return new Result(true, message.toString());
    }

    public Result giftRate(int giftNumber, int rating) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Gift gift = currentPlayer.findGift(giftNumber);

        if (gift == null) {
            return new Result(false, "Gift not found");
        }

        if (gift.getRating() != -1) {
            return new Result(false, "You have already rated this gift.");
        }

        if (rating < 1 || rating > 5) {
            return new Result(false, "Rating must be between 1 and 5");
        }

        gift.setRating(rating);

        PlayerFriendship playerFriendship = game.getFriendshipWith(gift.getSender());
        if (rating < 3) playerFriendship.reduceXp((3 - rating) * 30 - 15);
        else playerFriendship.addXp((rating - 3) * 30 + 15);

        Entity item = gift.getContent().clone();
        currentPlayer.getComponent(Inventory.class).addItem(item);

        return new Result(true, "Rated and collected successfully!");
    }

    public Result giftHistory(String username) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Player giftedPlayer = game.findPlayer(username);
        if (giftedPlayer == null) {
            return new Result(false, "Player not found");
        }

        if (giftedPlayer == currentPlayer) {
            return new Result(false, ":/");
        }

        StringBuilder message = new StringBuilder("Your gift history with ");
        message.append(giftedPlayer.getUsername()).append(":\n");

        ArrayList<Gift> gifts = new ArrayList<>();
        for (Gift gift : currentPlayer.getGiftLog()) {
            if (gift.getSender().equals(giftedPlayer)) {
                gifts.add(gift);
            }
        }

        for (Gift gift : giftedPlayer.getGiftLog()) {
            if (gift.getSender().equals(giftedPlayer)) {
                gifts.add(gift);
            }
        }

        for (Gift gift : gifts) {
            message.append(gift.getGiftDetail()).append("\n");
        }


        return new Result(true, message.toString());
    }

    public Result hug(String username) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Player huggedPlayer = game.findPlayer(username);
        if (huggedPlayer == null) {
            return new Result(false, "Player not found");
        }

        if (huggedPlayer == currentPlayer) {
            return new Result(false, "You can't hug yourself!");
        }

        if (!game.checkPlayerDistance(huggedPlayer, currentPlayer)) {
            return new Result(false, "You can't hug from this distance!");
        }

        PlayerFriendship playerFriendship = game.getFriendshipWith(huggedPlayer);

        if (playerFriendship.getLevel() < 2) {
            return new Result(false, "too soon for a hug;)");
        }


        if (playerFriendship.isHadHuggedToday()) {
            return new Result(false, "You have already hugged today!");
        }

        playerFriendship.setHadHuggedToday(true);
        playerFriendship.addXp(60);

        return new Result(true, "Hugged successfully!");
    }

    public Result flower(String username) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Player floweredPlayer = game.findPlayer(username);

        if (floweredPlayer == null) {
            return new Result(false, "Player not found");
        }

        if (floweredPlayer == currentPlayer) {
            return new Result(false, "You can't flower yourself!");
        }

        if (!game.checkPlayerDistance(floweredPlayer, currentPlayer)) {
            return new Result(false, "You can't flower from this distance!");
        }

        PlayerFriendship playerFriendship = game.getFriendshipWith(floweredPlayer);
        if (playerFriendship.getLevel() < 2 || playerFriendship.getXp() < 300) {
            return new Result(false, "not time for flower");
        }

        Inventory inventory = currentPlayer.getComponent(Inventory.class);
        if (!inventory.doesHaveItem("Bouquet")) {
            return new Result(false, "You don't have bouquet!");
        }

        if (playerFriendship.getLevel() == 2) {
            playerFriendship.setLevel(3);
            playerFriendship.setXp(0);
        }

        inventory.takeFromInventory("Bouquet", 1);
        return new Result(true, "You have flowered " + floweredPlayer.getUsername() + "!");
    }

    public Result askMarriage(String username, String ringName) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Player marriagePlayer = game.findPlayer(username);


        if (currentPlayer.getAccount().getGender().equals(Gender.FEMALE)) {
            // equal to "sangin bash dokhtar"
            return new Result(false, "Be heavy girl!");
        }

        if (marriagePlayer == null) {
            return new Result(false, "Player not found! You can't marry with ghosts!");
        }

        if (marriagePlayer.getAccount().getGender().equals(Gender.MALE)) {
            return new Result(false, "Astaghforrellah...");
        }

        if (currentPlayer.getSpouse() != null) {
            if (currentPlayer.getSpouse().equals(marriagePlayer)) {
                return new Result(false, "You are married with her bro!");
            } else {
                return new Result(false, "you cant cheat on your spouse!");
            }
        }

        if (marriagePlayer.getSpouse() != null) {
            return new Result(false, "She is married...");
        }

        PlayerFriendship playerFriendship = game.getFriendshipWith(marriagePlayer);
        if (playerFriendship.getLevel() < 3 || playerFriendship.getXp() < 400) {
            return new Result(false, "Your friendship is not at appropriate level!");
        }

        if (!game.checkPlayerDistance(marriagePlayer, currentPlayer)) {
            return new Result(false, "get closer to her!");
        }

        if (!App.entityRegistry.doesEntityExist(ringName)) {
            return new Result(false, "Ring " + ringName + " doesn't exist!");
        }
        Entity ring = App.entityRegistry.makeEntity(ringName);
        Inventory inventory = currentPlayer.getComponent(Inventory.class);

        if (!ring.hasTag(EntityTag.RING)) {
            return new Result(false, ringName + " is not a ring!");
        }

        if (!inventory.doesHaveItem(ring)) {
            return new Result(false, "You don't have this ring!");
        }

        // do all the stuff in this function
        marriagePlayer.addSuitor(currentPlayer, ring);
        inventory.takeFromInventory(ring, 1);

        return new Result(true, "your request sent successfully!");
    }

    public Result respond(String respond, String username) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Player respondPlayer = game.findPlayer(username);
        if (respondPlayer == null) {
            return new Result(false, "Player not found!");
        }

        if (!currentPlayer.getSuitors().containsKey(respondPlayer)) {
            return new Result(false, "Baa! He doesn't request you...");
        }
        Entity ring = currentPlayer.getSuitors().get(respondPlayer);

        if (respond.equals("accept")) {
            game.marry(currentPlayer, respondPlayer);
            currentPlayer.getComponent(Inventory.class).addItem(ring);
            return new Result(true, "lalalala mobarak bada!");
        } else if (respond.equals("reject")) {
            currentPlayer.getSuitors().remove(respondPlayer);
            respondPlayer.getComponent(Inventory.class).addItem(ring);
            //effects on friendship
            game.getFriendshipWith(respondPlayer).setLevel(0);
            game.getFriendshipWith(respondPlayer).setXp(0);

            // effects on energy
            respondPlayer.getEnergy().setModifier(0.5f, 7);

            return new Result(true, "hala fekr kardi ki hasti??");
        } else {
            return new Result(false, "type \"accept\" or \"reject\" as a response...");
        }


    }

    public Result startTrade() {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        App.setCurrentMenu(Menu.TRADE_MENU);
        StringBuilder message = new StringBuilder("Welcome to TradeMenu!\nYour new offers: \n");

        for (TradeOffer tradeOffer : currentPlayer.getTrades()) {
            if (tradeOffer.getReceiver().equals(currentPlayer) && !tradeOffer.isSeen()) {
                tradeOffer.setSeen(true);
                message.append(tradeOffer.infoMessage(false));
            }
        }

        return new Result(true, message.toString());
    }

    public Result talk(String receiverPlayerName, String messageString) {
        Game game = App.getActiveGame();
        Player receiverPlayer = game.findPlayer(receiverPlayerName);
        Player currentPlayer = game.getCurrentPlayer();

        // check player existence
        if (receiverPlayer == null) {
            return new Result(false, "Player with name " + receiverPlayerName + " not found");
        }

        if (!game.checkPlayerDistance(receiverPlayer, currentPlayer)) {
            return new Result(false, "Player is not next to you...");
        }

        Message message = new Message(game.getDate(), messageString, receiverPlayer, currentPlayer);
        currentPlayer.getMessageLog().add(message);
        receiverPlayer.getMessageLog().add(message);
        receiverPlayer.setHaveNewMessage(true);


        PlayerFriendship playerFriendship = game.getFriendshipWith(receiverPlayer);

        if (!playerFriendship.isHadMessageToday()) {
            playerFriendship.setHadMessageToday(true);
            playerFriendship.addXp(20);
        }

        return new Result(true, "Your message has been sent successfully!");
    }

    public Result talkHistory(String playerName) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        currentPlayer.makeMessagesSeen();
        Player talkedPlayer = game.findPlayer(playerName);
        currentPlayer.setHaveNewMessage(false);

        if (talkedPlayer == null) {
            return new Result(false, "Player with name " + playerName + " not found");
        }

        ArrayList<Message> commonMessages = new ArrayList<>(currentPlayer.getMessageLog());
        commonMessages.retainAll(talkedPlayer.getMessageLog());

        String outcomeMessage = Message.buildMessageHistory(currentPlayer, talkedPlayer, commonMessages);

        return new Result(true, outcomeMessage);

    }

    public Result meetNPC(String npcName) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        NPC npc = game.findNPC(npcName);
        NpcFriendship npcFriendship = currentPlayer.getNpcFriendships().get(npc);

        if (npc == null) {
            return new Result(false, "NPC with name " + npcName + " not found");
        }

        if (currentPlayer.getPosition().getDistance(npc.getComponent(PositionComponent.class).get()) > 2) {
            return new Result(false, "You are too far from this NPC");
        }

        String message = npc.getCorrectDialogue(game.getDate().getSeason(), npcFriendship.getLevel(),
                game.getTodayWeather(), game.getDate().getHour() < 16);
        if (message == null) {
            message = "not set yet!";
        }

        if (!npcFriendship.wasMetToday()) {
            npcFriendship.setWasMetToday(true);
            npcFriendship.addXp(20);
        }

        return new Result(true, message);
    }

    public Result giftNPC(String npcName, String itemName) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();

        NPC npc = game.findNPC(npcName);
        if (npc == null) {
            return new Result(false, "NPC with name " + npcName + " not found");
        }

        if (!App.entityRegistry.doesEntityExist(itemName)) {
            return new Result(false, "Item with name " + itemName + " does not exist");
        }
        Entity item = App.entityRegistry.makeEntity(itemName);

        Inventory inventory = currentPlayer.getComponent(Inventory.class);
        if (!inventory.doesHaveItem(itemName)) {
            return new Result(false, "You dont have this item");
        }

        if (item.hasTag(EntityTag.TOOL)) {
            return new Result(false, "You can't gift tools to NPC!");
        }

        inventory.takeFromInventory(itemName, 1);
        currentPlayer.addFriendshipByGift(npc, item);

        return new Result(true, "Your gift has been sent successfully!");
    }

    public Result questList() {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        StringBuilder message = new StringBuilder("Available quests:\n");

        for (Quest quest : game.getQuests()) {
            if (quest.doesHaveAccess(currentPlayer).isSuccessful()) {
                message.append(quest);
            }

        }
        return new Result(true, message.toString());
    }

    public Result questFinish(int questId) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Quest quest = game.findQuest(questId);
        if (quest == null) {
            return new Result(false, "Quest with id " + questId + " not found");
        }

        Result haveAccess = quest.doesHaveAccess(currentPlayer);
        if (!haveAccess.isSuccessful()) {
            return haveAccess;
        }
        if (!App.entityRegistry.doesEntityExist(quest.getRequest())) {
            return new Result(false, "This item isnt set yet!");
        }


        Entity item = App.entityRegistry.makeEntity(quest.getRequest());
        int itemAmount = quest.getRequestNumber();

        Inventory inventory = currentPlayer.getComponent(Inventory.class);
        if (!inventory.doesHaveItem(item.getEntityName(), itemAmount)) {
            return new Result(false, "You dont have enough \"" + item.getEntityName() + "\" items");
        }

        if(currentPlayer.getPosition().getDistance(quest.getNpc().getComponent(PositionComponent.class).get()) > 2) {
            return new Result(false, "You are too far from this NPC");
        }

        inventory.takeFromInventory(item.getEntityName(), itemAmount);
        int rewardNumber = quest.getRewardNumber();
        NpcFriendship npcFriendship = currentPlayer.getNpcFriendships().get(quest.getNpc());
        if (npcFriendship.getLevel() >= 2) {
            rewardNumber *= 2;
        }


        if (quest.getReward().equalsIgnoreCase("Gold")) {
            currentPlayer.getWallet().changeBalance(rewardNumber);
            return new Result(true, "Quest finished successfully!\n" +
                    "You got: " + rewardNumber + "Golds");
        }


        if (!App.entityRegistry.doesEntityExist(quest.getReward())) {
            return new Result(false, "Item not have set yet");
        }
        quest.setCompleted(true);
        Entity reward = App.entityRegistry.makeEntity(quest.getReward());
        reward.getComponent(Pickable.class).setStackSize(rewardNumber);


        inventory.addItem(reward);

        return new Result(true, "Quest finished successfully!\n" +
                "You got: " + rewardNumber + item.getEntityName());
    }


    public Result friendshipNPC() {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        StringBuilder message = new StringBuilder("NPC Friendship: \n");

        message.append(currentPlayer.npcFriendshipDetails());

        return new Result(true, message.toString());
    }

    public Result handleRawInput(char c) {
        Player player = App.getActiveGame().getCurrentPlayer();
        WalkProposal p;
        switch (c) {
            case 'a':
            case 'A':
                p = this.proposeWalk(player.getPosition().getCol() - 1, player.getPosition().getRow());
                return executeWalk(p);
            case 's':
            case 'S':
                p = this.proposeWalk(player.getPosition().getCol(), player.getPosition().getRow() + 1);
                return executeWalk(p);
            case 'w':
            case 'W':
                p = this.proposeWalk(player.getPosition().getCol(), player.getPosition().getRow() - 1);
                return executeWalk(p);
            case 'd':
            case 'D':
                p = this.proposeWalk(player.getPosition().getCol() + 1, player.getPosition().getRow());
                return executeWalk(p);
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
                return toolsUse(Direction.getDirection(c - '0'));
            case 'e':
                return pickupNearItems();
            case 't':
                return advanceTime(1);
            default:
                break;
        }
        return new Result(true, "");
    }

    public Result toggleMap() {
        App.getActiveGame().toggleMapVisibility();
        return null;
    }

    /*------------------------------------------cheat-------------------------------------------*/
    public Result cheatGiveItem(String name, int quantity) {
        Player currentPlayer = App.getActiveGame().getCurrentPlayer();
        if (quantity <= 0) {
            return new Result(false, "You should enter positive number!");
        }
        if (!App.entityRegistry.doesEntityExist(name)) {
            return new Result(false, "Entity doesnt exist");
        }
        Entity entity = App.entityRegistry.makeEntity(name);
        if (!currentPlayer.getComponent(Inventory.class).canAddItem(entity, quantity))
            return new Result(false, "Your inventory doesn't have enough size");
        if (entity.getComponent(Pickable.class) == null) {
            return new Result(false, "Entity isn't pickable");
        }
        entity.getComponent(Pickable.class).changeStackSize(quantity);
        currentPlayer.getComponent(Inventory.class).addItem(entity);
        return new Result(true, quantity + " " + name + (quantity > 1 ? "s" : "") +
                " were given to " + currentPlayer.getAccount().getNickname());
    }

    public Result cheatBuildBuilding(int x, int y, String name, boolean force) {
        Entity building = App.entityRegistry.makeEntity(name);
        if (building == null || !building.hasTag(EntityTag.BUILDING))
            return new Result(false, "no building exists with that name");
        if (!force && EntityPlacementSystem.canPlace(x, y, building.getComponent(Placeable.class)))
            return new Result(true, "Can't place that there ma lord");
        if (force) EntityPlacementSystem.clearArea(x, y, building.getComponent(Placeable.class));
        EntityPlacementSystem.placeEntity(building, new Position(x, y), App.getActiveGame().getMainMap());

        App.getActiveGame().getCurrentPlayer().addOwnedBuilding(building);
        if (building.getComponent(AnimalHouse.class) != null) {
            building.getComponent(AnimalHouse.class).setName("House" + (int) (Math.random() * 1000));
        }

        return new Result(true, "placed");
    }

    /**
     * if you amount is 0 it will reset to 0 the skill
     * else it will add to it experience
     */
    public Result cheatAddSkill(String name, int amount) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        SkillType skillType = SkillType.getSkillType(name);
        if (skillType == null) {
            return new Result(false, "Skill type not found");
        }

        Skill skill = currentPlayer.getSkill(skillType);
        if (amount <= 0) {
            skill.reset();
            return new Result(true, "skill reset successfully!");
        }

        skill.addExperience(amount);

        return new Result(true, "added " + amount + " experience");
    }

    public Result skillStatue() {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();

        StringBuilder message = new StringBuilder("Your Skills:\n");
        for (SkillType skillType : SkillType.values()) {
            Skill skill = currentPlayer.getSkill(skillType);
            message.append(skillType.name().toLowerCase()).append("\tLevel:").append(skill.getLevel())
                    .append("\tExperience: ").append(skill.getExperience()).append("\n");
        }

        return new Result(true, message.toString());
    }

    public Result addMoney(int amount) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        currentPlayer.getWallet().addBalance(amount);

        return new Result(true, "Your money: " + currentPlayer.getWallet().getBalance());
    }

    public Result toggleUnlimitedInventory(){
        Inventory inventory = App.getActiveGame().getCurrentPlayer().getComponent(Inventory.class);
        inventory.setUnlimited(!inventory.getUnlimited());

        return new Result(true, "your inventory is" + (inventory.getUnlimited() ? "" : " not") + " unlimited now.");
    }

    public Result cheatSetFriendship(int level, int xp, String name) {
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Player player = game.findPlayer(name);
        if (player == null) {
            return new Result(false, "Player not found");
        }

        if ((level + 1) * 100 < xp) {
            return new Result(false, "dige cheat ham hadi dare:/");
        }

        PlayerFriendship playerFriendship = game.getFriendshipWith(player);
        playerFriendship.setLevel(level);
        playerFriendship.setXp(xp);
        return new Result(true, "set!");
    }

    public Result waterAll() {
        Game game = App.getActiveGame();
        GrowthSystem.waterAll(game.getMainMap());

        return new Result(true, "watered!");
    }

    /*-----------------------------------------------------------------------------------------*/


    public Result putInFridge(String entityName, int amount, boolean put){
        Entity entity = App.entityRegistry.getEntityDetails(entityName);
        if(entity == null){
            return new Result(false, "food doesn't exist");
        }

        if(entity.getComponent(Edible.class) == null){
            return new Result(false, entityName + " isn't a food");
        }

        Player player = App.getActiveGame().getCurrentPlayer();
        Inventory fridge = player.getRefrigerator().getComponent(Inventory.class);
        Inventory playerInventory = player.getComponent(Inventory.class);
        Inventory source, destination;
        if(put){
            source = playerInventory;
            destination = fridge;
        }else{
            source = fridge;
            destination = playerInventory;
        }

        if(!destination.canAddItem(entity, amount)){
            return new Result(false, "not enough space");
        }

        Entity takenEntity = source.takeFromInventory(entityName, amount);

        if(takenEntity == null){
            return new Result(false, "not enough " + entityName + " in " + (put ? "your inventory" : "fridge") + ".");
        }

        destination.addItem(takenEntity);

        return new Result(true, amount + " " + entityName + " was " + (put ? "put in fridge" : "taken from fridge"));
    }
    public Result placeItem(String name, int direction){
        if(App.entityRegistry.getEntityDetails(name) == null){
            return new Result(false, "entity doesn't exist");
        }

        Player player = App.getActiveGame().getCurrentPlayer();

        Entity entity = player.getComponent(Inventory.class).takeFromInventory(name, 1);

        if(entity == null){
            return new Result(false, "you don't have " + name + " in your backpack");
        }

        Placeable placeable = entity.getComponent(Placeable.class);

        if(placeable == null){
            return new Result(false, name + " is not placeable");
        }

        Direction dir = Direction.getDirection(direction);

        if(dir == null){
            return new Result(false, "not a valid direction");
        }

        ;

        return EntityPlacementSystem.placeEntity(entity, player.getPosition().copy().add(dir.dx, dir.dy));
    }

    public Result pickupNearItems(){
        ArrayList<Pickable> pickables = new ArrayList<>(App.getActiveGame().getActiveMap().getComponentsOfType(Pickable.class));
        if(pickables == null) return new Result(false, "You picked up nothing dumbass");
        Player player = App.getActiveGame().getCurrentPlayer();
        Position playerPos = player.getPosition();
        GameMap map = playerPos.getMap();
        Inventory inventory = player.getComponent(Inventory.class);

        ArrayList<Entity> pickedUpItems = new ArrayList<>();


        ArrayList<Entity> entitiesToRemove = new ArrayList<>();
        for (Pickable pickable : pickables) {
            Entity entity = pickable.getEntity();

            if(playerPos.getDistance(entity.getComponent(PositionComponent.class).get()) > 2) continue;

            if(!inventory.canAddItem(entity)) continue;

            if(entity.getComponent(Forageable.class) != null){
                Forageable forageable = entity.getComponent(Forageable.class);
                if(!forageable.isForaged()){
                    player.getSkill(SkillType.FORAGING).addExperience(10);
                }
                forageable.setForaged(true);
            }

            Entity leftOver = inventory.addItem(entity);
            pickedUpItems.add(entity);
        }

        if(pickedUpItems.isEmpty()){
            return new Result(false, "You picked up nothing dumbass");
        }

        StringBuilder out = new StringBuilder("you picked up:");
        for(Entity entity : pickedUpItems){
            out.append("\n").append(entity.getEntityName());
        }

        return new Result(true, out.toString());
    }
    public Result cheatSpawnItem(String name, int quantity){
        Player currentPlayer = App.getActiveGame().getCurrentPlayer();
        if (quantity <= 0) {
            return new Result(false, "You should enter positive number!");
        }
        if (!App.entityRegistry.doesEntityExist(name)) {
            return new Result(false, "Entity doesnt exist");
        }
        Entity entity = App.entityRegistry.makeEntity(name);
        if (entity.getComponent(Pickable.class) == null) {
            return new Result(false, "Entity isn't pickable");
        }
        entity.getComponent(Pickable.class).changeStackSize(quantity);
        EntityPlacementSystem.placeOnMap(entity, currentPlayer.getPosition(), currentPlayer.getPosition().getMap());
        return new Result(true, quantity + " " + name + (quantity > 1 ? "s" : "") +
                " was spawned on ground.");
    }
    public Result buildGreenhouse(){
        Player player = App.getActiveGame().getCurrentPlayer();

        int wood = player.getComponent(Inventory.class).getItemCount("Wood");
        double money = player.getWallet().getBalance();

        if(money < 1000 || wood < 500){
            return new Result(false, "not enough resources to rebuild the greenhouse");
        }

        Position position = player.getGreenHouse().getComponent(PositionComponent.class).get();
        EntityPlacementSystem.removeExterior(player.getGreenHouse());

        Entity greenhouse = App.entityRegistry.makeEntity("Greenhouse");

        player.setGreenHouse(greenhouse);

        EntityPlacementSystem.clearArea(position.getCol(), position.getRow(), greenhouse.getComponent(Placeable.class));
        EntityPlacementSystem.placeEntity(greenhouse, position);

        player.getWallet().reduceBalance(1000);
        player.getComponent(Inventory.class).takeFromInventory("Wood", 500);

        return new Result(true, "greenhouse built");
    }
    public  Result showShippingBin(){
        Player player = App.getActiveGame().getCurrentPlayer();
        Game game = App.getActiveGame();
        Inventory inventory = player.getComponent(Inventory.class);

        int[][] directions = {{1, 0}, {1, -1}, {1, 1}, {0, 1}, {0, -1}, {-1, 1}, {-1, 0}, {-1, -1}};
        int x = player.getPosition().getCol();
        int y = player.getPosition().getRow();
        for (int[] dir : directions) {
            Tile tile = game.getActiveMap().getTileByPosition(y + dir[0], x + dir[1]);
            if (tile == null) continue;
            Entity tileContent = tile.getContent();
            if(tileContent != null && StringUtils.isNamesEqual(tileContent.getEntityName(), "Shipping Bin")) {
                return new Result(true, tileContent.getComponent(Inventory.class).toString());
            }
        }

        return new Result(false, "no shipping bin found");
    }
    public Result trashItem(String name, int amount){
        Player player = App.getActiveGame().getCurrentPlayer();
        Entity entity = player.getComponent(Inventory.class).takeFromInventory(name, player.getComponent(Inventory.class).getItemCount(name));
        if(entity == null) return new Result(false, "");
        entity.delete();
        //TODO: Parsa
        return new Result(false, "");
    }

    public Result saveGame(){
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("testSave.ser"));
            out.writeObject(App.getActiveGame());
            out.close();


            ObjectInputStream in = new ObjectInputStream(new FileInputStream("testSave.ser"));
            Game game = (Game) in.readObject();
            return new Result(true, "saved!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            advanceTime(1);
        }
    }
}
