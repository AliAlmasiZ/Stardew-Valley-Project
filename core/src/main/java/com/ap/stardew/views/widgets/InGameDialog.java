package com.ap.stardew.views.widgets;

import com.ap.stardew.controllers.GameAssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class InGameDialog extends Table {
    private final Stage stage;
    private final Image closeButton;
    private Table wrapperTable;

    private int closeButtonOffsetX = 0, closeButtonOffsetY = -15;

    public InGameDialog(Stage stage) {
        this.stage = stage;

        closeButton = new Image(GameAssetManager.getInstance().closeButton);
        addActor(closeButton);

        closeButton.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                closeButton.addAction(
                    Actions.alpha(1f, 0.1f, Interpolation.smooth)
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                closeButton.addAction(
                    Actions.alpha(0.5f, 0.1f, Interpolation.smooth)
                );
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
    }

    public void show(){
        clearActions();

        stage.cancelTouchFocus();
        stage.setKeyboardFocus(this);
        stage.setScrollFocus(this);

        wrapperTable = new Table();

        wrapperTable.setFillParent(true);
        wrapperTable.center();

        wrapperTable.add(this);
        stage.addActor(wrapperTable);

        addAction(
            Actions.sequence(
                Actions.alpha(0f),
                Actions.moveBy(0, -10),
                Actions.parallel(
                    Actions.alpha(1, 0.3f, Interpolation.smooth),
                    Actions.moveBy(0, 10, 0.3f, Interpolation.swingOut)
                )
            )
        );
    }

    @Override
    public void layout() {
        super.layout();
        closeButton.setPosition(getWidth() + closeButtonOffsetX, getHeight() + closeButtonOffsetY);
    }

    public Image getCloseButton() {
        return closeButton;
    }

    public void setCloseButtonOffset(int x, int y){
        closeButtonOffsetX = x;
        closeButtonOffsetY = y;
    }

    public void hide(){
        addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.alpha(0, 0.3f, Interpolation.smooth),
                    Actions.moveBy(0, -10, 0.3f, Interpolation.swingIn)
                ),
                Actions.run(() -> {
                    wrapperTable.remove();
                    remove();
                    wrapperTable = null;
                })
            )
        );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(1, 1, 1, 1);
    }
}
