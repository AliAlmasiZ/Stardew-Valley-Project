import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import models.App;
import views.AppView;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        /* load Jsons */
        loadDatas();
        /* Start game */
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("MyGame");
        config.setDecorated(true);
        config.setWindowedMode(1000, 800);
        config.setResizable(true);
        config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32, 3, 2);
        config.setBackBufferConfig(8, 8, 8, 8, 16, 8, 4);

        Lwjgl3Application app = new Lwjgl3Application(AppView.getInstance(), config);
    }

    private static void loadDatas() {
        App.getView().log("Loading Game...");
        App.shopRegistry.load("./data/shops");
        /* should load recipes first (because artisan has recipes) */
        App.recipeRegistry.loadRecipes("./data/recipes");
        App.entityRegistry.load("./data/entities");
        App.mapRegistry.load("data/maps");
        /* to check is Json entities ok or not */
        App.recipeRegistry.checkIngredients();
        App.buildingRegistry.load("data/buildings");

        App.entityRegistry.addChild(App.buildingRegistry);

//        App.entityRegistry.listEntities();

        App.getView().log("Done.");
    }
}