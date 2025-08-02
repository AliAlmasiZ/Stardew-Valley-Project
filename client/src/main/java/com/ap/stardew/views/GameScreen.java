package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.controllers.GameAssetManager;
import com.ap.stardew.controllers.GameMenuController;
import com.ap.stardew.controllers.PlayerController;
import com.ap.stardew.models.Actors.DialogActor;
import com.ap.stardew.models.App;
import com.ap.stardew.models.ClockActor;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.NPC.NPC;

import com.ap.stardew.models.Vec2;
import com.ap.stardew.models.animal.Animal;
import com.ap.stardew.models.animal.AnimalType;
import com.ap.stardew.models.animal.FishingMiniGame;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.Renderable;
import com.ap.stardew.models.entities.components.*;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.systems.EntityPlacementSystem;
import com.ap.stardew.models.enums.FishMovement;
import com.ap.stardew.models.enums.ProductQuality;
import com.ap.stardew.models.enums.SkillType;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.Skill;
import com.ap.stardew.models.player.friendship.PlayerFriendship;
import com.ap.stardew.models.shop.Shop;
import com.ap.stardew.models.shop.ShopProduct;
import com.ap.stardew.records.EntityResult;
import com.ap.stardew.views.widgets.*;
import com.ap.stardew.models.Result;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;

public class GameScreen extends AbstractScreen {
    public static final float WORLD_WIDTH = 800;
    public static final float WORLD_HEIGHT = 450;
    public static final int DISTANCE = 200;
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
    private EnergyBar energyBar;

    // Clock
    private ClockActor clockActor;

    // NPC
    ArrayList<DialogActor> dialogActors = new ArrayList<>();


    public GameScreen() {
        super();
        stack = new Stack();
        rootTable.add(stack).grow();

        controller = new GameMenuController();
        player = App.getActiveGame().getCurrentPlayer();
        currentPlayerSprite = player.getSprite();
        playerController = new PlayerController(this, player, controller);

        skin = GameAssetManager.getInstance().getSkin();
        customSkin = GameAssetManager.getInstance().getCustomSkin();

        //TODO: remove this later
        //**************************************
        controller.cheatGiveItem("Training Rod", 1);
        controller.cheatGiveItem("Hay", 500);
        controller.cheatGiveItem("Axe", 1);
        controller.cheatGiveItem("Hoe", 1);
        controller.cheatGiveItem("Hay", 500);
        controller.cheatGiveItem("Apple", 10);
        controller.cheatGiveItem("Bee House", 1);
        controller.cheatGiveItem("Cheese Press", 1);
        controller.cheatGiveItem("Keg", 1);
        controller.cheatGiveItem("    Dehydrator", 1);
        controller.cheatGiveItem("Charcoal Klin", 1);
        controller.cheatGiveItem("Loom", 1);
        controller.cheatGiveItem("Mayonnaise Machine", 1);
        controller.cheatGiveItem("Oil Maker", 1);
        controller.cheatGiveItem("Preserves Jar", 1);
        controller.cheatGiveItem("Fish Smoker", 1);
        controller.cheatGiveItem("Furnace", 1);

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


        NPC npc = App.getActiveGame().findNPC("Robin");
        npc.getComponent(PositionComponent.class).setPosition(player.getPosition().x + 20, player.getPosition().y + 200);
        System.out.println("NPC: " + EntityPlacementSystem.placeEntity(npc, npc.getComponent(PositionComponent.class).get()));

        // put some trees and crops to test
        Entity tree1 = App.entityRegistry.makeEntity("Apple Tree");
        tree1.getComponent(Growable.class).setDaysPastFromPlant(4);
        Vec2 vec2 = new Vec2(player.getPosition().x + 50, player.getPosition().y + 200);
        System.out.println("Apple tree1:" + EntityPlacementSystem.placeEntity(tree1, vec2));

        Entity tree2 = App.entityRegistry.makeEntity("Apple Tree");
        tree2.getComponent(Growable.class).setDaysPastFromPlant(15);
        vec2 = new Vec2(player.getPosition().x + 80, player.getPosition().y + 200);
        System.out.println("Apple tree2:" + EntityPlacementSystem.placeEntity(tree2, vec2));

        Entity tree3 = App.entityRegistry.makeEntity("Apple Tree");
        tree3.getComponent(Growable.class).setDaysPastFromPlant(27);
        vec2 = new Vec2(player.getPosition().x + 110, player.getPosition().y + 200);
        System.out.println("Apple tree3:" + EntityPlacementSystem.placeEntity(tree3, vec2));


        for (int i = 0; i < 7; i++) {
            Entity crop1 = App.entityRegistry.makeEntity("Kale");
            crop1.getComponent(Growable.class).setDaysPastFromPlant(i);
            vec2 = new Vec2(player.getPosition().x + 110 + 40 * i, player.getPosition().y + 100);
            System.out.println("crop" + i + ":" + EntityPlacementSystem.placeEntity(crop1, vec2));
        }
        //**************************************


        //TODO
    }

    public void initNPCDialogs() {
        Game game = App.getActiveGame();
        Player currentPlayer = App.getActiveGame().getCurrentPlayer();

        for (DialogActor dialogActor : dialogActors) {
            dialogActor.remove();
        }
        dialogActors.clear();

        for (NPC npc : game.getGameNPCs()) {
            if (npc.getComponent(PositionComponent.class).getX() < 10) continue;
            DialogActor dialogShow = new DialogActor(npc, this);
            gameStage.addActor(dialogShow);
            dialogActors.add(dialogShow);
        }
    }

    @Override
    public void show() {
        super.show();
        batch = ClientGame.getInstance().getBatch();
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
        map = new TmxMapLoader().load("./Content(unpacked)/Maps/untitled.tmx");
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

        // Energy bar
        energyBar = new EnergyBar();
        Table energyBarTable = new Table();
        energyBarTable.right().bottom();
        energyBarTable.pad(5);
        energyBarTable.add(energyBar);
        stack.add(energyBarTable);


        //inventory
        Table inventoryWrapper = new Table();
        inventoryWrapper.setFillParent(true);
        inventoryWrapper.bottom();
        uiStage.addActor(inventoryWrapper);

        Table inventoryTable = new Table();
        InventoryGrid inventoryGrid = new InventoryGrid(player.getComponent(Inventory.class), 10, 10, InventoryGrid.Type.TOOLBAR);
        inventoryGrid.setSlotSize(15);
        inventoryTable.setBackground(customSkin.getDrawable("smallPanelNinePatch"));
        inventoryTable.add(inventoryGrid).grow();
        inventoryWrapper.add(inventoryTable).pad(1);

        //Buttons at top right
        int buttonWidth = 20;
        Table buttonTable = new Table();
        Button button = new Button(customSkin);
        button.setWidth(buttonWidth);
        button.setHeight(buttonWidth);
        buttonTable.add(button).width(buttonWidth).height(buttonWidth);
        buttonTable.top().left().pad(15);
        stack.add(buttonTable);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openDataMenu();
            }
        });

        //NPC
        initNPCDialogs();
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
            entity.setEntityForComponents();
            //update animals:
            if (entity instanceof Animal) ((Animal) entity).renderUpdate(delta);
            Sprite sprite = GameAssetManager.getInstance().getEntitySpriteToRender(entity, delta);
            if (sprite != null) {
                sprite.setPosition(entity.getComponent(PositionComponent.class).getX(), entity.getComponent(PositionComponent.class).getY());
                sprite.draw(batch);
            }
        }

        switch (playerController.getEquippedItemState()){
            case PLACEABLE -> {
                batch.setColor(0, 1, 0, 0.3f);
                batch.draw(GameAssetManager.getInstance().tileSelectionBox, playerController.getCursorPos().getCol()*16
                    , playerController.getCursorPos().getRow() * 16, 16, 16);
                batch.draw(GameAssetManager.getInstance()
                    .get(player.getActiveSlot().getEntity().getComponent(Pickable.class).getIcon(), Texture.class),
                    playerController.getCursorPos().getCol()*16
                    , playerController.getCursorPos().getRow() * 16);
                batch.setColor(1, 1, 1, 1);
            }
            case PLACEABLE_INVALID -> {
                batch.setColor(1, 0, 0, 0.3f);
                batch.draw(GameAssetManager.getInstance().tileSelectionBox, playerController.getCursorPos().getCol()*16
                    , playerController.getCursorPos().getRow() * 16, 16, 16);
                batch.draw(GameAssetManager.getInstance()
                        .get(player.getActiveSlot().getEntity().getComponent(Pickable.class).getIcon(), Texture.class),
                    playerController.getCursorPos().getCol()*16
                    , playerController.getCursorPos().getRow() * 16);
                batch.setColor(1, 1, 1, 1);
            }
            case USEABLE -> {
                batch.setColor(0, 1, 0, 0.3f);
                batch.draw(GameAssetManager.getInstance().tileSelectionBox, playerController.getCursorPos().getCol()*16
                    , playerController.getCursorPos().getRow() * 16, 16, 16);
                batch.setColor(1, 1, 1, 1);
            }
        }

        batch.end();


        renderer.render(frontLayerIndices);
        gameStage.act(delta);
        gameStage.draw();

        Gdx.gl.glEnable(GL32.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0.2f, (App.getActiveGame().getDate().getHour() - 9) / 22f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL32.GL_BLEND);

        uiStage.act(delta);
        uiStage.draw();

        minigameStage.act(delta);
        minigameStage.draw();


        /**
         * UPDATES
         */
        // Clock
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


    public void showSkillDetails(SkillType type, Table table){
        table.clearChildren();
        table.setBackground(customSkin.getDrawable("smallPanelNinePatch"));
        table.top().left();

        Label title = new Label(type.name().substring(0,1) + type.name().toLowerCase().substring(1)
            + ":", customSkin);
        title.setColor(Color.BLACK);

        table.add(title);
    }
    public void openJournal() {
        InGameDialog dialog = new InGameDialog(uiStage);

        TabWidget tabWidget = new TabWidget();

        Table inventoryTable = new Table();
        InventoryGrid inventoryGrid = new InventoryGrid(player.getComponent(Inventory.class), 10, InventoryGrid.Type.PLAYER_INVENTORY);
        inventoryGrid.top();
        TrashCanActor trashCan = new TrashCanActor();
        Table trashCanTable = new Table();
        trashCanTable.setFillParent(true);
        trashCanTable.bottom().right();
        trashCanTable.add(trashCan);
        inventoryTable.add(inventoryGrid).grow();
        inventoryTable.addActor(trashCanTable);

        Table skillTable = new Table();
        Table bottom = new Table();

        {
            Table skillTableDivider = new Table();
            Table topLeft = new Table();
            Table portrait = new Table();
            portrait.setBackground(customSkin.getDrawable("daybg"));
            portrait.center();
            portrait.add(new Image(App.getActiveGame().getCurrentPlayer().getSpriteManager().getFrame(0, new Vec2(0, -1), Player.State.IDLE)));
            topLeft.add(portrait).row();
            topLeft.add(new Label(player.getAccount().getNickname(), customSkin){
                {
                    setColor(Color.BLACK);
                }
            });
            skillTableDivider.add(topLeft).pad(4).expandY().top();

            Table top = new Table();

            for (SkillType type : SkillType.values()) {
                Skill skill = player.getSkill(type);

                Label name = new Label(type.name().substring(0,1) + type.name().toLowerCase().substring(1), customSkin);
                name.setColor(Color.BLACK);
                name.setAlignment(Align.left);

                top.defaults().expandY();
                top.left();

                top.add(name).left().spaceRight(3);

                Image icon = new Image(customSkin.getDrawable(type.icon));
                icon.addListener(new ClickListener(){
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        if(pointer != -1) return;

                        icon.addAction(Actions.alpha(0.5f, 0.15f, Interpolation.smooth));
                    }
                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        if(pointer != -1) return;

                        icon.addAction(Actions.alpha(1f, 0.15f, Interpolation.smooth));
                    }

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        showSkillDetails(type, bottom);
                    }
                });
                top.add(icon);

                top.defaults().spaceRight(3);

                for (int i = 0; i < skill.getLevel(); i++) {
                    if(i != 3){
                        top.add(new Image(customSkin.getDrawable("sliderButtonUp")));
                    }else{
                        top.add(new Image(customSkin.getDrawable("buttonUp")));
                    }
                }
                for (int i = 0; i < 4 - skill.getLevel(); i++) {
                    if(i == 3 - skill.getLevel()){
                        top.add(new Image(customSkin.getDrawable("buttonDown")));
                    }else{
                        top.add(new Image(customSkin.getDrawable("sliderButtonDown")));
                    }
                }
                if(skill.getLevel() != 4){
                    top.defaults().spaceLeft(5);
                    top.add(new Label("xp :", customSkin){{setColor(Color.BLACK);}});
                    top.add(new Label(Integer.toString(skill.getExperience()), customSkin){{setColor(Color.BLACK);}});
                    top.add(new Label("/", customSkin){{setColor(Color.BLACK);}});
                    top.add(new Label(Integer.toString(skill.getMaxXp()), customSkin){{setColor(Color.BLACK);}});
                }

                top.row();
            }

            skillTableDivider.add(top).fill().expand().pad(4).row();
            skillTableDivider.add(bottom).fill().colspan(2).growX().height(30);
            skillTable.add(skillTableDivider).grow();
        }


        Table table2 = new Table();
        table2.add(new Label("test3", skin)).row();
        table2.add(new Label("test4", skin));

        Table table3 = new Table();
        table3.add(new Label("test3", skin)).row();
        table3.add(new Label("test4", skin));


        tabWidget.addTab(inventoryTable, customSkin.getDrawable("InventoryIcon"));
        tabWidget.addTab(skillTable, customSkin.getDrawable("skillMenuIcon"));
        tabWidget.addTab(table2, customSkin.getDrawable("MapIcon"));
        tabWidget.addTab(table3, customSkin.getDrawable("shit"));

//        dialog.getContentTable().add(tabWidget).fill().size(200, 130);
        dialog.add(tabWidget).size(230, 130).fill();

        dialog.show();
    }
    public void showStorage(Inventory inventory){
        Table panel = new Table();
        panel.setBackground(customSkin.getDrawable("frameNinePatch2"));

        Table storageGrid = new InventoryGrid(inventory, 10);

        panel.add(storageGrid).grow();

        Image icon = new Image(customSkin.getDrawable("storageIcon"));

        panel.addActor(icon);
        icon.setPosition(-icon.getWidth(), panel.getPrefHeight() - icon.getHeight() - 4);

        openMenuWithInventory(panel);
    }

    public void openMenuWithInventory(Table menu){
        InGameDialog dialog = new InGameDialog(uiStage);

        Table inventoryPanel = new Table();
        inventoryPanel.setBackground(customSkin.getDrawable("frameNinePatch2"));

        inventoryPanel.add(new InventoryGrid(player.getComponent(Inventory.class), 10)).grow();

        Image icon = new Image(customSkin.getDrawable("inventoryIconRotated"));

        inventoryPanel.addActor(icon);
        icon.setPosition(-icon.getWidth(), inventoryPanel.getPrefHeight() - icon.getHeight() - 4);

        dialog.add(menu).colspan(2).row();
        dialog.add().height(10).growX();
        dialog.add().height(10).growX().row();
        dialog.add(inventoryPanel).colspan(2).row();

        dialog.show();

        inventoryPanel.invalidateHierarchy();

        inventoryPanel.addAction(
            Actions.sequence(
                Actions.moveBy(0, 15),
                Actions.delay(0.1f),
                Actions.parallel(
                    Actions.moveTo(0, 0, 0.5f, Interpolation.swingOut),
                    Actions.alpha(1, 0.5f)
                )
            )
        );

    }

    public void openShopMenu(Shop shop){
        InGameDialog dialog = new InGameDialog(uiStage);

        TabWidget tabWidget = new TabWidget();

        Table testTable = new Table();
        testTable.top();

        for (ShopProduct availableProduct : shop.getAvailableProducts()) {
            testTable.add(new Label(availableProduct.getName(), customSkin)).row();
        }
        tabWidget.addTab(testTable, customSkin.getDrawable("InventoryIcon"));

        dialog.add(tabWidget);
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
        inputMultiplexer.addProcessor(uiStage);
        inputMultiplexer.addProcessor(gameStage);
        inputMultiplexer.addProcessor(playerController);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void openAnimalMenu(Animal animal) {
        InGameDialog dialog = new InGameDialog(uiStage);

        TabWidget tabWidget = new TabWidget();

        // Info Tab
        Table infoTab = new Table();
        Label animalLabel = new Label(animal.getDetail(), customSkin);
        animalLabel.setColor(Color.WHITE);
        infoTab.add(new Label(animal.getDetail(), customSkin)).row();

        // Functions Tab
        Table buttonTab = new Table();

        TextButton feedButton = new TextButton("Feed", customSkin);
        TextButton petButton = new TextButton("Pet", customSkin);
        TextButton collectProduceButton = new TextButton("Collect produce", customSkin);
        TextButton sellAnimalButton = new TextButton("Sell animal", customSkin);
        /*TODO: check if in house*/
        TextButton shepherdAnimalButton = new TextButton("Shephered Animal", customSkin);

        buttonTab.add(feedButton).growX().row();
        buttonTab.add(petButton).growX().row();
        buttonTab.add(collectProduceButton).growX().row();
        buttonTab.add(sellAnimalButton).growX().row();
        buttonTab.add(shepherdAnimalButton).growX().row();
//
        tabWidget.addTab(infoTab, customSkin.getDrawable("skillMenuIcon"));
        tabWidget.addTab(buttonTab, customSkin.getDrawable("skillMenuIcon"));

        dialog.add(tabWidget).fill().grow();

        dialog.show();

        feedButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Result result = controller.feedHay(animal.getName());
                if (!result.isSuccessful()) {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                } else {
                    animal.getComponent(Renderable.class).setStatue(Renderable.Statue.EATING, 5);

                }
                dialog.hide();
            }
        });

        petButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Result result = controller.pet(animal.getName());
                if (!result.isSuccessful()) {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                } else {
                    animal.getComponent(Renderable.class).setStatue(Renderable.Statue.PET, 2);
                }
                dialog.hide();
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
                dialog.hide();
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
                dialog.hide();
            }
        });

        shepherdAnimalButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
                openAnimalMovementMenu(animal);
            }
        });


    }

    public void openAnimalMovementMenu(Animal animal) {
        InGameDialog dialog = new InGameDialog(uiStage);
        TabWidget tabWidget = new TabWidget();

        //
        Table mainTable = new Table();

        Label infoLabel = new Label("Enter the vector that you want to move your animal:", skin);
        TextField xField = new TextField("", skin);

        xField.setMessageText("x");
        TextField yField = new TextField("", skin);
        yField.setMessageText("y");

        xField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                if (c == '-' && textField.getText().isEmpty()) {
                    return true;
                }
                return Character.isDigit(c);
            }
        });

        yField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                if (c == '-' && textField.getText().isEmpty()) {
                    return true;
                }
                return Character.isDigit(c);
            }
        });

        Label errorLabel = new Label("x, y must be less than 200!", skin);
        errorLabel.setVisible(false);
        errorLabel.setColor(Color.RED);
        TextButton confirmButton = new TextButton("Confirm", customSkin);

        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float dx, float dy) {
                if (xField.getText().length() == 0 || yField.getText().length() == 0) {
                    errorLabel.setVisible(true);
                    errorLabel.setText("You haven't entered any coordinates!");
                    return;
                }
                float x = Float.parseFloat(xField.getText());
                float y = Float.parseFloat(yField.getText());
                if (Math.abs(x) > 200 || Math.abs(y) > 200) {
                    errorLabel.setVisible(true);
                    errorLabel.setText("|x|, |y| must be less than 200!");
                    return;
                }

                animal.move(x, y);
                dialog.hide();
            }
        });


        mainTable.add(infoLabel).growX().row();
        mainTable.add(xField);
        mainTable.add(yField).row();
        mainTable.add(errorLabel).growX().row();
        mainTable.add(confirmButton).growX().row();

        tabWidget.addTab(mainTable, customSkin.getDrawable("skillMenuIcon"));

        dialog.add(tabWidget).fill().grow();

        dialog.show();
    }

    public void openNPCMenu(NPC npc) {
        InGameDialog dialog = new InGameDialog(uiStage);
        dialog.setBackground((Drawable) null);

        TabWidget tabWidget = new TabWidget();


        // Tab: Give gift
        // --- in your Screen or wherever you assemble the UI ---
        Table giftTable = new Table();
        TextButton chooseGift = new TextButton("Choose Gift", customSkin);
        Image giftImage = new Image(GameAssetManager.getInstance().redCross);
        Label giftNumberLabel = new Label("0X", skin);
        TextButton sendGift = new TextButton("Send Gift", customSkin);

        // keep a mutable holder so the listener can see updates
        final Entity[] chosenItemHolder = { null };

        giftTable.add(chooseGift).growX().row();
        giftTable.add(giftImage);
        giftTable.add(giftNumberLabel).row();
        giftTable.add(sendGift).growX().row();


        // prepare the inventory dialog once
        final Dialog inventoryDialog = new Dialog("Choose an Item", customSkin) {{
            // container for your inventory slots
            Table invTable = new Table();
            invTable.defaults().pad(5);

            // assume you have a List<Entity> inventory = ...
//            for (final Entity e : player.getComponent(Inventory.class).getEntities()) {
//                TextureRegion icon = new TextureRegion(GameAssetManager.getInstance().getTexture(e.getComponent(Pickable.class).getIcon()));
//                ImageButton slot = new ImageButton(
//                    new TextureRegionDrawable(icon),
//                    new TextureRegionDrawable()  // optional pressed state
//                );
//
//                slot.addListener(new ClickListener() {
//                    @Override
//                    public void clicked(InputEvent event, float x, float y) {
//                        // record the choice
//                        chosenItemHolder[0] = e;
//                        // update your giftImage & label in place
//                        giftImage.setDrawable(new TextureRegionDrawable(icon));
//                        giftNumberLabel.setText(
//                            e.getComponent(Pickable.class).getStackSize() + "X"
//                        );
//                        // hide the dialog
//                        inventoryDialog.hide();
//                    }
//                });
//
//                invTable.add(slot).size(64).pad(4);
//                // wrap to next row every N columns if you like:
//                if ((player.getComponent(Inventory.class).getEntities().indexOf(e, true) + 1) % 5 == 0) invTable.row();
//            }

            // make it scrollable if too large
            getContentTable().add(new ScrollPane(invTable, customSkin))
                .width(400).height(300);
            button("Cancel", "cancel");  // built-in cancel button
        }};

        // when “Choose Gift” is clicked, just show the dialog
        chooseGift.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inventoryDialog.show(uiStage);
            }
        });



        /**
         * @Tab: Quests
         */
        Table questTable = new Table();

        /**
         * @Tab: info
         */
        Table infoTable = new Table();
        infoTable.add(new Label(player.npcFriendshipDetails(npc), customSkin)).growX().row();

        /**/

        tabWidget.addTab(giftTable, customSkin.getDrawable("skillMenuIcon"));
        tabWidget.addTab(questTable, customSkin.getDrawable("skillMenuIcon"));
        tabWidget.addTab(infoTable, customSkin.getDrawable("skillMenuIcon"));


        dialog.add(tabWidget).fill().grow();

        dialog.show();
    }

    public void showNPCDialog(NPC npc) {
        // Root table aligned to bottom
        Table dialogTable = new Table();
        dialogTable.setFillParent(true);
        dialogTable.bottom().pad(10); // Align to bottom with optional padding

        // --- Avatar image
        Image npcAvatar = npc.getAvatar();

        // --- Dialog background with label
        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(new TextureRegion(GameAssetManager.getInstance().textBox));
        Table dialogBox = new Table();
        dialogBox.setBackground(bgDrawable);
        dialogBox.pad(10); // inner padding inside background

        // Dialog text
        Label dialogLabel = new Label(controller.meetNPC(npc.getName()).message(), customSkin);
        dialogLabel.setWrap(true); // allow wrapping if needed
        dialogBox.add(dialogLabel).width(360).left().padLeft(24); // fix width as needed

        // --- Continue button
        TextButton continueButton = new TextButton("Continue", customSkin);

        // --- Sub-table to hold dialog box and button vertically
        Table dialogContent = new Table();
        dialogContent.add(dialogBox).left().row();
        dialogContent.add(continueButton).left().padTop(10).row();

        // --- Final layout: avatar | (dialog + button)
        dialogTable.add(npcAvatar).bottom().padRight(10);
        dialogTable.add(dialogContent).bottom();

        // --- Add to stage
        uiStage.addActor(dialogTable);
        Gdx.input.setInputProcessor(uiStage);

        continueButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                dialogTable.remove();
                setGameInput();
            }
        });

    }

    /**
     * This will show table in InGameDialog
     * @param table the table which will be shown
     */
    public void showTable(Table table) {
        InGameDialog dialog = new InGameDialog(uiStage);
        dialog.setBackground((Drawable) null);

        TabWidget tabWidget = new TabWidget();

        tabWidget.addTab(table, customSkin.getDrawable("skillMenuIcon"));
        dialog.add(tabWidget).fill().grow();

        dialog.show();
    }

    public void openDataMenu() {
        InGameDialog dialog = new InGameDialog(uiStage);

        TabWidget tabWidget = new TabWidget();

        // Friendship tab
        Table friendshipTable = new Table();
        Game game = App.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        ArrayList<PlayerFriendship> playerFriendships = game.getCurrentPlayerFriendships();
        for (PlayerFriendship playerFriendship : playerFriendships) {
            Player friend = playerFriendship.getFriends().get(1);
            if (friend.equals(currentPlayer)) {
                friend = playerFriendship.getFriends().get(0);
            }

            Label label = new Label(friend.getUsername(), customSkin);
            Image friendshipDetail = new Image(customSkin.getDrawable("skillMenuIcon"));
            Image giftImage = new Image(GameAssetManager.getInstance().giftIcon);

            friendshipDetail.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    Table friendshipDataTable = new Table();
                    Label friendshipDataLabel = new Label("", customSkin);
                    friendshipDataLabel.setText(PlayerFriendship.buildFriendshipDetailMessage(currentPlayer, playerFriendship));
                    friendshipDataTable.add(friendshipDataLabel).growX().row();
                    showTable(friendshipDataTable);
                }
            });

            Player finalFriend = friend;
            giftImage.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    openGiftMenu(finalFriend);
                }
            });

            friendshipTable.add(label);
            friendshipTable.add(giftImage).pad(5);
            friendshipTable.add(friendshipDetail).pad(5);

        }


        //plant info table
        Table craftTable = new Table();
        Label label = new Label("Enter your crop name to get info: ", customSkin);
        TextField cropNameField = new TextField("", skin);
        cropNameField.setMessageText("Crop Name...");
        Label errorLabel = new Label("", customSkin);
        errorLabel.setColor(Color.RED);
        errorLabel.setWrap(true);
        errorLabel.setVisible(false);
        TextButton confirmButton = new TextButton("Confirm", customSkin);

        craftTable.add(label).pad(4).growX().row();
        craftTable.add(cropNameField).growX().row();
        craftTable.add(errorLabel).pad(4).growX().row();
        craftTable.add(confirmButton).growX().row();


        confirmButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                String cropName = cropNameField.getText();
                Result result = controller.craftInfoPhase1(cropName);
                if (result.isSuccessful()) {
                    dialog.hide();
                    showTable(controller.craftInfo(cropName));
                } else {
                    errorLabel.setVisible(true);
                    errorLabel.setText(result.toString());
                }
            }
        });


        tabWidget.addTab(friendshipTable, customSkin.getDrawable("skillMenuIcon"));
        tabWidget.addTab(craftTable, customSkin.getDrawable("skillMenuIcon"));

        dialog.add(tabWidget).fill().grow();

        dialog.show();
    }

    public void openGiftMenu(Player friend) {
        InGameDialog dialog = new InGameDialog(uiStage);

        TabWidget tabWidget = new TabWidget();

        // send gift
        Table sendGiftTable = new Table();

        // gift History
        Table giftHistory = new Table();


        // rate gift
        Table rateGift = new Table();
        Label rateLabel = new Label("Enter the Gift ID and your Rating: ", customSkin);
        TextField giftId = new TextField("", skin);
        giftId.setMessageText("Gift ID...");
        TextField rating = new TextField("", skin);
        rating.setMessageText("Rating");


        tabWidget.addTab(sendGiftTable, skin.getDrawable("skillMenuIcon"));
        tabWidget.addTab(giftHistory, skin.getDrawable("skillMenuIcon"));
        tabWidget.addTab(rateGift, skin.getDrawable("skillMenuIcon"));

        dialog.add(tabWidget).fill().grow();

        dialog.show();
    }

    public void openPlayerMenu(Player friend) {
        Table actionsTable = new Table();
        TextButton hugButton = new TextButton("Hug", customSkin);
        TextButton flowerButton = new TextButton("Give Flower", customSkin);
        TextButton MarryButton = new TextButton("Marry", customSkin);

        hugButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Result result = controller.hug(friend.getUsername());
                if (result.isSuccessful()) {
                    //TODO: GRAPHIC
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.GREEN);
                } else {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                }
            }
        });

        flowerButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Result result = controller.flower(friend.getUsername());
                if (result.isSuccessful()) {
                    //TODO: GRAPHIC
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.GREEN);

                } else {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                }
            }
        });

        MarryButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                //TODO
            }
        });

    }

    public Entity chooseFromInventory() {
        // TODO: open a dialog to select item and number
        Entity temptity = App.buildingRegistry.makeEntity("Salmon");
        temptity.getComponent(Pickable.class).setStackSize(1);
        return temptity;
    }

    public GameMenuController getController() {
        return controller;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
