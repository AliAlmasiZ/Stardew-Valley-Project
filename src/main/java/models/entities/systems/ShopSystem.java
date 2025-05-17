package models.entities.systems;

import models.App;
import models.Game;
import models.Position;
import models.entities.Entity;
import models.entities.components.Pickable;
import models.entities.components.Placeable;
import models.entities.components.Sellable;
import models.entities.components.Upgradable;
import models.entities.components.inventory.Inventory;
import models.enums.Material;
import models.player.Player;
import models.player.Wallet;
import models.shop.*;
import models.utils.StringUtils;
import records.Result;

public class ShopSystem {
    public static Result buyProduct(ShopProduct product, int amount) {
        if(amount > product.getStock() && product.getStock() >= 0)
            return new Result(false, "There isn't enough stock! go come tomorrow:)");
        if(product.getName().toLowerCase().contains("(recipe)")) {
            Result result = handlePay(product, 1);
            if(!result.isSuccessful())
                return result;
            product.addSold(1);
            App.getActiveGame().getCurrentPlayer().addRecipe(product.getName().replace("(Recipe)", ""));
            return new Result(true, "recipe added successfully");
        }



        Entity productEntity = product.getEntity();


        if(productEntity.getComponent(Pickable.class) != null && product instanceof OtherShopProduct) {
            return buyPickable(product, amount);
        }
/*
        if(productEntity.getComponent(Placeable.class) != null && product instanceof BuildingShopProduct) {
            return new Result(true, null);
        }

        if(product instanceof AnimalShopProduct) {
            return new Result(true, null);
        }*/

        App.getView().err("error: product " + productEntity.getEntityName() + " doesn't belong to any states");
        return new Result(false, "invalid Product!");
    }

    public static Result buyPickable(ShopProduct p, int amount) {
        Entity e = p.getEntity();
        e.getComponent(Pickable.class).setStackSize(amount);
        Inventory inventory = App.getActiveGame().getCurrentPlayer().getComponent(Inventory.class);
        if(inventory.canAddItem(e, amount)){
            Result result = handlePay(p, amount);
            if(!result.isSuccessful())
                return result;
            p.addSold(amount);
            inventory.addItem(e);
            return new Result(true, e.getEntityName() +  " x(" + amount + ")" +  " added to your inventory!");
        }
        return new Result(false, "Your inventory doesn't have enough space");
    }


    public static Result buildPlaceable(ShopProduct p, int x, int y) {
        if(p == null)
            return new Result(false, "this building isn't available in this shop");
        if(!(p instanceof BuildingShopProduct))
            return new Result(false, "this can not be placed");
        Result result = handlePay(p, 1);
        if(!result.isSuccessful())
            return result;
        Entity building = p.getEntity();
        EntityPlacementSystem.placeEntity(building, new Position(x, y), App.getActiveGame().getMainMap());
        //TODO: if can't place gets error
        p.addSold(1);
        Player player = App.getActiveGame().getCurrentPlayer();
        player.addOwnedBuilding(building);
        return new Result(true, "building build successfully!");
    }


    public static Result buyAnimal() {
        //TODO
        return null;
    }


    public static Result UpgradeTool(String toolName) {
        Player player = App.getActiveGame().getCurrentPlayer();
        Inventory inventory = player.getComponent(Inventory.class);
        Entity tool = inventory.getItem(toolName);
        if(tool == null)
            return new Result(false, "You don't have this tool!");
        Upgradable upgradable = tool.getComponent(Upgradable.class);
        Shop shop = player.getCurrentMap().getBuilding().getComponent(Shop.class);
        Material nextLevel = Material.getMaterialByLevel(upgradable.getMaterial().getLevel() + 1);
        if(nextLevel == null)
            return new Result(false, "You can upgrade Iridium tool");
        UpgradableShopProduct product = shop.getUpgradableShopProduct(nextLevel);
        //TODO: handle trashcan
        if(product.getStock() == 0)
            return new Result(false, "you can't upgrade this material today! try again tomorrow");
        if(!inventory.doesHaveItem(product.getIngredientName(), product.getIgredientCount()))
            return new Result(false, "You don't have enough " + product.getIngredientName());
        Result result = handlePay(product, 1);
        if(!result.isSuccessful())
            return result;
        inventory.takeFromInventory(product.getIngredientName(), product.getIgredientCount());
        upgradable.upgrade();
        product.addSold(1);
        return new Result(true, toolName + " successfully upgraded to " + nextLevel);

    }




    private static Result handlePay(ShopProduct p, int amount) {
        Wallet wallet = App.getActiveGame().getCurrentPlayer().getWallet();
        Inventory inventory = App.getActiveGame().getCurrentPlayer().getComponent(Inventory.class);
        if(wallet.getBalance() < amount * p.getPrice())
            return new Result(false, "You don't have enough money!");
        if(!inventory.doesHaveItem("Wood", amount * p.getWoodCost()))
            return new Result(false, "You don't have enough wood!");
        if(!inventory.doesHaveItem("Stone", amount * p.getStoneCost()))
            return new Result(false, "You don't have stone!");
        wallet.changeBalance(-amount * p.getPrice());
        inventory.takeFromInventory("Wood", amount * p.getWoodCost());
        inventory.takeFromInventory("Stone", amount * p.getStoneCost());
        return new Result(true, "paid successfully");
    }

    public static void updatePerDay(){
        Game game = App.getActiveGame();

        //TODO make this cleaner
        //emptying player shipping bins
        for(Player player : game.getPlayers()){
            double totalEarning = 0;
            for(Entity building : player.getOwnedBuildings()){
                if(StringUtils.isNamesEqual(building.getEntityName(), "Shipping Bin")){
                    Inventory inventory = building.getComponent(Inventory.class);

                    for(Entity entity : inventory.getEntities()){
                        Pickable pickable = entity.getComponent(Pickable.class);
                        Sellable sellable = entity.getComponent(Sellable.class);

                        if(pickable != null) totalEarning += sellable.getPrice() * pickable.getStackSize();
                        else totalEarning += sellable.getPrice();
                    }

                    inventory.empty();
                }
            }
            player.getWallet().addBalance(totalEarning);
        }

        //TODO reset shops
    }

}
