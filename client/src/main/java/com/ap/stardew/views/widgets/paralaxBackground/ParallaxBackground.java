package com.ap.stardew.views.widgets.paralaxBackground;

import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.view.VariableDurationAnimation;
import com.ap.stardew.views.widgets.AnimatedDrawable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import java.util.ArrayList;
import java.util.Random;

public class ParallaxBackground extends Table {
    private static final Skin customSkin = GameAssetManager.getInstance().getCustomSkin();
    private static final String[] cloudImages = new String[]{"cloud1", "cloud2","cloud3"};

    private final Image mountains1;
    private final Image mountains2;
    private final Image bushesLeft;
    private final Image bushesRight;
    private final Image cloudsBigRight;
    private final Image cloudsBigLeft;
    private final GradientActor gradientActor;
    private final Image stars;
    private final Group cloudsGroup;
    private final Group birdsGroup;
    private VariableDurationAnimation<TextureRegion> birdAnim;

    private final ArrayList<MovingActor> clouds = new ArrayList<>();
    private final ArrayList<MovingActor> birds = new ArrayList<>();

    private float mountains1Speed = 0.3f;
    private float mountains2Speed = 0.4f;
    private float bushesSpeed = 1;

    float altitude;

    public ParallaxBackground(Stage stage) {
        setFillParent(true);
        stage.addActor(this);
        toBack();

        mountains1 = new Image(customSkin.getDrawable("mountains1"));
        mountains2 = new Image(customSkin.getDrawable("mountains2"));
        bushesLeft = new Image(customSkin.getDrawable("backgroundBushesLeft"));
        bushesRight = new Image(customSkin.getDrawable("backgroundBushesRight"));
        cloudsBigRight = new Image(customSkin.getDrawable("cloudsBig"));
        cloudsBigLeft = new Image(customSkin.getDrawable("cloudsBig"));
        stars = new Image(new Texture("Content/menuBackground/stars.png"));
        gradientActor = new GradientActor("rgba(239, 251, 255, 1) 0%, rgba(211, 253, 216, 1) 9%, rgba(110, 242, 230, 1) 16%, rgba(64, 136, 248, 1) 27%, rgba(21, 17, 82, 1) 50%, rgba(0, 0, 0, 1) 100%");

        mountains1.setScaling(Scaling.fill);
        mountains2.setScaling(Scaling.fill);
        bushesLeft.setScaling(Scaling.fill);
        bushesRight.setScaling(Scaling.fill);
        stars.setScaling(Scaling.fill);
        cloudsBigLeft.setScaling(Scaling.fill);
        cloudsBigRight.setScaling(Scaling.fill);

        bushesRight.setOrigin(Align.bottomRight);
        bushesLeft.setOrigin(Align.bottomLeft);
        mountains1.setOrigin(Align.bottom);
        mountains2.setOrigin(Align.bottom);

        bushesRight.scaleBy(0.3f);
        bushesLeft.scaleBy(0.3f);

        bushesRight.setAlign(Align.bottomRight);
        bushesLeft.setAlign(Align.bottomLeft);
        mountains2.setAlign(Align.bottom);
        mountains1.setAlign(Align.bottom);

        bushesLeft.toBack();
        bushesRight.toBack();
        mountains1.toBack();
        mountains2.toBack();

        gradientActor.setHeight(920);

        cloudsGroup = new Group();
        cloudsGroup.setPosition(0, 0);
        birdsGroup = new Group();
        birdsGroup.setPosition(0, 0);

        addActor(gradientActor);
        addActor(stars);
        addActor(cloudsBigRight);
        addActor(cloudsBigLeft);
        addActor(mountains1);
        addActor(cloudsGroup);
        addActor(mountains2);
        addActor(birdsGroup);
        addActor(bushesLeft);
        addActor(bushesRight);

        mountains1.setY(20);

        stars.setWidth(gradientActor.getWidth());
        stars.setY(gradientActor.getHeight() - stars.getHeight());

//        cloudsBigLeft.setWidth(300);
//        cloudsBigRight.setWidth(400);
        cloudsBigRight.setY(-30);
        cloudsBigLeft.setY(-50);

        generateClouds(10, 0, 100, 1500, 200);
        initBirdAnim();
        initBirds(3, 0, 50, 900, 200);
    }

    private void generateClouds(int num, float offsetX, float offsetY, float width, float height) {
        Random rand = new Random();
        int attempts;

        for (int i = 0; i < num; i++) {
            attempts = 0;
            while (true) {
                // pick a random position in the world
                float x = offsetX + rand.nextFloat() * width;
                float y = offsetY + rand.nextFloat() * height;
                Vector2 candidate = new Vector2(x, y);

                // check minimum distance from all existing clouds
                boolean tooClose = false;
                for (Image c : clouds) {
                    Vector2 pos = new Vector2(c.getX(), c.getY());
                    if (pos.dst(candidate) < 100) {
                        tooClose = true;
                        break;
                    }
                }

                // if far enough, accept this position
                if (!tooClose) {
                    MovingActor image = new MovingActor(customSkin.getDrawable(cloudImages[rand.nextInt(0, cloudImages.length)]), 2 + rand.nextFloat() * 10, .27f + rand.nextFloat() * (.4f - .27f)){
                        @Override
                        public void onExit() {
                            setPosition(ParallaxBackground.this.getWidth() + 5 + rand.nextFloat() * 100, 100 + rand.nextFloat() * (gradientActor.getHeight() - 50 - 100) + bushesRight.getY());
                        }
                    };
                    image.setPosition(x, y);
                    clouds.add(image);
                    cloudsGroup.addActor(image);
                    break;
                }

                // avoid infinite loops if world too small
                attempts++;
                if (attempts > 100) break;
            }
        }
    }

    private void initBirds(int num, float offsetX, float offsetY, float width, float height) {
        Random rand = new Random();
        int attempts;

        for (int i = 0; i < num; i++) {
            attempts = 0;
            while (true) {
                // pick a random position in the world
                float x = offsetX + rand.nextFloat() * width;
                float y = offsetY + rand.nextFloat() * height;
                Vector2 candidate = new Vector2(x, y);

                // check minimum distance from all existing clouds
                boolean tooClose = false;
                for (Image c : clouds) {
                    Vector2 pos = new Vector2(c.getX(), c.getY());
                    if (pos.dst(candidate) < 100) {
                        tooClose = true;
                        break;
                    }
                }

                // if far enough, accept this position
                if (!tooClose) {
                    AnimatedDrawable animatedDrawable = new AnimatedDrawable(birdAnim);
                    animatedDrawable.update(rand.nextFloat());
                    MovingActor image = new MovingActor(animatedDrawable, 15 + rand.nextFloat() * 10, .4f + rand.nextFloat() * (0.8f - .4f)){
                        @Override
                        public void onExit() {
                            setPosition(ParallaxBackground.this.getWidth() + 5 + rand.nextFloat() * 100, 30 + rand.nextFloat() * (200) + bushesRight.getY());
                        }
                    };
                    image.setPosition(x, y);
                    birds.add(image);
                    birdsGroup.addActor(image);
                    break;
                }

                // avoid infinite loops if world too small
                attempts++;
                if (attempts > 100) break;
            }
        }
    }

    private void initBirdAnim(){
        Texture texture = new Texture("Content/menuBackground/bird1.png");
        TextureRegion[] split = TextureRegion.split(texture, texture.getWidth() / 4, texture.getHeight())[0];


        TextureRegion[] textures = new TextureRegion[]{split[0], split[1], split[2], split[3], split[2], split[1]};
        float[] durations = new float[]{1f, .2f, .2f, .2f, .2f, .2f};
        birdAnim = new VariableDurationAnimation<>(textures, durations);
    }

    @Override
    public void layout() {
        super.layout();
        mountains1.setWidth(700);
        mountains2.setWidth(700);

        mountains1.setX(0);
        mountains2.setX(0);

        bushesLeft.setX(0);
        bushesRight.setX(getWidth() - bushesRight.getWidth());

        gradientActor.setWidth(getWidth());
        gradientActor.setX(0);

        stars.setWidth(gradientActor.getWidth());
        cloudsBigRight.setX(getWidth() - cloudsBigRight.getWidth() / 2f + 30);
        cloudsBigLeft.setX(-cloudsBigLeft.getWidth() / 2f);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
//        mountains1.draw(batch, parentAlpha);
//        mountains2.draw(batch, parentAlpha);
//        bushesLeft.draw(batch, parentAlpha);
//        bushesRight.draw(batch, parentAlpha);
    }

    public void move(float distance, float duration, Interpolation interpolation) {
        gradientActor   .addAction(Actions.moveBy(0, -distance * 0.2f, duration, interpolation));
        stars           .addAction(Actions.moveBy(0, -distance * 0.2f, duration, interpolation));
        cloudsBigRight  .addAction(Actions.moveBy(0, -distance * 0.25f, duration, interpolation));
        cloudsBigLeft   .addAction(Actions.moveBy(0, -distance * 0.25f, duration, interpolation));
        mountains1      .addAction(Actions.moveBy(0, -distance * 0.27f, duration, interpolation));
        for (MovingActor cloud : clouds) {
            cloud.addAction(Actions.moveBy(0, -distance * cloud.getVerticalSpeed(), duration, interpolation));
        }
        mountains2      .addAction(Actions.moveBy(0, -distance * 0.4f, duration, interpolation));
        for (MovingActor bird : birds) {
            bird.addAction(Actions.moveBy(0, -distance * bird.getVerticalSpeed(), duration, interpolation));
        }
        bushesLeft      .addAction(Actions.moveBy(0, -distance * 1, duration, interpolation));
        bushesRight     .addAction(Actions.moveBy(0, -distance * 1, duration, interpolation));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
