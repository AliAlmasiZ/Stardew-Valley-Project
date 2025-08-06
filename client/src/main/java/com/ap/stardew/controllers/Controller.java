package com.ap.stardew.controllers;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Result;

public interface Controller {
    public Result changeMenu(String menuName);
    default void    exit() {
        App.shouldTerminate = true;
    }
}
