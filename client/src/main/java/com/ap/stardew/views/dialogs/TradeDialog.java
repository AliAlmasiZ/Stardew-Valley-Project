package com.ap.stardew.views.dialogs;

import com.ap.stardew.app.ClientApp;
import com.ap.stardew.app.GameController;
import com.ap.stardew.models.App;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Pickable;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.widgets.InGameDialog;
import com.ap.stardew.views.widgets.InventoryGrid;
import com.ap.stardew.views.widgets.TabWidget;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;


public class TradeDialog {
    private final Stage stage;
    private final Player playerToTrade;
    private final Skin customSkin;
    private final Player currentPlayer;
    private InGameDialog dialog;
    private Table table = new Table();
    private InventoryGrid senderInventoryGrid;
    private Inventory senderInventory;
    private InventoryGrid receiverInventoryGrid;
    private Inventory receiverInventory;
    public Label errorLabel;

    public TradeDialog(Stage stage, Player playerToTrade) {
        this.stage = stage;
        this.playerToTrade = playerToTrade;
        this.currentPlayer = ClientApp.getActiveGame().getCurrentPlayer();
        customSkin = GameAssetManager.getInstance().getCustomSkin();
        dialog = new InGameDialog(stage){
            @Override
            public void hide() {
                addAction(
                    Actions.sequence(
                        Actions.parallel(
                            Actions.alpha(0, 0.3f, Interpolation.smooth),
                            Actions.moveBy(0, -10, 0.3f, Interpolation.swingIn)
                        ),
                        Actions.run(() -> {
                            this.getWrapperTable().remove();
                            remove();
                            this.setWrapperTable(null);
                        })
                    )
                );
                GameController.stopTradeWithPlayer(playerToTrade, " stopped the trade!");
            }
        };
        dialog.setBackground((Drawable) null);

        TabWidget tabWidget = new TabWidget();

        tabWidget.addTab(table, customSkin.getDrawable("skillMenuIcon"));
        dialog.add(tabWidget).fill().grow();

        senderInventory = new Inventory(6);
        receiverInventory = new Inventory(6);
        senderInventoryGrid = new InventoryGrid(senderInventory, 0);
        receiverInventoryGrid = new InventoryGrid(receiverInventory, 0);

        errorLabel = new Label("", customSkin);
        errorLabel.setColor(Color.RED);
        errorLabel.setVisible(false);
    }


    public void openAsSender() {
        table.clear();
        Label label = new Label("Wait for \"" + playerToTrade.getUsername() + "\"...", customSkin);

        table.add(label).pad(3);

        dialog.show();
    }

    public void openAsReceiver() {
        table.clear();
        Label label = new Label("Do you want to start trade with \"" + playerToTrade.getUsername() + "\"?", customSkin);
        TextButton yesButton = new TextButton("Yes", customSkin);
        TextButton noButton = new TextButton("No", customSkin);

        table.add(label).colspan(2).pad(3).row();
        table.add(yesButton).growX().pad(3);
        table.add(noButton).growX().pad(3);

        yesButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                GameController.acceptTradeStart(playerToTrade);
            }
        });

        noButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide(true);
                GameController.stopTradeWithPlayer(playerToTrade, " rejected trade offer!");
            }
        });

        dialog.show();
    }

    public void openMainTradeAsSender() {
        table.clear();
        Label senderLabel = new Label("You send: ", customSkin);
        Label receiverLabel = new Label("You receive: ", customSkin);

        TextField itemToAddField = new TextField("", customSkin);
        itemToAddField.setMessageText("Item Name");
        TextField numberToAddField = new TextField("", customSkin);
        numberToAddField.setMessageText("Item Number");
        numberToAddField.setTextFieldFilter(new TextField.TextFieldFilter() {
            public boolean acceptChar(TextField textField, char c) {
                return Character.isDigit(c);
            }
        });

        TextButton whichInventoryButton = new TextButton("To Sender", customSkin);
        final boolean[] isSender = {true};
        TextButton addButton = new TextButton("Add", customSkin);
        TextButton confirmButton = new TextButton("Confirm", customSkin);
        addButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (numberToAddField.getText().isEmpty() || itemToAddField.getText().isEmpty()) {
                    errorLabel.setColor(Color.RED);
                    errorLabel.setVisible(true);
                    errorLabel.setText("Fields are empty");
                    return;
                }



                Entity itemToAdd;
                try {
                    itemToAdd = App.entityRegistry.makeEntity(itemToAddField.getText());
                } catch (Exception e) {
                    errorLabel.setText("No Entity with this name exists!");
                    errorLabel.setVisible(true);
                    return;
                }

                if (itemToAdd.getComponent(Pickable.class) == null) {
                    errorLabel.setText("This item can't be sent!");
                    errorLabel.setVisible(true);
                }

                itemToAdd.getComponent(Pickable.class).setStackSize(Integer.parseInt(numberToAddField.getText()));

                numberToAddField.setText("");
                itemToAddField.setText("");

                if (isSender[0]) {
                    senderInventory.addItem(itemToAdd);
                } else {
                    receiverInventory.addItem(itemToAdd);
                }
                GameController.updateTradeInventory(playerToTrade, itemToAdd, isSender[0]);
            }
        });

        confirmButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!currentPlayer.getComponent(Inventory.class).doesHaveItems(senderInventory)) {
                    errorLabel.setText("You don't have these items...");
                    errorLabel.setVisible(true);
                    return;
                }
                GameController.confirmTrade(playerToTrade);
            }
        });

        whichInventoryButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (isSender[0]) {
                    isSender[0] = false;
                    whichInventoryButton.setText("To Receiver");
                } else {
                    isSender[0] = true;
                    whichInventoryButton.setText("To Sender");
                }
            }
        });



        table.add(senderLabel).center().pad(5);
        table.add(receiverLabel).center().pad(5).row();
        table.add(senderInventoryGrid).center().pad(5);
        table.add(receiverInventoryGrid).center().pad(5).row();
        table.add(itemToAddField).center();
        table.add(numberToAddField).center().row();
        table.add(whichInventoryButton).center().growX().colspan(2).row();
        table.add(addButton).growX().colspan(2).row();
        table.add(errorLabel).center().colspan(2).pad(3).row();
        table.add(confirmButton).growX().colspan(2).row();
    }

    public void openMainTradeAsReceiver() {
        table.clear();
        Label senderLabel = new Label("You receive: ", customSkin);
        Label receiverLabel = new Label("You give: ", customSkin);

        Label waitLabel = new Label("Wait for sender to complete offer...", customSkin);
        waitLabel.setColor(Color.RED);

        table.add(senderLabel).center().pad(5);
        table.add(receiverLabel).center().pad(5).row();
        table.add(senderInventoryGrid).center().pad(5);
        table.add(receiverInventoryGrid).center().pad(5).row();
        table.add(waitLabel).colspan(2).center().pad(5).row();
    }

    public void openFinalTradeAsSender() {
        table.clear();
        Label senderLabel = new Label("You send: ", customSkin);
        Label receiverLabel = new Label("You receive: ", customSkin);

        Label waitLabel = new Label("Wait for \"" + playerToTrade.getUsername() + "\" to decide...", customSkin);

        table.add(senderLabel).center().pad(5);
        table.add(receiverLabel).center().pad(5).row();
        table.add(senderInventoryGrid).center().pad(5);
        table.add(receiverInventoryGrid).center().pad(5).row();
        table.add(waitLabel).colspan(2).center().pad(5).row();
    }

    public void openFinalTradeAsReceiver() {
        table.clear();
        Label senderLabel = new Label("You receive: ", customSkin);
        Label receiverLabel = new Label("You give: ", customSkin);

        Label label = new Label("Do you accept this trade?", customSkin);
        TextButton yesButton = new TextButton("Yes", customSkin);
        TextButton noButton = new TextButton("No", customSkin);

        yesButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!currentPlayer.getComponent(Inventory.class).doesHaveItems(receiverInventory)) {
                    errorLabel.setText("You don't have these items...");
                    errorLabel.setVisible(true);
                    return;
                }
                GameController.doTrade(playerToTrade);
            }
        });

        noButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                GameController.rejectTradeOffer(playerToTrade);
            }
        });


        table.add(senderLabel).center().pad(5);
        table.add(receiverLabel).center().pad(5).row();
        table.add(senderInventoryGrid).center().pad(5);
        table.add(receiverInventoryGrid).center().pad(5).row();
        table.add(label).center().colspan(2).row();
        table.add(yesButton).growX();
        table.add(noButton).growX().row();
        table.add(errorLabel).growX().colspan(2).row();
    }

    public void updateInventory(Entity itemToAdd, boolean isSender) {
        if (isSender) senderInventory.addItem(itemToAdd);
        else receiverInventory.addItem(itemToAdd);
    }

    public void hide() {
        dialog.hide(true);
    }


    public Inventory getReceiverInventory() {
        return receiverInventory;
    }

    public Inventory getSenderInventory() {
        return senderInventory;
    }
}
