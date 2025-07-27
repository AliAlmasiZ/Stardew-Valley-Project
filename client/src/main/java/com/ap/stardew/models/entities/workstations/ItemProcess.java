package com.ap.stardew.models.entities.workstations;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Date;
import com.ap.stardew.models.entities.Entity;

import java.io.Serializable;

public class ItemProcess implements Serializable {
    private Entity output;
    private Date startTime;
    int day;
    int hour;

    public ItemProcess(Entity output, int day, int hour) {
        this.output = output;
        startTime = App.getActiveGame().getDate().clone();
        this.day = day;
        this.hour = hour;
        if(hour != 0 && day != 0)
            throw new RuntimeException("one of day and hour should be 0 bro! something is wrong");
    }




    public Date getTimePassed() {
        Date date = App.getActiveGame().getDate();
        return new Date(date.getDay() - startTime.getDay(), date.getHour() - startTime.getHour());
    }

    public boolean isCompleted() {
        return getTimePassed().getDay() >= day && (getTimePassed().getDay() * 24 + getTimePassed().getHour() >= hour);
    }

    public Date remainingTime() {
        if(day == 0)
            return new Date(0, Math.max(0, hour - (getTimePassed().getDay() * 24 + getTimePassed().getHour())));
        if(hour == 0)
            return new Date(Math.max(0, day - getTimePassed().getDay()), 0);
        return new Date(0 , 0);
    }

    public Entity getOutput() {
        return output;
    }
}
