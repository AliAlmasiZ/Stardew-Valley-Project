package com.ap.stardew.models.player.buff;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ap.stardew.models.enums.SkillType;

import java.io.Serializable;

public class SkillBuff extends Buff implements Serializable {
    @JsonProperty("skillType")
    SkillType skillType;

    @JsonCreator
    public SkillBuff(@JsonProperty("skillType") SkillType skillType,@JsonProperty("buffTime") int buffTime) {
        super(buffTime);
        this.skillType = skillType;
    }

    @Override
    public double effectOnMaxEnergy() {
        return 0;
    }

    @Override
    public int effectOnSkill(SkillType skillType) {
        if(remainingTime() == 0)
            return 0;
        if(skillType != null && skillType.equals(this.skillType))
            return 1;
        return 0;
    }

}
