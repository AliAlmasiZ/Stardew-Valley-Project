package com.ap.stardew.views;

import com.ap.stardew.app.GameController;
import com.ap.stardew.controllers.GameMenuController;
import com.ap.stardew.models.Result;
import com.ap.stardew.models.crafting.Recipe;
import com.ap.stardew.models.crafting.RecipeType;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.workstations.ArtisanComponent;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.widgets.InGameDialog;
import com.ap.stardew.views.widgets.InventoryGrid;
import com.ap.stardew.views.widgets.PopUpMessage;
import com.ap.stardew.views.widgets.ToolTip;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;


public class ArtisanMenuContent extends Table {
    private final Entity artisanEntity;
    private final ArtisanComponent artisanComponent;
    private final GameScreen gameScreen;
    private final GameMenuController controller;
    private final Label remainingTimeLabel;
    private final TextButton getProductBtn;

    public ArtisanMenuContent(GameScreen gameScreen, Entity artisanEntity) {
        this.gameScreen = gameScreen;
        this.artisanEntity = artisanEntity;
        this.artisanComponent = artisanEntity.getComponent(ArtisanComponent.class);
        this.controller = new GameMenuController();

        top().left().pad(5);
        defaults().pad(2).align(Align.left);

        Label titleLabel = new Label("Artisan: " + artisanEntity.getEntityName(), gameScreen.customSkin);
        titleLabel.setColor(Color.WHITE);
        add(titleLabel).growX().row();

        if(artisanComponent.isInProcess()) {
            Label processLabel = new Label("Current Process:", gameScreen.customSkin);
            processLabel.setColor(Color.YELLOW);
            add(processLabel).growX().row();

            this.remainingTimeLabel = new Label("", gameScreen.customSkin);
            this.remainingTimeLabel.setColor(Color.ORANGE);
            add(this.remainingTimeLabel).growX().row();

            this.getProductBtn = new TextButton("Get Product", gameScreen.customSkin);
            this.getProductBtn.setDisabled(!this.artisanComponent.isProcessFinished());
            add(this.getProductBtn).growX().padTop(10).row();

            this.getProductBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Result result = controller.getArtisan(artisanEntity.getEntityName()); //TODO : Server side
                    gameScreen.showTemporaryMessage(result.message(), GameScreen.ERROR_MESSAGE_DELAY, result.isSuccessful() ? Color.GREEN : Color.RED);

                    if(getParent() != null && getParent().getParent() instanceof InGameDialog)
                        ((InGameDialog) getParent().getParent()).hide();
                }
            });


        } else {
            this.remainingTimeLabel = null;
            this.getProductBtn = null;

            Label title = new Label(artisanEntity.getEntityName() + "Recipes", gameScreen.customSkin);
            title.setColor(Color.CYAN);
            add(title).growX().row();

            Table recipeTable = new Table();
            recipeTable.top();

            int itemsCount = 0;
            int itemsPerRow = 5;

            for (Recipe recipe : artisanComponent.getRecipes()) {

                Image image = new Image(recipe.getEntityTexture());
                image.setScaling(Scaling.fit);

                Table recipeBtn = new Table();
                recipeBtn.setBackground(gameScreen.customSkin.getDrawable("frameNinePatch2"));
                recipeBtn.add(image).width(32).height(32).pad(5);

                recipeTable.add(recipeBtn).size(60, 60).pad(2);


                ToolTip toolTip = new ToolTip(recipeBtn);
                String labelText = recipe.getName() + "\n" + recipe.getDay() + "days, " + recipe.getHour() + "hours";
                labelText += "\n" + recipe.toString();
                Label toolTipLabel = new Label(labelText, gameScreen.customSkin);
                toolTip.add(toolTipLabel);


                recipeBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Result result = controller.useArtisan(artisanEntity, recipe.getName());
                        Image icon;
                        if(result.isSuccessful())
                            icon = new Image(GameAssetManager.getInstance().success);
                        else
                            icon = new Image(GameAssetManager.getInstance().error);

                        PopUpMessage popUp = new PopUpMessage();
                        Label label = new Label(result.message(), gameScreen.customSkin);
                        popUp.add(icon).size(16,16).pad(5);
                        popUp.add(label).pad(10);
                        popUp.show(gameScreen.uiStage);
                    }

                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        toolTip.show();
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        toolTip.hide();
                    }
                });

                itemsCount++;
                if(itemsCount % itemsPerRow == 0) {
                    recipeTable.row();
                }
            }

            ScrollPane scrollPane = new ScrollPane(recipeTable, gameScreen.customSkin);

            Table inventoryPanel = new Table();
            inventoryPanel.setBackground(gameScreen.customSkin.getDrawable("frameNinePatch2"));
            inventoryPanel.add(new InventoryGrid(gameScreen.player.getComponent(Inventory.class), 10)).grow();
            add(scrollPane).colspan(2).fillX().height(200).row();
            add(inventoryPanel).colspan(2).grow().padTop(10);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(artisanComponent.isInProcess() && remainingTimeLabel != null) {
            if(artisanComponent.isProcessFinished()) {
                remainingTimeLabel.setText("Process Finished!");
                getProductBtn.setDisabled(false);
            } else {
                Result timeResult = artisanComponent.remainingTime();
                remainingTimeLabel.setText(timeResult.message());
            }
        }
    }
}
