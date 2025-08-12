package com.ap.stardew.views.dialogs;


import com.ap.stardew.app.GameController;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.reaction.Emoji;
import com.ap.stardew.models.player.reaction.Reaction;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.GameScreen;
import com.ap.stardew.views.widgets.EmojiImage;
import com.ap.stardew.views.widgets.InGameDialog;
import com.ap.stardew.views.widgets.LabelMessage;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;

public class ReactionDialog extends InGameDialog {
    public ReactionDialog(Player player, GameScreen gameScreen) {
        super(gameScreen.getUiStage());

        Skin customSkin = GameAssetManager.getInstance().getCustomSkin();

        Table emojiTable = new Table();
        Table textTable = new Table();

        emojiTable.setBackground(customSkin.getDrawable("1122"));
        textTable.setBackground(customSkin.getDrawable("0011"));


        int i = 0;
        for (Emoji emoji : player.getEmojis()) {
            EmojiImage image = new EmojiImage(emoji);
            Table wrapper = new Table(){
                @Override
                public Actor hit(float x, float y, boolean touchable) {
                    if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) return null;
                    else return this;
                }
            };
            image.setScaling(Scaling.fit);
            wrapper.add(image).grow();
            emojiTable.add(wrapper).growX();

            image.addAction(
                Actions.sequence(
                    Actions.moveBy(0, 10),
                    Actions.alpha(0),
                    Actions.delay(i * 0.1f),
                    Actions.parallel(
                        Actions.moveBy(0, -10, 0.5f, Interpolation.bounceOut),
                        Actions.alpha(1, 0.5f)
                    )
                )
            );

            wrapper.addListener(new ClickListener(){
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if(pointer != -1) return;
                    image.addAction(
                        Actions.moveBy(0, 4, 0.3f, Interpolation.swingOut)
                    );
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    if(pointer != -1) return;
                    image.addAction(
                        Actions.moveBy(0, -4, 0.3f, Interpolation.bounceOut)
                    );
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    player.setCurrentReaction(new Reaction(emoji, 5f));
                    GameController.sendReactionUpdate(player);
                    hide();
                }
            });

            i++;
        }

        Image editButton = new Image(customSkin.getDrawable("editIcon")) {
            @Override
            public void act(float delta) {
                super.act(delta);
                setPosition(ReactionDialog.this.getPrefWidth() + 2, 2);
            }
        };

        editButton.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if(pointer != -1) return;
                editButton.addAction(Actions.alpha(0.5f, 0.3f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if(pointer != -1) return;
                editButton.addAction(Actions.alpha(1f, 0.3f));
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                gameScreen.openReactionEditMenu(player);
            }
        });

        addActor(editButton);

        TextField textField = new TextField("", customSkin);
        TextButton button = new TextButton("ok", customSkin);
        textTable.add(textField);
        textTable.add(button);

        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(textField.getText().isEmpty()){
                    new LabelMessage(textTable, "should'nt be empty", customSkin).show();
                    return;
                }
                if(textField.getText().length() > 10){
                    new LabelMessage(textTable, "too long", customSkin).show();
                    return;
                }

                player.setCurrentReaction(new Reaction(textField.getText(), 5f));
                GameController.sendReactionUpdate(player);
                hide();
            }
        });

        add(emojiTable).growX().row();
        add(textTable).growX();
    }
}
