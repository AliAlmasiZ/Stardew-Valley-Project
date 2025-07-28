package com.ap.stardew.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameStateSnapshot implements Serializable {
    @Serial
    private static final  long serialVersionUID = 1L;

    public long timestamp;
    public List<PlayerState> players = new ArrayList<>();
}
