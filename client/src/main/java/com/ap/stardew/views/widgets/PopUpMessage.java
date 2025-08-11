package com.ap.stardew.views.widgets;

import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.GameScreen;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PopUpMessage extends Table {
    private static PopUpMessage activeMessage = null;

    public enum PopUpMessageType {
        STANDARD(20),
        TOP_CENTER(5),
        ERROR_NOTIFICATION(3),
        SUCCESS_NOTIFICATION(3),
        ;

        private final float duration;

        PopUpMessageType(float duration) {
            this.duration = duration;
        }

        public float getDuration() {
            return duration;
        }
    }

    private Table wrapperTable;
    private PopUpMessageType popUpMessageType = PopUpMessageType.STANDARD;

    public PopUpMessage() {
        setBackground(GameAssetManager.getInstance().getCustomSkin().getDrawable("smallPanelNinePatch"));
    }
    public PopUpMessage(String message){
        this(message, PopUpMessageType.STANDARD);
    }


    public PopUpMessage(String message, PopUpMessageType popUpMessageType) {
        this();
        this.popUpMessageType = popUpMessageType;

        switch (popUpMessageType) {
            case ERROR_NOTIFICATION -> {
                Image icon = new Image(GameAssetManager.getInstance().error);
                add(icon).size(16,16).pad(5);
            }
            case SUCCESS_NOTIFICATION -> {
                Image icon = new Image(GameAssetManager.getInstance().success);
                add(icon).size(16, 16).pad(5);
            }
        }
        Label label = new Label(message, GameAssetManager.getInstance().getCustomSkin());

        if(label.getPrefWidth() < 200){
            add(label).grow().width(label.getPrefWidth());
        }else{
            add(label).grow().width(200);
            label.setWrap(true);
        }
    }

    public void show(Stage stage) {
        if (activeMessage != null) {
            activeMessage.hide();
        }

        activeMessage = this;

        wrapperTable = new Table();
        wrapperTable.setFillParent(true);
        switch (popUpMessageType) {
            case STANDARD -> {
                wrapperTable.bottom().right().pad(5);
            }
            case TOP_CENTER -> {
                wrapperTable.center().top().pad(8);
            }
        }

        stage.addActor(wrapperTable);

        wrapperTable.add(this);

        pack();
        this.addAction(
            Actions.sequence(
                Actions.moveBy(5 + getWidth() + 20, 0),
                Actions.moveBy(-5 - getWidth() - 20, 0, 0.5f, Interpolation.swingOut),
                Actions.delay(popUpMessageType.getDuration()),
                Actions.run(this::hide)
            )
        );

        setTouchable(Touchable.enabled);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
    }

    public void hide() {
        this.addAction(
            Actions.sequence(
                Actions.alpha(0, 1),
                Actions.run(() -> {
                    wrapperTable.remove();
                    clearChildren();
                    clearActions();
                    remove();
                })
            )
        );
    }
}
