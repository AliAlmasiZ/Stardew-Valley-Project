package com.ap.stardew.models.Commands;

public enum GameMenuCommands implements Commands {
    WALK              ("^\\s*walk\\s+-l\\s+<?\\s*(?<x>-?\\d+)\\s*,\\s*(?<y>-?\\d+)\\s*>?\\s*$"),
    NEXT_TURN         ("^\\s*next\\s+turn\\s*$"),
    ENERGY_SET        ("\\s*energy\\s+set\\s+-v\\s+(?<amount>\\d+)\\s*"),
    ENERGY_SHOW       ("\\s*energy\\s+show\\s*"),
    SHOW_INVENTORY    ("\\s*show\\s+inventory\\s*"),
    ENERGY_UNLIMITED  ("\\s*energy\\s+unlimited\\s*"),
    CHANGE_INPUT_TYPE ("^cit$"),
    START_TRADE       ("^\\s*start\\s+trade\\s*$"),
    EAT_FOOD          ("^eat\\s+(?<foodName>.+)$"),
    CHEAT_THOR        ("^\\s*cheat\\s+Thor\\s+-l\\s+<(?<x>\\d+),(?<y>\\d+)>\\s*$"),
    SHOW_FRIDGE_CONTENT("^\\s*show\\s+fridge\\s+content\\s*$"),
    BUILD_GREENHOUSE  ("^\\s*build\\s+greenhouse\\s*$"),
    SHOW_SHIPPING_BIN_CONTENT("^\\s*show\\s+shipping\\s+bin\\s+content\\s*$"),
    TRASH_ITEM("^\\s*trash\\s+item\\s+-n\\s+(?<name>.+?)\\s+-a\\s+(?<amount>\\S+)\\s*$"),
    SAVE_GAME                 ("^\\s*save\\s+game\\s*$"),

    /* ----------------------------------------- Plant commands ------------------------------------------- */
    CRAFTINFO         ("^\\s*craftinfo\\s+-n\\s+(?<name>.+)\\s*$"),
    FERTILIZE         ("fertilize\\s+-f\\s+(?<name>.+)\\s+-d\\s+(?<direction>\\S+)\\s*"),
    PLANT_SEED        ("\\s*plant\\s+-s\\s+(?<seed>.+)\\s+-d\\s+(?<direction>\\S+)\\s*"),
    SHOW_PLANT        ("\\s*showplant\\s+-l\\s+<\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*>\\s*"),
    GREEN_HOUSE_BUILD ("\\s*greenhouse\\s+build\\s*"),
    HELP_READING_MAP("^help\\s+reading\\s+map$"),
    /* ----------------------------------------------------------------------------------------------------- */

    /* ----------------------------------------- Weather commands ------------------------------------------- */
    WEATHER           ("\\s*weather\\s*"),
    SET_WEATHER       ("cheat\\s+weather\\s*set\\s*(?<type>\\S+)\\s*"),
    WEATHER_FORECAST  ("\\s*weather\\s*forecast\\s*"),
    /* ----------------------------------------------------------------------------------------------------- */


    /* --------------------------------------- Friendship commands ---------------------------------------- */
    TALK              ("\\s*talk\\s+-u\\s+(?<name>.+)\\s+-m\\s+(?<message>.+)\\s*"),
    TALK_HISTORY      ("^\\s*talk\\s+history\\s+-u\\s+(?<name>.+)\\s*"),
    FRIENDSHIPS       ("\\s*friendships\\s*"),
    GIFT              ("\\s*gift\\s+-u\\s+(?<username>.+)\\s+-i\\s+(?<item>.+?)\\s+-a\\s+(?<amount>\\d+)\\s*"),
    GIFT_LIST         ("\\s*gift\\s+list\\s*"),
    GIFT_RATE         ("\\s*gift\\s+rate\\s+-i\\s+(?<giftNumber>-?\\d+)\\s*-r\\s+(?<rate>-?\\d+)\\s*"),
    GIFT_HISTORY      ("\\s*gift\\s+history\\s+-u(?<username>.+)\\s*"),
    HUG               ("\\s*hug\\s+-u\\s+(?<username>.+)\\s*"),
    FLOWER            ("\\s*flower\\s+-u\\s+(?<username>.+)\\s*"),
    ASK_MARRIAGE      ("^\\s*ask\\s+marriage\\s+-u\\s+(?<username>.+)\\s+-r\\s+(?<ring>.+)\\s*$"),
    RESPOND           ("\\s*respond\\s+-(?<respond>.+)\\s+-u\\s+(?<username>.+)\\s*"),
    /* ----------------------------------------------------------------------------------------------------- */

    /* ------------------------------------------ Date commands  ------------------------------------------ */
    DATE              ("\\s*date\\s*"),
    TIME              ("\\s*time\\s*"),
    DATETIME          ("\\s*datetime\\s*"),
    SEASON            ("\\s*season\\s*"),
    ADVANCE_TIME      ("\\s*cheat\\s+advance\\s+time\\s+(?<amount>\\d+)h\\s*"),
    ADVANCE_DATE      ("\\s*cheat\\s+advance\\s+date\\s+(?<amount>\\d+)d\\s*"),
    DAY_OF_THE_WEEK   ("\\s*day\\s+of\\s+the\\s+week\\s*"),
    /* -------------------------------------------------- -------------------------------------------------- */

    /* ------------------------------------------ Tools commands ------------------------------------------ */
    TOOLS_EQUIP("^tools\\s+equip\\s+(?<toolName>.+)$"), //TODO: check name
    TOOLS_SHOW_CURRENT("^tools\\s+show\\s+current$"),
    TOOLS_AVAILABLE("^tools\\s+show\\s+available$"),
    TOOLS_UPGRADE("^tools\\s+upgrade\\s+(?<toolName>.+)$"),//TODO
    TOOLS_USE("^tools\\s+use\\s+-d\\s+(?<direction>.+)$"),//TODO
    ARTISAN_USE("^artisan\\s+use\\s+\"(?<artisanName>.+)\"\\s+(?<itemName>.+)$"),
    ARTISAN_GET("^artisan\\s+get\\s+(?<artisanName>.+)$"),
    /* -------------------------------------------------- -------------------------------------------------- */

    /* ------------------------------------------- Item Commands ------------------------------------------- */
    PLACE_ITEM("^place\\s+item\\s+-n\\s+(?<itemName>.+?)\\s+-d\\s+(?<direction>.+)$"),
    PICK_NEARBY_ITEMS("^\\s*pick\\s+nearby\\s+items\\s*$"),
    CHEAT_GIVE_ITEM   ("^item\\s*(?<name>.+?)\\s+(?<quantity>-?\\d+)\\s*$"),
    REFRIGERATOR("^cooking\\s+refrigerator\\s+(?<what>put|pick)\\s+(?<item>.+)\\s+(?<amount>\\d+)$"),

    /* -------------------------------------------------- -------------------------------------------------- */

    /* ------------------------------------------ Recipe Commands ------------------------------------------ */
    CRAFTING_SHOW_RECIPES("^crafting\\s+show\\s+recipes$"),
    CRAFTING_CRAFT("^crafting\\s+craft\\s+(?<itemName>.+)"),
    COOKING_SHOW_RECIPES("^cooking\\s+show\\s+recipes$"),
    COOKING_PREPARE("^cooking\\s+prepare\\s+(?<recipeName>.+)$"),
    /* -------------------------------------------------- -------------------------------------------------- */

    /* ------------------------------------------ Render Commands ------------------------------------------ */
    TOGGLE_MAP        ("^tm$"),
    CHANGE_MAP_RENDER ("^\\s*change\\s+map\\s+draw\\s*$"),
    /* -------------------------------------------------- -------------------------------------------------- */

    /* ------------------------------------------- NPC Commands -------------------------------------------- */
    MEET_NPC ("\\s*meet\\s+NPC\\s+(?<npcName>.+)\\s*"),
    GIFT_NPC ("\\s*gift\\s+NPC\\s+(?<npcName>.+)\\s+-i\\s+(?<item>.+)\\s*"),
    FRIENDSHIP_NPC ("\\s*friendship\\s+NPC\\s+list\\s*"),
    QUEST_LIST("\\s*quests\\s+list\\s*"),
    QUEST_FINISH("\\s*quests\\s+finish\\s+-i\\s+(?<index>-?\\d+)\\s*"),
    /* -------------------------------------------------- -------------------------------------------------- */

    /* ------------------------------------------ Animal Commands ------------------------------------------ */
    BUY_ANIMAL("\\s*buy\\s+animal\\s+-a\\s+(?<animalName>.+?)\\s+-n\\s+(?<name>.+?)\\s+-h\\s*(?<house>.+?)\\s*"),
    SHOW_MY_ANIMAL_HOUSES("^show\\s+my\\s+animal\\s+houses$"),
    PET_ANIMAL("\\s*pet\\s+-n\\s+(?<name>.+)\\s*"),
    SET_ANIMAL_FRIENDSHIP("\\s*cheat\\s+set\\s+friendship\\s+" +
            "-n\\s+(?<animalName>.+)\\s+-c\\s+(?<amount>-?\\d+)\\s*"),
    ANIMAL_INFO("\\s*animals\\s*"),
    SHEPHERD_ANIMAL("\\s*shepherd\\s+animals\\s+-n\\s+(?<animalName>.+)\\s+" +
            "-l\\s+<(?<x>\\d+)\\s*,\\s+(?<y>\\d+)>\\s*"),
    FEED_HAY("\\s*feed\\s+hay\\s+-n\\s+(?<animalName>.+)\\s*"),
    PRODUCES("\\s*produces\\s*"),
    COLLECT_PRODUCE("\\s*collect\\s+produce\\s+-n\\s+(?<name>.+)\\s*"),
    SELL_ANIMAL("\\s*sell\\s+animal\\s+-n\\s+(?<name>.+)\\s*"),
    FISHING("\\s*fishing\\s+-p\\s+(?<fishingPole>.+)\\s*"),

    /* ------------------------------------------- Shop Commands ------------------------------------------- */
    SHOW_ALL_PRODUCTS("^show\\s+all\\s+products$"),
    SHOW_ALL_AVAILABLE("^show\\s+all\\s+available\\s+products$"),
    PURCHASE("^purchase\\s+(?<productName>[^-]+?)\\s*(-n\\s+(?<count>\\d+))?$"),
    BUILD_BUILDING("^build\\s+-a\\s+(?<buildingName>.+?)\\s+-l\\s+<?\\s*(?<x>-?\\d+)\\s*,\\s*(?<y>-?\\d+)\\s*>?$"),
    SELL_PRODUCT("^sell\\s+(?<productName>[^-]+?)\\s*(-n\\s+(?<count>\\d+))?$"),

    /* -------------------------------------------------- -------------------------------------------------- */

    /* ------------------------------------- Additional cheat Commands ------------------------------------- */
    CHEAT_SKILL("add skill (?<skill>\\S+) -a (?<amount>\\d+)"),
    SKILL_STATUE("skill statue"),
    ADD_MONEY("add\\s+money\\s+-a\\s+(?<amount>\\d+)\\s*"),
    TOGGLE_UNLIMITED_INVENTORY("^\\s*cheat\\s+toggle\\s+unlimited\\s+inventory\\s*$"),
    CHEAT_TAKE_ITEM("^\\s*cheat\\s+take\\s+item\\s+\"\\s+(?<item>\\S+)\\s+\"\\s+(?<amount>\\d+)\\s*$"),
    CHEAT_SET_FRIENDSHIP("cheat\\s+-u\\s+(?<name>.+)\\s*level\\s*(?<level>\\d+)\\s*xp\\s*(?<xp>\\d+)\\s*"),
    CHEAT_BUILD_BUILDING("^\\s*cheat\\s+build\\s+building\\s+(?<name>\\S+)\\s+(?<x>-?\\d+)\\s+(?<y>-?\\d+)(?:\\s+(?<force>-force))?\\s*$"),
    CHEAT_SPAWN_ON_GROUND("^\\s*cheat\\s+spawn\\s+on\\s+ground\\s+-n\\s+(?<name>\\S+)\\s+-a\\s+(?<amount>\\S+)\\s*$"),
    CHEAT_WATER_ALL("water all"),
    /* -------------------------------------------------- -------------------------------------------------- */

    ;

    private final String pattern;

    GameMenuCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }
}
