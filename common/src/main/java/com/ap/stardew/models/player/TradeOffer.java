package com.ap.stardew.models.player;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Date;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.player.friendship.PlayerFriendship;

import java.io.Serializable;

public class TradeOffer implements Serializable {
    private final Player sender;
    private final Player receiver;
    // 1 for item to item , 2 for offer, 3 for request
    private final int type;
    private final double price;
    private final String targetItem;
    private final int targetItemAmount;
    private final String givenItem;
    private final int givenItemAmount;
    private final Date date;
    private final int id;
    private boolean decided = false;
    private boolean accepted = false;
    private boolean seen = false;


    public TradeOffer(Player sender, Player receiver, int type, double price, String targetItem, int targetItemAmount,
                      String givenItem, int givenItemAmount, Date date, int id) {
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.price = price;
        this.targetItem = targetItem;
        this.targetItemAmount = targetItemAmount;
        this.givenItem = givenItem;
        this.givenItemAmount = givenItemAmount;
        this.date = date;
        this.id = id;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public int getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public String getTargetItem() {
        return targetItem;
    }

    public int getTargetItemAmount() {
        return targetItemAmount;
    }

    public String getGivenItem() {
        return givenItem;
    }

    public int getGivenItemAmount() {
        return givenItemAmount;
    }

    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isDecided() {
        return decided;
    }

    public void setDecided(boolean decided) {
        this.decided = decided;
    }

    public String infoMessage(boolean forHistory) {
        StringBuilder result = new StringBuilder();
        result.append("ID: ").append(id).append("\n");
        result.append("Sender: ").append(sender.getUsername()).append("\n");
        if (forHistory) {
            result.append("Receiver: ").append(receiver.getUsername()).append("\n");
        }

        switch (type) {
            case 1 -> {
                result.append("givenItem: ").append(givenItem).append(" ").append(givenItemAmount)
                        .append("X").append("\n");
                result.append("targetItem: ").append(targetItem).append(" ").append(targetItemAmount)
                        .append("X").append("\n");
            }
            case 2 -> {
                result.append("givenItem: ").append(givenItem).append(" ").append(givenItemAmount)
                        .append("X").append("\n");
                result.append("targetMoney: ").append(price).append("$").append("\n");
            }
            case 3 -> {
                result.append("givenMoney: ").append(price).append("$").append("\n");
                result.append("targetItem: ").append(targetItem).append(" ").append(targetItemAmount)
                        .append("X").append("\n");
            }
        }

        result.append("Date: ").append(date).append("\n");
        if (forHistory) {
            result.append("Statue: ");
            if (isAccepted()) {
                result.append("Accepted \n");
            } else {
                result.append("Rejected \n");
            }
        }

        result.append("--------------------------------------------------------\n");

        return result.toString();
    }

    public void reject() {
        Game game = App.getActiveGame();
        setDecided(true);
        setAccepted(false);
        PlayerFriendship playerFriendship = game.getFriendshipWith(getSender());

        playerFriendship.reduceXp(10);
    }

    public boolean accept() {
        Game game = App.getActiveGame();
        setDecided(true);
        Player sender = getSender();
        Player receiver = getReceiver();

        switch (getType()) {
            case 1 -> {
                if (!sender.getComponent(Inventory.class).doesHaveItem(getGivenItem(), getGivenItemAmount())) {
                    setAccepted(false);
                    return false;
                }
                if (!receiver.getComponent(Inventory.class).doesHaveItem(getTargetItem(), getTargetItemAmount())) {
                    setAccepted(false);
                    return false;
                }

                //TODO: this used removeItem but it seemed buggy. i changed it to use takeItem;
                Entity item1 = sender.getComponent(Inventory.class).takeFromInventory(getGivenItem(), getGivenItemAmount() );
                Entity item2 = receiver.getComponent(Inventory.class).takeFromInventory(getTargetItem(), getTargetItemAmount());
                receiver.getComponent(Inventory.class).addItem(item1);
                sender.getComponent(Inventory.class).addItem(item2);
            }
            case 2 -> {
                if (!sender.getComponent(Inventory.class).doesHaveItem(getGivenItem(), getGivenItemAmount())) {
                    setAccepted(false);
                    return false;
                }
                if (receiver.getWallet().getBalance() < price) {
                    setAccepted(false);
                    return false;
                }

                Entity item = sender.getComponent(Inventory.class).takeFromInventory(getGivenItem(), getGivenItemAmount());
                receiver.getComponent(Inventory.class).addItem(item);
                receiver.getWallet().changeBalance(-price);
                sender.getWallet().changeBalance(price);

            }
            case 3 -> {
                if (!receiver.getComponent(Inventory.class).doesHaveItem(getTargetItem(), getTargetItemAmount())) {
                    setAccepted(false);
                    return false;
                }
                if (sender.getWallet().getBalance() < price) {
                    setAccepted(false);
                    return false;
                }

                sender.getWallet().changeBalance(-price);
                receiver.getWallet().changeBalance(price);

                Entity item = receiver.getComponent(Inventory.class).takeFromInventory(getTargetItem(), getTargetItemAmount());
                sender.getComponent(Inventory.class).addItem(item);

            }
        }


        setAccepted(true);
        PlayerFriendship playerFriendship = game.getFriendshipWith(getSender());
        playerFriendship.addXp(20);
        return true;
    }
}
