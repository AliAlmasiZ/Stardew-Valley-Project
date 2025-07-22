package com.ap.stardew.views;

import com.ap.stardew.StardewGame;
import com.ap.stardew.controllers.GameAssetManager;
import com.ap.stardew.controllers.GameMenuController;
import com.ap.stardew.controllers.PlayerController;
import com.ap.stardew.models.App;
import com.ap.stardew.models.ClockActor;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.animal.Animal;
import com.ap.stardew.models.animal.AnimalType;
import com.ap.stardew.models.animal.FishingMiniGame;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.Pickable;
import com.ap.stardew.models.entities.components.PositionComponent;
import com.ap.stardew.models.entities.components.Sellable;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.enums.FishMovement;
import com.ap.stardew.models.enums.ProductQuality;
import com.ap.stardew.models.enums.SkillType;
import com.ap.stardew.models.entities.components.Renderable;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.Skill;
import com.ap.stardew.records.EntityResult;
import com.ap.stardew.views.widgets.InGameDialog;
import com.ap.stardew.views.widgets.InventoryGrid;
import com.ap.stardew.records.Result;
import com.ap.stardew.views.widgets.TabWidget;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;

public class GameScreen extends AbstractScreen {
    public static final float WORLD_WIDTH = 800;
    public static final float WORLD_HEIGHT = 450;
    public static final float ERROR_MESSAGE_DELAY = 5;

    private GameMenuController controller;
    private PlayerController playerController;
    private Player player;
    private Sprite currentPlayerSprite;
    private Skin skin;
    private Skin customSkin;

    //Renderers
    private Batch batch;
    private Stage gameStage;
    private Stage minigameStage;
    private ShapeRenderer shapeRenderer;
    public OrthographicCamera camera;
    private Viewport gameView;

    //Map //TODO: this is just for test player movement and should be replace by PARSA
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private int mapWidth, mapHeight;
    private int tileWidth, tileHeight;

    // ui
    private final Stack stack;

    // Clock
    private ClockActor clockActor;

    public GameScreen() {
        super(2.5f);
        stack = new Stack();
        rootTable.add(stack).grow();

        controller = new GameMenuController();
        player = App.getActiveGame().getCurrentPlayer();
        currentPlayerSprite = player.getSprite();
        playerController = new PlayerController(this, player);

        skin = GameAssetManager.getInstance().getSkin();
        customSkin = GameAssetManager.getInstance().getCustomSkin();

        //TODO: remove it later
        //**************************************
        controller.cheatGiveItem("Training Rod", 1);
        controller.cheatGiveItem("Hay", 500);
        controller.cheatGiveItem("Axe", 1);
        controller.cheatGiveItem("Hay", 500);
        controller.cheatGiveItem("Hay", 500);
        controller.cheatAddSkill("fishing", 200);
        controller.cheatAddSkill("fishing", 200);
        controller.cheatAddSkill("fishing", 200);
        controller.cheatAddSkill("fishing", 200);
        controller.cheatAddSkill("fishing", 200);


        Player player = App.getActiveGame().getCurrentPlayer();
        Animal animal1 = new Animal(AnimalType.Cow, "Arteta");
        System.out.println(EntityPlacementSystem.placeEntity(animal1, player.getPosition()).message());
        player.getAnimals().add(animal1);
        //**************************************


        //TODO
    }

    @Override
    public void show() {
        super.show();
        batch = StardewGame.getInstance().getBatch();
        camera = new OrthographicCamera();
        gameView = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();
        camera.setToOrtho(false, gameView.getWorldWidth(), gameView.getWorldHeight());

        gameStage = new Stage(gameView, batch);
        minigameStage = new Stage(new ScreenViewport());
//        uiStage = new Stage(new ScreenViewport());
//        uiStage.getCamera().viewportWidth = uiStage.getCamera().viewportWidth / Gdx.graphics.getPpiX() * 50 / UI_SCALING;
//        uiStage.getCamera().viewportHeight = uiStage.getCamera().viewportHeight / Gdx.graphics.getPpiY() * 50 / UI_SCALING;
        camera.update();

        //Map : TODO: this is just for test player movement and should be replace by PARSA
        map = new TmxMapLoader().load("./Content(unpacked)/Maps/TestMap.tmx");
        System.out.println(map.getProperties());

        renderer = new OrthogonalTiledMapRenderer(map);
        renderer.setView(camera);
        renderer.getBatch().enableBlending();

        tileHeight = map.getProperties().get("tileheight", Integer.class);
        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        mapWidth = map.getProperties().get("width", Integer.class) * tileWidth;
        mapHeight = map.getProperties().get("height", Integer.class) * tileHeight;

        setGameInput();

        // Create clock
        clockActor = new ClockActor();
        Table clockTable = new Table();
        clockTable.top().right();
        clockTable.add(clockActor).pad(10);
        stack.add(clockTable);
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

        ArrayList<Integer> backLayers = new ArrayList<>();
        ArrayList<Integer> frontLayers = new ArrayList<>();
        for (int i = 0; i < App.getActiveGame().getActiveMap().getMapData().getLayers().size(); i++) {
            MapLayer mapLayer = App.getActiveGame().getActiveMap().getMapData().getLayers().get(i);
            if (mapLayer.getName().contains("Back") || mapLayer.getName().contains("Buildings")) {
                backLayers.add(i);
            } else {
                frontLayers.add(i);
            }
        }
        int[] backLayerIndices = new int[backLayers.size()];
        for (int i = 0; i < backLayers.size(); i++) {
            backLayerIndices[i] = backLayers.get(i);
        }

        int[] frontLayerIndices = new int[frontLayers.size()];
        for (int i = 0; i < frontLayers.size(); i++) {
            frontLayerIndices[i] = frontLayers.get(i);
        }

        renderer.setMap(App.getActiveGame().getActiveMap().getMapData());
        renderer.setView(camera);
        renderer.render(backLayerIndices);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Entity entity : App.getActiveGame().getActiveMap().getEntitiesWithComponent(Renderable.class)) {
            Sprite sprite = entity.getComponent(Renderable.class).getRenderingSprite(delta);
            if (sprite != null) {
                sprite.setPosition((float) entity.getComponent(PositionComponent.class).getX(), (float) entity.getComponent(PositionComponent.class).getY());
                sprite.draw(batch);
            }
        }
        batch.end();

        renderer.render(frontLayerIndices);
        gameStage.act(delta);
        gameStage.draw();

        uiStage.act(delta);
        uiStage.draw();

        minigameStage.act(delta);
        minigameStage.draw();

        clockActor.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        gameStage.getViewport().update(width, height, true);
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

    public void showTemporaryMessage(String message, float duration, Color color) {
        Label label = new Label(message, skin);
        label.setPosition(
            (uiStage.getWidth() - label.getWidth()) / 2f,
            (uiStage.getHeight() - label.getHeight() - 50)
        );

        label.setColor(color);
        label.scaleBy(2);
        label.getColor().a = 0; // start invisible

        // Sequence of actions: fade in → wait → fade out → remove
        label.addAction(Actions.sequence(
            Actions.fadeIn(0.5f),
            Actions.delay(duration),
            Actions.fadeOut(0.5f),
            Actions.removeActor()
        ));

        uiStage.addActor(label);
    }

    public void showTemporaryMessage(String message, float duration, Color color, float x, float y, float scale) {
        Label label = new Label(message, skin);
        label.setPosition(
            x, y
        );

        label.setColor(color);
        label.scaleBy(scale);
        label.getColor().a = 0; // start invisible

        // Sequence of actions: fade in → wait → fade out → remove
        label.addAction(Actions.sequence(
            Actions.fadeIn(0.5f),
            Actions.delay(duration),
            Actions.fadeOut(0.5f),
            Actions.removeActor()
        ));

        uiStage.addActor(label);
    }


    public void startFishing() {
        //TODO: Ilia doesnt know how to get equipped tool
        EntityResult entityResult = controller.fishing("Training Rod"); // This is just for test

        if (entityResult.entity() == null) {
            System.out.println(entityResult.message());
            showTemporaryMessage(entityResult.message(), ERROR_MESSAGE_DELAY, Color.RED);
            return;
        }

        FishingMiniGame fishingMiniGame = new FishingMiniGame(this, FishMovement.getRandomFishMovement(), entityResult.entity());
        minigameStage.addActor(fishingMiniGame);
        Gdx.input.setInputProcessor(minigameStage);
    }

    public void openTestDialog() {
        InGameDialog dialog = new InGameDialog(uiStage);

        TabWidget tabWidget = new TabWidget();

        Table table = new Table();
        InventoryGrid inventoryGrid = new InventoryGrid(player.getComponent(Inventory.class), 10);
        inventoryGrid.top();
        table.add(inventoryGrid).grow();

        Table table2 = new Table();
        table2.add(new Label("test3", skin)).row();
        table2.add(new Label("test4", skin));

        Table table3 = new Table();
        table3.add(new Label("test3", skin)).row();
        table3.add(new Label("test4", skin));

        tabWidget.addTab(table, new TextureRegionDrawable(GameAssetManager.getInstance().inventoryIcon));
        tabWidget.addTab(table2, new TextureRegionDrawable(GameAssetManager.getInstance().buildMenuIcon));
        tabWidget.addTab(table3, new TextureRegionDrawable(GameAssetManager.getInstance().mapIcon));

//        dialog.getContentTable().add(tabWidget).fill().size(200, 130);
        dialog.add(tabWidget).size(230, 130).fill();

        dialog.show();
    }

    public void stopFishing(FishingMiniGame fishingMiniGame) {
        minigameStage.clear();
        setGameInput();

        if (!fishingMiniGame.isSuccessful()) {
            showTemporaryMessage("You lost the mini game!\n Better luck next time!", ERROR_MESSAGE_DELAY, Color.RED);
            return;
        }

        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        Skill skill = player.getSkill(SkillType.FISHING);
        Inventory inventory = currentPlayer.getComponent(Inventory.class);

        StringBuilder message = new StringBuilder();
        Color color = Color.GREEN;
        Entity fish = fishingMiniGame.getFish();

        if (fishingMiniGame.isPerfect()) {
            skill.addExperience(40);
            color = Color.GOLD;
            ProductQuality productQuality = fish.getComponent(Sellable.class).getProductQuality();
            switch (productQuality) {
                case SILVER -> {
                    fish.getComponent(Sellable.class).setProductQuality(ProductQuality.GOLD);
                }
                case GOLD -> {
                    fish.getComponent(Sellable.class).setProductQuality(ProductQuality.IRIDIUM);
                }
            }
            message.append("\t PERFECT!\t\n");
        }

        message.append("You caught ").append(fish.getComponent(Pickable.class).getStackSize())
            .append(" ").append(fish.getEntityName()).append(" of quality ").append(fish.getComponent(Sellable.class).getProductQuality().name());
        skill.addExperience(10);
        inventory.addItem(fish);

        showTemporaryMessage(message.toString(), 7, color);
    }

    private void setGameInput() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(playerController);
        inputMultiplexer.addProcessor(gameStage);
        inputMultiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void openAnimalMenu(Animal animal) {
        InGameDialog dialog = new InGameDialog(uiStage);

        TabWidget tabWidget = new TabWidget();

        Table infoTab = new Table();
        Label animalLabel = new Label(animal.getDetail(), customSkin);
        animalLabel.setColor(Color.WHITE);
        infoTab.add(new Label(animal.getDetail(), customSkin)).row();

        Table buttonTab = new Table();

        TextButton feedButton = new TextButton("Feed", customSkin);
        TextButton petButton = new TextButton("Pet", customSkin);
        TextButton collectProduceButton = new TextButton("Collect produce", customSkin);
        TextButton sellAnimalButton = new TextButton("Sell animal", customSkin);
        /*TODO: check if in house*/
        TextButton shepherdAnimalButton = new TextButton("Shephered Animal", customSkin);
        TextButton exitButton = new TextButton("Exit", customSkin);

        buttonTab.add(feedButton).growX().row();
        buttonTab.add(petButton).growX().row();
        buttonTab.add(collectProduceButton).growX().row();
        buttonTab.add(sellAnimalButton).growX().row();
        buttonTab.add(shepherdAnimalButton).growX().row();
        buttonTab.add(exitButton).growX().row();
//
        tabWidget.addTab(infoTab, new TextureRegionDrawable(GameAssetManager.getInstance().inventoryIcon));
        tabWidget.addTab(buttonTab, new TextureRegionDrawable(GameAssetManager.getInstance().inventoryIcon));

        dialog.add(tabWidget).fill().grow();

        dialog.show();
        Gdx.input.setInputProcessor(uiStage);

        feedButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Result result = controller.feedHay(animal.getName());
                if (!result.isSuccessful()) {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                } else {
                    animal.getComponent(Renderable.class).setStatue(Renderable.Statue.EATING, 5);

                }
                setGameInput();
                dialog.remove();
            }
        });

        petButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Result result = controller.pet(animal.getName());
                if (!result.isSuccessful()) {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                } else {
                    animal.getComponent(Renderable.class).setStatue(Renderable.Statue.PET, 5);
                }
                setGameInput();
                dialog.remove();
            }
        });

        collectProduceButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Result result = controller.collectProduces(animal.getName());
                if (!result.isSuccessful()) {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                } else {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.GREEN);
                }
                setGameInput();
                dialog.remove();
            }
        });

        sellAnimalButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Result result = controller.sellAnimal(animal.getName());
                if (!result.isSuccessful()) {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                } else {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.GREEN);
                }
                setGameInput();
                dialog.remove();
            }
        });

        shepherdAnimalButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.remove();
                setGameInput();
            }
        });


    }

    public void openAnimalMovementMenu(Animal animal) {
        Dialog dialog = new Dialog("Move Animal", skin);
        dialog.setBackground((Drawable) null);

        TabWidget tabWidget = new TabWidget();

        //TODO: ILIA
    }

    public GameMenuController getController() {
        return controller;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
