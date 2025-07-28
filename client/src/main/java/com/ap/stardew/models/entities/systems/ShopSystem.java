package com.ap.stardew.models.entities.systems;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Pickable;
import com.ap.stardew.models.entities.components.Sellable;
import com.ap.stardew.models.entities.components.Upgradable;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.enums.Material;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.Wallet;
import com.ap.stardew.models.shop.*;
import com.ap.stardew.utils.StringUtils;
import com.ap.stardew.records.Result;

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

    public static Result upgradeBackPack(Shop shop, String toolName) {
        if(!StringUtils.isNamesEqual(shop.getName(), StringUtils.pierre))
            return new Result(false, "you can upgrade your backpack in " + StringUtils.pierre);
        Inventory inventory = App.getActiveGame().getCurrentPlayer().getComponent(Inventory.class);
        if(StringUtils.isNamesEqual(toolName, "Large Pack")) {
            if(inventory.getSize() != 12)
                return new Result(false,"You upgraded your Backpack before");
            Result res = handlePay(shop.getUpgradableShopProduct("Large Pack"), 1);
            if(!res.isSuccessful())
                return res;
            shop.getUpgradableShopProduct("Large Pack").addSold(1);
            inventory.setCapacity(24);
            return new Result(true, "your backpack upgraded");
        }
        else {
            if(inventory.getSize() == 12) {
                return new Result(false, "You should buy Large Pack first");
            }
            Result res = handlePay(shop.getUpgradableShopProduct("Deluxe Pack"), 1);
            if(!res.isSuccessful())
                return res;
            shop.getUpgradableShopProduct("Deluxe Pack").addSold(1);
            inventory.setUnlimited(true);
            return new Result(true, "your inventory is unlimited now!");

        }

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
