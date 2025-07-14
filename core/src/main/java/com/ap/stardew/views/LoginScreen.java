package com.ap.stardew.views;

import com.ap.stardew.StardewGame;
import com.ap.stardew.controllers.validators.NonEmptyValidator;
import com.ap.stardew.controllers.validators.PasswordValidator;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.App;
import com.ap.stardew.models.enums.SecurityQuestions;
import com.ap.stardew.views.widgets.ValidatedTextField;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AbstractScreen{
    private ValidatedTextField usernameTextfield;
    private ValidatedTextField passwordTextfield;
    private TextButton submitButton;
    private TextButton forgotPassworBtn;
    private TextButton backBtn;
    private Table mainBox;

    public LoginScreen(){
        super();

        mainBox = new Table();
        mainBox.center();
        mainBox.pack();
        mainBox.setSize(400, 200);
        mainBox.defaults().space(10);

        usernameTextfield = new ValidatedTextField(skin, new NonEmptyValidator());
        passwordTextfield = new ValidatedTextField(skin, new NonEmptyValidator());

        submitButton = new TextButton("login", skin);
        forgotPassworBtn = new TextButton("forgot password", skin);
        backBtn = new TextButton("back", skin);

        mainBox.add(backBtn).left().expandX().row();
        mainBox.add(usernameTextfield).fillX().row();
        mainBox.add(passwordTextfield).fillX().row();
        mainBox.add(submitButton).center().row();
        mainBox.add(forgotPassworBtn).center().row();

        rootTable.add(mainBox);

        backBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StardewGame.getInstance().setScreen(new MainScreen());
            }
        });
        submitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!usernameTextfield.validateText() || !passwordTextfield.validateText()){
                    usernameTextfield.ping();
                    passwordTextfield.ping();
                    return;
                }
                Account account = App.getUserByUsername(usernameTextfield.getText());
                if(account == null){
                    usernameTextfield.setMessage("Username doesnt exist");
                    return;
                }
                usernameTextfield.setMessage("");
                if(!account.isPasswordCorrect(passwordTextfield.getText())){
                    passwordTextfield.setMessage("incorrect password");
                    return;
                }

                App.setLoggedInAccount(account);
                StardewGame.getInstance().setScreen(new MainMenuScreen());
            }
        });
        forgotPassworBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showForgotPasswordDialogPhase1();
            }
        });
    }
    @Override
    public void render(float delta) {
        //test---------------
        Texture texture = new Texture("Content(unpacked)/LooseSprites/JunimoNoteMobile.png");
        stage.getBatch().begin();
        stage.getBatch().setColor(1, 1, 1, 1);
        stage.getBatch().draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        //---------------------
        stage.act(delta);
        stage.draw();
    }

    public void showForgotPasswordDialogPhase1(){
        Dialog dialog = new Dialog("", skin);

        ValidatedTextField usernameTextfield = new ValidatedTextField(skin, new NonEmptyValidator());
        TextButton submitBtn = new TextButton("submit", skin);
        TextButton backBtn = new TextButton("back", skin);

        Table contentTable = dialog.getContentTable();
        contentTable.pad(20).padRight(40).padLeft(40);

        dialog.getButtonTable().pad(10);

        dialog.getButtonTable().add(backBtn).expandX().left().row();
        contentTable.add(new Label("Enter your username:", skin)).left().row();
        contentTable.add(usernameTextfield).growX().width(300).row();
        contentTable.add(submitBtn).row();

        backBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });
        submitBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!usernameTextfield.validateText()){
                    usernameTextfield.ping();
                    return;
                }

                Account account = App.getUserByUsername(usernameTextfield.getText());
                if(account == null){
                    usernameTextfield.setMessage("username doesn't exist");
                    return;
                }
                showForgotPasswordDialogPhase2(dialog, account);
            }
        });
        dialog.show(stage);
    }
    public void showForgotPasswordDialogPhase2(Dialog dialog, Account account){
        Table contentTable = dialog.getContentTable();
        contentTable.clearChildren();

        contentTable.add(new Label("Answer the questions:", skin)).left().expandX().row();

        Map<SecurityQuestions, String> securityAnswers = account.getSecurityAnswers();
        Map<SecurityQuestions, ValidatedTextField> textFields = new HashMap<>();

        for (Map.Entry<SecurityQuestions, String> entry : securityAnswers.entrySet()) {
            contentTable.add(new Label(entry.getKey().getQuestion(), skin)).left().expandX().row();

            ValidatedTextField textField = new ValidatedTextField(skin, new NonEmptyValidator());
            textFields.put(entry.getKey(), textField);

            contentTable.add(textField).growX().spaceBottom(20).row();
        }

        TextButton submitButton = new TextButton("submit", skin);

        contentTable.add(submitButton).center();

        submitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean valid = true;
                for (Map.Entry<SecurityQuestions, ValidatedTextField> entry : textFields.entrySet()) {
                    if(!entry.getValue().getText().equals(securityAnswers.get(entry.getKey()))){
                        valid = false;
                        entry.getValue().setMessage("wrong answer");
                    }else{
                        entry.getValue().setMessage("");
                    }
                }

                if(valid){
                    showForgotPasswordDialogPhase3(dialog, account);
                }
            }
        });
        dialog.show(stage);
    }

    public void showForgotPasswordDialogPhase3(Dialog dialog, Account account){
        Table contentTable = dialog.getContentTable();
        contentTable.clearChildren();

        ValidatedTextField textField = new ValidatedTextField(skin, new PasswordValidator());
        TextButton submitButton = new TextButton("submit", skin);

        contentTable.add(new Label("Choose a new password:", skin)).left().expandX().row();
        contentTable.add(textField).growX().row();
        contentTable.add(submitButton).row();

        submitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!textField.validateText()){
                    textField.ping();
                    return;
                }

                account.setPassword(textField.getText());

                contentTable.clear();
                contentTable.pad(20).padRight(40).padLeft(40);
                contentTable.defaults().spaceBottom(10);
                contentTable.add(new Label("changed the password", skin));
            }
        });
        dialog.show(stage);
    }
}
