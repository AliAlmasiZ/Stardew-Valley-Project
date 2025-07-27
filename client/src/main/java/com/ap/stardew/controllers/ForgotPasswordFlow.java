package com.ap.stardew.controllers;

import com.ap.stardew.models.Account;
import com.ap.stardew.models.App;
import com.ap.stardew.models.enums.SecurityQuestions;
import com.ap.stardew.records.Result;

import java.util.ArrayList;
import java.util.Map;


public class ForgotPasswordFlow {
    private record QuestionAnswerPair(SecurityQuestions question, String answer){};

    Account account;
    ArrayList<QuestionAnswerPair> questionAnswerPairs = new ArrayList<>();
    String newPassword;
    int state = 0;
    int currentQuestion = 0;

    public Result handle(String input){
        switch (state){
            case 0:
                account = App.getUserByUsername(input);

                if(account == null){
                    return new Result(false, "username doesn't exist!");
                }

                state = 2;
                for (Map.Entry<SecurityQuestions, String> entry : account.getSecurityAnswers().entrySet()) {
                    this.questionAnswerPairs.add(new QuestionAnswerPair(entry.getKey(), entry.getValue()));
                }
                currentQuestion = 0;
                return new Result(true, "answer the questions one by one (give an empty answer to cancel the process)\n" +
                        questionAnswerPairs.get(currentQuestion).question.toString());
            case 2:
                if(input.isEmpty()){
                    return new Result(false, "the process was canceled");
                }

                if(!input.equals(questionAnswerPairs.get(currentQuestion).answer)){
                    return new Result(true, "wrong answer! try again");
                }

                currentQuestion++;
                if(currentQuestion == questionAnswerPairs.size()){
                    state = 3;
                    return new Result(true, "the answers were correct! now "+
                            "enter a new password (enter \"random\" for a random password):");
                }
                return new Result(true, "correct!\n" + questionAnswerPairs.get(currentQuestion).question.toString());
            case 3:
                if(input.equals("random")){
                    newPassword = LoginMenuController.generatePassword();
                    state++;
                    return new Result(true, "confirm your new password: " + newPassword +" (y/n)");
                }
                newPassword = input;
                if(!Account.isPasswordValid(newPassword).isSuccessful()){
                    return new Result(true, Account.isPasswordValid(newPassword).message() + ". try again");
                }
                state = 5;
                return new Result(true, "re-enter your new password:");
            case 4:
                if(input.equals("y")){
                    account.setPassword(newPassword);
                    return new Result(false, "password was set!");
                }else{
                    state--;
                    return new Result(true, "enter a password:");
                }
            case 5:
                if(input.equals(newPassword)){
                    account.setPassword(newPassword);
                    return new Result(false, "password was set!");
                }
                if(input.equals("back")){
                    state = 3;
                    return new Result(true, "enter a password:");
                }
                return new Result(true, "passwords don't match. re-enter your new password: (enter back to go back)");
            default:
                return null;
        }
    }
    public SecurityQuestions getCurrentQuestion(){
        if(currentQuestion >= questionAnswerPairs.size()) return null;
        return questionAnswerPairs.get(currentQuestion).question;
    }
}

