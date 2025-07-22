package com.ap.stardew.views.widgets;

import com.ap.stardew.controllers.GameAssetManager;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Pickable;
import com.ap.stardew.models.entities.components.inventory.InventorySlot;
import com.ap.stardew.views.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Null;

public class InventorySlotWidget extends FramedImage{
    private Entity entity;
    private InventorySlot slot;
    private Label quantityLabel;
    private Table labelTable;

    public InventorySlotWidget(InventorySlot slot) {
        super(GameAssetManager.getInstance().inventorySlotFrame, GameAssetManager.getInstance().emptyTexture);

        labelTable = new Table();
        labelTable.setFillParent(true);
        addActor(labelTable);
        labelTable.right().bottom();

        quantityLabel = new Label("", GameAssetManager.getInstance().getCustomSkin(), "inventoryQuantity");
        quantityLabel.setFontScale(.7f);
//        quantityLabel.setColor(0, 0, 0, 1);
        labelTable.add(quantityLabel);

        this.slot = slot;
        updateEntityIcon();

        addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                image.addAction(
                        Actions.moveBy(0, 2, 0.2f, Interpolation.swingOut)
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                image.addAction(
                        Actions.moveBy(0, -2, 0.2f, Interpolation.swingOut)
                );
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    if(entity == null) return;

                    Pickable pickable = entity.getComponent(Pickable.class);
                    int quantity = pickable.getStackSize();
                    if (quantity > 1) {
                        InGameDialog dialog = new InGameDialog(getStage());
                        dialog.setSkin(GameAssetManager.getInstance().getCustomSkin());
                        dialog.setBackground("frameNinePatch2");

                        Slider slider = new Slider(1, pickable.getStackSize(), 1, false, GameAssetManager.getInstance().getCustomSkin());
                        slider.setWidth(50);

                        Label label = new Label("", GameAssetManager.getInstance().getCustomSkin());

                        slider.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                label.setText(Integer.toString((int) slider.getValue()));
                            }
                        });
                        dialog.add(slider).width(50).pad(1);
                        dialog.add(label).width(20);
                        dialog.show();
                    }
                } else {
                    // Regular left click behavior (pick up item, etc.)
                }
            }
        });
    }
    @Override
    public @Null Actor hit (float x, float y, boolean touchable) {
        if (touchable && getTouchable() == Touchable.disabled) return null;
        if (!isVisible()) return null;

        if (x >= 0 && x <= frame.getWidth() && y >= 0 && y <= frame.getHeight()) {
            return this;
        }

        return null;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if(entity != slot.getEntity()){
            updateEntityIcon();
        }
    }

    private void updateEntityIcon(){
        entity = slot.getEntity();
        Texture icon;
        if(entity != null){
            Pickable pickable = entity.getComponent(Pickable.class);
            String iconPath = pickable.getIcon();

            if(iconPath != null){
                icon = GameAssetManager.getInstance().get(iconPath);
            }else{
                icon = GameAssetManager.getInstance().redCross;
            }
            image.setDrawable(new TextureRegionDrawable(icon));

            if(pickable.getMaxStack() != 1){
                quantityLabel.setText(Integer.toString(pickable.getStackSize()));
                quantityLabel.setVisible(true);
            }else{
                quantityLabel.setVisible(false);
            }
        }else{
            image.setDrawable(new TextureRegionDrawable(GameAssetManager.getInstance().emptyTexture));
            quantityLabel.setVisible(false);
        }
    }
}
