package com.ap.stardew.views.widgets;

import com.ap.stardew.controllers.GameAssetManager;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Pickable;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.components.inventory.InventorySlot;
import com.ap.stardew.views.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;

public class InventorySlotWidget extends FramedImage{
    private final static DragAndDrop dragAndDrop = new DragAndDrop();
    private static Entity draggedEntity;
    private static InventorySlotWidget sourceSlot;
    private static InventorySlotWidget destSlot = null;
    private static DragEntity ghost;

    private static void startDrag(Entity entity, InventorySlotWidget sourceSlot, float offsetX, float offsetY){
        InventorySlotWidget.sourceSlot = sourceSlot;
        destSlot = sourceSlot;
        draggedEntity = entity;

        Drawable item = sourceSlot.getImage();

        ghost = new DragEntity(item, sourceSlot.getStage());
        ghost.setColor(1, 1, 1, 0.5f);
        ghost.setSize(sourceSlot.image.getImageWidth() , sourceSlot.image.getHeight());
        ghost.setOffsetX(-offsetX);
        ghost.setOffsetY(offsetY);
    }
    private static void endDrag(){

    }

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
        labelTable.add(quantityLabel);

        this.slot = slot;
        updateEntityIcon();

        addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if(pointer != -1) return;
                if(draggedEntity != null){
                    Entity entity = slot.getEntity();
                    if(entity == null) {
                        frame.setOrigin(frame.getWidth()/2f, frame.getHeight()/2f);
                        frame.addAction(
                            Actions.parallel(
                                Actions.scaleTo(0.9f, 0.9f, 0.2f, Interpolation.swingOut),
                                Actions.alpha(0.3f, 0.2f)
                            )
                        );
                        destSlot = InventorySlotWidget.this;
                        return;
                    }
                    if(!draggedEntity.isTheSameAs(entity)){
                        image.addAction(
                            Actions.sequence(
                                Actions.moveBy(1, 0, 0.05f, Interpolation.swingOut),
                                Actions.moveBy(-2, 0, 0.1f, Interpolation.swingOut),
                                Actions.moveBy(1, 0, 0.05f, Interpolation.swingOut)
                            )
                        );
                        destSlot = null;
                    }else{
                        destSlot = InventorySlotWidget.this;
                    }
                }else{
                    image.addAction(
                            Actions.moveTo(0, 2, 0.2f, Interpolation.swingOut)
                    );
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if(pointer != -1) return;
                if(draggedEntity != null){
                    destSlot = null;
                    frame.addAction(
                        Actions.parallel(
                            Actions.scaleTo(1f, 1f, 0.2f, Interpolation.swingOut),
                            Actions.alpha(1f, 0.2f)
                        )
                    );
                }else{
                    image.addAction(
                            Actions.moveTo(0, 0, 0.2f, Interpolation.swingOut)
                    );
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    if(entity == null || draggedEntity != null) return;

                    Pickable pickable = entity.getComponent(Pickable.class);
                    int quantity = pickable.getStackSize();
                    if (quantity > 1) {
                        ToolTip toolTip = new ToolTip(InventorySlotWidget.this);
                        Slider slider = new Slider(1, pickable.getStackSize() - 1, 1, false, GameAssetManager.getInstance().getCustomSkin());
                        Label label = new Label("1", GameAssetManager.getInstance().getCustomSkin(), "inventoryQuantity");
                        TextButton button = new TextButton("split", GameAssetManager.getInstance().getCustomSkin());
                        label.setAlignment(Align.right);

                        slider.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                label.setText((int)slider.getValue());
                            }
                        });

                        GlyphLayout glyphLayout = new GlyphLayout(label.getStyle().font, "9999");

                        toolTip.add(slider).width(50);
                        toolTip.add(label).center().width(glyphLayout.width).pad(2).row();
                        toolTip.add(button).colspan(2).expandX().center();
                        toolTip.show();

                        button.addListener(new ClickListener(){
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                System.out.println((int)slider.getValue());
                                toolTip.hide();
                                getStage().removeListener(this);

                                Entity split = slot.getEntity().getComponent(Pickable.class).split((int) slider.getValue());

                                startDrag(split, InventorySlotWidget.this, InventorySlotWidget.this.image.getWidth()/2,
                                    InventorySlotWidget.this.image.getHeight()/2);
                            }
                        });
                    }
                }else {
                    if(draggedEntity != null){
                        if(destSlot == null) return;

                        Entity leftOver = Inventory.addItemToSlot(draggedEntity, destSlot.slot);
                        if(sourceSlot != destSlot && leftOver != null){
                            Inventory.addItemToSlot(leftOver, sourceSlot.slot);
                        }

                        ghost.remove();
                        ghost = null;
                        draggedEntity = null;
                        sourceSlot = null;
                        destSlot = null;

                        frame.addAction(
                            Actions.parallel(
                                Actions.scaleTo(1f, 1f, 0.2f, Interpolation.swingOut),
                                Actions.alpha(1f, 0.2f)
                            )
                        );
                    }else{
                        Entity entity = slot.getEntity();

                        if(entity == null) return;

                        startDrag(InventorySlotWidget.this.slot.getEntity(), InventorySlotWidget.this, x, y);

                        sourceSlot.slot.setEntity(null);
                    }
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
        if(entity != null){
            quantityLabel.setText(entity.getComponent(Pickable.class).getStackSize());
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

    public void setQuantityScale(float scale){
        quantityLabel.setFontScale(scale);
    }

    public InventorySlot getSlot() {
        return slot;
    }
}
