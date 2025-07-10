package com.ap.stardew.views;

import com.ap.stardew.controllers.GameAssetManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.File;

public class SignupScreen extends AbstractScreen {
    private TextField username;
    private TextField password;
    private TextField confirmPassword;
    private TextField name;
    private TextField email;
    SelectBox<String> gender;


    public SignupScreen() {
        super();


        // Create SelectBox
        gender = new SelectBox<>(skin);
        String[] genders = {"Boy", "Girl"};
        SignupScreen.this.gender.setItems(genders);

        // Add change listener
        SignupScreen.this.gender.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                String selectedCity = SignupScreen.this.gender.getSelected();
            }
        });

        rootTable.add(SignupScreen.this.gender);
        stage.addActor(rootTable);


    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        //test---------------
        Texture texture = new Texture("Content(unpacked)/LooseSprites/JunimoNoteMobile.png");
        stage.getBatch().begin();
        stage.getBatch().draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        //---------------------
        stage.draw();
    }
}
