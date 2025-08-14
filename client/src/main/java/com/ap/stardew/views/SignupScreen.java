package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.controllers.LoginMenuController;
import com.ap.stardew.models.App;
import com.ap.stardew.models.enums.Gender;
import com.ap.stardew.models.enums.SecurityQuestions;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.managers.ActorAnimManager;
import com.ap.stardew.views.managers.TransitionManager;
import com.ap.stardew.views.widgets.LabelMessage;
import com.ap.stardew.views.widgets.TransformWidgetWrapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class SignupScreen extends AbstractMenuScreen {
    LoginMenuController controller;
    private TextField username;
    private TextField password;
    private TextField confirmPassword;
    private TextField name;
    private TextField email;
    private Image portrait;

    String selectedCity = "male"; // :)

    public SignupScreen() {
        super();
        controller = new LoginMenuController();

        Table mainBox      = new Table();
        Table textFieldBox = new Table();
        textFieldBox.setBackground(customSkin.getDrawable("frameNinePatch2"));

        confirmPassword = new TextField("", customSkin);
        username        = new TextField("", customSkin);
        password        = new TextField("", customSkin);
        name            = new TextField("", customSkin);
        email           = new TextField("", customSkin);

        username.setMessageText("Username");
        password.setMessageText("Password");
        confirmPassword.setMessageText("Confirm Password");
        name.setMessageText("Name");
        email.setMessageText("Email");

        portrait = new Image(GameAssetManager.getInstance().characterSpriteManager.getFrame(0, new Vector2(0, -1), Player.Action.IDLE, Gender.FEMALE));

        TransformWidgetWrapper<Image> randomPasswordTable        = new TransformWidgetWrapper<>(new Image(customSkin.getDrawable("randomIcon")));
        TransformWidgetWrapper<Button> backButtonWrapper         = new TransformWidgetWrapper<>(new Button(customSkin, "back"));
        TransformWidgetWrapper<TextButton> registerButtonWrapper = new TransformWidgetWrapper<>(new TextButton("Register", customSkin, "big"));
        TransformWidgetWrapper<Image> femaleWrapper              = new TransformWidgetWrapper<>(new Image(customSkin.getDrawable("maleIcon")));
        TransformWidgetWrapper<Image> maleWrapper                = new TransformWidgetWrapper<>(new Image(customSkin.getDrawable("femaleIcon")));

        mainBox.defaults().spaceBottom(5);

        Table leftTable   = new Table();
        Table rightTable  = new Table();
        Table bottomTable = new Table();
        leftTable.defaults().spaceBottom(2);

        mainBox.add(backButtonWrapper).right().row();
        mainBox.add(textFieldBox).row();
        textFieldBox.add(leftTable).spaceRight(3);
        textFieldBox.add(rightTable).grow().row();
        leftTable.defaults().growX();
        leftTable.add(username).row();
        leftTable.add(name).row();
        leftTable.add(email).row();
        leftTable.add(bottomTable).row();
        bottomTable.add(password).spaceRight(2);
        bottomTable.add(randomPasswordTable);
        leftTable.add(confirmPassword).row();
        rightTable.add(new Table(){{setBackground(customSkin.getDrawable("daybg"));add(portrait);}}).expand().top().colspan(2).expand().row();
        rightTable.add(femaleWrapper);
        rightTable.add(maleWrapper);
        mainBox.add(registerButtonWrapper).growX().pad(0, 3, 0, 3).row();
        rootTable.add(mainBox);

        femaleWrapper.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if(pointer != -1) return;
                femaleWrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.moveBy(2, 2, 0.3f, Interpolation.exp5Out)
                    )
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if(pointer != -1) return;
                femaleWrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.moveBy(-2, -2, 0.2f, Interpolation.swingOut)
                    )
                );
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                femaleWrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.scaleTo(1.1f, 1.1f, 0.2f, Interpolation.swingOut),
                        Actions.scaleTo(1, 1, 0.2f, Interpolation.swingOut)
                    )
                );

                selectedCity = "female";
                portrait.setDrawable(new TextureRegionDrawable(GameAssetManager.getInstance().characterSpriteManager.getFrame(0, new Vector2(0, -1), Player.Action.IDLE, Gender.FEMALE)));
            }
        });
        maleWrapper.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if(pointer != -1) return;
                maleWrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.moveBy(0, 2, 0.3f, Interpolation.exp5Out)
                    )
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if(pointer != -1) return;
                maleWrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.moveBy(0, -2, 0.2f, Interpolation.swingOut)
                    )
                );
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                maleWrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.scaleTo(1.1f, 1.1f, 0.2f, Interpolation.swingOut),
                        Actions.scaleTo(1, 1, 0.2f, Interpolation.swingOut)
                    )
                );

                selectedCity = "male";
                portrait.setDrawable(new TextureRegionDrawable(GameAssetManager.getInstance().characterSpriteManager.getFrame(0, new Vector2(0, -1), Player.Action.IDLE, Gender.MALE)));
            }
        });

        ActorAnimManager.addRotateAction(registerButtonWrapper, 1);
        registerButtonWrapper.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Result isNewUsername = controller.suggestUsername(username.getText());
                if (!isNewUsername.isSuccessful()) {
                    new LabelMessage(username, "You should choose a new username! We filled new one for you!", customSkin){{setColor(ColorPalette.red);}}.show();
                    username.setText(isNewUsername.message());
                    return;
                }

                Result result = controller.register(username.getText(), password.getText(), confirmPassword.getText(),
                        name.getText(), email.getText(), selectedCity);

                if (!result.isSuccessful()) {
                    new LabelMessage(registerButtonWrapper.getActor(), result.message(), customSkin){{setColor(ColorPalette.red);}}.show();
                    return;
                }

                showSecurityQuestionDialog();
            }
        });

        ActorAnimManager.addWiggle(randomPasswordTable);
        randomPasswordTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                password.setText(LoginMenuController.generatePassword());
            }
        });

        ActorAnimManager.addHorizontalElastic(backButtonWrapper, true);
        backButtonWrapper.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TransitionManager.horizontalTransition(new MainScreen(), SignupScreen.this, true, 1, Interpolation.smoother);
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
        dialog.getContentTable().add(answerField).width(200).padBottom(10).row();
        dialog.getContentTable().add(confirmAnswerField).width(200).padBottom(10).row();
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

                ClientApp.setUsername(App.getRegisteredAccount().getUsername());
                ClientGame.getInstance().setScreen(new MainScreen());
//                ClientGame.getInstance().setScreen(new MainScreen());
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
