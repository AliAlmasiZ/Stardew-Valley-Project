package com.ap.stardew.models.player;

import com.ap.stardew.models.Date;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Pickable;

import java.io.Serializable;

public class Gift implements Serializable {
    private final Player sender;
    private final Player receiver;
    private final Entity content;
    private final Date date;
    private int id;
    private boolean seen;
    private int rating;

    public Gift(Player sender, Player receiver, Entity content, Date date) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content.clone();
        this.date = date;
        rating = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public Entity getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("ID: ").append(id).append("\n");
        result.append("Sender: ").append(sender.getUsername()).append("\n");
        return getString(result);
    }

    public String getGiftDetail() {
        StringBuilder result = new StringBuilder();
        result.append("Sender: ").append(sender.getUsername()).append("\n");
        result.append("Receiver: ").append(receiver.getUsername()).append("\n");
        return getString(result);
    }

    private String getString(StringBuilder result) {
        result.append("Gift: ").append(content.getEntityName()).append("\n");
        result.append("Amount: ").append(content.getComponent(Pickable.class).getStackSize()).append("\n");
        result.append(date.toString()).append("\n");
        if (rating == -1) {
            result.append("**Not rated yet...**\n");
        } else {
            result.append("Rating: ").append(rating).append("\n");
        }
        result.append("---------------------------------------------------\n");

        return result.toString();
    }
}
