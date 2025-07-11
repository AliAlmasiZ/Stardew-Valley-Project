package com.ap.stardew.controllers.validators;

import com.ap.stardew.models.Account;
import com.ap.stardew.records.Result;

public class PasswordValidator implements Validator<String>{
    @Override
    public Result isValid(String string) {
        return Account.isPasswordValid(string);
    }
}
