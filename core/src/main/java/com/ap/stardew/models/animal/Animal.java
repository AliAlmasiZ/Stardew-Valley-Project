package com.ap.stardew.models.animal;

import com.ap.stardew.models.App;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.entities.Entity;
import com.ap.stardew.models.entities.components.*;
import com.ap.stardew.models.enums.ProductQuality;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.views.old.inGame.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;
import java.util.ArrayList;

public class Animal extends Entity implements Serializable {
    private AnimalType animalType;
    private String name;
    private Entity todayProduct;
    private int daysPastLastProduce;

    // friendship
    private boolean isPetToday = false;
    private boolean isFedToday = false;
    private int friendshipLevel = 0;

    // Animal movement
    private float timeLeftToMove = 5;
    private Vector2 destination = new Vector2();

    public Animal(AnimalType animalType, String name) {
        super(animalType.name().toLowerCase());
        this.addComponent(new Renderable('A', new Color(255, 255, 255)));
        Renderable renderable = this.getComponent(Renderable.class);
        /*TODO: Check this later*/
        this.addComponent(new Placeable(true));
        renderable.setSprite(new Sprite(new Texture("Content/Animal/" + animalType.name().toLowerCase() + "/normal.png")));
        renderable.setEatingSprites(new Texture("Content/Animal/" + animalType.name().toLowerCase() + "/eating.png"), 4);
        renderable.setPetSprites(new Texture("Content/Animal/" + animalType.name().toLowerCase() + "/pet.png"), 4);
        renderable.setWalkingSprites(new Texture("Content/Animal/" + animalType.name().toLowerCase() + "/walking.png"), 4);
        renderable.getSprite().setSize(32, 32);
        this.name = name;
        this.animalType = animalType;
    }

    public Animal(AnimalType animalType) {
        this(animalType, null);
    }

    public AnimalType getAnimalType() {
        return animalType;
    }

    public void setAnimalType(AnimalType animalType) {
        this.animalType = animalType;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getProducts() {
        return this.animalType.getProducts();
    }

    public Entity getTodayProduct() {
        return todayProduct;
    }

    public void setTodayProduct(Entity todayProduct) {
        this.todayProduct = todayProduct;
    }

    public int getDaysBetweenProduces() {
        return this.animalType.getDaysBetweenProduces();
    }

    public int getDaysPastLastProduce() {
        return daysPastLastProduce;
    }

    public void setDaysPastLastProduce(int daysPastLastProduce) {
        this.daysPastLastProduce = daysPastLastProduce;
    }

    public boolean isPetToday() {
        return isPetToday;
    }

    public void setPetToday(boolean petToday) {
        isPetToday = petToday;
    }

    public boolean isFedToday() {
        return isFedToday;
    }

    public void setFedToday(boolean fedToday) {
        isFedToday = fedToday;
    }

    public int getFriendshipLevel() {
        return friendshipLevel;
    }

    public void setFriendshipLevel(int friendshipLevel) {
        this.friendshipLevel = friendshipLevel;
    }

    public void addFriendshipLevel(int amount) {
        this.friendshipLevel += amount;
        if (this.friendshipLevel > 1000) {
            this.friendshipLevel = 1000;
        }

    }

    public void reduceFriendshipLevel(int amount) {
        this.friendshipLevel -= amount;
        if (this.friendshipLevel < 0) {
            this.friendshipLevel = 0;
        }
    }

    public void updatePerDay() {
        setupTodayProduct();

        if (!isFedToday) {
            reduceFriendshipLevel(20);
        }
        isFedToday = false;

        if (!isPetToday) {
            reduceFriendshipLevel(10);
        }
        isPetToday = false;

        if (getComponent(PositionComponent.class).getMap().getBuilding() == null) {
            reduceFriendshipLevel(20);
        }
    }

    public void setupTodayProduct() {
        Entity todayProduct = null;
        if (daysPastLastProduce < getDaysBetweenProduces()) {
            daysPastLastProduce++;
            return;
        }

        if (isFedToday) {
            if (friendshipLevel > 100 && getProducts().size() > 1) {
                double rand = Math.random() + 0.5;
                double probability = (friendshipLevel + 150 * rand) / 1500;
                if (Math.random() < probability) {
                    todayProduct = produceProduct(1);
                } else {
                    todayProduct = produceProduct(0);
                }
            } else {
                todayProduct = produceProduct(0);
            }

        }

        setTodayProduct(todayProduct);
    }

    public Entity produceProduct(int whichOne) {
        double quality = ((double) friendshipLevel / 1000) * (0.5 + 0.5 * Math.random());
        Entity product = App.entityRegistry.makeEntity(getProducts().get(whichOne));
        product.getComponent(Sellable.class).setProductQuality(ProductQuality.getQuality(quality));

        product.getComponent(Pickable.class).setStackSize(1);
        return product;
    }

    public String getDetail() {
        StringBuilder result = new StringBuilder();
        result.append("Type: ").append(animalType).append("\n");
        result.append("Name: ").append(name).append("\n");
        result.append("IsPetToday: ").append(isPetToday).append("\n");
        result.append("IsFedToday: ").append(isFedToday).append("\n");
        result.append("FriendshipLevel: ").append(friendshipLevel).append("\n");

        return result.toString();
    }

    public int calculatePrice() {
        int price = 0;
        price = animalType.getCost();

        price = (int) (price * (0.3 + (getFriendshipLevel() / 1000)));
        return price;
    }

    public static Entity getHouse(Animal animal, Player player) {
        for (Entity building : player.getOwnedBuildings()) {
            AnimalHouse animalHouse = building.getComponent(AnimalHouse.class);
            if (animalHouse != null) {
                if (animalHouse.getAnimals().contains(animal)) {
                    return building;
                }
            }
        }
        return null;
    }

    public void renderUpdate(float delta) {
        //movement Update TODO: check that it doesnt go to baghalies
        PositionComponent positionComponent = getComponent(PositionComponent.class);
        if (timeLeftToMove > 0 && getComponent(Renderable.class).getCurrentStatue() == Renderable.Statue.NORMAL) {
            timeLeftToMove -= delta;
            if (timeLeftToMove <= 0) {
                Vector2 movementVector = new Vector2();
                movementVector.x = (MathUtils.random() - 0.5f) * 80;
                movementVector.y = (MathUtils.random() - 0.5f) * 80;
                destination.x = (float) (positionComponent.getX() + movementVector.x);
                destination.y = (float) (positionComponent.getY() + movementVector.y);
                if (movementVector.x > 0) {
                    getComponent(Renderable.class).setStatue(Renderable.Statue.RIGHT_WALKING, 10000);
                } else {
                    getComponent(Renderable.class).setStatue(Renderable.Statue.LEFT_WALKING, 10000);
                }

            }

        } else if (timeLeftToMove <= 0) {
            Position position = positionComponent.get();
            int moveFactor = 10;
            Vector2 movementVector = new Vector2();
            movementVector.x = destination.x - (float) positionComponent.getX();
            movementVector.y = destination.y - (float) positionComponent.getY();
            if (movementVector.len() < 0.1f) {
                timeLeftToMove = 30;
                getComponent(Renderable.class).setStatue(Renderable.Statue.NORMAL, 0);
            }
            movementVector.nor();
            position.add(movementVector.x * delta * moveFactor, movementVector.y * delta * moveFactor);
        }
    }

    public void move(float x, float y) {
        PositionComponent positionComponent = getComponent(PositionComponent.class);
        destination.x = (float) (positionComponent.getX() + x);
        destination.y = (float) (positionComponent.getY() + y);
        if (x > 0) {
            getComponent(Renderable.class).setStatue(Renderable.Statue.RIGHT_WALKING, 10000);
        } else {
            getComponent(Renderable.class).setStatue(Renderable.Statue.LEFT_WALKING, 10000);
        }
        timeLeftToMove = 0;
    }
}
