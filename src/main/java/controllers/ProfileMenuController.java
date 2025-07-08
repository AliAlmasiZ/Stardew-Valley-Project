package controllers;

import models.Account;
import models.App;
import models.enums.Menu;
import records.Result;

public class ProfileMenuController implements Controller{    @Override
    public Result changeMenu(String menuName) {
        Menu menu = Menu.getMenu(menuName);
        if(menu == null)
            return new Result(false, "this menu doesn't exist");
        if (!menu.equals(Menu.MAIN_MENU))
            return new Result(false, "you can only go to main menu when you are in profile menu");
        App.setCurrentMenu(menu);
        return new Result(false, "you are in " + menu + " now");
    }

    public Result changeUsername(String username) {
        Account user = App.getLoggedInAccount();
        if(user.getUsername().equals(username))
            return new Result(false, "your username must be different with old one");
        if(App.getUserByUsername(username) != null)
            return new Result(false, "this username is already taken");
        if(!Account.isUsernameValid(username).isSuccessful())
            return Account.isUsernameValid(username);
        user.setUsername(username);
        return new Result(true, "your user name changed to " + username + " successfully");
    }

    public Result changeNickname(String nickname) {
        Account account = App.getLoggedInAccount();
        if(account.getNickname().equals(nickname))
            return new Result(false, "your nickname must be different with old one");
        account.setNickname(nickname);
        return new Result(true, "your nickname changed to \"" + nickname + "\" successfully");
    }

    public Result changeEmail(String email) {
        Account account = App.getLoggedInAccount();
        if(!Account.isEmailValid(email).isSuccessful())
            return Account.isEmailValid(email);
        if(account.getEmail().equals(email))
            return new Result(false, "your email must be different with old one");
        account.setEmail(email);
        return new Result(true, "your email changed to " + email + " successfully");
    }

    public Result changePassword(String newPass, String oldPass) {
        Account account = App.getLoggedInAccount();
        if(!account.isPasswordCorrect(oldPass))
            return new Result(false, "your password is incorrect");
        if(!Account.isPasswordValid(newPass).isSuccessful())
            return Account.isPasswordValid(newPass);
        if(newPass.equals(oldPass))
            return new Result(false, "your password must be different with old one");
        account.setPassword(newPass);
        return new Result(true, "your password changed successfully");

    }

    public Result userInfo() {
        Account account = App.getLoggedInAccount();
        StringBuilder sb = new StringBuilder();
        sb
                .append("Username : ").append(account.getUsername()).append("\n")
                .append("Nickname : ").append(account.getNickname()).append("\n")
                .append("Maximum Money Earned : ").append(account.getMaximumMoneyEarned()).append("\n")
                .append("Played games : ").append(account.gamesCount()).append("\n");
        return new Result(true, sb.toString());
    }


}
