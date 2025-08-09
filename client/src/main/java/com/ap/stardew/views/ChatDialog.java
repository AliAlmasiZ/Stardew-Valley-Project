package com.ap.stardew.views;

import com.ap.stardew.models.player.Player;
import com.ap.stardew.views.widgets.InGameDialog;

public class ChatDialog {
    private InGameDialog dialog;
    private final Player otherPlayer;


    public ChatDialog(Player otherPlayer) {
        this.otherPlayer = otherPlayer;
    }
}
