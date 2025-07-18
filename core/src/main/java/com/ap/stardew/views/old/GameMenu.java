package com.ap.stardew.views.old;

import com.ap.stardew.controllers.GameMenuController;
import com.ap.stardew.models.App;
import com.ap.stardew.models.Commands.GameMenuCommands;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.Position;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.enums.Direction;
import com.ap.stardew.models.enums.SkillType;
import com.ap.stardew.models.gameMap.GameMap;
import com.ap.stardew.models.gameMap.Tile;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.models.player.Skill;
import com.ap.stardew.records.Result;
import com.ap.stardew.records.WalkProposal;

import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameMenu implements AppMenu {
    private final GameMenuController controller = new GameMenuController();
    private Result previousResult = null;

    private enum MapRenderType {
        DEFAULT,
        REGIONS
    }

    MapRenderType mapRenderType = MapRenderType.DEFAULT;

    @Override
    public void checker(Scanner scanner) {
        renderGame();

        previousResult = null;

        if (!scanner.hasNextLine()) return;
        String input = scanner.nextLine().trim();
        Matcher matcher;
        if (GameMenuCommands.DATE.getMatcher(input) != null) {
            App.getView().log(controller.getDate());

        } else if (GameMenuCommands.DATETIME.getMatcher(input) != null) {
            App.getView().log(controller.getDateTime());

        } else if (GameMenuCommands.TIME.getMatcher(input) != null) {
            App.getView().log(controller.getTime());

        } else if (GameMenuCommands.DAY_OF_THE_WEEK.getMatcher(input) != null) {
            App.getView().log(controller.getDayOfTheWeek());

        } else if ((matcher = GameMenuCommands.ADVANCE_TIME.getMatcher(input)) != null) {
            int amount = Integer.parseInt(matcher.group(1));
            App.getView().log(controller.advanceTime(amount));

        } else if ((matcher = GameMenuCommands.ADVANCE_DATE.getMatcher(input)) != null) {
            int amount = Integer.parseInt(matcher.group(1));
            App.getView().log(controller.advanceDate(amount));

        } else if (GameMenuCommands.SEASON.getMatcher(input) != null) {
            App.getView().log(controller.showSeason());

        } else if (GameMenuCommands.WEATHER.getMatcher(input) != null) {
            App.getView().log(controller.showWeather());

        } else if (GameMenuCommands.WEATHER_FORECAST.getMatcher(input) != null) {
            App.getView().log(controller.weatherForecast());

        } else if ((matcher = GameMenuCommands.SET_WEATHER.getMatcher(input)) != null) {
            App.getView().log(controller.setWeather(matcher.group("type")));

        } else if ((matcher = GameMenuCommands.GREEN_HOUSE_BUILD.getMatcher(input)) != null) {
            App.getView().log(/*TODO*/);

        } else if ((matcher = GameMenuCommands.WALK.getMatcher(input)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            handleWalk(x, y, scanner);

        } else if ((matcher = GameMenuCommands.REFRIGERATOR.getMatcher(input)) != null) {
            App.getView().log(controller.putInFridge(matcher.group(2), Integer.parseInt(matcher.group(3)),
                    matcher.group(1).equals("put")));

        } else if (GameMenuCommands.ENERGY_SHOW.getMatcher(input) != null) {
            App.getView().log(controller.energyShow());

        } else if ((matcher = GameMenuCommands.ENERGY_SET.getMatcher(input)) != null) {
            App.getView().log(controller.energySet(Integer.parseInt(matcher.group(1))));

        } else if (GameMenuCommands.ENERGY_UNLIMITED.getMatcher(input) != null) {
            App.getView().log(controller.energyUnlimited());

        }else if ((matcher = GameMenuCommands.CRAFTINFO.getMatcher(input)) != null) {
            App.getView().log(controller.craftInfo(matcher.group(1).trim()));

        } else if (input.toLowerCase().equals("tm") || (matcher = GameMenuCommands.TOGGLE_MAP.getMatcher(input)) != null) {
            App.getView().log(controller.toggleMap());

        } else if ((matcher = GameMenuCommands.SHOW_INVENTORY.getMatcher(input)) != null) {
            App.getView().log(App.getActiveGame().getCurrentPlayer().getComponent(Inventory.class).toString());

        } else if ((matcher = GameMenuCommands.CHEAT_GIVE_ITEM.getMatcher(input)) != null) {
            App.getView().log(controller.cheatGiveItem(
                    matcher.group("name"),
                    Integer.parseInt(matcher.group("quantity"))
            ));

        } else if ((matcher = GameMenuCommands.PLANT_SEED.getMatcher(input)) != null) {
            App.getView().log(controller.plant(matcher.group(1), matcher.group(2)));

        } else if ((matcher = GameMenuCommands.TOGGLE_UNLIMITED_INVENTORY.getMatcher(input)) != null) {
            App.getView().log(controller.toggleUnlimitedInventory());

        } else if ((matcher = GameMenuCommands.SHOW_PLANT.getMatcher(input)) != null) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            App.getView().log(controller.showPlant(x, y));

        } else if ((matcher = GameMenuCommands.NEXT_TURN.getMatcher(input)) != null) {
            App.getView().log(controller.nextTurn());

        } else if (input.startsWith("tools")) {
            toolsCommandParser(input);

        } else if ((matcher = GameMenuCommands.ARTISAN_USE.getMatcher(input)) != null) {
            App.getView().log(controller.useArtisan(matcher.group("artisanName"), matcher.group("itemName")));

        } else if ((matcher = GameMenuCommands.ARTISAN_GET.getMatcher(input)) != null) {
            App.getView().log(controller.getArtisan(matcher.group("artisanName")));

        } else if ((matcher = GameMenuCommands.FERTILIZE.getMatcher(input)) != null) {
            App.getView().log(controller.fertilize(matcher.group(1).trim(), matcher.group(2)));

        } else if ((matcher = GameMenuCommands.TALK.getMatcher(input)) != null) {
            App.getView().log(controller.talk(matcher.group(1).trim(), matcher.group(2).trim()));

        } else if ((matcher = GameMenuCommands.TALK_HISTORY.getMatcher(input)) != null) {
            App.getView().log(controller.talkHistory(matcher.group(1).trim()));

        } else if (GameMenuCommands.FRIENDSHIPS.getMatcher(input) != null) {
            App.getView().log(controller.friendship());

        } else if ((matcher = GameMenuCommands.GIFT.getMatcher(input)) != null) {
            App.getView().log(controller.giveGift(matcher.group(1).trim(), matcher.group(2).trim(),
                    Integer.parseInt(matcher.group(3))));

        } else if (GameMenuCommands.GIFT_LIST.getMatcher(input) != null) {
            App.getView().log(controller.giftList());

        } else if ((matcher = GameMenuCommands.GIFT_RATE.getMatcher(input)) != null) {
            App.getView().log(controller.giftRate(Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2))));

        } else if ((matcher = GameMenuCommands.GIFT_HISTORY.getMatcher(input)) != null) {
            App.getView().log(controller.giftHistory(matcher.group(1).trim()));

        } else if ((matcher = GameMenuCommands.HUG.getMatcher(input)) != null) {
            App.getView().log(controller.hug(matcher.group(1).trim()));

        } else if ((matcher = GameMenuCommands.FLOWER.getMatcher(input)) != null) {
            App.getView().log(controller.flower(matcher.group(1).trim()));

        } else if ((matcher = GameMenuCommands.EAT_FOOD.getMatcher(input)) != null) {
            App.getView().log(controller.eat(matcher.group("foodName")));

        } else if ((matcher = GameMenuCommands.HELP_READING_MAP.getMatcher(input)) != null) {
            App.getView().log(controller.helpReadingMap());
        }
        /* ------------------------------------------ Recipe Commands ------------------------------------------ */
        else if ((matcher = GameMenuCommands.CRAFTING_SHOW_RECIPES.getMatcher(input)) != null) {
            App.getView().log(controller.showRecipes("crafting"));

        } else if ((matcher = GameMenuCommands.CRAFTING_CRAFT.getMatcher(input)) != null) {
            App.getView().log(controller.craftingCraft(matcher.group("itemName")));

        } else if ((matcher = GameMenuCommands.COOKING_SHOW_RECIPES.getMatcher(input)) != null) {
            App.getView().log(controller.showRecipes("cooking"));

        } else if ((matcher = GameMenuCommands.COOKING_PREPARE.getMatcher(input)) != null) {
            App.getView().log(controller.cookingPrepare(matcher.group("recipeName")));

            App.getView().log();
        }
        /* -------------------------------------------------- -------------------------------------------------- */

        /* ------------------------------------------- NPC Commands -------------------------------------------- */
        else if ((matcher = GameMenuCommands.MEET_NPC.getMatcher(input)) != null) {
            App.getView().log(controller.meetNPC(matcher.group(1).trim()));

        } else if ((matcher = GameMenuCommands.GIFT_NPC.getMatcher(input)) != null) {
            App.getView().log(controller.giftNPC(matcher.group(1).trim(), matcher.group(2).trim()));

        } else if ((matcher = GameMenuCommands.FRIENDSHIP_NPC.getMatcher(input)) != null) {
            App.getView().log(controller.friendshipNPC());

        } else if ((matcher = GameMenuCommands.QUEST_LIST.getMatcher(input)) != null) {
            App.getView().log(controller.questList());

        } else if ((matcher = GameMenuCommands.QUEST_FINISH.getMatcher(input)) != null) {
            App.getView().log(controller.questFinish(Integer.parseInt(matcher.group(1).trim())));
        }
        /* -------------------------------------------------- -------------------------------------------------- */

        /* ------------------------------------------- Animal Commands ----------------------------------------- */
        else if ((matcher = GameMenuCommands.BUY_ANIMAL.getMatcher(input)) != null) {
//                AnimalPurchaseDetails details = controller.buyAnimal(matcher.group(1).trim(), matcher.group(2).trim());
//                App.getView()().log();(details.message());
//                if (details.canBuy()) {
//                    String chosenName = scanner.nextLine();
//                    App.getView()().log();(controller.chooseHouseForAnimal(details, chosenName));
//                }
            App.getView().log(controller.buyAnimal(
                    matcher.group("animalName"),
                    matcher.group("name"),
                    matcher.group("house")
            ));

        }
        else if((matcher = GameMenuCommands.SHOW_MY_ANIMAL_HOUSES.getMatcher(input)) != null) {
            App.getView().log(controller.showMyAnimalHouses());
        }

        else if ((matcher = GameMenuCommands.PET_ANIMAL.getMatcher(input)) != null) {
            App.getView().log(controller.pet(matcher.group(1).trim()));

        } else if ((matcher = GameMenuCommands.SET_ANIMAL_FRIENDSHIP.getMatcher(input)) != null) {
            App.getView().log(controller.setAnimalFriendship(matcher.group(1).trim(),
                    Integer.parseInt(matcher.group(2))));

        } else if (GameMenuCommands.ANIMAL_INFO.getMatcher(input) != null) {
            App.getView().log(controller.animals());

        } else if ((matcher = GameMenuCommands.SHEPHERD_ANIMAL.getMatcher(input)) != null) {
            App.getView().log(controller.shepherdAnimal(matcher.group(1).trim(), Integer.parseInt(matcher.group(2).trim()),
                    Integer.parseInt(matcher.group(3).trim()), false));
        } else if ((matcher = GameMenuCommands.FEED_HAY.getMatcher(input)) != null) {
            App.getView().log(controller.feedHay(matcher.group(1).trim()));

        } else if ((matcher = GameMenuCommands.PRODUCES.getMatcher(input)) != null) {
            App.getView().log(controller.showProduces());

        } else if ((matcher = GameMenuCommands.COLLECT_PRODUCE.getMatcher(input)) != null) {
            App.getView().log(controller.collectProduces(matcher.group(1).trim()));


        } else if ((matcher = GameMenuCommands.SELL_ANIMAL.getMatcher(input)) != null) {
            App.getView().log(controller.sellAnimal(matcher.group(1).trim()));

        } else if ((matcher = GameMenuCommands.CHEAT_TAKE_ITEM.getMatcher(input)) != null) {
            App.getView().log(controller.cheatTakeItem(matcher.group(1).trim(), Integer.parseInt(matcher.group(2).trim())));

        } else if ((matcher = GameMenuCommands.SHOW_FRIDGE_CONTENT.getMatcher(input)) != null) {
            App.getView().log(App.getActiveGame().getCurrentPlayer().getRefrigerator().getComponent(Inventory.class));

        } else if ((matcher = GameMenuCommands.SHOW_SHIPPING_BIN_CONTENT.getMatcher(input)) != null) {
            App.getView().log(controller.showShippingBin());

        } else if ((matcher = GameMenuCommands.FISHING.getMatcher(input)) != null) {
            App.getView().log(controller.fishingPhae1(matcher.group(1).trim()));

        } else if ((matcher = GameMenuCommands.PLACE_ITEM.getMatcher(input)) != null) {
            App.getView().log(controller.placeItem(matcher.group(1).trim(), Integer.parseInt(matcher.group(2))));

        } else if ((matcher = GameMenuCommands.PICK_NEARBY_ITEMS.getMatcher(input)) != null) {
            App.getView().log(controller.pickupNearItems());

        } else if ((matcher = GameMenuCommands.CHEAT_SPAWN_ON_GROUND.getMatcher(input)) != null) {
            App.getView().log(controller.cheatSpawnItem(matcher.group(1), Integer.parseInt(matcher.group(2))));

        }
        /* ------------------------------------------- cheat Commands ------------------------------------------ */
        else if ((matcher = GameMenuCommands.CHEAT_SKILL.getMatcher(input)) != null) {
            App.getView().log(controller.cheatAddSkill(matcher.group(1).trim(),
                    Integer.parseInt(matcher.group(2).trim())));

        } else if ((matcher = GameMenuCommands.SKILL_STATUE.getMatcher(input)) != null) {
            App.getView().log(controller.skillStatue());

        } else if ((matcher = GameMenuCommands.CHEAT_BUILD_BUILDING.getMatcher(input)) != null) {
            App.getView().log(controller.cheatBuildBuilding(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y")),
                    matcher.group("name"), matcher.group("force") != null));
        } else if ((matcher = GameMenuCommands.ADD_MONEY.getMatcher(input)) != null) {
            App.getView().log(controller.addMoney(Integer.parseInt(matcher.group(1).trim())));

        } else if ((matcher = GameMenuCommands.CHEAT_SET_FRIENDSHIP.getMatcher(input)) != null) {
            App.getView().log(controller.cheatSetFriendship(Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)), matcher.group(1).trim()));

        } else if (GameMenuCommands.CHEAT_WATER_ALL.getMatcher(input) != null) {
            App.getView().log(controller.waterAll());

        } else if ((matcher = GameMenuCommands.SELL_PRODUCT.getMatcher(input)) != null) {
            App.getView().log(controller.sellProduct(matcher.group("productName"), matcher.group("count")));

        }



        /* ------------------------------------------- Shop Commands ------------------------------------------- */
        else if ((matcher = GameMenuCommands.SHOW_ALL_AVAILABLE.getMatcher(input)) != null) {
            App.getView().log(controller.showAvailableProducts());
        } else if ((matcher = GameMenuCommands.SHOW_ALL_PRODUCTS.getMatcher(input)) != null) {
            App.getView().log(controller.showAllProducts());
        } else if ((matcher = GameMenuCommands.BUILD_GREENHOUSE.getMatcher(input)) != null) {
            App.getView().log(controller.buildGreenhouse());
        } else if ((matcher = GameMenuCommands.PURCHASE.getMatcher(input)) != null) {
            handlePurchase(
                    matcher.group("productName"),
                    matcher.group("count"),
                    scanner
            );
        } else if((matcher = GameMenuCommands.BUILD_BUILDING.getMatcher(input)) != null) {
            App.getView().log(controller.buildBuilding(
                    Integer.parseInt(matcher.group("x")),
                    Integer.parseInt(matcher.group("y")),
                    matcher.group("buildingName")
            ));
        }
        /* -------------------------------------------------- -------------------------------------------------- */

        else if ((matcher = GameMenuCommands.START_TRADE.getMatcher(input)) != null) {
            App.getView().log(controller.startTrade());

        } else if ((matcher = GameMenuCommands.CHEAT_THOR.getMatcher(input)) != null) {
            App.getView().log(controller.thor(Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2))));

        } else if ((matcher = GameMenuCommands.ASK_MARRIAGE.getMatcher(input)) != null) {
            App.getView().log(controller.askMarriage(matcher.group(1).trim(), matcher.group(2).trim()));

        } else if ((matcher = GameMenuCommands.RESPOND.getMatcher(input)) != null) {
            App.getView().log(controller.respond(matcher.group(1).trim(), matcher.group(2).trim()));

        } else if ((matcher = GameMenuCommands.CHANGE_MAP_RENDER.getMatcher(input)) != null) {
            this.mapRenderType = MapRenderType.values()[(mapRenderType.ordinal() + 1) % MapRenderType.values().length];
        } else if ((matcher = GameMenuCommands.TRASH_ITEM.getMatcher(input)) != null) {
            App.getView().log(controller.trashItem(matcher.group("name").trim(), Integer.parseInt(matcher.group("amount").trim())));
        } else if ((matcher = GameMenuCommands.SAVE_GAME.getMatcher(input)) != null) {
            App.getView().log(controller.saveGame());
        } else {
            App.getView().log("Invalid Command!");
        }
    }

    public void renderGame() {
        Game activeGame = App.getActiveGame();
        if (activeGame.isMapVisible()) {
            printMap(activeGame.getActiveMap());

            Player player = activeGame.getCurrentPlayer();
            System.out.println(player.getComponent(Inventory.class));
            System.out.println("day : " + App.getActiveGame().getDate().getDay() +
                    ", hour : " + App.getActiveGame().getDate().getHour() +
                    ", season : " + App.getActiveGame().getDate().getSeason());
            System.out.println("energy: " + player.getEnergy().getAmount());

            Position position = player.getPosition();
            System.out.println("position: " + position + " " + position.getMap());
            System.out.println("money: " + player.getWallet().getBalance());
            System.out.println("skills:");
            for(Map.Entry<SkillType, Skill> entry : player.getSkills().entrySet()){
                System.out.printf("  %n%s: %d %d", entry.getKey().toString(), entry.getValue().getLevel(), entry.getValue().getExperience());
            }

            System.out.println("\n" + App.getView().getPreviusMessage());
        }
    }

    public void printMap(GameMap map) {
        Tile[][] tiles = map.getTiles();
        Position position = App.getActiveGame().getCurrentPlayer().getPosition();
//        switch (mapRenderType) {
//            case DEFAULT -> {
//                for (Tile[] value : tiles) {
//                    for (Tile tile : value) {
//                        if (tile.getType() == null) continue;
//                        renderer.mvAddchColored(tile.getCol(), tile.getRow(), tile.getCharacter(), tile.getColor(), position);
//                    }
//                }
//                for (Entity e : map.getEntities()) {
//                    PositionComponent positionComponent = e.getComponent(PositionComponent.class);
//
//                    if (e.getComponent(Renderable.class) != null) {
//                        renderer.mvAddchColored(positionComponent.getCol(), positionComponent.getRow(),
//                                e.getComponent(Renderable.class).getCharacter(),
//                                e.getComponent(Renderable.class).getColor(),
//                                position);
//                    }
//                }
//            }
//            case REGIONS -> {
//                if (!(map instanceof WorldMap)) {
//                    break;
//                }
//
//                WorldMap map1 = (WorldMap) map;
//                for (Tile[] value : tiles) {
//                    for (Tile tile : value) {
//                        if (tile.getRegion() != null) {
//                            renderer.mvAddchColored(tile.getCol(), tile.getRow(), '0', tile.getRegion().getColor(), position);
//                        }
//                    }
//                }
//                for (MapRegion r : map1.getRegions()) {
//                    renderer.mvPrint(r.getCenter().getCol(), r.getCenter().getRow(), r.getName(), Color.WHITE, position);
//                    if (r.getOwner() != null) {
//                        renderer.mvPrint(r.getCenter().getCol(), r.getCenter().getRow() + 1, r.getOwner().getAccount().getNickname(), Color.WHITE, position);
//                    } else {
//                        renderer.mvPrint(r.getCenter().getCol(), r.getCenter().getRow() + 1, "no owner", Color.WHITE, position);
//                    }
//                }
//            }
//    }
}

private void handlePurchase(String productName, String count, Scanner scanner) {
    Pattern pattern = Pattern.compile(".+?(-?\\d+)[,\\s]+(-?\\d+).+");
    Result result = controller.purchase(productName, count);
    if (result.isSuccessful() && result.message() == null) {
        System.out.println("enter x and y to build " + productName);
        String input = scanner.nextLine().trim();
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            System.out.println("Invalid input! build canceled");
            return;
        }
        int x = Integer.parseInt(matcher.group(1));
        int y = Integer.parseInt(matcher.group(2));
        System.out.println(controller.buildBuilding(x, y, productName));
    }
    System.out.println(result);
}

private void handleWalk(int x, int y, Scanner scanner) {
    WalkProposal proposal = controller.proposeWalk(x, y);
    if (!proposal.isAllowed()) {
        System.out.println(proposal.message());
        return;
    }
    System.out.printf(
            "It will cost %.02f energy, Proceed? (y/n)%n",
            proposal.energyCost()
    );
    String ans = scanner.nextLine().trim().toLowerCase();
    if (ans.startsWith("y")) {
        controller.executeWalk(proposal);
    } else {
        System.out.println("Walk cancelled");
    }
}

public void toolsCommandParser(String input) {
    Player player = App.getActiveGame().getCurrentPlayer();
    Matcher matcher;
    if ((matcher = GameMenuCommands.TOOLS_EQUIP.getMatcher(input)) != null) {
        System.out.println(controller.toolsEquip(matcher.group("toolName")));
    } else if ((matcher = GameMenuCommands.TOOLS_SHOW_CURRENT.getMatcher(input)) != null) {
        System.out.println(controller.toolsShowCurrent());
    } else if ((matcher = GameMenuCommands.TOOLS_AVAILABLE.getMatcher(input)) != null) {
        System.out.println(controller.toolsShowAvailable());
    } else if ((matcher = GameMenuCommands.TOOLS_UPGRADE.getMatcher(input)) != null) {
        System.out.println(controller.toolsUpgrade(matcher.group("toolName")));
    } else if ((matcher = GameMenuCommands.TOOLS_USE.getMatcher(input)) != null) {
        System.out.println(controller.toolsUse(Direction.getDirection(Integer.parseInt(matcher.group("direction")))));

    } else {
        System.out.println("Invalid Command!");
    }
}
}
