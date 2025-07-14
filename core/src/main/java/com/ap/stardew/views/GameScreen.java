package com.ap.stardew.views;

import com.ap.stardew.StardewGame;
import com.ap.stardew.controllers.GameMenuController;
import com.ap.stardew.controllers.PlayerController;
import com.ap.stardew.models.Account;
import com.ap.stardew.models.App;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.enums.Gender;
import com.ap.stardew.models.gameMap.MapRegion;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.records.GameStartingDetails;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen {
    private GameMenuController controller;
    private PlayerController playerController;
    private Player player;
    private Position playerPosition;
    private Sprite currentPlayerSprite;

    //Renderers
    private Batch batch;
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    public OrthographicCamera camera;
    private Viewport gameView;

    //Map //TODO: this is just for test player movement and should be replace by PARSA
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private int mapWidth;
    private int mapHeight;
    private MapObject object;



    public GameScreen() {
        controller = new GameMenuController();
        player = App.getActiveGame().getCurrentPlayer();
        currentPlayerSprite = player.getSprite();
        playerController = new PlayerController(this, player);
        playerPosition = player.getPosition();

        batch = StardewGame.getInstance().getBatch();
        camera = new OrthographicCamera();
        gameView = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        camera.setToOrtho(false, gameView.getWorldWidth(), gameView.getWorldHeight());
        stage = new Stage(gameView, batch);

        //Map : TODO: this is just for test player movement and should be replace by PARSA
        map = new TmxMapLoader().load("Content(unpacked)/Maps/Farm.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        renderer.setView(camera);
        mapWidth = map.getProperties().get("width", Integer.class) * 32;
        mapHeight = map.getProperties().get("height", Integer.class) * 32;

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(playerController);
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
        //TODO
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        //TODO: make Vec2 to Vector2
        if (currentPlayerSprite.getX() + (camera.viewportWidth / 2 * camera.zoom) < mapWidth &&
            currentPlayerSprite.getX() - (camera.viewportWidth / 2 * camera.zoom) > 0)
            camera.position.x = (currentPlayerSprite.getX() + currentPlayerSprite.getWidth()) / 2f;

        if (currentPlayerSprite.getY() + camera.viewportHeight / 2 < mapHeight &&
            currentPlayerSprite.getY() - camera.viewportHeight / 2 > 0)
            camera.position.y = (currentPlayerSprite.getY() + currentPlayerSprite.getHeight()) / 2f;


        camera.update();
        controller.update(delta);
        playerController.update(delta);

        renderer.render();
        batch.setProjectionMatrix(camera.combined);
        renderer.setView(camera);

        batch.begin();




        batch.end();



        stage.act(delta);
        stage.draw();
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
        stage.dispose();
    }
}
