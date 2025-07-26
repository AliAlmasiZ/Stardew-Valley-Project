package com.ap.stardew.models.entities;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Vec2;
import com.ap.stardew.models.animal.Animal;
import com.ap.stardew.models.entities.components.*;
import com.ap.stardew.models.entities.components.harvestable.Harvestable;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.enums.EntityTag;
import com.ap.stardew.models.enums.ProductQuality;
import com.ap.stardew.models.enums.SkillType;
import com.ap.stardew.models.enums.TileType;
import com.ap.stardew.models.gameMap.GameMap;
import com.ap.stardew.models.gameMap.TIlePlower;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.records.Result;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import java.util.ArrayList;


public enum UseFunction {
    PLOW () {
        @Override
        protected Result use(Player player,Entity tool, Tile tile, Entity target) {
            if(tile.getContent() != null){
                return new Result(false, "The tile isn't empty");
            }
            if(tile.getType() != TileType.DIRT){
                return new Result(false, "You can only plow dirt");
            }
            tile.setType(TileType.PLOWED);
            int energyCost = 5 - tool.getComponent(Upgradable.class).getMaterial().getLevel();
            energyCost -= player.getSkill(SkillType.FARMING).getLevel() == 4 ? 1 : 0;
            if(player.getActiveBuff() != null)
                energyCost -= player.getActiveBuff().effectOnSkill(SkillType.FARMING);

            player.reduceEnergy(Math.max(energyCost, 0), App.getActiveGame().getTodayWeather());
            player.addExperince(SkillType.FARMING, 5);

            TIlePlower.plowTile(tile);

            return new Result(true, "Ground converted to Hoed ground");
        }
    },
    DE_PLOW(){
        @Override
        protected Result use(Player player,Entity tool, Tile tile, Entity target) {
            if(tile.getContent() != null){
                return new Result(false, "The tile isn't empty");
            }
            if(tile.getType() != TileType.PLOWED){
                return new Result(false, "you can only de_plow plowed ground");
            }
            tile.setType(TileType.DIRT);
            int energyCost = 5 - tool.getComponent(Upgradable.class).getMaterial().getLevel();
            energyCost -= player.getSkill(SkillType.FARMING).getLevel() == 4 ? 1 : 0;
            if(player.getActiveBuff() != null)
                energyCost -= player.getActiveBuff().effectOnSkill(SkillType.FARMING);

            player.reduceEnergy(Math.abs(energyCost), App.getActiveGame().getTodayWeather());
            player.addExperince(SkillType.FARMING, 5);
            return new Result(true, "Hoed Ground converted to Grass");
        }
    },
    MINE(){
        @Override
        protected Result use(Player player, Entity tool, Tile tile, Entity target) {
            Entity mineral = tile.getContent();
            if(mineral == null){
                return new Result(false, "Nothing to mine!");
            }
            if(!mineral.hasTag(EntityTag.BREAKABLE_WITH_PICKAXE)){
                return new Result(false, "you can't use a pickaxe on " + mineral.getEntityName());
            }

            Harvestable harvestable = mineral.getComponent(Harvestable.class);
            int energyCost = 5 - tool.getComponent(Upgradable.class).getMaterial().getLevel();
            energyCost -= player.getSkill(SkillType.MINING).getLevel() == 4 ? 1 : 0;
            if(player.getActiveBuff() != null)
                energyCost -= player.getActiveBuff().effectOnSkill(SkillType.MINING);

            player.reduceEnergy(Math.abs(energyCost), App.getActiveGame().getTodayWeather());
            if(harvestable.getMaterial().getLevel() > tool.getComponent(Upgradable.class).getMaterial().getLevel()){
                return new Result(false, "Your pickaxe cant mine that mineral. you need " + harvestable.getMaterial() + " pickaxe.");
            }
            player.addExperince(SkillType.MINING, 10);

            ArrayList<Entity> harvestedEntities = harvestable.harvest();

//            if(player.getSkill(SkillType.MINING))

            if(mineral.getComponent(Forageable.class) != null){
                player.getSkill(SkillType.FORAGING).addExperience(10);
            }

            mineral.delete();

            for(Entity e : harvestedEntities){
                player.getComponent(Inventory.class).addItem(e);
            }
            return new Result(true, "");
        }
    },
    DESTROY_ITEMS {
        @Override
        protected Result use(Player player, Entity tool, Tile tile, Entity target) {
            return null;
        }
    },
    CHOP_TREE {
        @Override
        protected Result use(Player player, Entity tool, Tile tile, Entity target) {
            Entity tree = tile.getContent();
            if(tree == null){
                return new Result(false, "No tree to chop!");
            }

            if(!tree.hasTag(EntityTag.BREAKABLE_WITH_AXE)){
                return new Result(false, "you can't use an axe on " + tree.getEntityName());
            }

            Harvestable harvestable = tree.getComponent(Harvestable.class);
            int energyCost = 5 - tool.getComponent(Upgradable.class).getMaterial().getLevel();
            energyCost -= player.getSkill(SkillType.FORAGING).getLevel() == 4 ? 1 : 0;
            if(player.getActiveBuff() != null)
                energyCost -= player.getActiveBuff().effectOnSkill(SkillType.FORAGING);

            player.reduceEnergy(Math.max(energyCost, 0), App.getActiveGame().getTodayWeather());
            if(harvestable.getMaterial().getLevel() > tool.getComponent(Upgradable.class).getMaterial().getLevel()){
                return new Result(false, "Your axe cant chop that tree. you need " + harvestable.getMaterial() + " axe.");
            }
            player.addExperince(SkillType.FORAGING, 5);

            ArrayList<Entity> harvestedEntities = harvestable.harvest();
            tree.delete();

            for(Entity e : harvestedEntities){
                player.getComponent(Inventory.class).addItem(e);
            }
            return new Result(true, "");
        }
    },
    DESTROY_BRANCHES {
        @Override
        //TODO
        protected Result use(Player player, Entity tool, Tile tile, Entity target) {
            return null;
        }
    },
    WATER_GROUND {
        @Override
        protected Result use(Player player, Entity tool, Tile tile, Entity target) {
            Container container =  tool.getComponent(Container.class);
            if(tile.getContent() == null || tile.getContent().getComponent(Growable.class) == null)
                return new Result(false, "you can't water this ground");
            if(container.getCharge() == 0)
                return new Result(false, "You should fill watering can first!");
            tile.getContent().getComponent(Growable.class).setWateredToday(true);
            int energyCost = 5 - tool.getComponent(Upgradable.class).getMaterial().getLevel();
            energyCost -= player.getSkill(SkillType.FARMING).getLevel() == 4 ? 1 : 0;
            if(player.getActiveBuff() != null)
                energyCost -= player.getActiveBuff().effectOnSkill(SkillType.FARMING);
            player.reduceEnergy(Math.max(energyCost, 0), App.getActiveGame().getTodayWeather());
            container.decreaseCharge();
            return new Result(true, "tile watered successfully");
        }
    },
    FILL_WATER {
        @Override
        protected Result use(Player player, Entity tool, Tile tile, Entity target) {
            if(tile.getType().equals(TileType.WATER) /*TODO: or greenhouse water*/) {
                tool.getComponent(Container.class).fillContainer();
                int energyCost = 5 - tool.getComponent(Upgradable.class).getMaterial().getLevel();
                energyCost -= player.getSkill(SkillType.FARMING).getLevel() == 4 ? 1 : 0;
                if(player.getActiveBuff() != null)
                    energyCost -= player.getActiveBuff().effectOnSkill(SkillType.FARMING);
                player.reduceEnergy(Math.max(0, energyCost));
                return new Result(true, "watering can filled successfully");
            }
            else
                return new Result(false, "Can't fill water with this tile");
        }
    },
    FISH {
        @Override
        protected Result use(Player player, Entity tool, Tile tile, Entity target) {
            return null;
        }
    },
    CUT_GRASS {
        @Override
        protected Result use(Player player, Entity tool, Tile tile, Entity targetEntity) {
            return null;
        }
    },
    HARVEST_PLANTS {
        @Override
        protected Result use(Player player, Entity tool, Tile tile, Entity target) {
            Entity entity = tile.getContent();
            Growable growable;
            if(entity == null || (growable = entity.getComponent(Growable.class)) == null){
                return new Result(false, "nothing to harvest in that tile");
            }

            Result result = growable.canCollectProduct();
            if (!result.isSuccessful()) {
                return result;
            }

            Inventory inventory = player.getComponent(Inventory.class);

            if (growable.isOneTime()) {

                EntityPlacementSystem.emptyTile(tile);
                tile.setType(TileType.DIRT);
                entity.getComponent(Sellable.class).
                        setProductQuality(ProductQuality.getQuality(Math.random() * 0.5f));
                entity.getComponent(Pickable.class).setStackSize(1);
                inventory.addItem(entity);
            } else {
                Entity fruit = growable.collectFruit();
                growable.setDaysPastFromRegrowth(0);
                fruit.getComponent(Pickable.class).setStackSize(1);
                fruit.getComponent(Sellable.class).
                        setProductQuality(ProductQuality.getQuality(Math.random() * 0.5f));
                inventory.addItem(fruit);
            }

            player.addExperince(SkillType.FARMING, 10);



            return new Result(true, "harvested");
        }
    },
    EXTRACT_MILK {

        @Override
        protected Result use(Player player, Entity tool, Tile tile, Entity target) {

            if(tool.getComponent(Container.class).getCharge() > 0)
                return new Result(false, "milk pail is full");
            player.reduceEnergy(4);// on success and fail
            if(target == null )
                return new Result(false, "you should select an animal");
            if(!(target instanceof Animal))
                return new Result(false, "you should select an animal");


            Animal animal = (Animal) target;
            if (!animal.getAnimalType().getNeededTool().equals("Milk pail")) {
                return new Result(false, target.getEntityName() + " doesn't produce milk.");
            }

            Entity product = animal.getTodayProduct();
            if (product == null) {
                return new Result(false, "It didn't produce milk today.");
            }
            animal.setTodayProduct(null);
            tool.getComponent(Container.class).fillContainer();
            Inventory inventory = player.getComponent(Inventory.class);
            inventory.addItem(product);
            animal.addFriendshipLevel(5);
            player.getSkill(SkillType.FARMING).addExperience(5);
            return new Result(true, "Milk extracted successfully");
        }
    },
    COLLECT_WOOL{
        @Override
        protected Result use(Player player, Entity tool, Tile tile, Entity target) {
            player.reduceEnergy(4, App.getActiveGame().getTodayWeather());
            if(!(target instanceof Animal))
                return new Result(false, "you should select an animal");

            Animal animal = (Animal) target;
            if (!animal.getAnimalType().getNeededTool().equals("Shears")) {
                return new Result(false, "You can not collect wool from this animal");
            }

            animal.setTodayProduct(null);
            Inventory inventory = player.getComponent(Inventory.class);
            inventory.addItem(animal);
            animal.addFriendshipLevel(5);
            player.getSkill(SkillType.FARMING).addExperience(5);


            return new Result(true, "wool collected successfully");
        }
    },
    BREAK_PLACEABLES{
        @Override
        protected Result use(Player player, Entity tool, Tile tile, Entity target) {
            Entity entity = tile.getContent();
            if(entity == null){
                return new Result(false, "nothing to break");
            }
            Pickable pickable = entity.getComponent(Pickable.class);
            if(pickable == null){
                return new Result(false, entity.getEntityName() + " is not a pickable");
            }

            Inventory inventory = player.getComponent(Inventory.class);
            if(!inventory.canAddItem(entity)){
                return new Result(false, "not enough space in inventory to pickup the " + entity.getEntityName());
            }

            inventory.addItem(entity);

            return new Result(true, "broke and picked up the " + entity.getEntityName());
        }
    },
    INSPECT_ENTITY{
        @Override
        protected Result use(Player player, Entity tool, Tile tile, Entity target) {
            Entity entity = tile.getContent();
            StringBuilder out = new StringBuilder();

            if(entity == null) {
                ArrayList<Pickable> pickables = player.getPosition().getMap().getComponentsOfType(Pickable.class);
                if(pickables == null) return new Result(true, "");

                Vec2 tilePosition = tile.getPosition();

                for(Pickable p : pickables){
                    if(p.getEntity().getComponent(PositionComponent.class).get().getDistance(tilePosition) < 0.1){
                        out.append(p.getEntity()).append("\n");
                    }
                }

                return new Result(true, out.toString());
            }

            return new Result(true, entity.toString());
        }
    }
    ;


    abstract protected Result use(Player player,Entity tool, Tile tile, Entity target);
    public Result use(Entity tool, Tile tile, Entity target){
        return this.use(App.getActiveGame().getCurrentPlayer(), tool, tile, target);
    }
    public Result use(Entity tool, Tile tile){
        return this.use(App.getActiveGame().getCurrentPlayer(), tool, tile, null);
    }
    public Result use(Entity tool, Entity target){
        return this.use(App.getActiveGame().getCurrentPlayer(), tool, null, target);
    }
}
