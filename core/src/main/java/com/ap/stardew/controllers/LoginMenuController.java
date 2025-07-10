package com.ap.stardew.controllers;

import com.ap.stardew.models.Account;
import com.ap.stardew.models.App;
import com.ap.stardew.models.enums.Gender;
import com.ap.stardew.models.enums.Menu;
import com.ap.stardew.models.enums.SecurityQuestions;
import com.ap.stardew.records.Result;

import java.security.SecureRandom;
import java.util.*;

public class LoginMenuController implements Controller{    @Override
    public Result changeMenu(String menuName) {
        //TODO
        return null;
    }

    public Result register(String username, String password, String confirmPassword, String name, String email, String gender) {

        if (!Account.isUsernameValid(username).isSuccessful()) {
            return Account.isUsernameValid(username);
        }

        if (!Account.isPasswordValid(password).isSuccessful()) {
            return Account.isPasswordValid(password);
        }

        if (!confirmPassword.equals(password)) {
            return new Result(false, "Passwords do not match");
        }

        if (!Account.isEmailValid(email).isSuccessful()) {
            return Account.isEmailValid(email);
        }

        Gender genderEnum = Gender.getGender(gender);
        if (genderEnum == null) {
            return new Result(false, "Invalid gender! type \"male\" or \"female\" for gender!");
        }



        Account account = new Account(genderEnum, email,name, password, username);
        App.setRegisteredAccount(account);
        App.addAccount(account);
        //TODO: add account to jason file

        StringBuilder message = new StringBuilder("Account registered successfully! now you can choose a security question:");
        message.append("\n").append(SecurityQuestions.getQuestionList());
        return new Result(true, message.toString());
    }

    public Result suggestUsername(String username) {
        if(!App.doesUsernameExist(username)){
            return new Result(true, "");
        }

        StringBuilder newUsername = new StringBuilder(username);
        Random rand = new Random();
        while (App.doesUsernameExist(newUsername.toString())) {
            int randomNumber = rand.nextInt() % 11;
            if (randomNumber == 10) {
                newUsername.append("-");
            } else {
                newUsername.append(randomNumber);
            }
        }

        return new Result(false, newUsername.toString());
    }

    public Result login(String username, String password, boolean stayLogged) {
        Account account = App.getUserByUsername(username);

        if (account == null) {
            return new Result(false, "username doesnt exist");
        }

        if (!account.isPasswordCorrect(password)) {
            return new Result(false, "incorrect password");
        }

        App.setStayLoggedIn(stayLogged);
        App.setLoggedInAccount(account);
        App.setCurrentMenu(Menu.MAIN_MENU);

        return new Result(true, "logged in successfully");
    }

    public Result pickQuestion(int number, String answer,String answerConfirm) {
        SecurityQuestions question = SecurityQuestions.getQuestion(number);
        if (question == null) {
            return new Result(false, "Invalid question!");
        }

        if (App.getRegisteredAccount() == null) {
            return new Result(false, "you should signup first!");
        }

        if (App.getRegisteredAccount().getSecurityAnswers().containsKey(question)) {
            return new Result(false, "You are already answered this question!");
        }

        if (!answer.equals(answerConfirm)) {
            return new Result(false, "answers do not match!");
        }

        App.getRegisteredAccount().getSecurityAnswers().put(question, answer);
        return new Result(true, "You answered question number " + number + " successfully!");
    }

    public Result forgetPassword(String username) {
        Account account = App.getUserByUsername(username);

        if(account == null){
            return new Result(false, "username doesn't exist!");
        }

        App.getView().log("answer the questions one by one");

        Map<SecurityQuestions, String> questions = account.getSecurityAnswers();

        for (Map.Entry<SecurityQuestions, String> q : questions.entrySet()){
            String answer = App.getView().inputWithPrompt(q.getKey().getQuestion());
            if(!answer.equals(q.getValue())){
                return new Result(false, "wrong answer");
            }
        }

        App.getView().log("the answers were correct");

        String newPassword;
        for(int i = 0;; i++){
            if(i==0){
                newPassword = App.getView().inputWithPrompt("enter a new password (enter \"random\" for a random password):");
            }else{
                newPassword = App.getView().inputWithPrompt("please retry:");
            }

            if(newPassword.equalsIgnoreCase("random")){
                newPassword = generatePassword();
                String confirm = App.getView().inputWithPrompt("Do you confirm your new password to be \"" + newPassword + "\"? (y/n)");

                if(confirm.equalsIgnoreCase("y")){
                    break;
                }else{
                    continue;
                }
            }

            Result validationResult;
            if(!(validationResult = Account.isPasswordValid(newPassword)).isSuccessful()){
                App.getView().log(validationResult.message());
            }

            String reEnteredPassword = App.getView().inputWithPrompt("re-enter the password:");
            if(reEnteredPassword.equals(newPassword)){
                break;
            }
            App.getView().log("passwords don't match.");
        }
        return new Result(true, "your new password is \"" + newPassword + "\"");
    }

    public static String generatePassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "?*&%$#!";

        SecureRandom random = new SecureRandom();
        List<Character> passwordChars = new ArrayList<>();
        int length = random.nextInt(8) + 8;

        passwordChars.add(upper.charAt(random.nextInt(upper.length())));
        passwordChars.add(lower.charAt(random.nextInt(lower.length())));
        passwordChars.add(digits.charAt(random.nextInt(digits.length())));
        passwordChars.add(special.charAt(random.nextInt(special.length())));

        String allChars = upper + lower + digits + special;
        for (int i = 4; i < length; i++) {
            passwordChars.add(allChars.charAt(random.nextInt(allChars.length())));
        }

        Collections.shuffle(passwordChars, random);

        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }
}
