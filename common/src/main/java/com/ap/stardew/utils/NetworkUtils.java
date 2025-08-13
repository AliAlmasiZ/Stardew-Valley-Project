package com.ap.stardew.utils;

import com.ap.stardew.models.*;
import com.ap.stardew.models.NPC.Dialogue;
import com.ap.stardew.models.NPC.NPC;
import com.ap.stardew.models.NPC.NpcFriendship;
import com.ap.stardew.models.NPC.Quest;
import com.ap.stardew.models.animal.Animal;
import com.ap.stardew.models.animal.AnimalType;
import com.ap.stardew.models.building.Door;
import com.ap.stardew.models.crafting.Ingredient;
import com.ap.stardew.models.crafting.Recipe;
import com.ap.stardew.models.crafting.RecipeType;
import com.ap.stardew.models.dto.AccountInfo;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.dto.PlayerState;
import com.ap.stardew.models.dto.SavedGameDetails;
import com.ap.stardew.models.entities.*;
import com.ap.stardew.models.entities.components.*;
import com.ap.stardew.models.entities.components.harvestable.Harvestable;
import com.ap.stardew.models.entities.components.harvestable.HarvestableResource;
import com.ap.stardew.models.entities.components.inventory.Inventory;
import com.ap.stardew.models.entities.components.inventory.InventorySlot;
import com.ap.stardew.models.enums.*;
import com.ap.stardew.models.gameMap.*;
import com.ap.stardew.models.player.*;
import com.ap.stardew.models.player.friendship.PlayerFriendship;
import com.ap.stardew.models.player.reaction.Emoji;
import com.ap.stardew.models.player.reaction.Reaction;
import com.ap.stardew.models.shop.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import io.github.cdimascio.dotenv.Dotenv;
import org.tiledreader.TiledMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class NetworkUtils {

    public static final int TCP_PORT = 54555;
    public static final int UDP_PORT = 54777;
    public static final int AUDIO_CHANNEL_TCP = 54556;
    public static final int AUDIO_CHANNEL_UDP = 54778;
    public static final String HOST;
    public static final int BUFFER_SIZE = 4 * 1024;

    static {
        Dotenv dotenv = Dotenv.load();
        HOST = dotenv.get("HOST", "localhost");
    }

    /**
     * Registers classes for kryo Serialization
     * Every class here should have empty constructor for Deserialization
     * @param kryo kryo object of connection
     */
    public static void registerClasses(Kryo kryo) {
        kryo.setRegistrationRequired(true);
        kryo.setReferences(true);

        /* java objects */
        kryo.register(HashMap.class);
        kryo.register(ArrayList.class);
        kryo.register(Vector2.class);
        kryo.register(HashSet.class);
        kryo.register(byte[].class);

        /* DTOs */
        kryo.register(JSONMessage.class);
        kryo.register(JSONMessage.Type.class);
        kryo.register(com.ap.stardew.models.LobbyInfo.class);
        kryo.register(PlayerState.class);
        kryo.register(Player.Action.class);
        kryo.register(AccountInfo.class);
        kryo.register(Result.class);

        kryo.register(GameMap.class);
        kryo.register(Date.class);
        kryo.register(Season.class);
        kryo.register(BiomeType.class);
        kryo.register(BiomeType[].class);
        kryo.register(BiomeType[][].class);
        kryo.register(MapRegion.class);
        kryo.register(MapRegion[].class);
        kryo.register(MapRegion[][].class);
        kryo.register(Weather.class);
        kryo.register(WorldMap.class);
        kryo.register(EntityList.class);
        kryo.register(Environment.class);
        kryo.register(Position.class);
        kryo.register(Vector2.class);
        kryo.register(Vec2.class);
        kryo.register(TileType.class);
        kryo.register(TileType[].class);
        kryo.register(TileType[][].class);
        kryo.register(TileType[][].class);
        kryo.register(Player.class);
        kryo.register(PlayerFriendship.class);
        kryo.register(Entity.class);
        kryo.register(EntityComponent.class);
        kryo.register(Inventory.class);
        kryo.register(InventorySlot.class);
        kryo.register(PositionComponent.class);
        kryo.register(Renderable.class);
        kryo.register(Energy.class);
        kryo.register(Wallet.class);
        kryo.register(SkillType.class);
        kryo.register(Animal.class);
        kryo.register(Color.class);
        kryo.register(AnimalHouse.class);
        kryo.register(AnimalType.class);
        kryo.register(Edible.class);
        kryo.register(Useable.class);
        kryo.register(Sellable.class);
        kryo.register(FishingPoleComponent.class);
        kryo.register(Growable.class);
        kryo.register(Pickable.class);
        kryo.register(SeedComponent.class);
        kryo.register(Placeable.class);
        kryo.register(Container.class);
        kryo.register(InteriorComponent.class);
        kryo.register(Skill.class);
        kryo.register(ProductQuality.class);
        kryo.register(NPC.class);
        kryo.register(NpcFriendship.class);
        kryo.register(PlayerFriendship.class);
        kryo.register(Gift.class);
        kryo.register(Message.class);
        kryo.register(TradeOffer.class);
        kryo.register(Rectangle.class);
        kryo.register(Renderable.Statue.class);
        kryo.register(Gender.class);
        kryo.register(EntityTag.class);
        kryo.register(InteriorComponent.class);
        kryo.register(Placeable.class);
        kryo.register(Door.class);
        kryo.register(Recipe.class);
        kryo.register(Ingredient.class);
        kryo.register(RecipeType.class);
        kryo.register(Shop.class);
        kryo.register(OtherShopProduct.class);
        kryo.register(BuildingShopProduct.class);
        kryo.register(AnimalShopProduct.class);
        kryo.register(UpgradableShopProduct.class);
        kryo.register(RenderFunction.class);
        kryo.register(UseFunction.class);
        kryo.register(Upgradable.class);
        kryo.register(Material.class);
        kryo.register(Forageable.class);
        kryo.register(SavedGameDetails.class);
        kryo.register(Harvestable.class);
        kryo.register(HarvestableResource.class);
        kryo.register(Emoji.class);
        kryo.register(Reaction.class);
        kryo.register(Direction.class);
        kryo.register(Container.class);
        kryo.register(BiomeType.Spawnable.class);
        kryo.register(Dialogue.class);
        kryo.register(Quest.class);
        kryo.register(Account.class);
        kryo.register(NPC.class);
        kryo.register(SecurityQuestions.class);
        kryo.register(Renderable.Statue.class);

        kryo.register(Tile.class);
        kryo.register(TiledMap.class);
        kryo.register(TradeHistoryItem.class);
        kryo.register(Position.class);
        kryo.register(MapRegion.class);
        kryo.register(Sprite.class);
        kryo.register(Rectangle.class);
        kryo.register(Vector2.class);



        kryo.register(Game.class);
        kryo.register(GameMap.class);
        kryo.register(WorldMap.class);
        kryo.register(Tile[][].class);
        kryo.register(Tile[].class);
        kryo.register(Tile.class);

    }
}
