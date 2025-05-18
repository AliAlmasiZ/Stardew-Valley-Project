package models.entities.workstations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.App;
import models.Date;
import models.crafting.Recipe;
import models.entities.Entity;
import models.entities.components.EntityComponent;
import models.entities.components.Sellable;
import models.entities.components.inventory.Inventory;
import records.Result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ArtisanComponent extends EntityComponent implements Serializable {

    /* Hard Codes :/ */
    private static Map<String, Integer> ingredientToPrice = new HashMap<>();
    private static Map<String, List<String>> materials = new HashMap<>();

    static {
        materials.put("Cheese", List.of("Milk", "Large Milk"));
        materials.put("Goat Cheese", List.of("Goat Milk", "Large Goat Milk"));
        materials.put("Mayonnaise", List.of("Large Egg", "Egg"));

        ingredientToPrice.put("Milk", 230);
        ingredientToPrice.put("Large Milk", 345);

        ingredientToPrice.put("Goat Milk", 400);
        ingredientToPrice.put("Large Goat Milk", 600);

        ingredientToPrice.put("Egg", 190);
        ingredientToPrice.put("Large Egg", 237);

    }
    /*-----------------------------------------------------------------------------*/

    private final List<Recipe> recipes = new ArrayList<>();
    private ItemProcess activeProcess;

    @JsonCreator
    ArtisanComponent(@JsonProperty("recipes")
                     ArrayList<String> recipes) {

        recipes.forEach(name -> this.recipes.add(App.recipeRegistry.getRecipe(name)));
    }

    ArtisanComponent(List<Recipe> recipes, ItemProcess process) {
        this.recipes.addAll(recipes);
        this.activeProcess = process;
    }


    @Override
    public EntityComponent clone() {
        return new ArtisanComponent(new ArrayList<>(this.recipes), this.activeProcess);
    }

    @Override
    public String toString() {
        return "recipes : " + recipes + "\n";
    }

    public boolean isInProcess() {
        return activeProcess != null;
    }

    public Result remainingTime() {
        Date date = activeProcess.remainingTime();
        if(date.getHour() == 0 && date.getDay() == 0)
            return new Result(true, "Process is finished");
        int day = 0;
        int hour = 0;
        if(date.getDay() == 0) {
            day = date.getHour() / 24;
            hour = date.getHour() - 24 * day;
        }
        if(date.getHour() == 0) {
            day = date.getDay() - 1;
            hour = 24 + 9 - App.getActiveGame().getDate().getHour();
        }
        return new Result(false, "Time left : " + day + "d , " + hour + "h");

    }

    public Entity getProduct() {
        if(isProcessFinished()) {
            Entity out =  activeProcess.getOutput();
            activeProcess = null;
            return out;
        }
        throw new RuntimeException("process should finished first");
    }

    public boolean isProcessFinished() {
        return activeProcess.isCompleted();
    }

    public Result addProcess(String name) {
        for (Recipe recipe : recipes) {
            if(recipe.getName().equals(name)) {
                Inventory inventory = App.getActiveGame().getCurrentPlayer().getComponent(Inventory.class);
                /* I hate this part :/ */
                if(materials.get(name) != null) {
                    List<String> ingredients = materials.get(name);

                    if(ingredients != null) {
                        for (String string : ingredients) {
                            if(inventory.doesHaveItem(string)) {
                                inventory.takeFromInventory(string, 1);
                                Entity product = App.entityRegistry.makeEntity(name);
                                product.getComponent(Sellable.class).setPrice(ingredientToPrice.get(string));
                                activeProcess = new ItemProcess(product, recipe.getDay(), recipe.getHour());
                                return new Result(true, "Process added successfully");
                            }
                        }
                        return new Result(false, "You don't have enough ingredients");
                    }

                }
                /* --------------------------------- end of hardCode ---------------------------------*/

                if(!recipe.canCraft(inventory))
                    return new Result(false, "You don't have enough ingredients");
                activeProcess = new ItemProcess(recipe.craft(inventory), recipe.getDay(), recipe.getHour());
                return new Result(true, "Process added successfully");
            }
        }
        return new Result(false, "This artisan doesn't have recipe with this name");
    }








}
