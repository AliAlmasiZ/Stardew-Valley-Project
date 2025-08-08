package com.ap.stardew.models.player;

import com.ap.stardew.models.Date;
import com.ap.stardew.models.entities.components.inventory.Inventory;

public class TradeHistoryItem {
    private Player sender;
    private Player receiver;
    private Inventory senderInventory;
    private Inventory receiverInventory;
    private Date date;
    private int id;
    private boolean accepted;

    public TradeHistoryItem() {
    }

    public TradeHistoryItem(Player sender, Player receiver, Inventory senderInventory, Inventory receiverInventory, Date date, int id, boolean accepted) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderInventory = senderInventory;
        this.receiverInventory = receiverInventory;
        this.date = date;
        this.id = id;
        this.accepted = accepted;
    }

    public Player getSender() {
        return sender;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public void setReceiver(Player receiver) {
        this.receiver = receiver;
    }

    public Inventory getSenderInventory() {
        return senderInventory;
    }

    public void setSenderInventory(Inventory senderInventory) {
        this.senderInventory = senderInventory;
    }

    public Inventory getReceiverInventory() {
        return receiverInventory;
    }

    public void setReceiverInventory(Inventory receiverInventory) {
        this.receiverInventory = receiverInventory;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean hasPlayer(Player player) {
        return sender.equals(player) || receiver.equals(player);
    }
}

