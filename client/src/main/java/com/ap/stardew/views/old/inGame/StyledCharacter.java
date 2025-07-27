package com.ap.stardew.views.old.inGame;

import java.io.Serializable;

public class StyledCharacter implements Serializable {
    public char character;
    public Color color;

    public StyledCharacter(char charachter, Color color) {
        this.character = charachter;
        this.color = color;
    }

    public StyledCharacter(char charachter) {
        this(charachter, null);
    }
}
