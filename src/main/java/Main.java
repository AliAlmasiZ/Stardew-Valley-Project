import models.App;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.print("Enter a command: ");
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            System.out.println("You typed: " + s);
        } else {
            System.out.println("No input received.");
        }
        /* load Jsons */
        loadDatas();
        /* Start game */
        App.start();
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