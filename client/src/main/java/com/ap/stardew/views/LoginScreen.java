package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.controllers.LoginMenuController;
import com.ap.stardew.controllers.validators.NonEmptyValidator;
import com.ap.stardew.controllers.validators.PasswordValidator;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.App;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.enums.SecurityQuestions;
import com.ap.stardew.views.managers.ActorAnimManager;
import com.ap.stardew.views.managers.TransitionManager;
import com.ap.stardew.views.widgets.InGameDialog;
import com.ap.stardew.views.widgets.PopUpMessage;
import com.ap.stardew.views.widgets.TransformWidgetWrapper;
import com.ap.stardew.views.widgets.ValidatedTextField;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AbstractMenuScreen{
    LoginMenuController controller = new LoginMenuController();

    public LoginScreen(){
        super();
        Table mainBox = new Table();
        mainBox.center();
        mainBox.pack();
        mainBox.setSize(400, 200);

        Table textFieldBox = new Table();
        textFieldBox.setBackground(customSkin.getDrawable("frameNinePatch2"));

        ValidatedTextField usernameTextField = new ValidatedTextField(customSkin, new NonEmptyValidator());
        ValidatedTextField passwordTextField = new ValidatedTextField(customSkin, new NonEmptyValidator());

        TransformWidgetWrapper<TextButton> submitButtonWrapper = new TransformWidgetWrapper<>(new TextButton("login", customSkin, "big"));
        TransformWidgetWrapper<Button> backButtonWrapper       = new TransformWidgetWrapper<>(new Button(customSkin, "backLeft"));

        TextButton forgotPasswordBtn = new TextButton("forgot password", customSkin);

        mainBox.add(backButtonWrapper).left().spaceBottom(5).row();
        textFieldBox.add(usernameTextField).fillX().spaceBottom(5).row();
        textFieldBox.add(passwordTextField).fillX().spaceBottom(5).row();
        textFieldBox.add(forgotPasswordBtn).center().row();
        mainBox.add(textFieldBox).spaceBottom(5).row();
        mainBox.add(submitButtonWrapper).growX().pad(0, 3, 0, 3).row();

        rootTable.add(mainBox);

        ActorAnimManager.addHorizontalElastic(backButtonWrapper, false);
        ActorAnimManager.addRotateAction(submitButtonWrapper, 1);

        backButtonWrapper.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TransitionManager.horizontalTransition(new MainScreen(), LoginScreen.this, false, 1, Interpolation.smoother);
            }
        });
        submitButtonWrapper.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!usernameTextField.validateText() || !passwordTextField.validateText()){
                    usernameTextField.ping();
                    passwordTextField.ping();
                    return;
                }
                Result result = controller.login(usernameTextField.getText(), passwordTextField.getText(), true);
                if(result.isSuccessful()){
                    MainMenuScreen mainMenuScreen = new MainMenuScreen();
                    mainMenuScreen.getUiStage().addAction(
                        Actions.sequence(
                            Actions.run(() -> mainMenuScreen.prepareForAnim(true)),
                            Actions.delay(4),
                            Actions.run(() -> mainMenuScreen.enterAnim(true, true))
                        )
                    );
                    TransitionManager.verticalTransition(mainMenuScreen, LoginScreen.this, 300, 4, Interpolation.smoother);
                    return;
                }
                if(result.message().equals("username doesn't exist")){
                    usernameTextField.setMessage("Username doesnt exist");
                }else if(result.message().equals("incorrect password")){
                    passwordTextField.setMessage("incorrect password");
                }else{
                    new PopUpMessage("failed to login").show(AbstractScreen.getFrontStage());
                }
            }
        });
        forgotPasswordBtn.addListener(new ClickListener(){
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

                account.setPasswordNotHashed(textField.getText());

                dialog.clear();
                dialog.pad(20).padRight(40).padLeft(40);
                dialog.defaults().spaceBottom(10);
                dialog.add(new Label("changed the password", skin));
            }
        });
    }
}
