package com.ap.stardew.models;

import com.ap.stardew.models.crafting.RecipeRegistry;
import com.ap.stardew.models.entities.EntityRegistry;
import com.ap.stardew.models.enums.Gender;
import com.ap.stardew.models.gameMap.MapRegistry;
import com.ap.stardew.models.shop.ShopRegistry;

import java.io.*;
import java.util.ArrayList;

public class App implements Serializable {
    private Account registeredAccount = null;
    private boolean stayLoggedIn   = false;
    public static boolean shouldTerminate = false;

    public static EntityRegistry entityRegistry = new EntityRegistry();
    public static EntityRegistry buildingRegistry = new EntityRegistry();
    public static RecipeRegistry recipeRegistry = new RecipeRegistry();
    public static MapRegistry mapRegistry = new MapRegistry();
    public static ShopRegistry shopRegistry = new ShopRegistry();

    static {
        shopRegistry.load("data/shops");
        /* should load recipes first (because artisan has recipes) */
        recipeRegistry.loadRecipes("./data/recipes");
        entityRegistry.load("./data/entities");
        mapRegistry.load("data/maps");
        /* to check is Json entities ok or not */
        recipeRegistry.checkIngredients();
        buildingRegistry.load("data/buildings");

        entityRegistry.addChild(buildingRegistry);
    }

    @Deprecated
    public static Game getActiveGame(){
        throw new RuntimeException("Deprecated");
    }
    @Deprecated
    public static Game setActiveGame(Game game){
        throw new RuntimeException("Deprecated");
    }
    @Deprecated
    public static Account getUserByUsername(String username){
        throw new RuntimeException("Deprecated");
    }
    @Deprecated
    public static Account getLoggedInAccount(){
        throw new RuntimeException("Deprecated");
    }
    @Deprecated
    public static void setLoggedInAccount(Account account){
        throw new RuntimeException("Deprecated");
    }
    @Deprecated
    public static boolean doesUsernameExist(String account){
        throw new RuntimeException("Deprecated");
    }

    private static App instance;

    public static App getInstance(){
        if(instance == null) instance = new App();
        return instance;
    }

    public static boolean getStayLoggedIn() {
        return getInstance().stayLoggedIn;
    }

    public static void setStayLoggedIn(boolean stayLoggedIn) {
        getInstance().stayLoggedIn = stayLoggedIn;
    }

    public static Account getRegisteredAccount() {
        return getInstance().registeredAccount;
    }

    public static void setRegisteredAccount(Account registeredAccount) {
        getInstance().registeredAccount = registeredAccount;
    }
}
