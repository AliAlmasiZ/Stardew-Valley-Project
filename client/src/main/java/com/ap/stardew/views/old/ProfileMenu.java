package com.ap.stardew.views.old;

import com.ap.stardew.controllers.ProfileMenuController;
import com.ap.stardew.models.Commands.ProfileMenuCommands;

import java.util.Scanner;
import java.util.regex.Matcher;

public class ProfileMenu implements AppMenu {    private final ProfileMenuController controller = new ProfileMenuController();

    @Override
    public void checker(Scanner scanner) {
        if (!scanner.hasNextLine()) return;
        String input = scanner.nextLine().trim();
        Matcher matcher;
        if ((matcher = ProfileMenuCommands.MENU_ENTER.getMatcher(input)) != null) {
            System.out.println(controller.changeMenu(matcher.group("menuName")));
        } else if ((matcher = ProfileMenuCommands.SHOW_CURRENT_MENU.getMatcher(input)) != null) {
            System.out.println(controller.showCurrentMenu());
        } else if ((matcher = ProfileMenuCommands.CHANGE_USERNAME.getMatcher(input)) != null) {
            System.out.println(controller.changeUsername(matcher.group("username")));
        } else if ((matcher = ProfileMenuCommands.CHANGE_NICKNAME.getMatcher(input)) != null) {
            System.out.println(controller.changeNickname(matcher.group("nickname")));
        } else if ((matcher = ProfileMenuCommands.CHANGE_EMAIL.getMatcher(input)) != null) {
            System.out.println(controller.changeEmail(matcher.group("email")));
        } else if ((matcher = ProfileMenuCommands.CHANGE_PASSWORD.getMatcher(input)) != null) {
            System.out.println(controller.changePassword(
                    matcher.group("newPassword"),
                    matcher.group("oldPassword")
            ));
        } else if ((matcher = ProfileMenuCommands.USER_INFO.getMatcher(input)) != null) {
            System.out.println(controller.userInfo());
        } else {
            System.out.println("invalid command");
        }
    }
}
