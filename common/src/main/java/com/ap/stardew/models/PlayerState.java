package com.ap.stardew.models;

import java.io.Serial;
import java.io.Serializable;

public class PlayerState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public String username;
    public float x, y;
}
