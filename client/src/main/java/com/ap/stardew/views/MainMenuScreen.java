package com.ap.stardew.views;

import com.ap.stardew.StardewGame;
import com.ap.stardew.models.App;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.checkerframework.checker.units.qual.C;

public class MainMenuScreen extends AbstractScreen {
    TextButton gameMenuBtn, profileMenuBtn, logoutBtn;
    Label menuTitle;

    public MainMenuScreen() {
        super();
        setupUI();
    }

    private void setupUI() {
        menuTitle = new Label("Main Menu", skin, "title");
        rootTable.add(menuTitle).padBottom(20).row();


        gameMenuBtn = new TextButton("Game Menu", skin);
        profileMenuBtn = new TextButton("Profile Menu", skin);
        logoutBtn = new TextButton("Logout", skin);

        rootTable.add(gameMenuBtn).pad(10).width(400).height(100).row();
        rootTable.add(profileMenuBtn).pad(10).width(400).height(100).row();
        rootTable.add(logoutBtn).pad(10).width(400).height(100).row();

        gameMenuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //TODO
            }
        });
        profileMenuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StardewGame.getInstance().setScreen(new ProfileScreen());
                dispose();
            }
        });
        logoutBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StardewGame.getInstance().setScreen(new MainScreen());
                dispose();
            }
        });
    }
}
