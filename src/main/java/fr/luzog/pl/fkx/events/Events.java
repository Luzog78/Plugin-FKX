package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.commands.Admin.Vanish;
import fr.luzog.pl.fkx.commands.Cheat.Freeze;
import fr.luzog.pl.fkx.utils.Crafting;
import fr.luzog.pl.fkx.utils.CustomNBT;
import fr.luzog.pl.fkx.utils.Loots;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Events implements Listener {

    public static List<Listener> events = new ArrayList<Listener>() {{
        /* *** - Customs Handlers - *** */
        add(new Vanish());
        add(new Freeze());
        add(new Crafting());

        /* *** - Raw Listeners - *** */
        add(new Events());
        add(new PlayerInteractHandler());
        add(new PlayerInteractAtEntityHandler());
        add(new EntityDamageByEntityHandler());
        add(new EntityDamageHandler());
        add(new BlockPlaceHandler());
        add(new BlockBreakHandler());
        add(new PlayerMoveHandler());
        add(new BucketHandler());
    }};

    public static final String canInteractTag = "canInteract";
    public static final String canClickOnTag = "canClickOn";
    public static final String lastDamageLootingLevelTag = "lastDamageLootingLevel";
    public static final String lastDamageSilkTouchTag = "lastDamageSilkTouch";

    public static List<Material> specialMat = Arrays.asList(Material.TNT, Material.TORCH, Material.REDSTONE_TORCH_ON,
            Material.REDSTONE_TORCH_OFF, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.WHEAT, Material.HAY_BLOCK,
            Material.SEEDS, Material.MELON_SEEDS, Material.PUMPKIN_SEEDS, Material.CARROT, Material.CARROT_ITEM,
            Material.POTATO, Material.POTATO_ITEM, Material.FIRE, Material.FLINT_AND_STEEL, Material.BUCKET,
            Material.WATER, Material.WATER_BUCKET, Material.LAVA, Material.LAVA_BUCKET, Material.WORKBENCH,
            Material.FURNACE, Material.ANVIL, Material.ENCHANTMENT_TABLE);
    public static List<Material> unbreakableMat = Arrays.asList(Material.MOB_SPAWNER, Material.WOOL);
    public static List<Material> unplaceableMat = Arrays.asList(Material.WOOL);

    public static List<BlockLootsItem> breakBlockLoots = new ArrayList<BlockLootsItem>() {{
        add(new BlockLootsItem(Arrays.asList(Material.LOG, Material.LOG_2), false, new Loots().add(new ItemStack(Material.LOG))));
        add(new BlockLootsItem(Collections.singletonList(Material.WOOD), false, new Loots().add(new ItemStack(Material.WOOD))));
        add(new BlockLootsItem(Collections.singletonList(Material.STONE), false, new Loots().add(new ItemStack(Material.STONE))));
        add(new BlockLootsItem(Collections.singletonList(Material.COBBLESTONE), false, new Loots().add(new ItemStack(Material.COBBLESTONE))));
        add(new BlockLootsItem(Collections.singletonList(Material.SAND), false, new Loots().add(new ItemStack(Material.SAND))));
        add(new BlockLootsItem(Collections.singletonList(Material.SAND), false, new Loots().add(new ItemStack(Material.SAND))));
        add(new BlockLootsItem(Arrays.asList(Material.LEAVES, Material.LEAVES), false, new Loots()
                .add(1, new CustomNBT(new ItemStack(Material.LEAVES)).setBoolean(canInteractTag, false).build(), -1, true)
                .add(0.25, new ItemStack(Material.LEAVES), -1, true)
                .add(0.2, new ItemStack(Material.SAPLING))
                .add(0.0666, new ItemStack(Material.APPLE), 0, null)
                .add(0.01, new ItemStack(Material.GOLDEN_APPLE), 0, null)
                .add(0.1, new ItemStack(Material.APPLE), 1, null)
                .add(0.02, new ItemStack(Material.GOLDEN_APPLE), 1, null)
                .add(0.2, new ItemStack(Material.APPLE), 2, null)
                .add(0.04, new ItemStack(Material.GOLDEN_APPLE), 2, null)
                .add(0.3, new ItemStack(Material.APPLE), 3, null)
                .add(0.08, new ItemStack(Material.GOLDEN_APPLE), 3, null)
                .add(1, new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), 32767, null)

                .add(0.005, new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), -1, null)
        ));
        add(new BlockLootsItem(Collections.singletonList(Material.IRON_ORE), true, new Loots()
                .add(0.15, new ItemStack(Material.IRON_INGOT, 2), 0, false)
                .add(0.85, new ItemStack(Material.IRON_INGOT, 1), 0, false)
                .add(0.20, new ItemStack(Material.IRON_INGOT, 3), 1, false)
                .add(0.75, new ItemStack(Material.IRON_INGOT, 2), 1, false)
                .add(0.05, new ItemStack(Material.IRON_INGOT, 1), 1, false)
                .add(0.15, new ItemStack(Material.IRON_INGOT, 4), 2, false)
                .add(0.75, new ItemStack(Material.IRON_INGOT, 3), 2, false)
                .add(0.08, new ItemStack(Material.IRON_INGOT, 2), 2, false)
                .add(0.02, new ItemStack(Material.IRON_INGOT, 1), 2, false)
                .add(0.30, new ItemStack(Material.IRON_INGOT, 5), 3, false)
                .add(0.45, new ItemStack(Material.IRON_INGOT, 4), 3, false)
                .add(0.20, new ItemStack(Material.IRON_INGOT, 3), 3, false)
                .add(0.05, new ItemStack(Material.IRON_INGOT, 2), 3, false)
                .add(0.15, new CustomNBT(new ItemStack(Material.IRON_ORE, 2)).setBoolean(canInteractTag, false).build(), 0, true)
                .add(0.85, new CustomNBT(new ItemStack(Material.IRON_ORE, 1)).setBoolean(canInteractTag, false).build(), 0, true)
                .add(0.20, new CustomNBT(new ItemStack(Material.IRON_ORE, 3)).setBoolean(canInteractTag, false).build(), 1, true)
                .add(0.75, new CustomNBT(new ItemStack(Material.IRON_ORE, 2)).setBoolean(canInteractTag, false).build(), 1, true)
                .add(0.05, new CustomNBT(new ItemStack(Material.IRON_ORE, 1)).setBoolean(canInteractTag, false).build(), 1, true)
                .add(0.15, new CustomNBT(new ItemStack(Material.IRON_ORE, 4)).setBoolean(canInteractTag, false).build(), 2, true)
                .add(0.75, new CustomNBT(new ItemStack(Material.IRON_ORE, 3)).setBoolean(canInteractTag, false).build(), 2, true)
                .add(0.08, new CustomNBT(new ItemStack(Material.IRON_ORE, 2)).setBoolean(canInteractTag, false).build(), 2, true)
                .add(0.02, new CustomNBT(new ItemStack(Material.IRON_ORE, 1)).setBoolean(canInteractTag, false).build(), 2, true)
                .add(0.40, new CustomNBT(new ItemStack(Material.IRON_ORE, 5)).setBoolean(canInteractTag, false).build(), 3, true)
                .add(0.55, new CustomNBT(new ItemStack(Material.IRON_ORE, 4)).setBoolean(canInteractTag, false).build(), 3, true)
                .add(0.20, new CustomNBT(new ItemStack(Material.IRON_ORE, 3)).setBoolean(canInteractTag, false).build(), 3, true)
                .add(0.05, new CustomNBT(new ItemStack(Material.IRON_ORE, 2)).setBoolean(canInteractTag, false).build(), 3, true)
        ));
        add(new BlockLootsItem(Collections.singletonList(Material.GOLD_ORE), true, new Loots()
                .add(0.85, new ItemStack(Material.GOLD_INGOT, 1), 0, false)
                .add(0.15, new ItemStack(Material.GOLD_INGOT, 2), 0, false)
                .add(0.05, new ItemStack(Material.GOLD_INGOT, 1), 1, false)
                .add(0.75, new ItemStack(Material.GOLD_INGOT, 2), 1, false)
                .add(0.20, new ItemStack(Material.GOLD_INGOT, 3), 1, false)
                .add(0.02, new ItemStack(Material.GOLD_INGOT, 1), 2, false)
                .add(0.08, new ItemStack(Material.GOLD_INGOT, 2), 2, false)
                .add(0.75, new ItemStack(Material.GOLD_INGOT, 3), 2, false)
                .add(0.15, new ItemStack(Material.GOLD_INGOT, 4), 2, false)
                .add(0.05, new ItemStack(Material.GOLD_INGOT, 1), 3, false)
                .add(0.20, new ItemStack(Material.GOLD_INGOT, 2), 3, false)
                .add(0.55, new ItemStack(Material.GOLD_INGOT, 3), 3, false)
                .add(0.40, new ItemStack(Material.GOLD_INGOT, 4), 3, false)
                .add(0.85, new CustomNBT(new ItemStack(Material.GOLD_ORE, 1)).setBoolean(canInteractTag, false).build(), 0, true)
                .add(0.15, new CustomNBT(new ItemStack(Material.GOLD_ORE, 2)).setBoolean(canInteractTag, false).build(), 0, true)
                .add(0.05, new CustomNBT(new ItemStack(Material.GOLD_ORE, 1)).setBoolean(canInteractTag, false).build(), 1, true)
                .add(0.75, new CustomNBT(new ItemStack(Material.GOLD_ORE, 2)).setBoolean(canInteractTag, false).build(), 1, true)
                .add(0.20, new CustomNBT(new ItemStack(Material.GOLD_ORE, 3)).setBoolean(canInteractTag, false).build(), 1, true)
                .add(0.02, new CustomNBT(new ItemStack(Material.GOLD_ORE, 1)).setBoolean(canInteractTag, false).build(), 2, true)
                .add(0.08, new CustomNBT(new ItemStack(Material.GOLD_ORE, 2)).setBoolean(canInteractTag, false).build(), 2, true)
                .add(0.75, new CustomNBT(new ItemStack(Material.GOLD_ORE, 3)).setBoolean(canInteractTag, false).build(), 2, true)
                .add(0.15, new CustomNBT(new ItemStack(Material.GOLD_ORE, 4)).setBoolean(canInteractTag, false).build(), 2, true)
                .add(0.05, new CustomNBT(new ItemStack(Material.GOLD_ORE, 1)).setBoolean(canInteractTag, false).build(), 3, true)
                .add(0.20, new CustomNBT(new ItemStack(Material.GOLD_ORE, 2)).setBoolean(canInteractTag, false).build(), 3, true)
                .add(0.55, new CustomNBT(new ItemStack(Material.GOLD_ORE, 3)).setBoolean(canInteractTag, false).build(), 3, true)
                .add(0.40, new CustomNBT(new ItemStack(Material.GOLD_ORE, 4)).setBoolean(canInteractTag, false).build(), 3, true)
        ));
    }};

    public static List<MobLootsItem> killMobLoots = new ArrayList<MobLootsItem>() {{
        add(new MobLootsItem(EntityType.CREEPER, EntityData.CREEPER_NORMAL, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(0.05, new ItemStack(Material.SULPHUR), 0, null)
                .add(0.10, new ItemStack(Material.SULPHUR), 0, null)
                .add(0.15, new ItemStack(Material.SULPHUR), 0, null)
                .add(0.05, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 0, false)
                .add(0.10, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 0, true)
                .add(0.10, new ItemStack(Material.SULPHUR), 1, null)
                .add(0.15, new ItemStack(Material.SULPHUR), 1, null)
                .add(0.20, new ItemStack(Material.SULPHUR), 1, null)
                .add(0.10, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 1, false)
                .add(0.15, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 1, true)
                .add(0.15, new ItemStack(Material.SULPHUR), 2, null)
                .add(0.20, new ItemStack(Material.SULPHUR), 2, null)
                .add(0.25, new ItemStack(Material.SULPHUR), 2, null)
                .add(0.15, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 2, false)
                .add(0.20, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 2, true)
                .add(0.80, new ItemStack(Material.SULPHUR), -2, null)
                .add(0.30, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), -2, false)
                .add(0.60, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), -2, true)
        ));
        add(new MobLootsItem(EntityType.CREEPER, EntityData.CREEPER_SUPERCHARGED, false, new Loots()
                // Just the same that normal creeper but x1.5
                .setChanceLvlAmountCoefficient(1)
                .add(0.075, new ItemStack(Material.SULPHUR), 0, null)
                .add(0.150, new ItemStack(Material.SULPHUR), 0, null)
                .add(0.225, new ItemStack(Material.SULPHUR), 0, null)
                .add(0.075, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 0, false)
                .add(0.150, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 0, true)
                .add(0.150, new ItemStack(Material.SULPHUR), 1, null)
                .add(0.225, new ItemStack(Material.SULPHUR), 1, null)
                .add(0.300, new ItemStack(Material.SULPHUR), 1, null)
                .add(0.150, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 1, false)
                .add(0.175, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 1, true)
                .add(0.225, new ItemStack(Material.SULPHUR), 2, null)
                .add(0.300, new ItemStack(Material.SULPHUR), 2, null)
                .add(0.375, new ItemStack(Material.SULPHUR), 2, null)
                .add(0.175, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 2, false)
                .add(0.300, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 2, true)
                .add(1.200, new ItemStack(Material.SULPHUR), -2, null)
                .add(0.450, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), -2, false)
                .add(0.900, new ItemStack(Material.SKULL_ITEM, 1, (short) 4), -2, true)
        ));
        add(new MobLootsItem(EntityType.SKELETON, EntityData.SKELETON_NORMAL, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(0.05, new ItemStack(Material.BOW), 0, null)
                .add(0.33, new ItemStack(Material.ARROW), 0, null)
                .add(0.33, new ItemStack(Material.ARROW), 0, null)
                .add(0.33, new ItemStack(Material.ARROW), 0, null)
                .add(0.05, new ItemStack(Material.SKULL_ITEM, 1, (short) 0), 0, false)
                .add(0.10, new ItemStack(Material.SKULL_ITEM, 1, (short) 0), 0, true)
                .add(0.15, new ItemStack(Material.BOW), 1, null)
                .add(0.66, new ItemStack(Material.ARROW), 1, null)
                .add(0.66, new ItemStack(Material.ARROW), 1, null)
                .add(0.66, new ItemStack(Material.ARROW), 1, null)
                .add(0.10, new ItemStack(Material.SKULL_ITEM, 1, (short) 0), 1, false)
                .add(0.15, new ItemStack(Material.SKULL_ITEM, 1, (short) 0), 1, true)
                .add(0.25, new ItemStack(Material.BOW), 2, null)
                .add(0.99, new ItemStack(Material.ARROW), 2, null)
                .add(0.99, new ItemStack(Material.ARROW), 2, null)
                .add(0.99, new ItemStack(Material.ARROW), 2, null)
                .add(0.15, new ItemStack(Material.SKULL_ITEM, 1, (short) 0), 2, false)
                .add(0.20, new ItemStack(Material.SKULL_ITEM, 1, (short) 0), 2, true)
                .add(0.50, new ItemStack(Material.BOW), -2, null)
                .add(1, new ItemStack(Material.ARROW), -2, null)
                .add(1, new ItemStack(Material.ARROW), -2, null)
                .add(1, new ItemStack(Material.ARROW), -2, null)
                .add(0.30, new ItemStack(Material.SKULL_ITEM, 1, (short) 0), -2, false)
                .add(0.60, new ItemStack(Material.SKULL_ITEM, 1, (short) 0), -2, true)
        ));
        add(new MobLootsItem(EntityType.SKELETON, EntityData.SKELETON_WITHER, true, new Loots()
                .setChanceLvlProbaCoefficient(1)
                .add(0.25, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), -2, null)
        ));
        add(new MobLootsItem(EntityType.SPIDER, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(0.50, new ItemStack(Material.STRING), -2, null)
                .add(0.50, new ItemStack(Material.STRING), -2, null)
                .add(0.50, new ItemStack(Material.STRING), -2, null)
                .add(0.50, new ItemStack(Material.STRING), -2, null)
                .add(0.25, new ItemStack(Material.SPIDER_EYE), -2, null)
                .add(0.25, new ItemStack(Material.SPIDER_EYE), -2, null)
                .add(0.10, new ItemStack(Material.FERMENTED_SPIDER_EYE), -2, null)
                .add(0.10, new ItemStack(Material.FERMENTED_SPIDER_EYE), -2, null)
        ));
        add(new MobLootsItem(EntityType.CAVE_SPIDER, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(0.50, new ItemStack(Material.SPIDER_EYE), -2, null)
                .add(0.50, new ItemStack(Material.SPIDER_EYE), -2, null)
                .add(0.50, new ItemStack(Material.STRING), -2, null)
                .add(0.50, new ItemStack(Material.STRING), -2, null)
                .add(0.25, new ItemStack(Material.FERMENTED_SPIDER_EYE), -2, null)
                .add(0.25, new ItemStack(Material.FERMENTED_SPIDER_EYE), -2, null)
        ));
        add(new MobLootsItem(EntityType.ZOMBIE, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(0.33, new ItemStack(Material.COOKED_BEEF), -2, null)
                .add(0.33, new ItemStack(Material.COOKED_BEEF), -2, null)
                .add(0.25, new ItemStack(Material.GOLDEN_CARROT), -2, null)
        ));
        add(new MobLootsItem(EntityType.PIG_ZOMBIE, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(0.33, new ItemStack(Material.COOKED_BEEF), -2, null)
                .add(0.33, new ItemStack(Material.GOLDEN_CARROT), -2, null)
                .add(0.33, new ItemStack(Material.GOLDEN_CARROT), -2, null)
                .add(0.25, new ItemStack(Material.GOLD_INGOT), -2, null)
                .add(0.25, new ItemStack(Material.GOLD_INGOT), -2, null)
        ));
        add(new MobLootsItem(EntityType.BLAZE, false, new Loots()
                .setChanceLvlProbaCoefficient(2)
                .add(0.20, new ItemStack(Material.BLAZE_ROD), -2, null)
                .add(0.20, new ItemStack(Material.BLAZE_ROD), -2, null)
        ));
        add(new MobLootsItem(EntityType.ENDERMAN, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(0.33, new ItemStack(Material.ENDER_PEARL), -2, null)
                .add(0.33, new ItemStack(Material.ENDER_PEARL), -2, null)
                .add(0.33, new ItemStack(Material.ENDER_PEARL), -2, null)
        ));
        add(new MobLootsItem(EntityType.ENDERMITE, false, new Loots()
                .setChanceLvlProbaCoefficient(1)
                .add(0.33, new ItemStack(Material.ENDER_PEARL), -2, null)
                .add(0.33, new ItemStack(Material.ENDER_PEARL), -2, null)
                .add(0.33, new ItemStack(Material.ENDER_PEARL), -2, null)
                .add(0.33, new ItemStack(Material.ENDER_PEARL), -2, null)
        ));
        add(new MobLootsItem(EntityType.WITCH, false, new Loots()
                .setChanceLvlProbaCoefficient(2)
                .add(0.33, new ItemStack(Material.GLASS_BOTTLE), -2, null)
                .add(0.33, new ItemStack(Material.GLASS_BOTTLE), -2, null)
                .add(0.33, new ItemStack(Material.SUGAR_CANE), -2, null)
                .add(0.33, new ItemStack(Material.SUGAR_CANE), -2, null)
                .add(0.33, new ItemStack(Material.SPIDER_EYE), -2, null)
                .add(0.33, new ItemStack(Material.SPIDER_EYE), -2, null)
                .add(0.33, new ItemStack(Material.GLOWSTONE), -2, null)
                .add(0.33, new ItemStack(Material.GLOWSTONE), -2, null)
                .add(0.33, new ItemStack(Material.REDSTONE), -2, null)
                .add(0.33, new ItemStack(Material.REDSTONE), -2, null)
                .add(0.33, new ItemStack(Material.SULPHUR), -2, null)
                .add(0.33, new ItemStack(Material.SULPHUR), -2, null)
        ));
        add(new MobLootsItem(EntityType.COW, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(0.5, new ItemStack(Material.LEATHER), -1, null)
                .add(0.5, new ItemStack(Material.LEATHER), -1, null)
                .add(0.5, new ItemStack(Material.LEATHER), 1, null)
                .add(0.5, new ItemStack(Material.LEATHER), 2, null)
                .add(0.5, new ItemStack(Material.LEATHER), 3, null)
                .add(1, new ItemStack(Material.COOKED_BEEF), -1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), -1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), -1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 2, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 3, null)
        ));
        add(new MobLootsItem(EntityType.MUSHROOM_COW, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(0.5, new ItemStack(Material.LEATHER), -1, null)
                .add(0.5, new ItemStack(Material.LEATHER), -1, null)
                .add(0.5, new ItemStack(Material.LEATHER), 1, null)
                .add(0.5, new ItemStack(Material.LEATHER), 2, null)
                .add(0.5, new ItemStack(Material.LEATHER), 3, null)
                .add(1, new ItemStack(Material.COOKED_BEEF), -1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), -1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), -1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 2, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 3, null)
                .add(0.5, new ItemStack(Material.GOLDEN_CARROT), -1, null)
                .add(0.5, new ItemStack(Material.GOLDEN_CARROT), -1, null)
                .add(0.5, new ItemStack(Material.GOLDEN_CARROT), 1, null)
                .add(0.5, new ItemStack(Material.GOLDEN_CARROT), 2, null)
                .add(0.5, new ItemStack(Material.GOLDEN_CARROT), 3, null)
        ));
        add(new MobLootsItem(EntityType.PIG, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(0.5, new ItemStack(Material.LEATHER), -1, null)
                .add(0.5, new ItemStack(Material.LEATHER), -1, null)
                .add(0.5, new ItemStack(Material.LEATHER), 1, null)
                .add(0.5, new ItemStack(Material.LEATHER), 2, null)
                .add(0.5, new ItemStack(Material.LEATHER), 3, null)
                .add(1, new ItemStack(Material.COOKED_BEEF), -1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), -1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), -1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 2, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 3, null)
        ));
        add(new MobLootsItem(EntityType.CHICKEN, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(0.5, new ItemStack(Material.ARROW), -1, null)
                .add(0.5, new ItemStack(Material.ARROW), -1, null)
                .add(0.5, new ItemStack(Material.ARROW), 1, null)
                .add(0.5, new ItemStack(Material.ARROW), 2, null)
                .add(0.5, new ItemStack(Material.ARROW), 3, null)
                .add(1, new ItemStack(Material.COOKED_BEEF), -1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 2, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 3, null)
        ));
        add(new MobLootsItem(EntityType.SHEEP, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(1, new ItemStack(Material.WOOL), -1, null)
                .add(0.5, new ItemStack(Material.WOOL), -1, null)
                .add(0.5, new ItemStack(Material.WOOL), 1, null)
                .add(0.5, new ItemStack(Material.WOOL), 2, null)
                .add(0.5, new ItemStack(Material.WOOL), 3, null)
                .add(1, new ItemStack(Material.COOKED_BEEF), -1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), -1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 1, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 2, null)
                .add(0.5, new ItemStack(Material.COOKED_BEEF), 3, null)
        ));
        add(new MobLootsItem(EntityType.HORSE, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(1, new ItemStack(Material.LEATHER), -1, null)
                .add(0.75, new ItemStack(Material.LEATHER), -1, null)
                .add(0.5, new ItemStack(Material.LEATHER), 1, null)
                .add(0.5, new ItemStack(Material.LEATHER), 2, null)
                .add(0.5, new ItemStack(Material.LEATHER), 3, null)
        ));
        add(new MobLootsItem(EntityType.SQUID, false, new Loots() {{
            setChanceLvlAmountCoefficient(1);
            for (short i = 0; i < 16; i++)
                add(0.5, new ItemStack(Material.LEATHER, 1, i), -1, null);
        }}));
        add(new MobLootsItem(EntityType.VILLAGER, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(0.5, new ItemStack(Material.EMERALD), -1, null)
                .add(0.5, new ItemStack(Material.EMERALD), -1, null)
                .add(0.5, new ItemStack(Material.EMERALD), -1, null)
                .add(0.5, new ItemStack(Material.EMERALD), -1, null)
                .add(0.5, new ItemStack(Material.EMERALD), 1, null)
                .add(0.5, new ItemStack(Material.EMERALD), 1, null)
                .add(0.5, new ItemStack(Material.EMERALD), 2, null)
                .add(0.5, new ItemStack(Material.EMERALD), 2, null)
                .add(0.5, new ItemStack(Material.EMERALD), 3, null)
                .add(0.5, new ItemStack(Material.EMERALD), 3, null)
        ));
        add(new MobLootsItem(EntityType.WITHER, false, new Loots()
                .add(1, new ItemStack(Material.NETHER_STAR), -1, null)
                .add(0.5, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), 0, null)
                .add(0.25, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), 0, null)
                .add(0.125, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), 0, null)
                .add(1, new ItemStack(Material.NETHER_STAR), 1, null)
                .add(0.75, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), 1, null)
                .add(0.50, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), 1, null)
                .add(0.25, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), 1, null)
                .add(1, new ItemStack(Material.NETHER_STAR), 2, null)
                .add(1, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), 2, null)
                .add(0.75, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), 2, null)
                .add(0.50, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), 2, null)
                .add(1, new ItemStack(Material.NETHER_STAR), 3, null)
                .add(1, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), 3, null)
                .add(1, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), 3, null)
                .add(0.75, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), 3, null)
                .add(1, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), -2, null)
                .add(1, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), -2, null)
                .add(1, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), -2, null)
        ));
        add(new MobLootsItem(EntityType.IRON_GOLEM, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(1, new ItemStack(Material.IRON_INGOT), -1, null)
                .add(1, new ItemStack(Material.IRON_INGOT), -1, null)
                .add(1, new ItemStack(Material.IRON_INGOT), -1, null)
                .add(0.5, new ItemStack(Material.IRON_INGOT), -1, null)
                .add(0.5, new ItemStack(Material.IRON_INGOT), -1, null)
                .add(0.5, new ItemStack(Material.IRON_INGOT), -1, null)
                .add(0.5, new ItemStack(Material.IRON_INGOT), -1, null)
                .add(0.1, new ItemStack(Material.JACK_O_LANTERN), 0, false)
                .add(0.33, new ItemStack(Material.JACK_O_LANTERN), 0, true)
                .add(0.33, new ItemStack(Material.JACK_O_LANTERN), 1, false)
                .add(0.66, new ItemStack(Material.JACK_O_LANTERN), 1, true)
                .add(0.66, new ItemStack(Material.JACK_O_LANTERN), 2, false)
                .add(1, new ItemStack(Material.JACK_O_LANTERN), 2, true)
                .add(1, new ItemStack(Material.IRON_INGOT, 3), -2, null)
                .add(1, new ItemStack(Material.JACK_O_LANTERN), -2, false)
                .add(1, new ItemStack(Material.JACK_O_LANTERN), -2, true)
        ));
        add(new MobLootsItem(EntityType.SLIME, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(0.75, new ItemStack(Material.SLIME_BALL), -2, null)
                .add(0.5, new ItemStack(Material.SLIME_BALL), -2, null)
        ));
        add(new MobLootsItem(EntityType.MAGMA_CUBE, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(0.75, new ItemStack(Material.MAGMA_CREAM), -2, null)
                .add(0.5, new ItemStack(Material.MAGMA_CREAM), -2, null)
        ));
        add(new MobLootsItem(EntityType.GHAST, false, new Loots()
                .setChanceLvlAmountCoefficient(1)
                .add(1, new ItemStack(Material.GHAST_TEAR), -2, null)
                .add(0.75, new ItemStack(Material.GHAST_TEAR), -2, null)
                .add(0.5, new ItemStack(Material.GHAST_TEAR), -2, null)
        ));
        add(new MobLootsItem(EntityType.ENDER_DRAGON, false, new Loots()
                .setChanceLvlProbaCoefficient(0.5)
                .add(1, new ItemStack(Material.NETHER_STAR), -2, null)
                .add(1, new ItemStack(Material.NETHER_STAR), -2, null)
                .add(1, new ItemStack(Material.NETHER_STAR), -2, null)
                .add(0.75, new ItemStack(Material.NETHER_STAR), -2, null)
                .add(0.5, new ItemStack(Material.NETHER_STAR), -2, null)
                .add(0.5, new ItemStack(Material.NETHER_STAR), -2, null)
                .add(0.25, new ItemStack(Material.NETHER_STAR), -2, null)
        ));
    }};

    public static class BlockLootsItem {
        private List<Material> materials;
        private boolean isExclusive;
        private Loots loots;

        public BlockLootsItem(List<Material> materials, boolean isExclusive, Loots loots) {
            this.materials = materials;
            this.isExclusive = isExclusive;
            this.loots = loots;
        }

        public List<Material> getMaterials() {
            return materials;
        }

        public void setMaterials(List<Material> materials) {
            this.materials = materials;
        }

        public boolean isExclusive() {
            return isExclusive;
        }

        public void setExclusive(boolean inclusive) {
            isExclusive = inclusive;
        }

        public Loots getLoots() {
            return loots;
        }

        public void setLoots(Loots loots) {
            this.loots = loots;
        }
    }

    public static class MobLootsItem {
        private EntityType type;
        private EntityData data;
        private boolean isExclusive;
        private Loots loots;

        public MobLootsItem(EntityType type, boolean isExclusive, Loots loots) {
            this.type = type;
            data = EntityData.WHATEVER;
            this.isExclusive = isExclusive;
            this.loots = loots;
        }

        public MobLootsItem(EntityType type, EntityData data, boolean isExclusive, Loots loots) {
            this.type = type;
            this.data = data;
            this.isExclusive = isExclusive;
            this.loots = loots;
        }

        public EntityType getType() {
            return type;
        }

        public void setType(EntityType type) {
            this.type = type;
        }

        public EntityData getData() {
            return data;
        }

        public void setData(EntityData data) {
            this.data = data;
        }

        public boolean isExclusive() {
            return isExclusive;
        }

        public void setExclusive(boolean inclusive) {
            isExclusive = inclusive;
        }

        public Loots getLoots() {
            return loots;
        }

        public void setLoots(Loots loots) {
            this.loots = loots;
        }
    }

    public static enum EntityData {
        CREEPER_NORMAL, CREEPER_SUPERCHARGED,
        SKELETON_NORMAL, SKELETON_WITHER,
        WHATEVER;
    }

    @EventHandler
    public static void onBreakBlock(BlockBreakEvent e) {
    }

    @EventHandler
    public static void onPlaceBlock(BlockPlaceEvent e) {
    }

    @EventHandler
    public static void onDropItem(PlayerDropItemEvent e) {
    }

    @EventHandler
    public static void onPickupItem(PlayerPickupItemEvent e) {
    }

    @EventHandler
    public static void onDamageByEntity(EntityDamageByEntityEvent e) {
    }

    @EventHandler
    public static void onDamages(EntityDamageEvent e) {
    }

    @EventHandler
    public static void onMove(PlayerMoveEvent e) {
    }

    @EventHandler
    public static void onExplode(ExplosionPrimeEvent e) {
    }

    @EventHandler
    public static void onSpawn(CreatureSpawnEvent e) {
    }

    @EventHandler
    public static void onPortal(EntityCreatePortalEvent e) {
    }

    @EventHandler
    public static void onFood(FoodLevelChangeEvent e) {
    }

    @EventHandler
    public static void onEntityDeath(EntityDeathEvent e) {
        e.getDrops().clear();
    }

    @EventHandler
    public static void onInteractAtBlock(PlayerInteractEvent e) {
    }

    public static boolean isInside(Location loc, Location loc1, Location loc2) {
        return loc1.getWorld().getUID()
                == loc.getWorld().getUID() && loc.getWorld().getUID()
                == loc2.getWorld().getUID()
                && Math.max(loc1.getX(), loc2.getX()) >= loc.getX() && loc.getX() >= Math.min(loc1.getX(), loc2.getX())
                && Math.max(loc1.getY(), loc2.getY()) >= loc.getY() && loc.getY() >= Math.min(loc1.getY(), loc2.getY())
                && Math.max(loc1.getZ(), loc2.getZ()) >= loc.getZ() && loc.getZ() >= Math.min(loc1.getZ(), loc2.getZ());
    }

}
