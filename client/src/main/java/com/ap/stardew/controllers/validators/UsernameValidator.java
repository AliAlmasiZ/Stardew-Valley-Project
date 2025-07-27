package com.ap.stardew.controllers.validators;

import com.ap.stardew.StardewGame;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.App;
import com.ap.stardew.records.Result;

public class UsernameValidator implements Validator<String>{
    @Override
    public Result isValid(String username) {
        if(username.isEmpty()){
            return new Result(false, "should not be empty");
        }
        if(App.getUserByUsername(username) != null){
            return new Result(false, "username exists");
        }
        if(!Account.isUsernameValid(username).isSuccessful())
            return Account.isUsernameValid(username);
        return new Result(true, "");
    }
}
