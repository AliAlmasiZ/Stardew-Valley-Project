package com.ap.stardew.models.player;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Date;
import com.ap.stardew.models.Game;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private final String sender;
    private final String receiver;
    private final String message;
    private final Date date;
    private boolean seen;

    public Message(Date date, String message, Player receiver, Player sender) {
        this.date = date;
        this.message = message;
        if (receiver != null) {
            this.receiver = receiver.getUsername();
        }else {
            this.receiver = null;
        }
        this.sender = sender.getUsername();

        seen = false;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public static String buildMessageHistory(Player currentPlayer, Player talkedPlayer, ArrayList<Message> messages) {
        StringBuilder output = new StringBuilder();
        output.append("Your message history with ").append(talkedPlayer.getUsername()).append(":\n\n");

        for (Message message : messages) {
            output.append(Message.buildMessageDetails(message));
        }

        return output.toString();
    }

    public static String buildMessageDetails(Message message) {
        Game game = App.getActiveGame();

        StringBuilder result = new StringBuilder();
        result.append("Sender: ").append(message.getSender()).append("\n");
        result.append("Receiver: ").append(message.getReceiver()).append("\n");
        result.append("Message: ").append(message.getMessage()).append("\n");
        result.append(message.getDate().toString()).append("\n");
        if (game.getCurrentPlayer().getUsername().equals(message.getSender())) {
            result.append("Seen: ").append(message.isSeen()).append("\n");
        }
        result.append("-----------------------------------------------\n");

        return result.toString();
    }
}
