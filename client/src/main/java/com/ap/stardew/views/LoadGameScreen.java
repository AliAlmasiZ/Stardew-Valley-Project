package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.dto.SavedGameDetails;
import com.ap.stardew.views.managers.ActorAnimManager;
import com.ap.stardew.views.managers.TransitionManager;
import com.ap.stardew.views.widgets.InGameDialog;
import com.ap.stardew.views.widgets.PopUpMessage;
import com.ap.stardew.views.widgets.TransformWidgetWrapper;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

public class LoadGameScreen extends AbstractMenuScreen{
    public LoadGameScreen(ArrayList<SavedGameDetails> games){
        Table dialog = new Table();
        Table mainBox = new Table();

        Table gamesTable = new Table();
        gamesTable.top();

        ScrollPane scrollPane1 = new ScrollPane(gamesTable);
        scrollPane1.setFadeScrollBars(false);
        scrollPane1.setFlickScroll(false);
        scrollPane1.setOverscroll(false, false);
        scrollPane1.setForceScroll(false, true);

        TransformWidgetWrapper<Button> backButtonWrapper = new TransformWidgetWrapper<>(new Button(customSkin, "backLeft"));

        initGameCards(games, gamesTable);

        ActorAnimManager.addHorizontalElastic(backButtonWrapper, false);
        backButtonWrapper.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MainMenuScreen mainMenuScreen = new MainMenuScreen();
                uiStage.addAction(
                    Actions.sequence(
                        Actions.run(() -> mainMenuScreen.prepareForAnim(false)),
                        Actions.run(() -> TransitionManager.horizontalTransition(mainMenuScreen, LoadGameScreen.this, false, 1.5f, Interpolation.smoother)),
                        Actions.delay(0.5f),
                        Actions.run(() -> mainMenuScreen.enterAnim(false, false))
                    )
                );
            }
        });

        dialog.add(scrollPane1).grow().maxHeight(150).top();
        dialog.setBackground(customSkin.getDrawable("frameNinePatch2"));


        mainBox.defaults().spaceBottom(5);
        mainBox.add(backButtonWrapper).expandX().left().row();
        mainBox.add(dialog);

        rootTable.add(mainBox);
    }

    private void initGameCards(ArrayList<SavedGameDetails> games, Table gamesTable){
        for (int i = 0; i < games.size(); i++) {
            SavedGameDetails game = games.get(i);
            Label dateLabel = new Label(game.inGameDate, customSkin);
            Label playersLabel = new Label("", customSkin);
            Label goldLabel = new Label(Integer.toString(game.gold.get(ClientApp.getUsername())), customSkin, "inventoryQuantity");
            Label farmLabel = new Label(game.farms.get(ClientApp.getUsername()), customSkin, "black");

            Button button = new Button(customSkin, "play");
            button.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    JSONMessage request = new JSONMessage(JSONMessage.Type.lobby_command);
                    request.put("command", "hostSavedGame");
                    request.put("host_username", ClientApp.getUsername());
                    request.put("saved_game_id", game.gameId);

                    JSONMessage response = ClientApp.sendAndWaitForResponse(request, 1000);

                    if(response == null || response.getFromBody("result") == null){
                        new PopUpMessage("failed to create lobby").show(AbstractScreen.getFrontStage());
                        return;
                    }

                    if(response.getFromBody("lobby_info") == null || !response.getFromBody("result",Result.class).isSuccessful()){
                        new PopUpMessage(response.getFromBody("result",Result.class).message()).show(AbstractScreen.getFrontStage());
                        return;
                    }

                    LobbyScreen lobbyScreen = new LobbyScreen(response.getFromBody("lobby_info"), true, LoadGameScreen.class);
                    TransitionManager.verticalTransition(lobbyScreen, LoadGameScreen.this, 500, 4, Interpolation.smoother);
                }
            });

            StringBuilder playersString = new StringBuilder();
            for (int j = 0; j < game.players.size(); j++) {
                playersString.append(game.players.get(j));
                if(j != game.players.size() - 1){
                    playersString.append(", ");
                }
            }
            playersLabel.setText(playersString.toString());
            playersLabel.setColor(ColorPalette.green);
            farmLabel.setFontScale(0.2f);
            dateLabel.setFontScale(0.24f);
            playersLabel.setFontScale(0.2f);
            dateLabel.setFontScale(0.2f);
            goldLabel.setColor(ColorPalette.yellow);
            dateLabel.setColor(ColorPalette.orange);

            goldLabel.setFontScale(1);

            Table gameTable = new Table();
            Table leftTable = new Table();
            Table middleTable = new Table();
            Table rightTable = new Table();

            rightTable.bottom();
            leftTable.top();


            gameTable.defaults().spaceRight(2);
            middleTable.defaults().spaceBottom(4).spaceRight(2);
            leftTable.add(new Label((i+1) + ".", customSkin, "big"){{setColor(ColorPalette.red); setAlignment(Align.top);}
            }).top();
            middleTable.add(new Label("Game with: ", customSkin, "black"){{setFontScale(0.24f);}}).left();
            middleTable.add(playersLabel);
            middleTable.add(farmLabel).expandX().right();
            middleTable.row();
            middleTable.add(dateLabel).expandX().left().colspan(2);
            middleTable.add(new Table(){
                {
                    add(new Image(customSkin.getDrawable("goldCoin"))).size(8, 8).spaceRight(1);
                    add(goldLabel).center();
                }
            });
            rightTable.add(button).bottom();

            gameTable.add(leftTable);
            gameTable.add(middleTable).growX();
            gameTable.add(rightTable);

            gameTable.setBackground(customSkin.getDrawable("savedGameBox"));
            gamesTable.add(gameTable).spaceBottom(2).growX().row();
        }
    }
}
