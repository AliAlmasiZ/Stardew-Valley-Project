package com.ap.stardew.views;

import com.ap.stardew.StardewGame;
import com.ap.stardew.controllers.ProfileMenuController;
import com.ap.stardew.controllers.validators.NonEmptyValidator;
import com.ap.stardew.controllers.validators.PasswordValidator;
import com.ap.stardew.controllers.validators.UsernameValidator;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.App;
import com.ap.stardew.records.Result;
import com.ap.stardew.views.widgets.ValidatedTextField;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class ProfileScreen extends AbstractScreen {
    Account activeAccount;
    Label title, message;
    Label usernameLabel, nicknameLabel, emailLabel, genderLabel, gender;
    ValidatedTextField usernameField, nicknameField, emailField;
    TextButton submitBtn, backBtn, changePassBtn;
    Dialog changePassDialog;
    ProfileMenuController controller;


    public ProfileScreen() {
        super();
        controller = new ProfileMenuController();
        activeAccount = App.getLoggedInAccount();

         setupUI();
         setupDialog();
    }

    private void setupUI() {
        title = new Label("Profile", skin, "title");
        rootTable.add(title).padBottom(20).row();

        message = new Label("", skin);
        message.setVisible(false);

        //maybe change if skin changed
        usernameLabel = new Label("Username:",  skin, "subtitle");
        nicknameLabel = new Label("Nickname:", skin, "subtitle");
        emailLabel = new Label("Email:", skin, "subtitle");
        genderLabel = new Label("Gender:", skin, "subtitle");
        gender = new Label(activeAccount.getGender().toString(), skin);

        usernameField = new ValidatedTextField(activeAccount.getUsername(), skin, new UsernameValidator());
        emailField = new ValidatedTextField(activeAccount.getEmail(), skin, controller.getEmailValidator());
        nicknameField = new ValidatedTextField(activeAccount.getNickname(), skin, new NonEmptyValidator());

//        usernameField = new TextField(activeAccount.getUsername(), skin);
//        passField = new TextField("", skin);
//        emailField = new TextField(activeAccount.getEmail(), skin);
//        nicknameField = new TextField(activeAccount.getNickname(), skin);

        usernameField.setMessageText("Username");
        emailField.setMessageText("Email");
        nicknameField.setMessageText("Nickname");

        changePassBtn = new TextButton("Change Password", skin);
        changePassBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changePassDialog.show(uiStage);
            }
        });

        Table detailTable = new Table();
        detailTable.defaults().pad(20).width(300).height(100);
        detailTable.add(usernameLabel);
        detailTable.add(usernameField);
        detailTable.row();
        detailTable.add(nicknameLabel);
        detailTable.add(nicknameField);
        detailTable.row();
        detailTable.add(emailLabel).width(200);
        detailTable.add(emailField).width(550); //TODO: message show incomplete
        detailTable.row();
        detailTable.add(genderLabel);
        detailTable.add(gender);
        detailTable.row();
        detailTable.add(changePassBtn).center().width(500).height(90).colspan(2);


        submitBtn = new TextButton("Submit", skin);
        backBtn = new TextButton("Back", skin);

        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!usernameField.validateText()) {
                    usernameField.ping();
                    return;
                }
                if(!emailField.validateText()) {
                    emailField.ping();
                    return;
                }
                if(!nicknameField.validateText()) {
                    nicknameField.ping();
                    return;
                }
                controller.changeUsername(usernameField.getText());
                controller.changeNickname(nicknameField.getText());
                controller.changeEmail(emailField.getText());
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
        rootTable.add(submitBtn).pad(20).width(300).height(90);
        rootTable.add(backBtn).pad(20).width(300).height(90);

    }

    private void setupDialog() {
        changePassDialog = new Dialog("", skin);
        Table table = changePassDialog.getContentTable();
        ValidatedTextField oldPass = new ValidatedTextField(skin, new NonEmptyValidator());
        ValidatedTextField newPass = new ValidatedTextField(skin, new PasswordValidator());
        oldPass.setMessageText("Old Password");
        oldPass.setPasswordMode(true);
        newPass.setMessageText("New Password");
        newPass.setPasswordMode(true);

        TextButton changeBtn = new TextButton("change password", skin);
        TextButton backBtn = new TextButton("Back", skin);

        changeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!ProfileScreen.this.activeAccount.isPasswordCorrect(oldPass.getText()))  {
                    oldPass.setMessage("Password is incorrect");
                    oldPass.ping();
                    return;
                }
                if(!newPass.validateText()) {
                    newPass.ping();
                    return;
                }

                Result res = controller.changePassword(newPass.getText(), oldPass.getText());
                if(!res.isSuccessful())
                    Gdx.app.log("Profile", res.message());

            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        table.add(oldPass).colspan(2).pad(20).width(400).row();
        table.add(newPass).colspan(2).pad(20).width(400).row();
        table.add(submitBtn).pad(20).width(200).height(90);
        table.add(backBtn).pad(20).width(200).height(90).row();
    }


}
