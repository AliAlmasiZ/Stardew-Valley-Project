package com.ap.stardew.views.widgets;

import com.ap.stardew.view.GameAssetManager;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PopUpMessage extends Table {
    private static PopUpMessage activeMessage = null;
    private static PopUpMessageType popUpMessageType = PopUpMessageType.STANDARD;

    private Table wrapperTable;
    public PopUpMessage(){
        setBackground(GameAssetManager.getInstance().getCustomSkin().getDrawable("smallPanelNinePatch"));
    }

    public PopUpMessage(PopUpMessageType popUpMessageType){
        setBackground(GameAssetManager.getInstance().getCustomSkin().getDrawable("smallPanelNinePatch"));
        this.popUpMessageType = popUpMessageType;
    }

    public enum PopUpMessageType {
        STANDARD,
        TOP_CENTER,

    }

    public void show(Stage stage){
        if(activeMessage != null){
            activeMessage.hide();
        }

        activeMessage = this;

        wrapperTable = new Table();
        wrapperTable.setFillParent(true);
        switch(popUpMessageType){
            case STANDARD -> {
                wrapperTable.bottom().right().pad(5);
            }
            case TOP_CENTER -> {
                wrapperTable.top().center().pad(8);
            }
        }

        stage.addActor(wrapperTable);

        wrapperTable.add(this);

        pack();
        this.addAction(
            Actions.sequence(
                Actions.moveBy(5 + getWidth() + 20, 0),
                Actions.moveBy(-5 - getWidth() - 20, 0, 0.5f, Interpolation.swingOut),
                Actions.delay(2),
                Actions.run(this::hide)
            )
        );

        setTouchable(Touchable.enabled);
        addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
    }

    public void hide(){
        this.addAction(
            Actions.sequence(
                Actions.alpha(0, 1),
                Actions.run(()->{
                    wrapperTable.remove();
                    clearChildren();
                    clearActions();
                    remove();
                })
            )
        );
    }
}
