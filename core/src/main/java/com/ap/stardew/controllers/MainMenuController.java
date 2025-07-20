package com.ap.stardew.controllers;

import com.ap.stardew.models.Account;
import com.ap.stardew.models.App;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.enums.Menu;
import com.ap.stardew.models.gameMap.MapRegion;
import com.ap.stardew.models.gameMap.WorldMapType;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.records.GameStartingDetails;
import com.ap.stardew.records.Result;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class MainMenuController implements Controller{    @Override
    public Result changeMenu(String menuName) {
        Menu menu = Menu.getMenu(menuName);
        if(menu == null)
            return new Result(false, "This menu doesn't exist");
        if (menu.equals(Menu.MAIN_MENU))
            return new Result(false, "you are already in com.ap.stardew.Main Menu");
        if (menu.equals(Menu.GAME_MENU))
            return new Result(false, "Use \"game new -u <username1> [username2] [username3]\"\n to enter this menu");
        App.setCurrentMenu(menu);
        return new Result(true, "You are in " + menu + " now");
    }

    public Result logout() {
        if(changeMenu("Login Menu").isSuccessful()) {
            App.setLoggedInAccount(null);
            App.setCurrentMenu(Menu.LOGIN_MENU);
            return new Result(true, "user logged out successfully. you are in Login Menu now");
        }
        return new Result(false, "Something went wrong : couldn't logout");
    }

    public Result showMenus() {
        StringBuilder sb = new StringBuilder("Menus : \n");
        for (Menu menu : Menu.values()) {
            if(menu.equals(Menu.MAIN_MENU)) continue;
            sb.append(menu.toString()).append("\n");
        }
        return new Result(true, sb.toString());
    }

    public GameStartingDetails newGame(String input) {
        String[] usernames = input.split("\\s");
        if(usernames.length == 0)
            return new GameStartingDetails(false, "you should enter at least  1 username");
        if(usernames.length > 3)
            return new GameStartingDetails(false, "You can't play with more than 3 players");

        Account[] accounts = new Account[usernames.length + 1];
        accounts[0] = App.getLoggedInAccount();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < usernames.length; i++) {
            if (Arrays.asList(accounts).contains(App.getUserByUsername(usernames[i])))
                return new GameStartingDetails(false, "you should enter different users");
            accounts[i + 1] = App.getUserByUsername(usernames[i]);
            if (accounts[i + 1] == null)
                sb.append("username ").append(usernames[i]).append(" doesn't exist\n");
        }
        if(!sb.isEmpty())
            return new GameStartingDetails(false, sb.deleteCharAt(sb.length() - 1).toString());

        ArrayList<MapRegion> availableRegions = new ArrayList<>();
        for(MapRegion r : WorldMapType.DEFAULT.getData().getRegions()){
            if(r.isFarm()){
                availableRegions.add(r);
            }
        }

        Queue<Account> notChosen = new LinkedList<>(Arrays.asList(accounts));

        Map<Account, MapRegion> selections = new HashMap<>();
        for(Account a : accounts){
            selections.put(a, null);
        }

        return new GameStartingDetails(true, "choose your starting maps", accounts, notChosen, selections, availableRegions);
    }
    public Result chooseMap(GameStartingDetails details, String input){
        MapRegion chosen = null;
        for(MapRegion m : details.availableRegions()){
            if(m.getName().equals(input)){
                chosen = m;
            }
        }

        if(chosen == null){
            return new Result(false, "farm doesn't exist");
        }

        details.selections().put(details.notChosen().peek(), chosen);
        details.availableRegions().remove(chosen);

        return new Result(true, details.notChosen().poll().getNickname() + " chose " + chosen.getName());
    }

    public void startGame(GameStartingDetails details){
        Game game = new Game();
        App.setActiveGame(game);
        game.initGame(details);
        App.setCurrentMenu(Menu.GAME_MENU);
    }

    public Result loadGame() {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("testSave.ser"));
            Game game = (Game) in.readObject();
            ArrayList<Player> players = game.getPlayers();
            for(Player player : players){
                player.setAccount(App.getUserByUsername(player.getUsername()));
            }
            App.setActiveGame(game);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        App.setCurrentMenu(Menu.GAME_MENU);
        return new Result(true, "loaded!");
    }
}
