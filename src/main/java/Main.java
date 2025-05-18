import models.App;
import views.AppView;
import views.inGame.CharacterTexture;
import views.inGame.Color;
import views.inGame.Renderer;
import views.inGame.StyledCharacter;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        /* load Jsons */
        loadDatas();
        /* Start game */
        App.start();
    }

    private static void loadDatas() {
        App.getView().log("Loading Game...");
        App.shopRegistry.load("./src/data/shops");
        /* should load recipes first (because artisan has recipes) */
        App.recipeRegistry.loadRecipes("./src/data/recipes");
        App.entityRegistry.load("./src/data/entities");
        App.mapRegistry.load("src/data/maps");
        /* to check is Json entities ok or not */
        App.recipeRegistry.checkIngredients();
        App.buildingRegistry.load("src/data/buildings");

        App.entityRegistry.addChild(App.buildingRegistry);

//        App.entityRegistry.listEntities();

        App.getView().log("Done.");
    }
}