package models.entities.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.App;
import models.entities.UseFunction;
import models.gameMap.Tile;
import models.player.Player;
import records.Result;

import java.io.Serializable;
import java.util.ArrayList;

public class Useable extends EntityComponent implements Serializable {
    @JsonProperty("useFunctions")
    private final ArrayList<UseFunction> functions = new ArrayList<>();

    @JsonProperty("energyCost")
    private int energyCost;

    public Useable(ArrayList<UseFunction> functions) {
        this.functions.addAll(functions);
    }
    private Useable(Useable other){
        this.functions.addAll(other.functions);
    }
    public Useable(){
    }

    @Override
    public String toString() {
        return "Useable{" +
                "functions=" + functions + ", " +
                "energyCost=" + energyCost +
                '}';
    }

    @Override
    public EntityComponent clone() {
        return new Useable(this);
    }

    public ArrayList<UseFunction> getFunctions() {
        return functions;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public void setEnergyCost(int energyCost) {
        this.energyCost = energyCost;
    }


    public Result use(Tile targetTile) {
        Player player = App.getActiveGame().getCurrentPlayer();

        if(
                Math.abs(player.getPosition().getCol() - targetTile.getCol()) > 2
                        || Math.abs(player.getPosition().getRow() - targetTile.getRow()) > 2
        )
            return new Result(false, "You don't access this tile");
        return applyFunctions(targetTile);
    }

    private Result applyFunctions(Tile targetTile) {
        StringBuilder errors = new StringBuilder();
        for (UseFunction function : functions) {
            Result result = function.use(entity, targetTile);
            if(!result.isSuccessful())
                errors.append(result).append("\n");
            else
                return result;
        }
        if(errors.isEmpty())
            return new Result(true, "Tool used successfully");
        errors.deleteCharAt(errors.length() - 1);
        return new Result(false, errors.toString());
    }
}
