package com.ap.stardew.views.old.inGame;

import com.ap.stardew.controllers.TradeMenuController;
import com.ap.stardew.models.Commands.TradeMenuCommands;
import com.ap.stardew.views.old.AppMenu;

import java.io.Serializable;
import java.util.Scanner;
import java.util.regex.Matcher;

public class TradeMenu implements Serializable, AppMenu {    @Override
    public void checker(Scanner scanner) {
        TradeMenuController controller = new TradeMenuController();
        String input = scanner.nextLine().trim();
        Matcher matcher;

        if ((matcher = TradeMenuCommands.TRADE_BY_MONEY.getMatcher(input)) != null) {
            System.out.println(controller.trade(matcher.group(1).trim(), matcher.group(2).trim(), matcher.group(3),
                    Integer.parseInt(matcher.group(4)), true,
                    Double.parseDouble(matcher.group("price")),
                    null, 0));

        } else if ((matcher = TradeMenuCommands.TRADE_ITEM.getMatcher(input)) != null) {
            System.out.println(controller.trade(matcher.group(1).trim(), matcher.group(2).trim(), matcher.group(3),
                    Integer.parseInt(matcher.group(4)), false,
                    0, matcher.group("targetItem"),
                    Integer.parseInt(matcher.group("targetAmount"))));

        } else if ((matcher = TradeMenuCommands.TRADE_LIST.getMatcher(input)) != null) {
            System.out.println(controller.tradeList());

        } else if ((matcher = TradeMenuCommands.TRADE_HISTORY.getMatcher(input)) != null) {
            System.out.println(controller.tradeHistory());

        } else if ((matcher = TradeMenuCommands.TRADE_RESPONSE.getMatcher(input)) != null) {
            System.out.println(controller.tradeRespond(matcher.group(1).trim(), Integer.parseInt(matcher.group(2))));

        } else if (TradeMenuCommands.BACK.getMatcher(input) != null) {
            System.out.println(controller.back());

        } else {
            System.out.println("Invalid command! type \"back\" to go to game menu.");
        }
    }
}
