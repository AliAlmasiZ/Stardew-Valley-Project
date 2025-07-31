package com.ap.stardew.controllers;

import com.ap.stardew.app.ServerApp;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.JSONMessage;
import com.ap.stardew.models.Result;

public class ServerConnectionController {
    public static JSONMessage handleCommand(JSONMessage message) {
        if(message.getType() == JSONMessage.Type.player_input_command) {
            //TODO
        }
        if(message.getType() == JSONMessage.Type.command){
            JSONMessage response = new JSONMessage(JSONMessage.Type.response);

            switch ((String) message.getFromBody("command")){
                case "login" -> {
                    Result login = login(message.getFromBody("username"), message.getFromBody("password"));
                    response.put("success", login.isSuccessful());
                    response.put("message", login.message());
                }
            }

            return response;
        }
        return null;
    }

    public static Result login(String username, String password){
        Account account = ServerApp.getAccountByUsername(username);

        if (account == null) {
            return new Result(false, "username doesn't exist");
        }

        System.out.println(Account.hashPassword(password));
        System.out.println(account.getPassword());

        if (!account.isPasswordCorrect(password)) {
            return new Result(false, "incorrect password");
        }

        return new Result(true, "logged in successfully");
    }
}
