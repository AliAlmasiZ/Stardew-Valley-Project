package views;

import controllers.MainMenuController;
import models.Commands.MainMenuCommands;
import records.GameStartingDetails;

import java.util.Scanner;
import java.util.regex.Matcher;

public class MainMenu implements AppMenu {    private final MainMenuController controller = new MainMenuController();

    @Override
    public void checker(Scanner scanner) {
        if (!scanner.hasNextLine()) return;
        String input = scanner.nextLine().trim();
        Matcher matcher;
        if ((matcher = MainMenuCommands.SHOW_CURRENT_MENU.getMatcher(input)) != null) {
            System.out.println(controller.showCurrentMenu());
        } else if ((matcher = MainMenuCommands.MENU_ENTER.getMatcher(input)) != null) {
            System.out.println(controller.changeMenu(matcher.group("menuName")));
        } else if ((matcher = MainMenuCommands.USER_LOGOUT.getMatcher(input)) != null) {
            System.out.println(controller.logout());
        } else if ((matcher = MainMenuCommands.SHOW_MENUS.getMatcher(input)) != null) {
            System.out.println(controller.showMenus());
        } else if ((matcher = MainMenuCommands.NEW_GAME.getMatcher(input)) != null) {
            GameStartingDetails details = controller.newGame(matcher.group("usernames"));
            System.out.println(details.message());

            if(!details.success()) return;

            while (!details.notChosen().isEmpty()){
                System.out.println("available maps");
                for(int i = 0 ; i < details.availableRegions().size(); i++){
                    System.out.printf("%-2d- %s%n", i+1, details.availableRegions().get(i).getName());
                }
                System.out.println(controller.chooseMap(details, scanner.nextLine()));
            }

            System.out.println("game started!");

            controller.startGame(details);
        } else if ((matcher = MainMenuCommands.LOAD_GAME.getMatcher(input)) != null) {
            System.out.println(controller.loadGame());
        } else if ((matcher = MainMenuCommands.EXIT.getMatcher(input)) != null) {
            controller.exit();
        } else {
            System.out.println("invalid command");
        }
    }
}
