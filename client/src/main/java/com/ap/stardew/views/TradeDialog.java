package com.ap.stardew.views;

import com.ap.stardew.app.GameController;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.widgets.InGameDialog;
import com.ap.stardew.views.widgets.TabWidget;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;


public class TradeDialog {
    private final Stage stage;
    private final Player playerToTrade;
    private final Skin customSkin;
    private InGameDialog dialog;
    private Table table = new Table();
    private Inventory senderInventory;
    private Inventory receiverInventory;

    public TradeDialog(Stage stage, Player playerToTrade) {
        this.stage = stage;
        this.playerToTrade = playerToTrade;
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
                GameController.stopTradeWithPlayer(playerToTrade);
            }
        };
        dialog.setBackground((Drawable) null);

        TabWidget tabWidget = new TabWidget();

        tabWidget.addTab(table, customSkin.getDrawable("skillMenuIcon"));
        dialog.add(tabWidget).fill().grow();
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
                GameController.rejectTradeStart(playerToTrade);
            }
        });

        dialog.show();
    }

    public void hide() {
        dialog.hide(true);
    }

}
