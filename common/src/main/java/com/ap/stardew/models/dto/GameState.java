package com.ap.stardew.models.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
    @Serial
    private static final  long serialVersionUID = 1L;

    public List<PlayerState> players;



}
