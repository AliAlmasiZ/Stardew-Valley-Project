package com.ap.stardew.controllers;

import com.ap.stardew.models.App;
import com.ap.stardew.records.Result;

public interface Controller {
    public Result changeMenu(String menuName);
    default void    exit() {
        App.shouldTerminate = true;
        App.saveState();
    }

    default Result showCurrentMenu() {
        return new Result(true, App.getCurrentMenu().toString());
    }
}
