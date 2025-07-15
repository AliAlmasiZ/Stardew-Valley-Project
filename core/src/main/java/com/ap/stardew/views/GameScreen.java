package com.ap.stardew.views;

import com.ap.stardew.StardewGame;
import com.ap.stardew.controllers.GameAssetManager;
import com.ap.stardew.controllers.GameMenuController;
import com.ap.stardew.controllers.PlayerController;
import com.ap.stardew.models.App;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {


    public static final float WORLD_WIDTH = 800;
    public static final float WORLD_HEIGHT = 450;

    private GameMenuController controller;
    private PlayerController playerController;
    private Player player;
    private Position playerPosition;
    private Sprite currentPlayerSprite;
    private Skin skin;

    //Renderers
    private Batch batch;
    private Stage gameStage;
    private Stage uiStage;
    private ShapeRenderer shapeRenderer;
    public OrthographicCamera camera;
    private Viewport gameView;

    //Map //TODO: this is just for test player movement and should be replace by PARSA
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private int mapWidth, mapHeight;
    private int tileWidth, tileHeight;

    private MapObject object;


    // Clock
    private Label timeLabel;
    private Label dateLabel;
    private Label moneyLabel;



    public GameScreen() {
        controller = new GameMenuController();
        player = App.getActiveGame().getCurrentPlayer();
        player.setSprite(new Sprite(new Texture("./Content(unpacked)/Characters/Bouncer.png")));
        currentPlayerSprite = player.getSprite();
        playerController = new PlayerController(this, player);
        playerPosition = player.getPosition();

        skin = GameAssetManager.getInstance().getSkin();

        //TODO
    }

    @Override
    public void show() {

        batch = StardewGame.getInstance().getBatch();
        camera = new OrthographicCamera();
        gameView = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();
        camera.setToOrtho(false, gameView.getWorldWidth(), gameView.getWorldHeight());
        gameStage = new Stage(gameView, batch);
        uiStage = new Stage(new ScreenViewport());

        //Map : TODO: this is just for test player movement and should be replace by PARSA
        map = new TmxMapLoader().load("./Content(unpacked)/Maps/Farm.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        renderer.setView(camera);
        tileHeight = map.getProperties().get("tileheight", Integer.class);
        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        mapWidth = map.getProperties().get("width", Integer.class) * tileWidth;
        mapHeight = map.getProperties().get("height", Integer.class)* tileHeight;

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(playerController);
        inputMultiplexer.addProcessor(gameStage);
        inputMultiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(inputMultiplexer);

        createClockUI();


    }

    @Override
    public void render(float delta) {
        controller.update(delta);
        playerController.update(delta);

        //TODO: make Vec2 to Vector2
        //center Camera:
        camera.position.x = currentPlayerSprite.getX() + currentPlayerSprite.getWidth() / 2f;
        camera.position.y = currentPlayerSprite.getY() + currentPlayerSprite.getHeight() / 2f;
        float cameraHalfWidth = camera.viewportWidth * camera.zoom / 2;
        float cameraHalfHeight = camera.viewportHeight * camera.zoom / 2;
        camera.position.x = Math.max(cameraHalfWidth, camera.position.x);
        camera.position.x = Math.min(mapWidth - cameraHalfWidth, camera.position.x);
        camera.position.y = Math.max(cameraHalfHeight, camera.position.y);
        camera.position.y = Math.min(mapHeight - cameraHalfHeight, camera.position.y);


        camera.update();

        renderer.setView(camera);
        renderer.render();
        batch.setProjectionMatrix(camera.combined);
        renderer.setView(camera);

        batch.begin();
        currentPlayerSprite.draw(batch);


        batch.end();



        gameStage.act(delta);
        gameStage.draw();

        uiStage.act(delta);
        uiStage.draw();

        updateClockUI();
    }

    @Override
    public void resize(int width, int height) {
        gameView.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        gameStage.dispose();
    }

    private void createClockUI() {
        Table table = new Table();
        table.setFillParent(true);

        timeLabel = new Label("", skin);

        table.add(timeLabel);
        table.left().top();
        uiStage.addActor(table);
    }

    private void updateClockUI() {
        timeLabel.setText(controller.getDateTime().message());
    }
}
