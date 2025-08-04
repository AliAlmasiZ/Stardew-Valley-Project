package com.ap.stardew.models.player;

import com.ap.stardew.models.App;

import java.io.Serializable;

public class Energy implements Serializable{

    private double amount;
    private double maxEnergy;
    private int maxEnergyChangeHoursLeft;
    private int modifierDaysLeft;
    private double modifier;
    private boolean isUnlimited;
    private boolean isGhashed = false;

    public void changeEnergy() {

    }

    public Energy() {
        this.amount = 200;
        this.modifierDaysLeft = 0;
        isUnlimited = false;
        this.maxEnergy = 200;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void addEnergy(int amount) {
        this.amount += amount;
        if (this.amount > maxEnergy) {
            this.amount = maxEnergy;
        }
    }

    public boolean isUnlimited() {
        return isUnlimited;
    }

    public double getAmount() {
        if (isUnlimited) return Double.POSITIVE_INFINITY;
        return amount;
    }

    public void toggleUnlimited() {
        isUnlimited = !isUnlimited;
    }

    public void setModifier(double modifier, int daysLeft) {
        if (modifierDaysLeft >= daysLeft && this.modifier > modifier) {
            return;
        }
        this.modifier = modifier;
        this.modifierDaysLeft = daysLeft;
    }


    public void reduceEnergy(double energy) {
        if (isUnlimited) return;
        this.amount -= energy;
        if (this.amount <= 0) {
            this.amount = 0;
            this.ghash();
            App.getActiveGame().nextTurn();
        }
    }

    public void buff(double buff, int hoursLeft) {
        maxEnergy = 200 + buff;
        maxEnergyChangeHoursLeft = hoursLeft;
    }

    public void updatePerHour() {
        if (maxEnergyChangeHoursLeft > 0) {
            maxEnergyChangeHoursLeft -= 1;
        }

        if (maxEnergyChangeHoursLeft == 0) {
            maxEnergy = 200;
        }
    }

    public void updatePerDay() {
        amount = maxEnergy;
        if (modifierDaysLeft > 0) {
            amount *= modifier;
            modifierDaysLeft--;
        }

        isGhashed = false;
        //TODO: other effects
    }

    public boolean isGhashed() {
        return isGhashed;
    }

    public void setGhashed(boolean ghashed) {
        isGhashed = ghashed;
    }

    private void ghash(){
        this.isGhashed = true;
        this.setModifier(0.75, 1);
    }

    public double getMaxEnergy() {
        return maxEnergy;
    }
}
