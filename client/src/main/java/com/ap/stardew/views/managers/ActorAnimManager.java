package com.ap.stardew.views.managers;

import com.ap.stardew.views.widgets.TransformWidgetWrapper;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.security.SecureRandom;

public class ActorAnimManager {
    private static SecureRandom random = new SecureRandom();
    public static void addRotateAction(TransformWidgetWrapper<? extends Actor> wrapper, float amount){
        if(wrapper.getActor() instanceof Button button){
            button.setTransform(true);
        }
        wrapper.addListener(new InputListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if(pointer != -1) return;
                wrapper.getActor().setOrigin(Align.center);
                float rot = 2 + (random.nextFloat() * amount);
                if(random.nextFloat() > 0.5f) rot = -rot;
                wrapper.getActor().addAction(
                    Actions.rotateBy(rot, 0.3f)
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if(pointer != -1) return;
                wrapper.getActor().setOrigin(Align.center);
                wrapper.getActor().addAction(
                    Actions.rotateTo(0, 0.3f)
                );
            }
        });
    }

    public static void addHorizontalElastic(TransformWidgetWrapper<? extends Actor> wrapper, boolean toRight){
        float dir = toRight ? 1 : -1;
        wrapper.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if(pointer != -1) return;
                wrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.moveBy(dir * 5, 0, 0.3f, Interpolation.exp5Out)
                    )
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if(pointer != -1) return;
                wrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.moveBy(-dir * 5, 0, 0.2f, Interpolation.swingOut)
                    )
                );
            }
        });
    }
    public static void addVerticalElastic(TransformWidgetWrapper<? extends Actor> wrapper, boolean toDown){
        float dir = toDown ? -1 : 1;
        wrapper.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if(pointer != -1) return;
                wrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.moveBy(0, dir * 5, 0.3f, Interpolation.exp5Out)
                    )
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if(pointer != -1) return;
                wrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.moveBy(0, -dir * 5, 0.2f, Interpolation.swingOut)
                    )
                );
            }
        });
    }

    public static void addHorizontalDrop(TransformWidgetWrapper<? extends Actor> wrapper, boolean toRight){
        float dir = toRight ? 1 : -1;
        wrapper.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if(pointer != -1) return;
                wrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.moveBy(dir * 5, 0, 0.3f, Interpolation.exp5Out)
                    )
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if(pointer != -1) return;
                wrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.moveBy(-dir * 5, 0, 0.2f, Interpolation.bounceOut)
                    )
                );
            }
        });
    }
    public static void addHorizontalDrop(TransformWidgetWrapper<? extends Actor> wrapper, boolean toRight, float amount){
        float dir = toRight ? 1 : -1;
        wrapper.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if(pointer != -1) return;
                wrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.moveBy(dir * amount, 0, 0.3f, Interpolation.exp5Out)
                    )
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if(pointer != -1) return;
                wrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.moveBy(-dir * amount, 0, 0.2f, Interpolation.bounceOut)
                    )
                );
            }
        });
    }

    //Wobble it, wiggle it, wobble it, wiggle it
    public static void addWiggle(TransformWidgetWrapper<? extends Actor> wrapper){
        if(wrapper.getActor() instanceof Button button){
            button.setTransform(true);
        }

        wrapper.addListener(new InputListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                wrapper.getActor().setOrigin(Align.center);
                wrapper.getActor().addAction(
                    Actions.sequence(
                        Actions.rotateBy(10, 0.1f, Interpolation.swingOut),
                        Actions.rotateBy(-20, 0.2f, Interpolation.swingOut),
                        Actions.rotateBy(10, 0.1f, Interpolation.swingOut)
                    )
                );
            }
        });
    }
}
