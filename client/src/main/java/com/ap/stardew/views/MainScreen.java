package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.controllers.LoginMenuController;
import com.ap.stardew.models.*;
import com.ap.stardew.models.LobbyInfo;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.gameMap.MapRegion;
import com.ap.stardew.models.gameMap.WorldMap;
import com.ap.stardew.utils.TiledMapUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.security.SecureRandom;

public class MainScreen extends AbstractMenuScreen {
    private TextButton registerButton;
    private TextButton loginButton;
    private TextButton guestButton;
    private TextButton play;

    public MainScreen() {
        super();
        Table mainBox = new Table();

        registerButton = new TextButton("Register", customSkin, "big");
        loginButton = new TextButton("Login", customSkin, "big");
        guestButton = new TextButton("Guest", customSkin, "big");
        play = new TextButton("Play(for now)", customSkin, "big");


        mainBox.defaults().spaceBottom(3);
        mainBox.add(registerButton).growX();
        mainBox.row();
        mainBox.add(loginButton).growX();
        mainBox.row();
        mainBox.add(guestButton).growX();
        mainBox.row();
        mainBox.add(play).growX();
        mainBox.row();

        mainBox.pack();

        rootTable.add(mainBox);

        // Adding listeners
        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ClientGame.getInstance().setScreen(new SignupScreen());
                dispose();
            }
        });

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ClientGame.getInstance().setScreen(new LoginScreen());
            }
        });

        guestButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parallaxBackground.move(300, 4, Interpolation.smoother);
                rootTable.addAction(Actions.sequence(
                    Actions.moveBy(0, -300, 4, Interpolation.smoother),
                    Actions.run(()->{
                        MainMenuScreen mainMenuScreen = new MainMenuScreen();
                        mainMenuScreen.enterAnim();
                        ClientGame.getInstance().setScreen(mainMenuScreen);
                    })
                ));
            }
        });

        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if(!ClientApp.isConnected())
                    ClientApp.connectClients();
//                Game game = new Game();
////                App.setActiveGame(game);
//
//                Account[] accounts = {App.getUserByUsername("parsa"), App.getUserByUsername("ali"), App.getUserByUsername("ilia")};
//
//                game.initGame(new GameStartingDetails(true, "asd", accounts, null, null, null));
////                ClientGame.getInstance().setScreen(new GameScreen());

                SecureRandom random = new SecureRandom();

                String[] usernames = new String[] {"a", "s", "d", "f"};
                String password = "a";

                LoginMenuController loginController = new LoginMenuController();

                loginController.login(usernames[random.nextInt(0, 3)], password, true);
                ClientGame.getInstance().setScreen(new MultiplayerScreen());

                JSONMessage message = new JSONMessage(JSONMessage.Type.lobby_command);
                message.put("command", "host");
                message.put("lobby_name", "meow");
                message.put("host_username", ClientApp.getUsername());
                message.put("max_players", 10);
                message.put("password", "");
                message.put("is_visible", true);

                JSONMessage response = ClientApp.sendAndWaitForResponse(message, 5000);
                LobbyInfo lobbyInfo = response.getFromBody("lobby_info");
                if(lobbyInfo == null)
                    return;

                ClientGame.getInstance().setScreen(new LobbyScreen(lobbyInfo, true));

                WorldMap worldMap = TiledMapUtils.getRegionData("./Content(unpacked)/Maps/untitled.tmx");
                MapRegion region = null;
                for (MapRegion mapRegion : worldMap.getRegions()) {
                    if(mapRegion.getTilesNum() == 0) continue;
                    region = mapRegion;
                    break;
                }

                JSONMessage selectMap = new JSONMessage(JSONMessage.Type.lobby_command);
                selectMap.put("command", "chooseMapRegion");
                selectMap.put("token", ClientApp.getToken());
                selectMap.put("lobby_id", lobbyInfo.getLobbyId());
                selectMap.put("mapRegion", region.getName());

                ClientApp.sendAndWaitForResponse(selectMap, 500);



                JSONMessage m1 = new JSONMessage(JSONMessage.Type.lobby_command);
                m1.put("command", "startGame");
                m1.put("lobby_id", lobbyInfo.getLobbyId());
                m1.put("token", ClientApp.getToken());

                JSONMessage message1 = ClientApp.sendAndWaitForResponse(m1, 500);

                if(message1 == null) return;

                System.out.println(message1.getBody());

                Result result = message1.getFromBody("result");
                if(!result.isSuccessful()){
                    System.out.println(result.message());
                }






            }
        });
    }

    public void registerDialog() {
        //TODO
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        uiStage.dispose();
    }



    public void enterAnim(){
        registerButton.addAction(
            Actions.sequence(
                Actions.alpha(0),
                Actions.delay(0.1f),
                Actions.alpha(1, 0.5f, Interpolation.smooth)
            )
        );
        loginButton.addAction(
            Actions.sequence(
                Actions.alpha(0),
                Actions.delay(0.2f),
                Actions.alpha(1, 0.5f, Interpolation.smooth)
            )
        );
        guestButton.addAction(
            Actions.sequence(
                Actions.alpha(0),
                Actions.delay(0.3f),
                Actions.alpha(1, 0.5f, Interpolation.smooth)
            )
        );
        play.addAction(
            Actions.sequence(
                Actions.alpha(0),
                Actions.delay(0.4f),
                Actions.alpha(1, 0.5f, Interpolation.smooth)
            )
        );
    }
    public void exitAnim(){

    }
}
