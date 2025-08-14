package com.ap.stardew.views.dialogs;

import com.ap.stardew.app.GameController;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.reaction.Emoji;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.managers.EmojiSpriteManager;
import com.ap.stardew.views.GameScreen;
import com.ap.stardew.views.widgets.AnimatedDrawable;
import com.ap.stardew.views.widgets.EmojiImage;
import com.ap.stardew.views.widgets.InGameDialog;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import java.util.ArrayList;

public class ReactionEditDialog extends InGameDialog {
    private static Emoji pendingEmojiSelect = null;
    private final ArrayList<EmojiImage> selectedEmojis = new ArrayList<>();
    private final ArrayList<EmojiImage> allEmojisMap = new ArrayList<>();
    private final Player player;

    public ReactionEditDialog(Player player, GameScreen gameScreen){
        super(gameScreen.getUiStage());

        this.player = player;
        Skin customSkin = GameAssetManager.getInstance().getCustomSkin();

        Table selectedTable = new Table();
        Table allEmojisTable = new Table();

        selectedTable.defaults().space(1);
        allEmojisTable.defaults().space(1);

        selectedTable.setBackground(customSkin.getDrawable("1122"));
        allEmojisTable.setBackground(customSkin.getDrawable("0011"));

        int i = 0;
        for (Emoji emoji : player.getEmojis()) {
            EmojiImage image = new EmojiImage(emoji);
            image.setOrigin(Align.center);
            selectedEmojis.add(image);
            Table wrapper = new Table(){
                @Override
                public Actor hit(float x, float y, boolean touchable) {
                    if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) return null;
                    else return this;
                }
            };
            image.setScaling(Scaling.fit);
            image.setOrigin(Align.center);
            wrapper.add(image).grow();
            selectedTable.add(wrapper).growX();

            image.addAction(
                Actions.sequence(
                    Actions.moveBy(0, 10),
                    Actions.alpha(0),
                    Actions.delay(i * 0.1f),
                    Actions.parallel(
                        Actions.moveBy(0, -10, 0.5f, Interpolation.bounceOut),
                        Actions.alpha(1f, 0.5f)
                    ),
                    Actions.forever(
                        Actions.sequence(
                            Actions.scaleTo(1.1f, 1.1f, 0.3f, Interpolation.smoother),
                            Actions.scaleTo(1, 1, 0.3f, Interpolation.smoother)
                        )
                    )
                )
            );

            wrapper.addListener(new ClickListener(){
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if(pointer != -1) return;
                    if(pendingEmojiSelect == null) return;
                    image.addAction(
                        Actions.rotateBy(30, 0.5f, Interpolation.exp5Out)
                    );
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    if(pointer != -1) return;
                    if(pendingEmojiSelect == null) return;
                    image.addAction(
                        Actions.rotateBy(-30, 0.5f, Interpolation.swingOut)
                    );
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(pendingEmojiSelect != null){
                        int index = player.getEmojis().indexOf(image.emoji);
                        image.setDrawable(new AnimatedDrawable(EmojiSpriteManager.getInstance().getAnimation(pendingEmojiSelect)));
                        player.getEmojis().set(index, pendingEmojiSelect);
                        for (EmojiImage emojiImage : allEmojisMap) {
                            if(emojiImage.emoji == pendingEmojiSelect){
                                emojiImage.addAction(Actions.moveBy(0, -4, 0.3f, Interpolation.bounceOut));
                            }
                        }
                        image.emoji = pendingEmojiSelect;
                        pendingEmojiSelect = null;
                        updateAllEmojiAlphas();

                        int i = 0;
                        for (Image selectedEmoji : selectedEmojis) {
                            selectedEmoji.addAction(
                                Actions.sequence(
                                    Actions.parallel(
                                        Actions.scaleTo(1, 1, 0.3f, Interpolation.smoother),
                                        Actions.rotateTo(0, 0.5f, Interpolation.swingOut)
                                    ),
                                    Actions.delay(i * 0.1f),
                                    Actions.forever(
                                        Actions.sequence(
                                            Actions.scaleTo(1.1f, 1.1f, 0.3f, Interpolation.smoother),
                                            Actions.scaleTo(1, 1, 0.3f, Interpolation.smoother)
                                        )
                                    )
                                )
                            );
                            i++;
                            GameController.sendSelectedEmojiUpdate(player);
                        }

                    }
                }
            });

            i++;
        }

        i = 0;
        for (Emoji emoji : Emoji.values()){
            EmojiImage image = new EmojiImage(emoji);
            image.setOrigin(Align.center);
            allEmojisMap.add(image);
            Table wrapper = new Table(){
                @Override
                public Actor hit(float x, float y, boolean touchable) {
                    if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) return null;
                    else return this;
                }
            };
            image.setScaling(Scaling.fit);
            wrapper.add(image).grow();
            allEmojisTable.add(wrapper).growX();

            image.addAction(
                Actions.sequence(
                    Actions.moveBy(0, 5),
                    Actions.alpha(0),
                    Actions.delay(i * 0.1f),
                    Actions.parallel(
                        Actions.moveBy(0, -5, 0.5f, Interpolation.smoother),
                        Actions.alpha(player.getEmojis().contains(emoji) ? 0.5f : 1f, 0.5f)
                    )
                )
            );

            wrapper.addListener(new ClickListener(){
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if(pointer != -1) return;
                    if(player.getEmojis().contains(emoji)) return;
                    if(pendingEmojiSelect != null) return;
                    image.addAction(
                        Actions.moveBy(0, 4, 0.3f, Interpolation.swingOut)
                    );
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    if(pointer != -1) return;
                    if(player.getEmojis().contains(emoji)) return;
                    if(pendingEmojiSelect != null) return;
                    image.addAction(
                        Actions.moveBy(0, -4, 0.3f, Interpolation.bounceOut)
                    );
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(player.getEmojis().contains(emoji)) return;
                    if(pendingEmojiSelect != null) return;

                    pendingEmojiSelect = emoji;

                    int i = 0;
                    for (Image selectedEmoji : selectedEmojis) {
                        selectedEmoji.setOrigin(Align.center);
                        selectedEmoji.clearActions();
                        selectedEmoji.addAction(
                            Actions.sequence(
                                Actions.delay(i * 0.1f),
                                Actions.scaleTo(1.1f, 1.1f, 0.3f, Interpolation.swingOut)
                            )
                        );

                        i++;
                    }

                    for (EmojiImage value : allEmojisMap) {
                        value.addAction(
                            Actions.alpha(0.5f, 0.3f, Interpolation.smoother)
                        );
                    }
                }
            });
            i++;

            if(i % 6 == 0){
                allEmojisTable.row();
            }
        }

        add(selectedTable).growX().row();
        add(allEmojisTable).growX().row();

        TextButton back = new TextButton("back", customSkin){
            @Override
            public void act(float delta) {
                super.act(delta);
                setPosition((ReactionEditDialog.this.getWidth() - getWidth()) / 2f, -getHeight() - 2);
            }
        };
        back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                gameScreen.openReactionMenu(player);
            }
        });
        addActor(back);

        showCloseButton(false);
    }

    private void updateAllEmojiAlphas(){
        for (EmojiImage emojiImage : allEmojisMap) {
            if(player.getEmojis().contains(emojiImage.emoji)){
                emojiImage.addAction(
                    Actions.alpha(0.5f, 0.3f, Interpolation.smoother)
                );
            }else {
                emojiImage.addAction(
                    Actions.alpha(1f, 0.3f, Interpolation.smoother)
                );
            }
        }
    }
}
