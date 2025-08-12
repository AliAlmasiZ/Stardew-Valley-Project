package com.ap.stardew.models.player.reaction;

public class Reaction {
    public float timeLeft;
    public String text;
    public Emoji emoji;

    public Reaction(String text, float timeLeft) {
        this.text = text;
        this.timeLeft = timeLeft;
    }
    public Reaction(Emoji emoji, float timeLeft) {
        this.timeLeft = timeLeft;
        this.emoji = emoji;
    }

    private Reaction() {
    }
}
