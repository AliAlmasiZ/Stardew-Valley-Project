package com.ap.stardew.models.NPC;

import com.ap.stardew.models.player.Player;
import com.ap.stardew.records.Result;

import java.io.Serializable;

public class Quest implements Serializable {
    private String npcName;
    private NPC npc;
    private int id;
    private int requiredFriendship;
    private int daysToUnlocked;
    private String request;
    private int requestNumber;
    private String reward;
    private int rewardNumber;
    private boolean completed;
    private String doneByPlayerName;

    public Quest() {
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDoneByPlayerName() {
        return doneByPlayerName;
    }

    public void setDoneByPlayerName(String doneByPlayerName) {
        this.doneByPlayerName = doneByPlayerName;
    }

    public String getNpcName() {
        return npcName;
    }

    public void setNpcName(String npcName) {
        this.npcName = npcName;
    }

    public NPC getNpc() {
        return npc;
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequiredFriendship() {
        return requiredFriendship;
    }

    public void setRequiredFriendship(int requiredFriendship) {
        this.requiredFriendship = requiredFriendship;
    }

    public int getDaysToUnlocked() {
        return daysToUnlocked;
    }

    public void setDaysToUnlocked(int daysToUnlocked) {
        this.daysToUnlocked = daysToUnlocked;
    }

    public void reduceDaysToUnlocked() {
        daysToUnlocked--;
        if (daysToUnlocked < 0) {
            daysToUnlocked = 0;
        }
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
    }

    public int getRewardNumber() {
        return rewardNumber;
    }

    public void setRewardNumber(int rewardNumber) {
        this.rewardNumber = rewardNumber;
    }

    @Override
    public String toString() {
        StringBuilder message = new StringBuilder();
        message.append("Quest ID: " + id + "\n");
        message.append("NPC: " + npcName + "\n");
        message.append("request: ").append(request).append(" ").append(requestNumber).append("\n");
        message.append("reward: ").append(reward).append(" ").append(rewardNumber).append("\n");
        message.append("..................................................\n");

        return message.toString();
    }

    public Result doesHaveAccess(Player player) {
        NpcFriendship npcFriendship = player.getNpcFriendships().get(this.npc);
        if (npcFriendship.getLevel() < this.requiredFriendship) {
            return new Result(false, "You don't have enough friendships to access this quest.");
        }

        if (daysToUnlocked > 0) {
            return new Result(false, "This quest isn't unlocked.");
        }

        if (this.completed) {
            return new Result(false, "This quest is completed by " + this.doneByPlayerName + "!");
        }

        return new Result(true, ".");
    }
}
