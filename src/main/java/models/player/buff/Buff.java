package models.player.buff;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import models.App;
import models.Date;
import models.entities.components.*;
import models.entities.components.harvestable.Harvestable;
import models.entities.components.inventory.Inventory;
import models.entities.workstations.ArtisanComponent;
import models.enums.SkillType;
import models.shop.Shop;

import java.io.Serializable;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EnergyBuff.class, name = "EnergyBuff"),
        @JsonSubTypes.Type(value = SkillBuff.class, name = "SkillBuff"),
})
abstract public class Buff implements Serializable {
    protected Date startDate;
    @JsonProperty("buffTime")
    protected int buffTime;
    abstract public double effectOnMaxEnergy();
    abstract public int effectOnSkill(SkillType skillType);


    public Buff(int buffTime) {
        this.buffTime = buffTime;
    }

    public int remainingTime() {
        //TODO: delete in active buff when its 0
        Date currentDate = App.getActiveGame().getDate();
        int pastTime = (currentDate.getDay() - startDate.getDay()) * 24 + (currentDate.getHour() - startDate.getHour());
        if(pastTime > buffTime)
            return 0;
        return buffTime - pastTime;
    }

    public void setBuff() {
        this.startDate = App.getActiveGame().getDate().clone();
        App.getActiveGame().getCurrentPlayer().setActiveBuff(this);
        App.getActiveGame().getCurrentPlayer().getEnergy().buff(this.effectOnMaxEnergy(), this.remainingTime());
    }

}
