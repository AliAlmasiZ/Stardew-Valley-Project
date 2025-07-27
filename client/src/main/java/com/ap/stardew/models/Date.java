package com.ap.stardew.models;

import com.ap.stardew.models.enums.Season;
import com.ap.stardew.models.enums.WeekDay;

import java.io.Serializable;

public class Date implements Serializable, Cloneable{    private Season season;
    private int day;
    private int hour;
    private int totalHours;

    public Date(int day, int hour) {
        this.day = day;
        this.hour = hour;
    }
    public Date() {
        this.season = Season.SPRING;
        this.day = 1;
        this.hour = 9;
        this.totalHours = 0;
    }

    public Date(Date other) {
        this.season = other.season;
        this.day = other.day;
        this.hour = other.hour;
        this.totalHours = other.totalHours;
    }

    public Season getSeason() {
        return season;
    }

    public int getDay() {
        return day;
    }

    public WeekDay getWeekDay() {
        WeekDay weekDay;
        weekDay = WeekDay.getWeekDay(day % 7);

        return weekDay;
    }

    public int getHour() {
        return hour;
    }

    public void addHour(int amount, Game game) {
        for (int i = 0 ; i < amount ; i++) {
            if (hour == 21) {
                hour = 9;
                addDay(1, game);
                totalHours += 12;
            } else {
                game.updateGamePerHour();
                hour++;
                totalHours ++;
            }
        }
    }

    public void addDay(int amount, Game game) {
        for (int i = 0 ; i < amount ; i++) {
            game.updateGamePerDay();
            if (day == 28)  {
                day = 1;
                this.season = Season.nextSeason(this.season);
            } else {
                day++;
            }
        }
    }

    @Override
    public String toString() {
        return "Day: " + day + "  Hour: " + hour + "  Season: " + season.name().toLowerCase();
    }

    @Override
    public Date clone() {
        return new Date(this);
    }
}
