package com.ap.stardew.views.widgets;

import com.ap.stardew.controllers.GameAssetManager;
import com.ap.stardew.views.GameScreen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

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

        wrapperTable = new Table();

        wrapperTable.setFillParent(true);
        wrapperTable.center();

        wrapperTable.add(this);
        stage.addActor(wrapperTable);

        stage.cancelTouchFocus();
        stage.setKeyboardFocus(this);
        stage.setScrollFocus(this);

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
        addListener(new InputListener() {
            float startX, startY, lastX, lastY;

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
            }

            public void touchDragged (InputEvent event, float x, float y, int pointer) {
                event.stop();
            }

            public boolean mouseMoved (InputEvent event, float x, float y) {
                return true;
            }

            public boolean scrolled (InputEvent event, float x, float y, int amount) {
                return true;
            }

            public boolean keyDown (InputEvent event, int keycode) {
                return true;
            }

            public boolean keyUp (InputEvent event, int keycode) {
                return true;
            }

            public boolean keyTyped (InputEvent event, char character) {
                return true;
            }
        });
        toFront();
        setTouchable(Touchable.enabled);
        wrapperTable.setTouchable(Touchable.enabled);
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
