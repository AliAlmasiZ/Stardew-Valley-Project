package models.player.buff;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.enums.SkillType;

import java.io.Serializable;

public class EnergyBuff extends Buff implements Serializable {

    //TODO : handle max energy
    private double energyBuff;


    @JsonCreator
    public EnergyBuff(@JsonProperty("energyBuff") double energyBuff, @JsonProperty("buffTime") int buffTime) {
        super(buffTime);
        this.energyBuff = energyBuff;
    }

    @Override
    public double effectOnMaxEnergy() {
        if(remainingTime() == 0)
            return 0;
        return energyBuff;
    }

    @Override
    public int effectOnSkill(SkillType skillType) {
        return 0;
    }


}
