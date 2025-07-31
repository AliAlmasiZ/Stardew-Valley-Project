package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.controllers.validators.NonEmptyValidator;
import com.ap.stardew.controllers.validators.PasswordValidator;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.App;
import com.ap.stardew.models.enums.SecurityQuestions;
import com.ap.stardew.views.widgets.InGameDialog;
import com.ap.stardew.views.widgets.ValidatedTextField;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AbstractMenuScreen{
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

        Table textFieldBox = new Table();
        textFieldBox.setBackground(customSkin.getDrawable("frameNinePatch2"));

        usernameTextfield = new ValidatedTextField(customSkin, new NonEmptyValidator());
        passwordTextfield = new ValidatedTextField(customSkin, new NonEmptyValidator());

        submitButton = new TextButton("login", customSkin, "big");
        forgotPassworBtn = new TextButton("forgot password", customSkin);
        backBtn = new TextButton("back", customSkin, "big");

        backBtn.getLabel().setFontScale(0.35f);

        mainBox.add(backBtn).left().spaceBottom(5).row();
        textFieldBox.add(usernameTextfield).fillX().spaceBottom(5).row();
        textFieldBox.add(passwordTextfield).fillX().spaceBottom(5).row();
        textFieldBox.add(forgotPassworBtn).center().row();
        mainBox.add(textFieldBox).spaceBottom(5).row();
        mainBox.add(submitButton).center().growX().pad(0, 3, 0, 3).row();

        rootTable.add(mainBox);

        backBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ClientGame.getInstance().setScreen(new MainScreen());
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

                MainMenuScreen mainMenuScreen = new MainMenuScreen();
                mainMenuScreen.enterAnim();
                ClientGame.getInstance().setScreen(mainMenuScreen);
            }
        });
        forgotPassworBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showForgotPasswordDialogPhase1();
            }
        });
    }

    public void showForgotPasswordDialogPhase1(){
        InGameDialog dialog = new InGameDialog(uiStage);
        dialog.setBackground(customSkin.getDrawable("frameNinePatch2"));

        ValidatedTextField usernameTextfield = new ValidatedTextField(customSkin, new NonEmptyValidator());
        TextButton submitBtn = new TextButton("submit", customSkin);
        TextButton backBtn = new TextButton("back", customSkin);

        dialog.add(new Label("Enter your username:", customSkin)).left().row();
        dialog.add(usernameTextfield).growX().width(300).row();
        dialog.add(submitBtn).row();

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
        dialog.show();
    }
    public void showForgotPasswordDialogPhase2(InGameDialog dialog, Account account){
        dialog.clearChildren();

        dialog.add(new Label("Answer the questions:", customSkin)).left().expandX().row();

        Map<SecurityQuestions, String> securityAnswers = account.getSecurityAnswers();
        Map<SecurityQuestions, ValidatedTextField> textFields = new HashMap<>();

        for (Map.Entry<SecurityQuestions, String> entry : securityAnswers.entrySet()) {
            dialog.add(new Label(entry.getKey().getQuestion(), customSkin)).left().expandX().row();

            ValidatedTextField textField = new ValidatedTextField(customSkin, new NonEmptyValidator());
            textFields.put(entry.getKey(), textField);

            dialog.add(textField).growX().spaceBottom(20).row();
        }

        TextButton submitButton = new TextButton("submit", customSkin);

        dialog.add(submitButton).center();

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
    }

    public void showForgotPasswordDialogPhase3(InGameDialog dialog, Account account){
        dialog.clearChildren();

        ValidatedTextField textField = new ValidatedTextField(customSkin, new PasswordValidator());
        TextButton submitButton = new TextButton("submit", customSkin);

        dialog.add(new Label("Choose a new password:", customSkin)).left().expandX().row();
        dialog.add(textField).growX().row();
        dialog.add(submitButton).row();

        submitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!textField.validateText()){
                    textField.ping();
                    return;
                }

                account.setPassword(textField.getText());

                dialog.clear();
                dialog.pad(20).padRight(40).padLeft(40);
                dialog.defaults().spaceBottom(10);
                dialog.add(new Label("changed the password", skin));
            }
        });
    }
}
