package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.models.crafting.Ingredient;
import com.ap.stardew.models.crafting.Recipe;
import com.ap.stardew.models.crafting.RecipeType;
import com.ap.stardew.app.GameController;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.entities.RenderFunction;
import com.ap.stardew.models.entities.workstations.ArtisanComponent;
import com.ap.stardew.models.enums.*;
import com.ap.stardew.models.gameMap.GameMap;
import com.ap.stardew.models.player.TradeHistoryItem;
import com.ap.stardew.models.player.reaction.Reaction;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.controllers.GameMenuController;
import com.ap.stardew.controllers.PlayerController;
import com.ap.stardew.models.*;
import com.ap.stardew.models.Actors.DialogActor;
import com.ap.stardew.models.NPC.NPC;

import com.ap.stardew.models.NPC.Quest;
import com.ap.stardew.models.animal.Animal;
import com.ap.stardew.models.animal.FishingMiniGame;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.Renderable;
import com.ap.stardew.models.entities.components.*;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.Skill;
import com.ap.stardew.models.player.friendship.PlayerFriendship;
import com.ap.stardew.models.shop.Shop;
import com.ap.stardew.models.shop.ShopProduct;
import com.ap.stardew.models.records.EntityResult;
import com.ap.stardew.view.ToolFrameInfo;
import com.ap.stardew.view.VariableDurationAnimation;
import com.ap.stardew.views.dialogs.*;
import com.ap.stardew.views.managers.EmojiSpriteManager;
import com.ap.stardew.views.managers.MapRenderManager;
import com.ap.stardew.views.widgets.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
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
    public Player player;
    private Sprite currentPlayerSprite;

    //Renderers
    private Batch batch;
    private Stage gameStage;
    private Stage minigameStage;
    private ShapeRenderer shapeRenderer;
    public OrthographicCamera camera;
    private Viewport gameView;
    private final MapRenderManager mapRenderManager = new MapRenderManager();

    //Map //TODO: this is just for test player movement and should be replace by PARSA
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    // ui
    private final Stack stack;
    private EnergyBar energyBar;

    // Clock
    private ClockActor clockActor;

    // more dialogs
    public TradeDialog tradeDialog;
    public ChatDialog chatDialog;
    public ScoreBoardDialog scoreBoardDialog;

    public RadioDialog radioDialog;


    private final Game game;

    // NPC
    ArrayList<DialogActor> dialogActors = new ArrayList<>();


    public GameScreen(Game game) {
        super();

        this.game = game;
        player = game.getCurrentPlayer();
        controller = new GameMenuController();
//        currentPlayerSprite = player.getSprite();
        playerController = new PlayerController(this, player, controller);

        stack = new Stack();
        rootTable.add(stack).grow();

        batch = ClientGame.getInstance().getBatch();
        camera = new OrthographicCamera();
        gameView = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();
        camera.setToOrtho(false, gameView.getWorldWidth(), gameView.getWorldHeight());

        camera.update();

        renderer = new OrthogonalTiledMapRenderer(map);
        renderer.setView(camera);
        renderer.getBatch().enableBlending();

        gameStage = new Stage(gameView, batch);
        minigameStage = new Stage(new ScreenViewport());

        // Create clock
        clockActor = new ClockActor(game);
        Table clockTable = new Table();
        clockTable.top().right();
        clockTable.add(clockActor).pad(10);
        stack.add(clockTable);

        // Energy bar
        energyBar = new EnergyBar(player);
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
        int buttonHeight = 20;
        Table buttonTable = new Table();

        Button button = new Button(customSkin);
        button.setWidth(buttonWidth);
        button.setHeight(buttonHeight);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openDataMenu();
            }
        });

        Image chatButton = new Image(new Texture("Content/Bale.png"));
        chatButton.setWidth(buttonWidth);
        chatButton.setHeight(buttonHeight);
        chatButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                chatDialog.show();
            }
        });

        Image radioButton = new Image(new Texture("Content/radio.png"));
        radioButton.setWidth(buttonWidth);
        radioButton.setHeight(buttonHeight);
        radioButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                radioDialog.show();
            }
        });


        buttonTable.add(button).width(buttonWidth).height(buttonHeight).pad(5);
        buttonTable.add(chatButton).width(buttonWidth).height(buttonHeight).pad(5);
        buttonTable.add(radioButton).width(buttonWidth).height(buttonHeight).pad(5);
        buttonTable.top().left().pad(15);
        stack.add(buttonTable);

        //NPC
        initNPCDialogs();

        chatDialog = new ChatDialog(ClientApp.getActiveGame().getPlayers(), uiStage);
        scoreBoardDialog = new ScoreBoardDialog(uiStage);
        radioDialog = new RadioDialog(this);

        //TODO: remove this later
        //**************************************

//        Player player = ClientApp.getActiveGame().getCurrentPlayer();
//        Animal animal1 = new Animal(AnimalType.Cow, "Arteta");
//        System.out.println(EntityPlacementSystem.placeEntity(animal1, player.getPosition()).message());
//        player.getAnimals().add(animal1);
//
//
//        NPC npc = ClientApp.getActiveGame().findNPC("Robin");
//        npc.getComponent(PositionComponent.class).setPosition(player.getPosition().x + 20, player.getPosition().y + 200);
//        System.out.println("NPC: " + EntityPlacementSystem.placeEntity(npc, npc.getComponent(PositionComponent.class).get()));
//
//        // put some trees and crops to test
//        Entity tree1 = App.entityRegistry.makeEntity("Apple Tree");
//        tree1.getComponent(Growable.class).setDaysPastFromPlant(4);
//        Vec2 vec2 = new Vec2(player.getPosition().x + 50, player.getPosition().y + 200);
//        System.out.println("Apple tree1:" + EntityPlacementSystem.placeEntity(tree1, vec2));
//
//        Entity tree2 = App.entityRegistry.makeEntity("Apple Tree");
//        tree2.getComponent(Growable.class).setDaysPastFromPlant(15);
//        vec2 = new Vec2(player.getPosition().x + 80, player.getPosition().y + 200);
//        System.out.println("Apple tree2:" + EntityPlacementSystem.placeEntity(tree2, vec2));
//
//        Entity tree3 = App.entityRegistry.makeEntity("Apple Tree");
//        tree3.getComponent(Growable.class).setDaysPastFromPlant(27);
//        vec2 = new Vec2(player.getPosition().x + 110, player.getPosition().y + 200);
//        System.out.println("Apple tree3:" + EntityPlacementSystem.placeEntity(tree3, vec2));


//        for (int i = 0; i < 7; i++) {
//            Entity crop1 = App.entityRegistry.makeEntity("Kale");
//            crop1.getComponent(Growable.class).setDaysPastFromPlant(i);
//            vec2 = new Vec2(player.getPosition().x + 110 + 40 * i, player.getPosition().y + 100);
//            System.out.println("crop" + i + ":" + EntityPlacementSystem.placeEntity(crop1, vec2));
//        }
        //**************************************


    }

    public void initNPCDialogs() {
        Game game = ClientApp.getActiveGame();
        Player currentPlayer = ClientApp.getActiveGame().getCurrentPlayer();

        for (DialogActor dialogActor : dialogActors) {
            dialogActor.remove();
        }
        dialogActors.clear();

        for (NPC npc : game.getGameNPCs()) {
            DialogActor dialogShow = new DialogActor(npc, this);
            gameStage.addActor(dialogShow);
            dialogActors.add(dialogShow);
        }
    }

    @Override
    public void show() {
        super.show();
        setGameInput();
        initialCheats();
    }

    @Override
    public void render(float delta) {
        if (!ClientApp.isConnected()) {
            GameController.handleGameDisconnection(this);
            return;
        }

        controller.update(delta);
        playerController.update(delta);

        GameMap activeMap = ClientApp.getActiveGame().getActiveMap();

        //center Camera:
        camera.position.set(player.getPosition().cpy(), camera.position.z);
//        float cameraHalfWidth = camera.viewportWidth * camera.zoom / 2;
//        float cameraHalfHeight = camera.viewportHeight * camera.zoom / 2;
//        camera.position.x = Math.max(cameraHalfWidth, camera.position.x);
//        camera.position.x = Math.min(activeMap.getWidth() - cameraHalfWidth, camera.position.x);
//        camera.position.y = Math.max(cameraHalfHeight, camera.position.y);
//        camera.position.y = Math.min(activeMap.getHeight() - cameraHalfHeight, camera.position.y);


        camera.update();

        renderer.setView(camera);
        mapRenderManager.renderBackLayers(renderer, activeMap);

        batch.setProjectionMatrix(camera.combined);
        ArrayList<Entity> renderableEntities = activeMap.getEntitiesWithComponent(Renderable.class);
        renderableEntities.sort((e1, e2) -> {
            float y1 = e1.getComponent(PositionComponent.class).getY();
            float y2 = e2.getComponent(PositionComponent.class).getY();
            return Float.compare(y2, y1); // descending order: bigger Y first
        });

        batch.begin();
        for (Entity entity : renderableEntities) {
            entity.setEntityForComponents();

            Renderable renderable = entity.getComponent(Renderable.class);
            //update animals:
            if (entity instanceof Animal){
                JSONMessage animalMessage = ((Animal) entity).renderUpdate(delta, player.getAnimals().contains(((Animal) entity)));
                if (animalMessage != null) {
                    ClientApp.sendTCP(animalMessage);
                }
            }


            if (entity instanceof Player player){ //sorry
                player.update(delta);
                renderPlayer(player, batch);
            } else if(renderable.getRenderFunction() != null){
                renderable.getRenderFunction().render(entity, batch);
            } else {
                Sprite sprite = GameAssetManager.getInstance().getEntitySpriteToRender(entity, game, delta);
                if (sprite != null) {
                    sprite.setPosition(entity.getComponent(PositionComponent.class).getX(), entity.getComponent(PositionComponent.class).getY());
                    sprite.draw(batch);
                }
            }
        }

        switch (playerController.getEquippedItemState()) {
            case PLACEABLE -> {
                batch.setColor(0, 1, 0, 0.3f);
                batch.draw(GameAssetManager.getInstance().tileSelectionBox, playerController.getCursorPos().getCol() * 16
                    , playerController.getCursorPos().getRow() * 16, 16, 16);
                batch.draw(GameAssetManager.getInstance()
                        .get(player.getActiveSlot().getEntity().getComponent(Pickable.class).getIcon(), Texture.class),
                    playerController.getCursorPos().getCol() * 16
                    , playerController.getCursorPos().getRow() * 16);
                batch.setColor(1, 1, 1, 1);
            }
            case PLACEABLE_INVALID -> {
                batch.setColor(1, 0, 0, 0.3f);
                batch.draw(GameAssetManager.getInstance().tileSelectionBox, playerController.getCursorPos().getCol() * 16
                    , playerController.getCursorPos().getRow() * 16, 16, 16);
                batch.draw(GameAssetManager.getInstance()
                        .get(player.getActiveSlot().getEntity().getComponent(Pickable.class).getIcon(), Texture.class),
                    playerController.getCursorPos().getCol() * 16
                    , playerController.getCursorPos().getRow() * 16);
                batch.setColor(1, 1, 1, 1);
            }
            case USEABLE -> {
                batch.setColor(0, 1, 0, 0.3f);
                batch.draw(GameAssetManager.getInstance().tileSelectionBox, playerController.getCursorPos().getCol() * 16
                    , playerController.getCursorPos().getRow() * 16, 16, 16);
                batch.setColor(1, 1, 1, 1);
            }
        }
        batch.end();

        /* --- Artisan Progress Bar --- */
        Gdx.gl.glEnable(GL32.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity entity : activeMap.getEntitiesWithComponent(ArtisanComponent.class)) {
            ArtisanComponent artisanComponent = entity.getComponent(ArtisanComponent.class);
            if (artisanComponent.isInProcess()) {
                PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
                float progress = artisanComponent.getProcessProgress();

                RenderFunction renderFunction = entity.getComponent(Renderable.class).getRenderFunction();


                float barWidth = 32;
                float barHeight = 4;
                float barX = positionComponent.getX() + (renderFunction.getCurrentTexture(entity).getWidth() / 2f) - (barWidth / 2f);
                float barY = positionComponent.getY() + renderFunction.getCurrentTexture(entity).getHeight() + 2;

                // Draw background of the bar
                shapeRenderer.setColor(Color.DARK_GRAY);
                shapeRenderer.rect(barX, barY, barWidth, barHeight);

                // Draw filled portion of the bar
                shapeRenderer.setColor(Color.LIME);
                shapeRenderer.rect(barX, barY, barWidth * progress, barHeight);
            }
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL32.GL_BLEND);
        /* --- Artisan Progress Bar --- */

        mapRenderManager.renderFrontLayers(renderer, activeMap);

        gameStage.act(delta);
        gameStage.draw();

        Gdx.gl.glEnable(GL32.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0.2f, (ClientApp.getActiveGame().getDate().getHour() - 9) / 22f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL32.GL_BLEND);

        uiStage.act(delta);
        uiStage.draw();

        minigameStage.act(delta);
        minigameStage.draw();


        frontStage.act(delta);
        frontStage.draw();


        /**
         * UPDATES
         */
        // Clock
        clockActor.update(delta);

        scoreBoardDialog.refreshFromGame();
    }


    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        gameStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }


    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
        gameStage.dispose();
    }

    private void renderPlayer(Player player, Batch batch){
        TextureRegion frame = GameAssetManager.getInstance().characterSpriteManager.getFrame(player.getStateTime(), player.getLastDir(), player.getAction(), Gender.FEMALE);
        if(player.getCurrentReaction() != null){
            Reaction currentReaction = player.getCurrentReaction();
            if(currentReaction.emoji != null){
                batch.draw(EmojiSpriteManager.getInstance().getFrame(currentReaction.emoji, currentReaction.timeLeft), player.getPosition().x,
                    player.getPosition().y + frame.getRegionHeight() - 2+ (float)Math.sin(currentReaction.timeLeft * 2));
            }else{
                GameAssetManager.getInstance().getFont().draw(batch, currentReaction.text,
                    player.getPosition().x, player.getPosition().y + frame.getRegionHeight() + 6 + (float)Math.sin(currentReaction.timeLeft * 2));
            }
        }

        player.getSprite().setRegion(frame);
        player.getSprite().setBounds(player.getPosition().x, player.getPosition().y, frame.getRegionWidth(), frame.getRegionHeight());

        Sprite entitySprite;
        player.getSprite().draw(batch);

        Player.Action action = player.getAction();
        if (action == Player.Action.USING_TOOL || (action == Player.Action.WATERING) || (action == Player.Action.USING_SCYTHE)
            || (action == Player.Action.HARVESTING)) {
            Entity tool = player.getActionItem();
            Direction direction = Direction.getDirection(player.getLastDir());

            ToolFrameInfo keyFrame;
            int animIndex;
            Texture texture;

            VariableDurationAnimation<ToolFrameInfo> toolFrames = GameAssetManager.getInstance().characterSpriteManager.toolFrames.get(action).get(direction);
            keyFrame = toolFrames.getKeyFrame(player.getStateTime(), false);
            animIndex = toolFrames.getKeyFrameIndex(player.getStateTime(), false);

            Vector2 offset = new Vector2();
            Vector2 origin = new Vector2();
            float alpha = 1;

            switch (action) {
                case WATERING -> {
                    switch (direction) {
                        case DOWN -> {
                            if (animIndex <= 1) {
                                texture = GameAssetManager.getInstance().get("Content/Tools/WateringCan/0.png");
                            } else {
                                texture = GameAssetManager.getInstance().get("Content/Tools/WateringCan/1.png");
                            }
                            offset.x = texture.getWidth() / 2f;
                            offset.y = texture.getHeight();
                        }
                        case UP -> {
                            texture = GameAssetManager.getInstance().get("Content/Tools/WateringCan/2.png");
                            offset.x = texture.getWidth() / 2f;
                            offset.y = 3;
                        }
                        default -> {
                            texture = GameAssetManager.getInstance().get("Content/Tools/WateringCan/3.png");
                            offset.x = 2;
                            offset.y = texture.getHeight() - 0f;
                            origin.x = 2;
                            origin.y = texture.getHeight();
                        }
                    }
                }
                case USING_TOOL -> {
                    switch (direction) {
                        case DOWN -> {
                            if (animIndex == 0) {
                                texture = GameAssetManager.getInstance().get("Content/Tools/" + tool.getEntityName() + "/0.png");
                            } else {
                                texture = GameAssetManager.getInstance().get("Content/Tools/" + tool.getEntityName() + "/1.png");
                            }

                        }
                        case UP -> {
                            if (animIndex == 0) {
                                texture = GameAssetManager.getInstance().get("Content/Tools/" + tool.getEntityName() + "/3.png");
                            } else {
                                texture = GameAssetManager.getInstance().get("Content/Tools/" + tool.getEntityName() + "/4.png");
                            }
                        }
                        default -> {
                            texture = GameAssetManager.getInstance().get("Content/Tools/" + tool.getEntityName() + "/2.png");
                        }
                    }
                    offset.x = texture.getWidth() / 2f;
                    offset.y = (animIndex != 0 && direction == Direction.DOWN) ? texture.getHeight() : 0;
                    origin.x = texture.getWidth() / 2f;
                    origin.y = 0;
                }
                case HARVESTING -> {
                    if(tool.getComponent(Pickable.class).getIcon() != null){
                        texture = GameAssetManager.getInstance().get(tool.getComponent(Pickable.class).getIcon());
                    }else {
                        texture = GameAssetManager.getInstance().redCross;
                    }
                    offset.x = texture.getWidth()/2f;
                    if(animIndex == 2){
                        alpha = 0.5f;
                    }else if(animIndex == 3){
                        alpha = 0.2f;
                    }
                }
                default -> texture = GameAssetManager.getInstance().get("Content/Tools/Scythe/0.png");
            }
            entitySprite = new Sprite(texture);

            entitySprite.setColor(1, 1, 1, alpha);
            entitySprite.setOrigin(origin.x, origin.y);
            if (direction == Direction.LEFT) {
                entitySprite.flip(true, false);
                offset.x = texture.getWidth() - offset.x;
                entitySprite.setOrigin(texture.getWidth() - origin.x, origin.y);
            }
            entitySprite.setPosition(player.getPosition().x + keyFrame.origin().x - offset.x,
                player.getPosition().y + keyFrame.origin().y - offset.y);
            entitySprite.rotate(keyFrame.rotation() / (float) Math.PI * 180);

            if (direction == Direction.UP) {
                entitySprite.draw(batch);
                player.getSprite().draw(batch);
            } else {
                player.getSprite().draw(batch);
                entitySprite.draw(batch);
            }
        }
    }

    public void showTemporaryMessage(String message, float duration, Color color) {
        new PopUpMessage(message, PopUpMessage.PopUpMessageType.TOP_CENTER).show(AbstractScreen.getFrontStage());
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

    public void showSkillDetails(SkillType type, Table table) {
        table.clearChildren();
        table.setBackground(customSkin.getDrawable("smallPanelNinePatch"));
        table.top().left();

        Label title = new Label(type.name().substring(0, 1) + type.name().toLowerCase().substring(1)
            + ":", customSkin);
        title.setColor(Color.BLACK);

        table.add(title);
    }

    public void openReactionMenu(Player player){
        ReactionDialog reactionDialog = new ReactionDialog(player, this);
        reactionDialog.show();
    }

    public void openReactionEditMenu(Player player){
        ReactionEditDialog reactionEditDialog = new ReactionEditDialog(player, this);
        reactionEditDialog.show();
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

        //skill menu
        {
            Table skillTableDivider = new Table();
            Table topLeft = new Table();
            Table portrait = new Table();
            portrait.setBackground(customSkin.getDrawable("daybg"));
            portrait.center();
//            portrait.add(new Image(ClientApp.getActiveGame().getCurrentPlayer().getSpriteManager().getFrame(0, new Vec2(0, -1), Player.State.IDLE)));
            topLeft.add(portrait).row();
            topLeft.add(new Label(player.getNickname(), customSkin) {
                {
                    setColor(Color.BLACK);
                }
            });
            skillTableDivider.add(topLeft).pad(4).expandY().top();

            Table top = new Table();

            for (SkillType type : SkillType.values()) {
                Skill skill = player.getSkill(type);

                Label name = new Label(type.name().substring(0, 1) + type.name().toLowerCase().substring(1), customSkin);
                name.setColor(Color.BLACK);
                name.setAlignment(Align.left);

                top.defaults().expandY();
                top.left();

                top.add(name).left().spaceRight(3);

                Image icon = new Image(customSkin.getDrawable(type.icon));
                icon.addListener(new ClickListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        if (pointer != -1) return;

                        icon.addAction(Actions.alpha(0.5f, 0.15f, Interpolation.smooth));
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        if (pointer != -1) return;

                        icon.addAction(Actions.alpha(1f, 0.15f, Interpolation.smooth));
                    }

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        showSkillDetails(type, bottom);
                    }
                });
                top.add(icon).growX();

                top.defaults().spaceRight(3);

                for (int i = 0; i < skill.getLevel(); i++) {
                    if (i != 3) {
                        top.add(new Image(customSkin.getDrawable("sliderButtonUp")));
                    } else {
                        top.add(new Image(customSkin.getDrawable("buttonUp")));
                    }
                }
                for (int i = 0; i < 4 - skill.getLevel(); i++) {
                    if (i == 3 - skill.getLevel()) {
                        top.add(new Image(customSkin.getDrawable("buttonDown")));
                    } else {
                        top.add(new Image(customSkin.getDrawable("sliderButtonDown")));
                    }
                }
                if (skill.getLevel() != 4) {
                    top.defaults().spaceLeft(5);
                    top.add(new Label("xp :", customSkin) {{
                        setColor(Color.BLACK);
                    }});
                    top.add(new Label(Integer.toString(skill.getExperience()), customSkin) {{
                        setColor(Color.BLACK);
                    }});
                    top.add(new Label("/", customSkin) {{
                        setColor(Color.BLACK);
                    }});
                    top.add(new Label(Integer.toString(skill.getMaxXp()), customSkin) {{
                        setColor(Color.BLACK);
                    }});
                }

                top.row();
            }

            skillTableDivider.add(top).fill().expand().pad(4).row();
            skillTableDivider.add(bottom).fill().colspan(2).growX().height(30);
            skillTable.add(skillTableDivider).grow();
        }


        Table mapTable = new Table();
        {
            Tile[][] tiles = ClientApp.getActiveGame().getMainMap().getTiles();
            Pixmap pixmap = new Pixmap(tiles.length, tiles.length, Pixmap.Format.RGBA8888);

            int height = tiles.length;
            int width = tiles[0].length;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Tile tile = tiles[y][x];
                    if (tile == null) {
                        pixmap.setColor(0, 0, 0, 0);
                        pixmap.drawPixel(x, height - y);
                        continue;
                    }

                    TileType type = tile.getType();

                    pixmap.drawPixel(x, height - y);
                }
            }
            Texture texture = new Texture(pixmap);

            Image map = new Image(GameAssetManager.getInstance().miniMap);
            map.setScaling(Scaling.fit);
            Table background = new Table();
            background.setBackground(customSkin.getDrawable("miniMapBackground"));

            ScrollPane scrollPane = new ScrollPane(map);

            background.add(scrollPane);

            mapTable.add(background);
        }

        Table table3 = new Table();
        table3.add(new Label("test3", skin)).row();
        table3.add(new Label("test4", skin));


        tabWidget.addTab(inventoryTable, customSkin.getDrawable("InventoryIcon"));
        tabWidget.addTab(skillTable, customSkin.getDrawable("skillMenuIcon"));
        tabWidget.addTab(mapTable, customSkin.getDrawable("MapIcon"));
        tabWidget.addTab(table3, customSkin.getDrawable("shit"));

//        dialog.getContentTable().add(tabWidget).fill().size(200, 130);
        dialog.add(tabWidget).fill();
        tabWidget.getContentTable().setSize(230, 130);

        dialog.show();
    }

    public void showStorage(Inventory inventory) {
        Table panel = new Table();
        panel.setBackground(customSkin.getDrawable("frameNinePatch2"));

        Table storageGrid = new InventoryGrid(inventory, 10);

        panel.add(storageGrid).grow();

        Image icon = new Image(customSkin.getDrawable("storageIcon"));

        panel.addActor(icon);
        icon.setPosition(-icon.getWidth(), panel.getPrefHeight() - icon.getHeight() - 4);

        openMenuWithInventory(panel);
    }

    public void openMenuWithInventory(Table menu) {
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

    public void openShopMenu(Shop shop) {
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

    public void stopFishing(FishingMiniGame fishingMiniGame) {
        minigameStage.clear();
        setGameInput();

        if (!fishingMiniGame.isSuccessful()) {
            showTemporaryMessage("You lost the mini game!\n Better luck next time!", ERROR_MESSAGE_DELAY, Color.RED);
            return;
        }

        Game game = ClientApp.getActiveGame();
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
                Result result = controller.canFeedHay(animal.getName());
                if (!result.isSuccessful()) {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                } else {
                    JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.request);
                    jsonMessage.put("command", "feed_animal");
                    jsonMessage.put("animal", animal.getName());
                    jsonMessage.put("sender", game.getCurrentPlayer().getUsername());

                    ClientApp.sendTCP(jsonMessage);
//                    animal.getComponent(Renderable.class).setStatue(Renderable.Statue.EATING, 5);
                }
                dialog.hide();
            }
        });

        petButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Result result = controller.canPet(animal.getName());
                if (!result.isSuccessful()) {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                } else {
                    JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.request);
                    jsonMessage.put("command", "pet_animal");
                    jsonMessage.put("animal", animal.getName());
                    jsonMessage.put("sender", game.getCurrentPlayer().getUsername());

                    ClientApp.sendTCP(jsonMessage);
                }
                dialog.hide();
            }
        });

        collectProduceButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                openCollectProduceMenu(animal);
                dialog.hide();
            }
        });

        sellAnimalButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Result result = controller.canSellAnimal(animal.getName());
                if (!result.isSuccessful()) {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                } else {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.GREEN);
                    JSONMessage jsonMessage = new JSONMessage(JSONMessage.Type.request);
                    jsonMessage.put("command", "sell_animal");
                    jsonMessage.put("animal", animal.getName());
                    jsonMessage.put("sender", game.getCurrentPlayer().getUsername());

                    ClientApp.sendTCP(jsonMessage);
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

    private void openAnimalMovementMenu(Animal animal) {
        InGameDialog dialog = new InGameDialog(uiStage);
        TabWidget tabWidget = new TabWidget();

        //
        Table mainTable = new Table();

        Label infoLabel = new Label("Enter the vector that you want to move your animal:", customSkin);
        TextField xField = new TextField("", customSkin);

        xField.setMessageText("x");
        TextField yField = new TextField("", customSkin);
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

        Label errorLabel = new Label("x, y must be less than 200!", customSkin);
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


        mainTable.add(infoLabel).center().colspan(2).pad(3).row();
        mainTable.add(xField).right().pad(3);
        mainTable.add(yField).left().pad(3).row();
        mainTable.add(errorLabel).center().colspan(2).pad(3).growX().row();
        mainTable.add(confirmButton).center().colspan(2).growX().pad(3).row();

        tabWidget.addTab(mainTable, customSkin.getDrawable("skillMenuIcon"));

        dialog.add(tabWidget).fill().grow();

        dialog.show();
    }

    private void openCollectProduceMenu(Animal animal) {
        Table table = new Table();

        Entity product = animal.getTodayProduct();

        if (product == null) {
            Label message = new Label("This animal doesn't have produce today...", customSkin);
            message.setColor(Color.RED);
            table.add(message).pad(3).growX().row();
        } else {
            Label message = new Label("Do you want to collect " + product.getEntityName() + "?", customSkin);
            TextButton collectButton = new TextButton("Collect", customSkin);

            collectButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Result result = controller.collectProduces(animal.getName());
                    if (!result.isSuccessful()) {
                        showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                    } else {
                        showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.GREEN);
                    }

                    Actor actor = table;
                    while (actor != null && !(actor instanceof InGameDialog)) {
                        actor = actor.getParent();
                    }
                    if (actor != null) {
                        ((InGameDialog) actor).hide();
                    }
                }
            });
        }

        showTable(table);
    }

    public void openNPCMenu(NPC npc) {
        InGameDialog dialog = new InGameDialog(uiStage);
        dialog.setBackground((Drawable) null);

        TabWidget tabWidget = new TabWidget();

        Game game = ClientApp.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        GameAssetManager GAM = GameAssetManager.getInstance();


        // Tab: Give gift
        // --- in your Screen or wherever you assemble the UI ---
        Table giftTable = new Table();
        TextButton sendGift = new TextButton("Send Gift", customSkin);

        giftTable.add(sendGift).growX().row();

        sendGift.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
                openSendGiftMenu(npc);
            }
        });


        /**
         * @Tab: Quests
         */
        int questTablePad = 10;
        Table questTable = new Table();
        // define column defaults for consistent padding and alignment
        questTable.defaults().pad(questTablePad).center();

        // Header row
        Label questIdLabel = new Label("Quest ID", customSkin);
        Label requestLabel = new Label("Request", customSkin);
        Label rewardLabel = new Label("Reward", customSkin);
        Label statueLabel = new Label("Statue", customSkin);

        questIdLabel.setColor(Color.BLACK);
        requestLabel.setColor(Color.BLACK);
        rewardLabel.setColor(Color.BLACK);
        statueLabel.setColor(Color.BLACK);

        questTable.add(questIdLabel);
        questTable.add(requestLabel);
        questTable.add(rewardLabel);
        questTable.add(statueLabel);
        questTable.row();

        // Body rows
        for (Quest quest : game.getQuests()) {
            if (quest.getNpc() != npc) continue;

            Label idLabel = new Label(String.valueOf(quest.getId()), customSkin);
            idLabel.setColor(Color.CYAN);
            Label request = new Label(quest.getRequest() + " " + quest.getRequestNumber() + "X", customSkin);
            request.setColor(Color.RED);
            Label reward = new Label(quest.getReward() + " " + quest.getRewardNumber() + "X", customSkin);
            reward.setColor(Color.GREEN);
            Image statueImage;

            if (quest.isCompleted()) {
                statueImage = new Image(
                    currentPlayer.getUsername().equals(quest.getDoneByPlayerName())
                        ? GAM.questDone : GAM.questDoneByOther
                );
            } else {
                statueImage = new Image(
                    quest.doesHaveAccess(currentPlayer).isSuccessful()
                        ? GAM.questNotDone : GAM.questLocked
                );
            }

            // Make statue icon feel clickable with hover-scale effect
            statueImage.setOrigin(Align.center);
            statueImage.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    openQuestMenu(quest);
                    return true;
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    statueImage.addAction(Actions.scaleTo(1.2f, 1.2f, 0.1f));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    statueImage.addAction(Actions.scaleTo(1f, 1f, 0.1f));
                }
            });

            questTable.add(idLabel).padTop(questTablePad * 1.2f);
            questTable.add(request).padTop(questTablePad * 1.2f);
            questTable.add(reward).padTop(questTablePad * 1.2f);
            questTable.add(statueImage).padTop(questTablePad * 1.2f).width(20).height(20);
            questTable.row();
        }

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

    private void openSendGiftMenu(Entity giftedOne) {
        Table table = new Table();
        table.setBackground(customSkin.getDrawable("frameNinePatch2"));

        Inventory giftInventory = new Inventory(1);

        Table giftGrid = new InventoryGrid(giftInventory, 0);

        TextField amountField = new TextField("", customSkin);
        amountField.setMessageText("Amount...");
        amountField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return Character.isDigit(c);
            }
        });

        Label errorLabel = new Label("", customSkin);
        errorLabel.setColor(Color.RED);
        errorLabel.setVisible(false);

        TextButton sendButton = new TextButton("Send Gift", customSkin);

        table.add(giftGrid).pad(3).row();
        table.add(amountField).pad(3).row();
        table.add(errorLabel).pad(3).row();
        table.add(sendButton).pad(3);

        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Entity gift;

                try {
                    gift = giftInventory.getEntities().get(0);
                } catch (Exception e) {
                    errorLabel.setVisible(true);
                    errorLabel.setText("Please select a gift first!");
                    return;
                }

                if (amountField.getText().isEmpty()) {
                    errorLabel.setVisible(true);
                    errorLabel.setText("Amount cannot be empty!");
                    return;
                }

                int amount = Integer.parseInt(amountField.getText());
                if (amount == 0) {
                    errorLabel.setVisible(true);
                    errorLabel.setText("Amount cannot be zero!");
                    return;
                }

                ClientApp.getActiveGame().getCurrentPlayer().getComponent(Inventory.class).addItem(gift);
                giftInventory.getItem(gift);
                amountField.setText("");

                if (giftedOne instanceof NPC) {
                    Result result = controller.giftNPC(((NPC) giftedOne).getName(), gift.getEntityName(), amount);
                    if (!result.isSuccessful()) {
                        errorLabel.setVisible(true);
                        errorLabel.setText(result.message());
                        return;
                    } else {
                        errorLabel.setVisible(false);
                        Actor current = table;
                        while (current != null && !(current instanceof InGameDialog)) {
                            current = current.getParent();
                        }
                        if (current != null) {
                            ((InGameDialog) current).hide();
                        }

                        showNPCDialog(((NPC) giftedOne), "Thanks for the gift!");
                        return;
                    }

                } else if (giftedOne instanceof Player) {
                    Result result = controller.canGiveGift(((Player) giftedOne).getUsername(), gift.getEntityName(), amount);
                    if (!result.isSuccessful()) {
                        errorLabel.setVisible(true);
                        errorLabel.setText(result.message());
                        return;
                    } else {
                        errorLabel.setVisible(true);
                        errorLabel.setColor(Color.GREEN);
                        errorLabel.setText(result.message());
                        GameController.giftPlayer((Player) giftedOne, gift.getEntityName(), amount);
                        return;
                    }
                }
            }
        });

        openMenuWithInventory(table);
    }

    private void openQuestMenu(Quest quest) {
        InGameDialog dialog = new InGameDialog(uiStage);
        dialog.setBackground((Drawable) null);

        TabWidget tabWidget = new TabWidget();


        Table table = new Table();
        Game game = ClientApp.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();

        if (quest.isCompleted()) {
            if (currentPlayer.getUsername().equals(quest.getDoneByPlayerName())) {
                Label messageLabel = new Label("You have already did this Quest!", customSkin);
                messageLabel.setColor(Color.GREEN);
                table.add(messageLabel).pad(3).growX();
            } else {
                Label messageLabel = new Label("This Quest has been completed already by:", customSkin);
                messageLabel.setColor(Color.RED);
                Label playerName = new Label(quest.getDoneByPlayerName(), customSkin);
                playerName.setColor(Color.BLACK);

                table.add(messageLabel).pad(3).center();
                table.add(playerName).pad(3).center();
            }

        } else {
            if (quest.doesHaveAccess(currentPlayer).isSuccessful()) {
                Label messageLabel = new Label("Do you want to complete this Quest?", customSkin);
                TextButton yesButton = new TextButton("Yes", customSkin);
                yesButton.setColor(Color.GREEN);

                table.add(messageLabel).pad(3).center().row();
                table.add(yesButton).pad(3).growX();

                yesButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Result result = controller.questFinish(quest.getId());
                        if (result.isSuccessful()) {
                            showNPCDialog(quest.getNpc(), "Congratulations! You have completed this Quest!");
                            dialog.hide();
                            return;
                        } else {
                            showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                            dialog.hide();
                        }
                    }
                });
            } else {
                Label messageLabel = new Label(quest.doesHaveAccess(currentPlayer).message(), customSkin);
                messageLabel.setColor(Color.RED);

                table.add(messageLabel).pad(5).growX();
            }
        }

        tabWidget.addTab(table, customSkin.getDrawable("skillMenuIcon"));
        dialog.add(tabWidget).fill().grow();

        dialog.show();
    }

    public void showNPCDialog(NPC npc, String message) {
        // Root table aligned to bottom
        Table dialogTable = new Table();
        dialogTable.setFillParent(true);
        dialogTable.bottom().pad(10); // Align to bottom with optional padding

        // --- Avatar image
        Image npcAvatar = new Image(GameAssetManager.getInstance().getTexture(npc.getAvatarPath()));

        // --- Dialog background with label
        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(new TextureRegion(GameAssetManager.getInstance().textBox));
        Table dialogBox = new Table();
        dialogBox.setBackground(bgDrawable);
        dialogBox.pad(10); // inner padding inside background

        // Dialog text
        Label dialogLabel;
        if (message == null) dialogLabel = new Label(controller.meetNPC(npc.getName()).message(), customSkin);
        else dialogLabel = new Label(message, customSkin);

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

    public void openDataMenu() {
        InGameDialog dialog = new InGameDialog(uiStage);

        TabWidget tabWidget = new TabWidget();

        // Friendship tab
        Table friendshipTable = new Table();
        Game game = ClientApp.getActiveGame();
        Player currentPlayer = game.getCurrentPlayer();
        ArrayList<PlayerFriendship> playerFriendships = game.getCurrentPlayerFriendships();
        for (PlayerFriendship playerFriendship : playerFriendships) {
            Player friend = game.getPlayerByUsername(playerFriendship.getFriends().get(1));
            if (friend.equals(currentPlayer)) {
                friend = game.getPlayerByUsername(playerFriendship.getFriends().get(0));
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
                    openPlayerGiftMenu(finalFriend);
                }
            });

            friendshipTable.add(label).left();
            friendshipTable.add(giftImage).right().pad(5);
            friendshipTable.add(friendshipDetail).right().pad(5).row();

        }


        //plant info table
        Table craftTable = new Table();
        Label label = new Label("Enter your crop name to get info: ", customSkin);
        TextField cropNameField = new TextField("", customSkin);
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

        // Trade Tab
        Table tradeTable = new Table();
        TextButton tradeButton = new TextButton("Trade with", customSkin);
        TextButton tradeHistoryButton = new TextButton("Trade History", customSkin);

        tradeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Table contentTable = new Table();
                Label label = new Label("Who do you want to start trade?", customSkin);
                contentTable.add(label).growX().row();

                for (Player player1 : ClientApp.getActiveGame().getPlayers()) {
                    if (player1.equals(currentPlayer)) continue;

                    TextButton playerNameButton = new TextButton(player1.getUsername(), customSkin);
                    playerNameButton.addListener(new ClickListener() {
                        public void clicked(InputEvent event, float x, float y) {
                            GameController.startTradeWithPlayer(player1);

                            Actor current = contentTable;
                            while (current != null && !(current instanceof InGameDialog)) {
                                current = current.getParent();
                            }
                            if (current != null) ((InGameDialog) current).hide();
                        }
                    });
                    contentTable.add(playerNameButton).growX().row();
                }

                dialog.hide();
                showTable(contentTable);

            }
        });
        tradeHistoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Table contentTable = new Table(customSkin);
                ArrayList<TradeHistoryItem> trades = currentPlayer.getTradeHistory();

                // 1) Header row
                contentTable.add("ID").pad(5).center();
                contentTable.add("Sender").pad(5).center();
                contentTable.add("Receiver").pad(5).center();
                contentTable.add("Sender Inv").pad(5).center();
                contentTable.add("Receiver Inv").pad(5).center();
                contentTable.add("Accepted").pad(5).center();
                contentTable.add("Date").pad(5).center();
                contentTable.row();

                // 2) Sort trades from newest to oldest by ID (or by date if you prefer)
                trades.sort((a, b) -> Integer.compare(b.getId(), a.getId()));

                // 3) Create an inner table for the rows
                Table rowsTable = new Table(customSkin);
                for (TradeHistoryItem t : trades) {
                    rowsTable.add(String.valueOf(t.getId())).pad(5);
                    rowsTable.add(t.getSender().getUsername()).pad(5);
                    rowsTable.add(t.getReceiver().getUsername()).pad(5);
                    rowsTable.add(t.getSenderInventory().toString()).pad(5);
                    rowsTable.add(t.getReceiverInventory().toString()).pad(5);
                    rowsTable.add(t.isAccepted() ? "Yes" : "No").pad(5);
                    rowsTable.add(t.getDate().toString()).pad(5);
                    rowsTable.row();
                }

                // 4) Wrap rows into a ScrollPane and add it
                ScrollPane scroll = new ScrollPane(rowsTable);
                scroll.setFadeScrollBars(true);
                scroll.setScrollingDisabled(false, false);

                // Make the scroll take up all available space
                contentTable.add(scroll).colspan(7).expand().fill();

                // 5) Finally show it
                showTable(contentTable);

            }
        });

        tradeTable.add(tradeButton).pad(3).growX().row();
        tradeTable.add(tradeHistoryButton).pad(3).growX().row();

        tabWidget.addTab(friendshipTable, customSkin.getDrawable("skillMenuIcon")); //TODO: change them
        tabWidget.addTab(craftTable, customSkin.getDrawable("skillMenuIcon"));
        tabWidget.addTab(tradeTable, customSkin.getDrawable("skillMenuIcon"));

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
                Result result = controller.canHug(friend.getUsername());
                if (result.isSuccessful()) {
                    JSONMessage message = new JSONMessage(JSONMessage.Type.request);
                    message.put("command", "hug");
                    message.put("sender", game.getCurrentPlayer().getUsername());
                    message.put("receiver", friend.getUsername());

                    ClientApp.sendTCP(message);

                } else {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                }
                hideTable(actionsTable);
            }
        });

        flowerButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Result result = controller.canFlower(friend.getUsername());
                if (result.isSuccessful()) {
                    JSONMessage message = new JSONMessage(JSONMessage.Type.request);
                    message.put("command", "flower");
                    message.put("sender", game.getCurrentPlayer().getUsername());
                    message.put("receiver", friend.getUsername());

                    ClientApp.sendTCP(message);
                } else {
                    showTemporaryMessage(result.message(), ERROR_MESSAGE_DELAY, Color.RED);
                }
                hideTable(actionsTable);

            }
        });

        MarryButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                openMarriageDialog(friend);
            }
        });

        actionsTable.add(hugButton).pad(5).growX().row();
        actionsTable.add(flowerButton).pad(5).growX().row();
        actionsTable.add(MarryButton).pad(5).growX().row();

        showTable(actionsTable);

    }

    public void openMarriageDialog(Player friend) {
        Table table = new Table();
        TextField ringNameField = new TextField("", customSkin);
        ringNameField.setMessageText("Enter the ring name...");
        Label errorLabel = new Label("", customSkin);
        errorLabel.setVisible(false);
        errorLabel.setColor(Color.RED);
        TextButton marriageButton = new TextButton("Marry \"" + friend.getUsername() + "\"", customSkin);

        marriageButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (ringNameField.getText().isEmpty()) {
                    errorLabel.setVisible(true);
                    errorLabel.setText("Please enter a ring name.");
                    return;
                }

                Result result = controller.canAskMarriage(friend.getUsername(), ringNameField.getText());
                if (!result.isSuccessful()) {
                    errorLabel.setText(result.message());
                    errorLabel.setVisible(true);

                } else {
                    JSONMessage message = new JSONMessage(JSONMessage.Type.request);
                    message.put("command", "ask_marriage");
                    message.put("sender", game.getCurrentPlayer().getUsername());
                    message.put("receiver", friend.getUsername());

                    ClientApp.sendTCP(message);
                    hideTable(table);
                }
            }
        });


        table.add(ringNameField).pad(5).growX().row();
        table.add(marriageButton).pad(5).growX().row();
        showTable(table);
    }

    public void openAskMarriageDialog(Player suitor) {
        Player currentPlayer = game.getCurrentPlayer();
        Entity ring = currentPlayer.getSuitors().get(suitor);

        Table table = new Table();
        Label label = new Label("Do you want to marry \"" + suitor.getUsername() + "\" ?", customSkin);
        Label ringName = new Label(ring.getEntityName(), customSkin);
        ringName.setColor(Color.GOLDENROD);
        Image ringImage = new Image(GameAssetManager.getInstance()
            .getTexture(ring.getComponent(Pickable.class).getIcon()));
        TextButton yesButton = new TextButton("Yes", customSkin);
        TextButton noButton = new TextButton("No", customSkin);
        yesButton.setColor(Color.GREEN);
        noButton.setColor(Color.RED);

        yesButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                JSONMessage message = new JSONMessage(JSONMessage.Type.request);
                message.put("command", "accept_marriage");
                message.put("sender", game.getCurrentPlayer().getUsername());
                message.put("receiver", suitor.getUsername());

                ClientApp.sendTCP(message);
                hideTable(table);
            }
        });
        noButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                JSONMessage message = new JSONMessage(JSONMessage.Type.request);
                message.put("command", "reject_marriage");
                message.put("sender", game.getCurrentPlayer().getUsername());
                message.put("receiver", suitor.getUsername());

                ClientApp.sendTCP(message);
                hideTable(table);
            }
        });

        table.add(label).colspan(2).growX().row();
        table.add(ringName).pad(2).right();
        table.add(ringImage).pad(2).center();
        table.add(yesButton).pad(5).center().growX();
        table.add(noButton).pad(5).center().growX();
        table.row();

        showTable(table);
    }

    public void openCheatMenu() {
        InGameDialog dialog = new InGameDialog(uiStage);
        dialog.setBackground((Drawable) null);
        Player currentPlayer = game.getCurrentPlayer();

        TabWidget tabWidget = new TabWidget();

        // Energy Tab
        Table energyTable = new Table();
        Label energyLabel = new Label("cheat add energy", customSkin);
        TextField energyField = new TextField("", customSkin);
        energyField.setMessageText("Enter the energy...");
        TextButton energyButton = new TextButton("Confirm", customSkin);

        energyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int amount;
                try {
                    amount = Integer.parseInt(energyField.getText());
                } catch (Exception e) {
                    return;
                }
                JSONMessage message = new JSONMessage(JSONMessage.Type.cheat);
                message.put("command", "energy");
                message.put("amount", amount);
                message.put("sender", currentPlayer.getUsername());

                ClientApp.sendTCP(message);
                dialog.hide();
            }
        });

        energyTable.add(energyLabel).center().pad(5).growX().row();
        energyTable.add(energyField).pad(5).growX().row();
        energyTable.add(energyButton).pad(5).growX().row();


        // friendship
        Table friendshipTable = new Table();
        Label label = new Label("set friendship", customSkin);
        Label errorLabel = new Label("", customSkin);
        errorLabel.setColor(Color.RED);
        errorLabel.setVisible(false);
        TextField nameField = new TextField("", customSkin);
        nameField.setMessageText("Enter name...");
        TextField levelField = new TextField("", customSkin);
        levelField.setMessageText("Enter level...");
        TextField xpField = new TextField("", customSkin);
        xpField.setMessageText("Enter xp...");
        TextButton nameButton = new TextButton("Confirm", customSkin);

        nameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int level;
                int xp;
                String name;
                try {
                    level = Integer.parseInt(levelField.getText());
                    xp = Integer.parseInt(xpField.getText());
                    name = nameField.getText();
                } catch (NumberFormatException e) {
                    errorLabel.setText("Are you testing errors in CHEAT?");
                    errorLabel.setVisible(true);
                    return;
                }

                Result result = controller.canCheatSetFriendship(level, xp, name);
                if (!result.isSuccessful()) {
                    errorLabel.setText(result.message());
                    errorLabel.setVisible(true);
                } else {
                    JSONMessage message = new JSONMessage(JSONMessage.Type.cheat);
                    message.put("command", "friendship");
                    message.put("sender", currentPlayer.getUsername());
                    message.put("receiver", name);
                    message.put("level", level);
                    message.put("xp", xp);

                    ClientApp.sendTCP(message);
                    dialog.hide();
                }


            }
        });

        friendshipTable.add(label).growX().pad(3).row();
        friendshipTable.add(nameField).growX().pad(3).row();
        friendshipTable.add(levelField).growX().pad(3).row();
        friendshipTable.add(xpField).growX().pad(3).row();
        friendshipTable.add(errorLabel).growX().pad(3).row();
        friendshipTable.add(nameButton).growX().pad(3).row();

        // give item
        Table giveItemTable = new Table();
        Label giveItemLabel = new Label("cheat give item", customSkin);
        Label errorLabelItem = new Label("", customSkin);
        errorLabelItem.setColor(Color.RED);
        errorLabelItem.setVisible(false);
        TextField itemNameField = new TextField("", customSkin);
        itemNameField.setMessageText("item name...");
        TextField amountField = new TextField("", customSkin);
        amountField.setMessageText("Enter amount...");
        TextButton giveItemButton = new TextButton("Confirm", customSkin);

        giveItemButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name;
                int amount;
                try {
                    name = itemNameField.getText();
                    amount = Integer.parseInt(amountField.getText());
                } catch (NumberFormatException e) {
                    errorLabelItem.setText("Are you testing errors in CHEAT?");
                    errorLabelItem.setVisible(true);
                    return;
                }

                Result result = controller.cheatGiveItem(name, amount);
                if (!result.isSuccessful()) {
                    errorLabelItem.setText(result.message());
                    errorLabelItem.setVisible(true);
                } else {
                    JSONMessage message = new JSONMessage(JSONMessage.Type.cheat);
                    message.put("command", "give_item");
                    message.put("name", name);
                    message.put("amount", amount);

                    ClientApp.sendTCP(message);
                    dialog.hide();
                }
            }


        });


        giveItemTable.add(giveItemLabel).growX().pad(3).row();
        giveItemTable.add(itemNameField).growX().pad(3).row();
        giveItemTable.add(amountField).growX().pad(3).row();
        giveItemTable.add(errorLabelItem).growX().pad(3).row();
        giveItemTable.add(giveItemButton).growX().pad(3).row();


        tabWidget.addTab(energyTable, customSkin.getDrawable("skillMenuIcon"));
        tabWidget.addTab(giveItemTable, customSkin.getDrawable("skillMenuIcon"));
        tabWidget.addTab(friendshipTable, customSkin.getDrawable("skillMenuIcon"));
        dialog.add(tabWidget).fill().grow();

        dialog.show();
    }


    /* --- Crafting --- */
    public void openCraftingMenu() {
        InGameDialog craftingDialog = new InGameDialog(uiStage);
        craftingDialog.pad(10);


        Table mainTable = new Table();
        mainTable.top().left();

        Table recipeTable = new Table();
        recipeTable.top().pad(5);

        int itemsPerRow = 5;
        int itemsCount = 0;

        java.util.List<Recipe> recipes = App.recipeRegistry.getRecipesByType(RecipeType.CRAFTING);

        for (Recipe recipe : recipes) {
            String recipeName = recipe.getName();
            boolean isUnlocked = player.hasRecipe(recipe);

            Image itemImage = new Image(recipe.getEntityTexture());
            itemImage.setScaling(Scaling.fit);

            Table recipeButton = new Table();
            recipeButton.setBackground(customSkin.getDrawable("frameNinePatch2"));
            recipeButton.add(itemImage).width(32).height(32).pad(5);

            if (!isUnlocked) {
                recipeButton.setColor(Color.GRAY);
                itemImage.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            } else {
                recipeButton.setColor(Color.WHITE);
                itemImage.setColor(Color.WHITE);
            }

            //TODO : Make ToolTip contents graphical
            ToolTip toolTip = new ToolTip(recipeButton);
            Label toolTipLabel = new Label(recipeName, customSkin);
            toolTipLabel.setText(toolTipLabel.getText() + (isUnlocked ? " (Unlocked)" : " (Locked)"));
            toolTipLabel.setText(toolTipLabel.getText() + "\n" + "Ingredients:\n");
            toolTipLabel.setAlignment(Align.left);

            for (Ingredient ingredient : recipe.getIngredients()) {
                toolTipLabel.setText(toolTipLabel.getText() + "- " + ingredient.toString() + "\n");
            }
            toolTip.add(toolTipLabel).pad(5).grow();

            recipeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Result result = controller.craftingCraft(recipeName); // TODO: do it on server side
                    Image icon;
                    if (result.isSuccessful())
                        icon = new Image(GameAssetManager.getInstance().success);
                    else
                        icon = new Image(GameAssetManager.getInstance().error);

                    PopUpMessage popUp = new PopUpMessage();
                    Label label = new Label(result.message(), customSkin);
                    popUp.add(icon).size(16, 16).pad(5);
                    popUp.add(label).pad(10);
                    popUp.show(uiStage);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    toolTip.show();
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    toolTip.hide();
                }
            });

            recipeTable.add(recipeButton).size(60, 60).pad(2);
            itemsCount++;

            if (itemsCount % itemsPerRow == 0) {
                recipeTable.row();
            }
        }

        ScrollPane recipeScrollPane = new ScrollPane(recipeTable, customSkin);


        /* -- Show Player's Inventory -- */
        Table inventoryPanel = new Table();
        inventoryPanel.setBackground(customSkin.getDrawable("frameNinePatch2"));
        inventoryPanel.add(new InventoryGrid(player.getComponent(Inventory.class), 10)).grow();

        mainTable.add(recipeScrollPane).colspan(2).fillX().height(200).row();
        mainTable.add(inventoryPanel).colspan(2).grow().padTop(10);


        craftingDialog.add(mainTable);
        craftingDialog.show();
    }

    /* --- Cooking --- */
    public void openCookingMenu() {
        InGameDialog cookingDialog = new InGameDialog(uiStage);
        cookingDialog.pad(10);


        Table mainTable = new Table();
        mainTable.top().left();

        Table recipeTable = new Table();
        recipeTable.top().pad(5);

        int itemsPerRow = 5;
        int itemsCount = 0;


        java.util.List<Recipe> recipes = App.recipeRegistry.getRecipesByType(RecipeType.COOKING);

        for (Recipe recipe : recipes) {
            String recipeName = recipe.getName();
            boolean isUnlocked = player.hasRecipe(recipe);

            Image itemImage = new Image(recipe.getEntityTexture());
            itemImage.setScaling(Scaling.fit);

            Table recipeButton = new Table();
            recipeButton.setBackground(customSkin.getDrawable("frameNinePatch2"));
            recipeButton.add(itemImage).width(32).height(32).pad(5);

            if (!isUnlocked) {
                recipeButton.setColor(Color.GRAY);
                itemImage.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            } else {
                recipeButton.setColor(Color.WHITE);
                itemImage.setColor(Color.WHITE);
            }

            //TODO : Make ToolTip contents graphical
            ToolTip toolTip = new ToolTip(recipeButton);
            Label toolTipLabel = new Label(recipeName, customSkin);
            toolTipLabel.setText(toolTipLabel.getText() + (isUnlocked ? " (Unlocked)" : " (Locked)"));
            toolTipLabel.setText(toolTipLabel.getText() + "\n" + "Ingredients:\n");
            toolTipLabel.setAlignment(Align.left);

            for (Ingredient ingredient : recipe.getIngredients()) {
                toolTipLabel.setText(toolTipLabel.getText() + "- " + ingredient.toString() + "\n");
            }
            toolTip.add(toolTipLabel).pad(5).grow();

            recipeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Result result = controller.cookingPrepare(recipeName); // TODO: do it on server side
                    Image icon;
                    if (result.isSuccessful())
                        icon = new Image(GameAssetManager.getInstance().success);
                    else
                        icon = new Image(GameAssetManager.getInstance().error);

                    PopUpMessage popUp = new PopUpMessage();
                    Label label = new Label(result.message(), customSkin);
                    popUp.add(icon).size(16, 16).pad(5);
                    popUp.add(label).pad(10);
                    popUp.show(uiStage);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    toolTip.show();
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    toolTip.hide();
                }
            });

            recipeTable.add(recipeButton).size(60, 60).pad(2);
            itemsCount++;

            if (itemsCount % itemsPerRow == 0) {
                recipeTable.row();
            }
        }

        ScrollPane recipeScrollPane = new ScrollPane(recipeTable, customSkin);


        /* -- Show Player's Inventory -- */
        Table inventoryPanel = new Table();
        inventoryPanel.setBackground(customSkin.getDrawable("frameNinePatch2"));
        inventoryPanel.add(new InventoryGrid(player.getComponent(Inventory.class), 10)).grow();

        mainTable.add(recipeScrollPane).colspan(2).fillX().height(200).row();
        mainTable.add(inventoryPanel).colspan(2).grow().padTop(10);

        cookingDialog.add(mainTable).grow();
        cookingDialog.show();
    }


    /* --- Artisan Menu --- */
    public void openArtisanMenu(Entity artisanEntity) {
        ArtisanMenuContent content = new ArtisanMenuContent(this, artisanEntity);
        InGameDialog artisanDialog = new InGameDialog(uiStage);
        artisanDialog.add(content).grow();
        artisanDialog.show();

    }


    public void openPlayerGiftMenu(Player friend) {
        InGameDialog dialog = new InGameDialog(uiStage);

        TabWidget tabWidget = new TabWidget();

        // send gift
        Table sendGiftTable = new Table();

        TextButton sendGiftButton = new TextButton("Send Gift", customSkin);
        sendGiftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openSendGiftMenu(friend);
            }
        });

        sendGiftTable.add(sendGiftButton).growX().pad(3);

        // gift History
        Table giftHistoryTable = new Table();
        TextButton giftHistory = new TextButton("Gift history", customSkin);
        TextButton giftList = new TextButton("Gift received", customSkin);

        giftHistory.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Table table = new Table();
                Label label = new Label(controller.giftHistory(friend.getUsername()).message(), customSkin);
                table.add(label).pad(3);
                showTable(table);
            }
        });

        giftList.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Table table = new Table();
                Label label = new Label(controller.giftList().message(), customSkin);
                table.add(label).pad(3);
                showTable(table);
            }
        });

        giftHistoryTable.add(giftHistory).growX().pad(3).row();
        giftHistoryTable.add(giftList).growX().pad(3);

        // rate gift
        Table rateGift = new Table();

        Label rateLabel = new Label("Enter the Gift ID and your Rating: ", customSkin);
        Label errorLabel = new Label("", customSkin);
        errorLabel.setColor(Color.RED);
        errorLabel.setVisible(false);
        TextField giftId = new TextField("", customSkin);
        giftId.setMessageText("Gift ID...");
        TextField ratingField = new TextField("", customSkin);
        ratingField.setMessageText("Rating");

        giftId.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return Character.isDigit(c);
            }
        });

        ratingField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return Character.isDigit(c);
            }
        });
        TextButton confirmButton = new TextButton("Confirm", customSkin);

        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (giftId.getText().isEmpty()) {
                    errorLabel.setVisible(true);
                    errorLabel.setText("Enter the Gift ID");
                    return;
                } else if (ratingField.getText().isEmpty()) {
                    errorLabel.setVisible(true);
                    errorLabel.setText("Enter the Rating");
                    return;
                }

                int id = Integer.parseInt(giftId.getText());
                int rating = Integer.parseInt(ratingField.getText());
                Result result = controller.canRateGift(id, rating);
                if (!result.isSuccessful()) {
                    errorLabel.setVisible(true);
                    errorLabel.setText(result.message());
                    return;
                } else {
                    GameController.rateGift(id, rating, friend);
                    dialog.hide();
                    showTemporaryMessage(result.message(), 5, Color.CYAN);
                }
            }
        });

        rateGift.add(rateLabel).colspan(2).grow().pad(3).row();
        rateGift.add(giftId).grow().pad(3);
        rateGift.add(ratingField).grow().pad(3).row();
        rateGift.add(errorLabel).colspan(2).grow().pad(3).row();
        rateGift.add(confirmButton).colspan(2).pad(3).row();


        tabWidget.addTab(sendGiftTable, customSkin.getDrawable("skillMenuIcon"));
        tabWidget.addTab(giftHistoryTable, customSkin.getDrawable("skillMenuIcon"));
        tabWidget.addTab(rateGift, customSkin.getDrawable("skillMenuIcon"));

        dialog.add(tabWidget).fill().grow();

        dialog.show();
    }

    /**
     * This will show table in InGameDialog
     *
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

    public void hideTable(Table table) {
        Actor current = table;
        while (current != null && !(current instanceof InGameDialog)) {
            current = current.getParent();
        }
        if (current != null) {
            ((InGameDialog) current).hide();
        }
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

    public Stage getUiStage() {
        return uiStage;
    }

    private void setGameInput() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(uiStage);
        inputMultiplexer.addProcessor(frontStage);
        inputMultiplexer.addProcessor(gameStage);
        inputMultiplexer.addProcessor(playerController);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }


    private void initialCheats() {

/// IMPORTANT do it in server


        player.addRecipe("Preserves Jar");
//        controller.cheatGiveItem("Training Rod", 1);
//        controller.cheatGiveItem("Hay", 500);
//        controller.cheatGiveItem("Wood", 50);
//        controller.cheatGiveItem("Stone", 40);
//        controller.cheatGiveItem("Coal", 8);
//        controller.cheatGiveItem("Axe", 1);
//        controller.cheatGiveItem("vegetable", 1);
//        controller.cheatGiveItem("Cherry", 10);
//        controller.cheatGiveItem("Pickaxe", 1);
//        controller.cheatGiveItem("Hoe", 1);
//        controller.cheatGiveItem("Hay", 500);
//        controller.cheatGiveItem("Apple", 10);
//        controller.cheatGiveItem("Bee House", 1);
//        controller.cheatGiveItem("Frozen Tear", 10);
//        controller.cheatGiveItem("Cheese Press", 1);
//        controller.cheatGiveItem("Keg", 1);
//        controller.cheatGiveItem("Dehydrator", 1);
//        controller.cheatGiveItem("Charcoal Klin", 1);
//        controller.cheatGiveItem("Loom", 1);
//        controller.cheatGiveItem("Mayonnaise Machine", 1);
//        controller.cheatGiveItem("Oil Maker", 1);
//        controller.cheatGiveItem("Preserves Jar", 1);
//        controller.cheatGiveItem("Fish Smoker", 1);
//        controller.cheatGiveItem("Furnace", 1);
//
//        controller.cheatGiveItem("Hay", 500);
//        controller.cheatAddSkill("fishing", 200);
//        controller.cheatAddSkill("fishing", 200);
//        controller.cheatAddSkill("fishing", 200);
//        controller.cheatAddSkill("fishing", 200);
//        controller.cheatAddSkill("fishing", 200);
    }
}
