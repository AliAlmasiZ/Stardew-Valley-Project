package com.ap.stardew.view;

import com.ap.stardew.models.enums.Direction;
import com.ap.stardew.models.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.util.HashMap;
import java.util.Map;



public class CharacterSpriteManager {
    public static final int FRAME_WIDTH = 16;
    public static final int FRAME_HEIGHT = 32;
    private static final float FRAME_DURATION = 0.2f;

    private final Texture baseTexture;
    private final Texture hairTexture;
    private final Texture shirtsTexture;
    private final Texture pantsTexture;

    private Direction lastDir = Direction.DOWN;

    private final Map<Player.Action, Map<Direction, VariableDurationAnimation<TextureRegion>>> animations = new HashMap<>();
    private final Map<Player.Action, Map<Direction, VariableDurationAnimation<ToolFrameInfo>>> toolFrames = new HashMap<>();

    private final TextureRegion[][] baseFrames;
    private final TextureRegion[][] hairFrames;
    private final TextureRegion[][] pantFrames;
    private final TextureRegion[][] shirtFrames;

    private final Map<Direction, Integer> baseHeight = Map.of(Direction.DOWN, 27, Direction.RIGHT, 28, Direction.UP, 27, Direction.LEFT, 28);
    private final Map<Texture, Pixmap> texturePixmapMap = new HashMap<>();


    public void exportAnimations(Map<Player.Action, Map<Direction, VariableDurationAnimation<TextureRegion>>> animations) {
        for (Player.Action action : animations.keySet()) {
            Map<Direction, VariableDurationAnimation<TextureRegion>> directionsMap = animations.get(action);

            // Create folder for action
            File actionDir = new File("exported_animations/" + action.name());
            if (!actionDir.exists()) {
                actionDir.mkdirs();
            }

            for (Direction direction : directionsMap.keySet()) {
                File directionDir = new File(actionDir, direction.name());
                if (!directionDir.exists()) {
                    directionDir.mkdirs();
                }
                VariableDurationAnimation<TextureRegion> animation = directionsMap.get(direction);

                // Get frames
                TextureRegion[] frames = animation.getKeyFrames(); // Assuming this method exists

                // Calculate combined width and max height for the row image
                int frameWidth = frames[0].getRegionWidth();
                int frameHeight = frames[0].getRegionHeight();
                int totalWidth = frameWidth * frames.length;
                int totalHeight = frameHeight;

                for (int i = 0; i < frames.length; i++) {
                    TextureRegion frame = frames[i];
                    Texture texture = frame.getTexture();

                    // Get pixel data from the texture region
                    if(!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
                    Pixmap framePixmap = flipPixmapVertically(texture.getTextureData().consumePixmap());
                    File outputFile = new File(directionDir, i + ".png");
                    PixmapIO.writePNG(Gdx.files.absolute(outputFile.getAbsolutePath()), framePixmap);
                    framePixmap.dispose();
                }
            }
        }
    }
    public Pixmap flipPixmapVertically(Pixmap original) {
        int width = original.getWidth();
        int height = original.getHeight();
        Pixmap flipped = new Pixmap(width, height, original.getFormat());

        for (int y = 0; y < height; y++) {
            // Copy each row from bottom to top
            flipped.drawPixmap(original,
                0, y,          // target x, y in flipped
                0, height - y - 1, // source x, y in original
                width, 1);     // width, height of row to copy
        }

        return flipped;
    }


    public CharacterSpriteManager() {
        GameAssetManager assets = GameAssetManager.getInstance();

        baseTexture = assets.get("Content(unpacked)/Characters/Farmer/farmer_base.png", Texture.class);
        hairTexture = assets.get("Content(unpacked)/Characters/Farmer/hairstyles.png", Texture.class);
        pantsTexture = assets.get("Content(unpacked)/Characters/Farmer/pants.png", Texture.class);
        shirtsTexture = assets.get("Content(unpacked)/Characters/Farmer/shirts.png", Texture.class);

        baseTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        hairTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pantsTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        shirtsTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        baseFrames = TextureRegion.split(baseTexture, FRAME_WIDTH, FRAME_HEIGHT);
        hairFrames = TextureRegion.split(hairTexture, FRAME_WIDTH, FRAME_HEIGHT);
        pantFrames = TextureRegion.split(pantsTexture, FRAME_WIDTH, FRAME_HEIGHT);
        shirtFrames = TextureRegion.split(shirtsTexture, 8, 8);

        loadAnimations();
    }


    public int pixelsFromBottomToTopOpaque(TextureRegion region) {
        Texture texture = region.getTexture();

        // Get texture data, prepare if needed
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        Pixmap pixmap = texture.getTextureData().consumePixmap();

        int startX = region.getRegionX();
        int startY = region.getRegionY();
        int width = region.getRegionWidth();
        int height = region.getRegionHeight();

        for (int y = 0; y < height; y++) { // scan bottom to top within the region
            for (int x = 0; x < width; x++) {
                int pixel = pixmap.getPixel(startX + x, startY + y);
                int alpha = (pixel >>> 24) & 0xFF;
                if (alpha != 0) {
                    pixmap.dispose();  // free pixmap resources
                    return height - y;
                }
            }
        }

        pixmap.dispose();
        return height; // no opaque pixel found
    }

    public int getHeadPsition(TextureRegion region) {
        Texture texture = region.getTexture();

        // Get texture data, prepare if needed
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        Pixmap pixmap = texture.getTextureData().consumePixmap();

        int startX = region.getRegionX();
        int startY = region.getRegionY();
        int width = region.getRegionWidth();
        int height = region.getRegionHeight();

        int counter = 0; // the third row of the character head is the back of the head i guess :?

        for (int y = 0; y < height; y++) { // scan bottom to top within the region
            for (int x = 0; x < width; x++) {
                int pixel = pixmap.getPixel(startX + x, startY + y);
                int alpha = (pixel >>> 24) & 0xFF;
                if (alpha != 0) {
                    if(counter == 2){
                        pixmap.dispose();
                        return x;
                    };
                    counter++;
                    break;
                }
            }
        }

        pixmap.dispose();
        return 0; // no opaque pixel found
    }

    private void loadAnimations() {
        loadAnim(Direction.DOWN , Player.Action.WALKING , "0@90, 1@60, 18@120, 1@60, 0@90, 2@60, 19@120, 2@60");
        loadAnim(Direction.UP   , Player.Action.WALKING , "12@90, 13@60, 22@120, 13@60, 12@90, 14@60, 23@120, 14@60");
        loadAnim(Direction.RIGHT, Player.Action.WALKING , "6@90, 21@140, 17@100, 6@90, 20@140, 11@100");
        loadAnim(Direction.LEFT , Player.Action.WALKING , "6@90, 21@140, 17@100, 6@90, 20@140, 11@100");

        loadAnim(Direction.DOWN , Player.Action.HARVESTING , "54@100, 55@100, 56@100, 57@100");
        loadAnim(Direction.UP   , Player.Action.HARVESTING , "62@100, 63@100, 64@100, 65@100");
        loadAnim(Direction.RIGHT, Player.Action.HARVESTING , "58@100, 59@100, 60@100, 61@100");
        loadAnim(Direction.LEFT , Player.Action.HARVESTING , "58@100, 59@100, 60@100, 61@100");

        loadAnim(Direction.DOWN , Player.Action.USING_TOOL , "66@150, 67@40, 68@40, 69@170, 70@75");
        loadAnim(Direction.UP   , Player.Action.USING_TOOL , "36@100, 37@40, 38@40, 63@220, 62@75");
        loadAnim(Direction.RIGHT, Player.Action.USING_TOOL , "48@100, 49@40, 50@40, 51@220, 52@75");
        loadAnim(Direction.LEFT , Player.Action.USING_TOOL , "48@100, 49@40, 50@40, 51@220, 52@75");

        loadAnim(Direction.DOWN , Player.Action.PASSING_OUT , "16@1000, 0@500, 16@1000, 4@200, 5@6000");
        loadAnim(Direction.UP   , Player.Action.PASSING_OUT , "16@1000, 0@500, 16@1000, 4@200, 5@6000");
        loadAnim(Direction.RIGHT, Player.Action.PASSING_OUT , "16@1000, 0@500, 16@1000, 4@200, 5@6000");
        loadAnim(Direction.LEFT , Player.Action.PASSING_OUT , "16@1000, 0@500, 16@1000, 4@200, 5@6000");

        loadAnim(Direction.DOWN , Player.Action.USING_SCYTHE , "24@55, 25@45, 26@25, 27@25, 28@25, 29@25");
        loadAnim(Direction.UP   , Player.Action.USING_SCYTHE , "36@55, 37@45, 38@25, 39@25, 40@25, 41@25");
        loadAnim(Direction.RIGHT, Player.Action.USING_SCYTHE , "30@55, 31@45, 32@25, 33@25, 34@25, 35@25");
        loadAnim(Direction.LEFT , Player.Action.USING_SCYTHE , "30@55, 31@45, 32@25, 33@25, 34@25, 35@25");

        loadAnim(Direction.DOWN , Player.Action.WATERING , "54@75, 55@100, 25@500");
        loadAnim(Direction.UP   , Player.Action.WATERING , "62@75, 63@100, 46@500");
        loadAnim(Direction.RIGHT, Player.Action.WATERING , "58@75, 59@100, 45@500");
        loadAnim(Direction.LEFT , Player.Action.WATERING , "58@75, 59@100, 45@500");
    }

    synchronized private void loadAnim(Direction direction, Player.Action action, String data) {
        Array<TextureRegion> frames = new Array<>();
        Array<Float> durations = new Array<>();

        int hair = 0, shirt = 0;
        boolean[] flip = new boolean[2];

        switch (direction){
            case DOWN -> {
                hair = 0;
                shirt = 0;
                flip = new boolean[]{false, true};
            }
            case UP -> {
                hair = 2;
                shirt = 3;
                flip = new boolean[]{false, true};
            }
            case RIGHT ->{
                hair = 1;
                shirt = 1;
                flip = new boolean[]{false, true};
            }
            case LEFT ->{
                hair = 1;
                shirt = 1;
                flip = new boolean[]{true, true};
            }
        }

        for (String entry : data.split(",")) {
            entry = entry.trim();
            String[] parts = entry.split("@");

            int index = Integer.parseInt(parts[0]);
            float duration = Integer.parseInt(parts[1]) / 1000f;

            int row = index / 6;
            int column = index % 6;

            TextureRegion frame = loadTexture(direction, action, new int[]{row, column}, new int[]{row, column + 6},
                new int[]{shirt, 0}, new int[]{row, column}, new int[]{hair, 0});
            frame.flip(flip[0], flip[1]);

            frames.add(frame);
            durations.add(duration * 2);
        }


        float[] durationsArray = new float[durations.size];
        TextureRegion[] framesArray = new TextureRegion[frames.size];

        for (int i = 0; i < durationsArray.length; i++) {
            durationsArray[i] = durations.get(i);
        }
        for (int i = 0; i < framesArray.length; i++) {
            framesArray[i] = frames.get(i);
        }

        VariableDurationAnimation<TextureRegion> animation = new VariableDurationAnimation<>(framesArray, durationsArray);
        animations.putIfAbsent(action, new HashMap<>());
        animations.get(action).put(direction, animation);
    }

    private TextureRegion loadTexture(Direction direction, Player.Action action, int[] baseBody, int[] baseHand, int[] shirt, int[] pants, int[] hair) {
        SpriteBatch fbBatch = new SpriteBatch();
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, 16, 32, false);
        fbo.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Matrix4 m = new Matrix4();
        m.setToOrtho2D(0, 0, fbo.getWidth(), fbo.getHeight());
        fbBatch.setProjectionMatrix(m);

        fbo.begin();

        int hairY = pixelsFromBottomToTopOpaque(baseFrames[baseBody[0]][baseBody[1]]) - baseHeight.get(direction);
        int hairX = 0;
        if((direction == Direction.RIGHT || direction == Direction.LEFT)){
            hairX = getHeadPsition(baseFrames[baseBody[0]][baseBody[1]]) - 3;
        }

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        fbBatch.begin();

        fbBatch.draw(baseFrames[baseBody[0]][baseBody[1]], 0, 0);//body
        fbBatch.setColor(Color.BLUE);
        fbBatch.draw(pantFrames[pants[0]][pants[1]], 0, 0);
        fbBatch.setColor(Color.WHITE);
        fbBatch.draw(shirtFrames[shirt[0]][shirt[1]], 4, hairY + 10);
        fbBatch.setColor(Color.BROWN);
        fbBatch.draw(hairFrames[hair[0]][hair[1]], hairX,hairY - 1);
        fbBatch.setColor(Color.WHITE);

        fbBatch.draw(baseFrames[baseHand[0]][baseHand[1]], 0, 0); //hand
        fbBatch.setColor(Color.WHITE);

        fbBatch.end();

        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, fbo.getWidth(), fbo.getHeight());

        fbo.end();
        fbo.dispose();
        fbBatch.dispose();

        Texture texture = new Texture(pixmap);

        texture.getTextureData().consumePixmap();

        return new TextureRegion(texture);
    }

    public TextureRegion getFrame(float stateTime, Vector2 dir, Player.Action action) {
        if (dir.x > 0) {
            lastDir = Direction.RIGHT;
        } else if (dir.x < 0) {
            lastDir = Direction.LEFT;
        } else if (dir.y > 0) {
            lastDir = Direction.UP;
        } else if (dir.y < 0) {
            lastDir = Direction.DOWN;
        }

        if(action.equals(Player.Action.IDLE))
            return animations.get(Player.Action.WALKING).get(lastDir).getKeyFrame(0, true);

        return animations.get(action).get(lastDir).getKeyFrame(stateTime, true);
    }

    public float getAnimationDuration(Vector2 dir, Player.Action action){
        return animations.get(action).get(lastDir).getDuration();
    }
}
