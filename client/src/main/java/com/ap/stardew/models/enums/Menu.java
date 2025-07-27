package com.ap.stardew.models.enums;

import com.ap.stardew.views.old.inGame.TradeMenu;
import com.ap.stardew.views.old.*;

import java.util.Scanner;

public enum Menu {
    PROFILE_MENU(new ProfileMenu()),
    LOGIN_MENU(new LoginMenu()),
    MAIN_MENU(new MainMenu()),
    GAME_MENU(new GameMenu()),
    TRADE_MENU(new TradeMenu()),
    ;
    private final AppMenu menu;

    Menu(AppMenu menu) {
        this.menu = menu;
    }

    public void checker(Scanner scanner) {
        this.menu.checker(scanner);
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String[] words = this.name().split("_");
        for (String word : words) {
            result.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }
        return result.toString();
    }

    public static Menu getMenu(String menuName) { // makes string menuName to menu
        String[] words = menuName.split("\\s");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(word.toLowerCase());
        }
        menuName = sb.toString();
        for (Menu menu : Menu.values()) {
            if (menu.toString().replace(" ", "").toLowerCase().equals(menuName))
                return menu;
        }
        return null;

    }
}
