package com.ap.stardew.models.player.friendship;

import com.ap.stardew.models.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayerFriendship implements Serializable {
    private final ArrayList<Player> friends;
    private int level;
    private int xp = 0;
    private boolean hadContactToday = false;
    private boolean hadMessageToday = false;
    private boolean hadHuggedToday = false;


    public boolean isHadContactToday() {
        return hadContactToday;
    }

    public void setHadContactToday(boolean hadContactToday) {
        this.hadContactToday = hadContactToday;
    }

    public boolean isHadHuggedToday() {
        return hadHuggedToday;
    }

    public void setHadHuggedToday(boolean hadHuggedToday) {
        this.hadHuggedToday = hadHuggedToday;
    }

    public boolean isHadMessageToday() {
        return hadMessageToday;
    }

    public void setHadMessageToday(boolean hadMessageToday) {
        this.hadMessageToday = hadMessageToday;
    }

    public ArrayList<Player> getFriends() {
        return friends;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public PlayerFriendship(Player player1, Player player2) {
        friends = new ArrayList<>(List.of(new Player[]{player1, player2}));
    }

    public void addXp(int xp) {
        if (!hadContactToday && level == 4) {
            for (Player player : friends) {
                player.getEnergy().addEnergy(50);
            }
        }
        hadContactToday = true;
        this.xp += xp;
        if (this.xp >= (100 * (level + 1))) {
            if (level < 2) {
                level++;
                this.xp -= 100 * level;
            } else if (level == 2) {
                this.xp = 300;
            } else if (level == 3) {
                this.xp = 400;
            }
        }
    }

    public void reduceXp(int xp) {
        hadContactToday = true;
        this.xp -= xp;
        if (this.xp < 0) {
            if (level == 0) {
                this.xp = 0;
            } else {
                this.xp += 100 * level;
                level--;
                // TODO: I dont know what happen if they are married
            }
        }
    }

    public static String buildFriendshipMessage(Player currentPlayer, ArrayList<PlayerFriendship> playerFriendships) {
        StringBuilder friendshipMessage = new StringBuilder("Your friendships: \n");

        for (PlayerFriendship playerFriendship : playerFriendships) {
            friendshipMessage.append(buildFriendshipDetailMessage(currentPlayer, playerFriendship));
        }

        return friendshipMessage.toString();
    }

    public static String buildFriendshipDetailMessage(Player currentPlayer, PlayerFriendship playerFriendship) {
        StringBuilder result = new StringBuilder();
        Player friend = playerFriendship.getFriends().get(0);
        if (friend == currentPlayer) {
            friend = playerFriendship.getFriends().get(1);
        }

        result.append("Friend Name: ").append(friend.getUsername()).append("\n");
        result.append("Friendship level: ").append(playerFriendship.getLevel()).append("\n");
        result.append("Friendship XP: ").append(playerFriendship.getXp()).append("\n");
        result.append(".............................................................\n");

        return result.toString();
    }

    public void updateDaily() {
        if (!hadContactToday) {
            if (level < 4) {
                reduceXp(10);
            }
        }
        hadMessageToday = false;
        hadHuggedToday = false;
        hadContactToday = false;
    }
}
