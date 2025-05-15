package models.entities.components;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FishingPoleComponent extends EntityComponent {
    @JsonProperty("fishingPower")
    private double fishingPower;
    @JsonProperty("energyNeeded")
    private int energyNeeded;

    public double getFishingPower() {
        return fishingPower;
    }

    public int getEnergyNeeded() {
        return energyNeeded;
    }

    public FishingPoleComponent(double fishingPower) {
        this.fishingPower = fishingPower;
    }

    public FishingPoleComponent(FishingPoleComponent other) {
        this.fishingPower = other.fishingPower;
        this.energyNeeded = other.energyNeeded;
    }

    public FishingPoleComponent() {
    }

    @Override
    public EntityComponent clone() {
        return new  FishingPoleComponent(this);
    }
}
