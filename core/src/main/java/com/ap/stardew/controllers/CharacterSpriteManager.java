package com.ap.stardew.controllers;

import com.ap.stardew.StardewGame;
import com.ap.stardew.models.enums.Direction;
import com.ap.stardew.models.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CharacterSpriteManager {
    public static final int FRAME_WIDTH = 16;
    public static final int FRAME_HEIGHT = 32;
    private static final float FRAME_DURATION = 0.15f;

    private final Texture baseTexture;
    private final Texture hairTexture;
    private final Texture shirtsTexture;
    private final Texture pantsTexture;

    private Direction lastDir = Direction.DOWN;


    private final Map<Direction, Animation<TextureRegion>> animations = new HashMap<>();

    private final TextureRegion[][] baseFrames;
    private final TextureRegion[][] hairFrames;
    private final TextureRegion[][] pantFrames;
    private final TextureRegion[][] shirtFrames;

    public CharacterSpriteManager() {
        GameAssetManager assets = GameAssetManager.getInstance();



        baseTexture = assets.get("Content(unpacked)/Characters/Farmer/farmer_base.png", Texture.class);
        hairTexture = assets.get("Content(unpacked)/Characters/Farmer/hairstyles.png", Texture.class);
        pantsTexture = assets.get("Content(unpacked)/Characters/Farmer/pants.png", Texture.class);
        shirtsTexture = assets.get("Content(unpacked)/Characters/Farmer/shirts.png", Texture.class);

        baseFrames = TextureRegion.split(baseTexture, FRAME_WIDTH, FRAME_HEIGHT);
        hairFrames = TextureRegion.split(hairTexture, FRAME_WIDTH, FRAME_HEIGHT);
        pantFrames = TextureRegion.split(pantsTexture, FRAME_WIDTH, FRAME_HEIGHT);
        shirtFrames = TextureRegion.split(shirtsTexture, 8, 8);


        loadAnimations();
    }


    private void loadAnimations() {
        loadWalkAnim(Direction.DOWN, 0, 0, 0, 0, new boolean[]{false, true});
        loadWalkAnim(Direction.UP, 2, 2, 2, 2, new boolean[]{false, true});
        loadWalkAnim(Direction.RIGHT, 1, 1, 1, 1, new boolean[]{false, true});
        loadWalkAnim(Direction.LEFT, 1, 1, 1, 1, new boolean[]{true, true});


    }

    synchronized private void loadWalkAnim(Direction direction, int baseBody, int shirt, int pants, int hair, boolean[] flip) {
        Array<TextureRegion> frames = new Array<>();


        for (int i = 0; i < 3; i++) {

            TextureRegion frame = loadTexture(new int[]{baseBody, i}, new int[]{baseBody, i + 6},
                new int[]{shirt, 0}, new int[]{pants, i}, new int[]{hair, 0});

            frame.flip(flip[0], flip[1]);

            frames.add(frame);
        }


        Animation<TextureRegion> animation = new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
        animations.put(direction, animation);
    }

    private TextureRegion loadTexture(int[] baseBody, int[] baseHand, int[] shirt, int[] pants, int[] hair) {
        SpriteBatch fbBatch = new SpriteBatch();
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, 16, 32, false);

        Matrix4 m = new Matrix4();
        m.setToOrtho2D(0, 0, fbo.getWidth(), fbo.getHeight());
        fbBatch.setProjectionMatrix(m);

        fbo.begin();


        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        fbBatch.begin();

        fbBatch.draw(baseFrames[baseBody[0]][baseBody[0]], 0, 0);//body
        fbBatch.draw(baseFrames[baseHand[0]][baseHand[1]], 0, 0); //hand
        fbBatch.draw(shirtFrames[shirt[0]][shirt[1]], 4, 8);

        fbBatch.setColor(Color.BROWN);
        fbBatch.draw(hairFrames[hair[0]][hair[1]],0 ,0);

        fbBatch.setColor(Color.BLUE);
        fbBatch.draw(pantFrames[pants[0]][pants[1]], 0, 0);
        fbBatch.setColor(Color.WHITE);

        fbBatch.end();
        fbo.end();

        TextureRegion frame = new TextureRegion(fbo.getColorBufferTexture());

        fbBatch.dispose();
        return frame;

    }

    public TextureRegion getFrame(float stateTime, Vector2 dir, Player.State state) {
        if (dir.x > 0) {
            lastDir = Direction.RIGHT;
        } else if (dir.x < 0) {
            lastDir = Direction.LEFT;
        } else if (dir.y > 0) {
            lastDir = Direction.UP;
        } else if (dir.y < 0) {
            lastDir = Direction.DOWN;
        }

        if(state.equals(Player.State.IDLE))
            return animations.get(lastDir).getKeyFrame(0, true);

        return animations.get(lastDir).getKeyFrame(stateTime, true);

    }

}
