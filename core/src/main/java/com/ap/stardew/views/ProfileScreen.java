package com.ap.stardew.views;

import com.ap.stardew.StardewGame;
import com.ap.stardew.controllers.ProfileMenuController;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.App;
import com.ap.stardew.models.enums.Gender;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class ProfileScreen extends AbstractScreen {
    Account activeAccount;
    Label title, message;
    Label usernameLabel, passLabel, nicknameLabel, emailLabel, genderLabel, gender;
    TextField usernameField, passField, nicknameField, emailField;
    TextButton submitBtn, backBtn;
    ProfileMenuController controller;

    public ProfileScreen() {
        super();
        controller = new ProfileMenuController();
         activeAccount = App.getLoggedInAccount();
         if(activeAccount == null) {
             activeAccount = new Account(Gender.MALE, "ali@gmail.com", "alialm", "12345", "alialm");
         }

         setupUI();
    }

    private void setupUI() {
        title = new Label("Profile", skin, "title");
        rootTable.add(title).padBottom(20).row();

        message = new Label("", skin);
        message.setVisible(false);

        //maybe change if skin changed
        usernameLabel = new Label("Username:",  skin, "subtitle");
        passLabel = new Label("Password:", skin, "subtitle");
        nicknameLabel = new Label("Nickname:", skin, "subtitle");
        emailLabel = new Label("Email:", skin, "subtitle");
        genderLabel = new Label("Gender:", skin, "subtitle");
        gender = new Label(activeAccount.getGender().toString(), skin);

        usernameField = new TextField(activeAccount.getUsername(), skin);
        passField = new TextField("", skin);
        emailField = new TextField(activeAccount.getEmail(), skin);
        nicknameField = new TextField(activeAccount.getNickname(), skin);

        usernameField.setMessageText("Username");
        passField.setMessageText("Password");
        emailField.setMessageText("Email");
        nicknameField.setMessageText("Nickname");


        Table detailTable = new Table();
        detailTable.defaults().pad(20).width(400).height(100);
        detailTable.add(usernameLabel);
        detailTable.add(usernameField);
        detailTable.row();
        detailTable.add(nicknameLabel);
        detailTable.add(nicknameField);
        detailTable.row();
        detailTable.add(emailLabel);
        detailTable.add(emailField);
        detailTable.row();
        detailTable.add(passLabel);
        detailTable.add(passField);
        detailTable.row();
        detailTable.add(genderLabel);
        detailTable.add(gender);


        submitBtn = new TextButton("Submit", skin);
        backBtn = new TextButton("Back", skin);

        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StardewGame.getInstance().setScreen(new MainMenuScreen());
                dispose();
            }
        });




        rootTable.add(detailTable).colspan(2).row();
        rootTable.add(submitBtn).pad(20).width(300).height(100);
        rootTable.add(backBtn).pad(20).width(300).height(100);

    }

    public void setMessage(String text) {
        message.setText(text);
        message.setVisible(true);
    }

}
