package models;

import models.crafting.RecipeRegistry;
import models.entities.EntityRegistry;
import models.enums.Menu;
import models.gameMap.MapRegistry;
import models.shop.ShopRegistry;
import views.AppView;

import java.io.*;
import java.util.ArrayList;

public class App implements Serializable {
    private final ArrayList<Account> accountList = new ArrayList<>();
    private Account loggedInAccount = null;
    private Account registeredAccount = null;
    private boolean stayLoggedIn   = false;
    public static Game activeGame = null;
    private static final AppView view = new AppView();
    public transient static boolean shouldTerminate = false;
    private static Menu currentMenu = Menu.LOGIN_MENU;
    public static EntityRegistry entityRegistry = new EntityRegistry();
    public static EntityRegistry buildingRegistry = new EntityRegistry();
    public static RecipeRegistry recipeRegistry = new RecipeRegistry();
    public static MapRegistry mapRegistry = new MapRegistry();
    public static ShopRegistry shopRegistry = new ShopRegistry();

    private static App instance;

    public static App getInstance(){
        if(instance == null) instance = new App();
        return instance;
    }

    /***
     * Returns null if the username doesn't exist.
     */
    public static Account getUserByUsername(String username){
        for(Account a : getInstance().accountList){
            if(a.getUsername().equals(username)){
                return a;
            }
        }
        return null;
    }

    public static boolean getStayLoggedIn() {
        return getInstance().stayLoggedIn;
    }

    public static void setStayLoggedIn(boolean stayLoggedIn) {
        getInstance().stayLoggedIn = stayLoggedIn;
    }

    public static Menu getCurrentMenu() {
        return getInstance().currentMenu;
    }

    public static void setCurrentMenu(Menu currentMenu) {
        getInstance().currentMenu = currentMenu;
    }

    public static boolean doesUsernameExist(String username){
        return getUserByUsername(username) != null;
    }

    public static Account getLoggedInAccount() {
        return getInstance().loggedInAccount;
    }

    public static void setLoggedInAccount(Account loggedInAccount) {
        getInstance().loggedInAccount = loggedInAccount;
    }

    public static void start(){
        File configFile = new File("data/appState/config.ser");

        try {
            if(configFile.exists()){
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(configFile));
                instance = (App) in.readObject();
                in.close();
            }else {
                saveState();
            }
        } catch (IOException | ClassNotFoundException e) {
            saveState();
        }

        if(!instance.stayLoggedIn) instance.loggedInAccount = null;

        if(instance.loggedInAccount != null) setCurrentMenu(Menu.MAIN_MENU);
    }

    public static void saveState(){
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("data/appState/config.ser"));
            out.writeObject(getInstance());
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static AppView getView(){
        return getInstance().view;
    }

    public static Account getRegisteredAccount() {
        return getInstance().registeredAccount;
    }

    public static void setRegisteredAccount(Account registeredAccount) {
        getInstance().registeredAccount = registeredAccount;
    }

    public static void addAccount(Account account){
        getInstance().accountList.add(account);
    }

    public static Game getActiveGame() {
        return getInstance().activeGame;
    }

    public static void setActiveGame(Game activeGame) {
        getInstance().activeGame = activeGame;
    }

}