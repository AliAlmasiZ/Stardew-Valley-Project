package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.controllers.LoginMenuController;
import com.ap.stardew.models.enums.SecurityQuestions;
import com.ap.stardew.records.Result;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class SignupScreen extends AbstractMenuScreen {
    LoginMenuController controller;
    private TextField username;
    private TextField password;
    private TextField confirmPassword;
    private TextField name;
    private TextField email;
    SelectBox<String> gender;


    private TextButton randomPasswordButton;
    private TextButton registerButton;
    private TextButton backButton;
    private Label message;


    public SignupScreen() {
        super();
        controller = new LoginMenuController();
        float fieldWidth = Gdx.graphics.getWidth() * 0.2f;

        username = new TextField("", customSkin);
        username.setMessageText("Username");
        username.setWidth(fieldWidth);
        password = new TextField("", customSkin);
        password.setMessageText("Password");
        password.setWidth(fieldWidth);
        confirmPassword = new TextField("", customSkin);
        confirmPassword.setMessageText("Confirm Password");
        confirmPassword.setWidth(fieldWidth);
        name = new TextField("", customSkin);
        name.setMessageText("Name");
        email = new TextField("", customSkin);
        email.setMessageText("Email");
        email.setWidth(fieldWidth);
        gender = new SelectBox<>(customSkin);
        String[] genders = {"male", "female"};
        SignupScreen.this.gender.setItems(genders);

        randomPasswordButton = new TextButton("Random Password", customSkin);
        registerButton = new TextButton("Register", customSkin);
        backButton = new TextButton("Back", customSkin);

        message = new Label("", customSkin);
        message.setVisible(false);

        float padFromLabel = 0.1f;

        rootTable.add(message).pad(30);
        rootTable.row();
        rootTable.add(new Label("Username:", customSkin)).padBottom(padFromLabel);
        rootTable.row();
        rootTable.add(username).width(fieldWidth);
        rootTable.row();
        rootTable.add(new Label("Password:", customSkin)).padBottom(padFromLabel);
        rootTable.row();
        rootTable.add(password).width(fieldWidth);
        rootTable.add(randomPasswordButton).pad(15);
        rootTable.row();
        rootTable.add(new Label("Confirm Password:", customSkin)).padBottom(padFromLabel);
        rootTable.row();
        rootTable.add(confirmPassword).width(fieldWidth);
        rootTable.row();
        rootTable.add(new Label("Name", customSkin)).padBottom(padFromLabel);
        rootTable.row();
        rootTable.add(name).width(fieldWidth);
        rootTable.row();
        rootTable.add(new Label("Email:", customSkin)).padBottom(padFromLabel);
        rootTable.row();
        rootTable.add(email).width(fieldWidth);
        rootTable.row();
        rootTable.add(new Label("Gender:", customSkin));
        rootTable.row();
        rootTable.add(gender);
        rootTable.row();
        rootTable.add(registerButton).pad(20);
        rootTable.row();
        rootTable.add(backButton).pad(20);



        // Add change listener
        SignupScreen.this.gender.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                String selectedCity = SignupScreen.this.gender.getSelected();
            }
        });

        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Result isNewUsername = controller.suggestUsername(username.getText());
                if (!isNewUsername.isSuccessful()) {
                    username.setText(isNewUsername.message());
                    message.setColor(Color.RED);
                    message.setVisible(true);
                    message.setText("You should choose a new username! We filled new one for you!");
                    return;
                }

                Result result = controller.register(username.getText(), password.getText(), confirmPassword.getText(),
                        name.getText(), email.getText(), gender.getSelected());

                if (!result.isSuccessful()) {
                    message.setColor(Color.RED);
                    message.setVisible(true);
                    message.setText(result.message());
                    return;
                }

                message.setVisible(false);
                showSecurityQuestionDialog();
            }
        });

        randomPasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                password.setText(LoginMenuController.generatePassword());
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ClientGame.getInstance().setScreen(new MainScreen());
            }
        });



    }

    public void showSecurityQuestionDialog() {
        Dialog dialog = new Dialog("Security Question", customSkin);

        SelectBox<Integer> integerSelectBox = new SelectBox<>(customSkin);
        integerSelectBox.setItems(new Integer[]{1, 2, 3, 4, 5});
        Label questionLabel = new Label(SecurityQuestions.getQuestionList(), customSkin);
        TextField answerField = new TextField("", customSkin);
        answerField.setMessageText("Your answer");
        TextField confirmAnswerField = new TextField("", customSkin);
        confirmAnswerField.setMessageText("Your confirm answer");

        Label errorLabel = new Label("", customSkin);
        errorLabel.setColor(Color.RED);
        dialog.getContentTable().add(questionLabel).padTop(10).padLeft(10).padRight(10).row();
        dialog.getContentTable().add(new Label("which one will you answer?", customSkin)).padTop(10).padLeft(10).padRight(10).row();
        dialog.getContentTable().add(integerSelectBox).padTop(10).padLeft(10).padRight(10).row();
        dialog.getContentTable().add(answerField).width(300).padBottom(10).row();
        dialog.getContentTable().add(confirmAnswerField).width(300).padBottom(10).row();
        dialog.getContentTable().add(errorLabel).padBottom(10).row();

        TextButton confirmButton = new TextButton("Confirm", customSkin);
        TextButton skipButton = new TextButton("Skip", customSkin);

        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (answerField.getText().trim().length() < 2) {
                    errorLabel.setText("Answer is too short.");
                    return;
                }

                Result result = controller.pickQuestion(integerSelectBox.getSelected(),
                                     answerField.getText(), confirmAnswerField.getText());
                if (!result.isSuccessful()) {
                    errorLabel.setText(result.message());
                    return;
                }

                ClientGame.getInstance().setScreen(new MainScreen()); //TODO: go to proper screen
            }

        });

        skipButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });

        dialog.getContentTable().center();
        dialog.getButtonTable().add(confirmButton).padBottom(5).row();
        dialog.getButtonTable().add(skipButton).padBottom(5).row();

        dialog.setMovable(false);
        dialog.setResizable(false);
        dialog.show(uiStage);
        dialog.center();
        dialog.getTitleTable().padTop(20).padBottom(20);
        dialog.getButtonTable().center();
        dialog.getTitleLabel().setFontScale(1.2f);
        dialog.getTitleLabel().setAlignment(Align.center);
    }
}
