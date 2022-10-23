package fr.luzog.pl.fkx.utils;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.events.Events;
import fr.luzog.pl.fkx.game.GManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class Crafting implements Listener {

    public static List<ShapedCraft> shapedCrafts = new ArrayList<>();
    public static List<ShapelessCraft> shapelessCrafts = new ArrayList<>();

    public static final String NAME = "§8Crafting Table";
    public static final List<Integer> SLOTS = Utils.zoneOf(Utils.posOf(1, 2), Utils.posOf(3, 4));
    public static final Integer RESULT = Utils.posOf(6, 3);

    public static final Ingredient
            stick = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.STICK)),
            log = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.LOG)),
            wood = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.WOOD)),
            cobble = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.COBBLESTONE)),
            stone = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.STONE)),
            coal = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.COAL)),
            iron = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.IRON_INGOT)),
            gold = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.GOLD_INGOT)),
            redstone = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.REDSTONE)),
    // Lapis needs to be SIMILAR 'cause of durability
    lapis = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.INK_SACK, 1, (short) 4, null)),
            diamond = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.DIAMOND)),
            emerald = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.EMERALD)),
            quartz = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.QUARTZ)),
            leather = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.LEATHER)),
            apple = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.APPLE)),
            carrot = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.CARROT)),
            feather = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.FEATHER)),
            flint = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.FLINT)),
            obsi = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.OBSIDIAN)),
            pearl = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.ENDER_PEARL)),
            eye = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.EYE_OF_ENDER)),
            rod = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.BLAZE_ROD)),
            blaze_powder = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.BLAZE_POWDER)),
            powder = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.SULPHUR)),
            sand = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.SAND)),
            glass = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.GLASS)),
            sandstone = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.SANDSTONE)),
            book = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.BOOK)),
            string = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.STRING)),
            glow = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.GLOWSTONE_DUST)),
            clay = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.CLAY_BALL)),
            snow = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.SNOW_BALL)),
            slime = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.SLIME_BALL)),
            brick_ingot = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.CLAY_BRICK)),
            brick_block = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.BRICK)),
            nether_brick = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.NETHER_BRICK)),
            minecart = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.MINECART)),
            cane = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.SUGAR_CANE)),
            sugar = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.SUGAR)),
            wheat = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.WHEAT)),
            melon = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.MELON)),
            egg = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.EGG)),
            milk = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.MILK_BUCKET), AfterLevel.SIMILAR, Items.i(Material.BUCKET)),
            paper = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.PAPER)),
            wool = new Ingredient(SeverityLevel.TYPE, Items.i(Material.WOOL)),
            brown_mushroom = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.BROWN_MUSHROOM)),
            red_mushroom = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.RED_MUSHROOM)),
            bowl = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.BOWL)),
            spider_eye = new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.SPIDER_EYE));

    public static ItemStack getItem() {
        return Items.builder(Material.WORKBENCH).setName(NAME).setLore(
                "§8------------------------",
                " ",
                "§7 Bienvenue sur cette",
                "§7 Table de Crafts Custom !",
                " ",
                "§7 Les crafts disponibles",
                "§7  sont les mêmes que dans",
                "§7  Minecraft Vanilla !",
                "§7 Mais évidemment, il y en",
                "§7  a plusieurs nouveaux,",
                "§7  d'autre modifiés, ainsi",
                "§7  que de bloqués pour le",
                "§7  bon déroulement du Jeu...",
                " ",
                "§7 Amusez-vous !",
                " ",
                "§8------------------------"
        ).getNBT().setBoolean(Events.cantClickOnTag, true).build();
    }

    public static ItemStack getNone() {
        return Items.builder(Items.l_gray()).setName("§4------------------------").setLore(
                " ",
                "§c Il est possible que les",
                "§c  crafts n'aient pas été",
                "§c  mis à jour.",
                " ",
                "§c Vous n'avez qu'à",
                "§c  cliquer n'importe où",
                "§c  pour rafraichir",
                "§c  l'inventaire.",
                " ",
                "§4------------------------"
        ).getNBT().setBoolean(Events.cantClickOnTag, true).build();
    }

    public static Inventory getInv() {
        Inventory inv = Bukkit.createInventory(null, 54, NAME);
        Utils.fill(inv, 0, 53, Items.gray());
        Utils.fill(inv, 1, 7, Items.blue());
        Utils.fill(inv, 3, 5, Items.orange());
        inv.setItem(4, getItem());
        SLOTS.forEach(s -> inv.setItem(s, Items.air()));
        inv.setItem(RESULT, getNone());
        return inv;
    }

    public static void initCrafts() {
        new ShapedCraft(Items.builder(Material.GOLDEN_APPLE).setDurability((short) 1).setName("§5§k00§r §b-=|[§r §6§nNotch's Apple§r §b]|=-§r §5§k00§r").build())
                .setIngredient(new Ingredient(SeverityLevel.TYPE, Items.i(Material.GOLD_BLOCK)), 0, 2, 6, 8)
                .setIngredient(new Ingredient(SeverityLevel.TYPE_AMOUNT, Items.i(Material.DIAMOND, 4)), 1, 3, 5, 7)
                .setIngredient(new Ingredient(SeverityLevel.TYPE, Items.i(Material.GOLDEN_APPLE)), 4)
                .register();

        /* -- ----------------- -- */
        /* -- TOOLS AND WEAPONS -- */
        /* -- ----------------- -- */
        registerSwordShape(Items.stone_sword(), wood, stick);
        registerSwordShape(Items.stone_sword(), cobble, stick);
        registerSwordShape(Items.iron_sword(), iron, stick);
        registerSwordShape(Items.gold_sword(), gold, stick);
        registerSwordShape(Items.diamond_sword(), diamond, stick);
        registerPickaxeShape(Items.stone_pickaxe(), wood, stick);
        registerPickaxeShape(Items.stone_pickaxe(), cobble, stick);
        registerPickaxeShape(Items.iron_pickaxe(), iron, stick);
        registerPickaxeShape(Items.gold_pickaxe(), gold, stick);
        registerPickaxeShape(Items.diamond_pickaxe(), diamond, stick);
        registerAxeShape(Items.stone_axe(), wood, stick);
        registerAxeShape(Items.stone_axe(), cobble, stick);
        registerAxeShape(Items.iron_axe(), iron, stick);
        registerAxeShape(Items.gold_axe(), gold, stick);
        registerAxeShape(Items.diamond_axe(), diamond, stick);
        registerShovelShape(Items.stone_shovel(), wood, stick);
        registerShovelShape(Items.stone_shovel(), cobble, stick);
        registerShovelShape(Items.iron_shovel(), iron, stick);
        registerShovelShape(Items.gold_shovel(), gold, stick);
        registerShovelShape(Items.diamond_shovel(), diamond, stick);
        registerHoeShape(Items.stone_hoe(), wood, stick);
        registerHoeShape(Items.stone_hoe(), cobble, stick);
        registerHoeShape(Items.iron_hoe(), iron, stick);
        registerHoeShape(Items.gold_hoe(), gold, stick);
        registerHoeShape(Items.diamond_hoe(), diamond, stick);
        registerHelmetShape(Items.leather_helmet(), leather);
        registerHelmetShape(Items.iron_helmet(), iron);
        registerHelmetShape(Items.gold_helmet(), gold);
        registerHelmetShape(Items.diamond_helmet(), diamond);
        registerChestPlateShape(Items.leather_chestplate(), leather);
        registerChestPlateShape(Items.iron_chestplate(), iron);
        registerChestPlateShape(Items.gold_chestplate(), gold);
        registerChestPlateShape(Items.diamond_chestplate(), diamond);
        registerLeggingsShape(Items.leather_leggings(), leather);
        registerLeggingsShape(Items.iron_leggings(), iron);
        registerLeggingsShape(Items.gold_leggings(), gold);
        registerLeggingsShape(Items.diamond_leggings(), diamond);
        registerBootsShape(Items.leather_boots(), leather);
        registerBootsShape(Items.iron_boots(), iron);
        registerBootsShape(Items.gold_boots(), gold);
        registerBootsShape(Items.diamond_boots(), diamond);
        registerBowShape(Items.bow(), string, stick);
        registerFishingRodShape(Items.fishing_rod(), string, stick);


        /* -- ------------------ -- */
        /* -- ORE AND RESSOURCES -- */
        /* -- ------------------ -- */
        register9BlockShape(Items.i(Material.COAL_BLOCK), coal);
        register9BlockShape(Items.i(Material.IRON_BLOCK), iron);
        register9BlockShape(Items.i(Material.GOLD_BLOCK), gold);
        register9BlockShape(Items.i(Material.REDSTONE_BLOCK), redstone);
        register9BlockShape(Items.i(Material.LAPIS_BLOCK), lapis);
        register9BlockShape(Items.i(Material.DIAMOND_BLOCK), diamond);
        register9BlockShape(Items.i(Material.EMERALD_BLOCK), emerald);
        register2BlockShape(Items.i(Material.QUARTZ_BLOCK), quartz);
        new ShapelessCraft(Items.i(Material.COAL, 9)).addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.COAL_BLOCK))).register();
        new ShapelessCraft(Items.i(Material.IRON_INGOT, 9)).addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.IRON_BLOCK))).register();
        new ShapelessCraft(Items.i(Material.GOLD_INGOT, 9)).addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.GOLD_BLOCK))).register();
        new ShapelessCraft(Items.i(Material.REDSTONE, 9)).addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.REDSTONE_BLOCK))).register();
        new ShapelessCraft(Items.i(Material.INK_SACK, 9, (short) 4, null)).addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.LAPIS_BLOCK))).register();
        new ShapelessCraft(Items.i(Material.DIAMOND, 9)).addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.DIAMOND_BLOCK))).register();
        new ShapelessCraft(Items.i(Material.EMERALD, 9)).addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.EMERALD_BLOCK))).register();
        new ShapelessCraft(Items.i(Material.QUARTZ, 4)).addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.QUARTZ_BLOCK))).register();

        new ShapelessCraft(Items.i(Material.WOOD, 4)).addIngredient(log).register();
        new ShapelessCraft(Items.i(Material.STICK, 4)).addIngredient(wood).addIngredient(wood).register();
        register2BlockShape(Items.i(Material.WOOL), string);
        register2BlockShape(Items.i(Material.GLOWSTONE), glow);
        register2BlockShape(Items.i(Material.CLAY), clay);
        register2BlockShape(Items.i(Material.SNOW), snow);
        register2BlockShape(Items.i(Material.BRICK), brick_ingot);
        register2BlockShape(Items.i(Material.SANDSTONE), sand);


        /* -- ------------- -- */
        /* -- USEFUL BLOCKS -- */
        /* -- ------------- -- */
        register2BlockShape(Items.i(Material.WORKBENCH), wood);
        registerSimpleUpgradeShape(Items.i(Material.CHEST), null, wood);
        registerSimpleUpgradeShape(Items.i(Material.FURNACE), null, cobble);
        registerSimpleUpgradeShape(Items.i(Material.ENDER_CHEST), eye, obsi);
        new ShapedCraft(Items.i(Material.ENCHANTMENT_TABLE)).setIngredient(obsi, 2, 4, 5, 8).setIngredient(diamond, 1, 7).setIngredient(book, 3).register();
        new ShapedCraft(Items.i(Material.BOOKSHELF)).setIngredient(book, 1, 4, 7).setIngredient(wood, 0, 2, 3, 5, 6, 8).register();
        registerComplexUpgradeShape(Items.tnt(), powder, powder, sand);
        new ShapedCraft(Items.i(Material.BED)).setIngredient(wool, 0, 3, 6).setIngredient(wood, 1, 4, 7).register();
        new ShapedCraft(Items.i(Material.BED)).setIngredient(wool, 1, 4, 7).setIngredient(wood, 2, 5, 8).register();
        new ShapedCraft(Items.i(Material.SIGN)).setIngredient(wood, 0, 1, 3, 4, 6, 7).setIngredient(stick, 5).register();
        new ShapedCraft(Items.i(Material.LADDER, 3)).setIngredient(stick, 0, 1, 2, 4, 6, 7, 8).register();
        new ShapelessCraft(Items.i(Material.BREWING_STAND_ITEM)).addIngredient(rod).addIngredient(cobble).addIngredient(cobble).addIngredient(cobble).register();
        new ShapedCraft(Items.i(Material.CAULDRON_ITEM)).setIngredient(iron, 0, 1, 2, 5, 6, 7, 8).register();
        new ShapedCraft(Items.i(Material.ANVIL)).setIngredient(iron, 2, 4, 5, 8)
                .setIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.IRON_BLOCK)), 0, 3, 6).register();


        /* -- ------------ -- */
        /* -- USEFUL ITEMS -- */
        /* -- ------------ -- */
        registerBucketShape(Items.i(Material.BUCKET), iron);
        registerBucketShape(Items.i(Material.GLASS_BOTTLE, 3), glass);
        registerBucketShape(Items.i(Material.BOWL, 3), wood);
        register9BlockShape(Items.i(Material.HAY_BLOCK), wheat);
        new ShapelessCraft(Items.i(Material.WHEAT, 9)).addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.HAY_BLOCK))).register();
        new ShapelessCraft(Items.i(Material.SHEARS)).addIngredient(iron).addIngredient(iron).register();
        new ShapelessCraft(Items.i(Material.ARROW, 4)).addIngredient(stick).addIngredient(flint).addIngredient(feather).register();
        new ShapedCraft(Items.i(Material.BOAT)).setIngredient(wood, 0, 1, 4, 6, 7).register();
        new ShapedCraft(Items.i(Material.BOAT)).setIngredient(wood, 1, 2, 5, 7, 8).register();
        new ShapedCraft(Items.i(Material.MINECART)).setIngredient(iron, 0, 1, 4, 6, 7).register();
        new ShapedCraft(Items.i(Material.MINECART)).setIngredient(iron, 1, 2, 5, 7, 8).register();
        new ShapelessCraft(Items.i(Material.POWERED_MINECART)).addIngredient(minecart).addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.FURNACE))).register();
        new ShapelessCraft(Items.i(Material.STORAGE_MINECART)).addIngredient(minecart).addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.CHEST))).register();
        new ShapelessCraft(Items.i(Material.HOPPER_MINECART)).addIngredient(minecart).addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.HOPPER))).register();
        new ShapelessCraft(Items.i(Material.EXPLOSIVE_MINECART)).addIngredient(minecart).addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.TNT))).register();
        new ShapelessCraft(Items.i(Material.PAPER, 3)).addIngredient(cane).addIngredient(cane).addIngredient(cane).register();
        new ShapelessCraft(Items.i(Material.BOOK)).addIngredient(paper).addIngredient(paper).addIngredient(paper).addIngredient(leather).register();
        new ShapedCraft(Items.i(Material.LEASH)).setIngredient(slime, 4).setIngredient(string, 0, 1, 3, 8).register();


        /* -- --------------- -- */
        /* -- FOOD AND POTION -- */
        /* -- --------------- -- */
        registerSimpleUpgradeShape(Items.i(Material.GOLDEN_APPLE), apple, gold);
        registerSimpleUpgradeShape(Items.i(Material.GOLDEN_CARROT), carrot, gold);
        new ShapelessCraft(Items.i(Material.SUGAR)).addIngredient(cane).register();
        new ShapelessCraft(Items.i(Material.COOKED_BEEF)).addIngredient(wheat).addIngredient(wheat).addIngredient(wheat).register();
        new ShapelessCraft(Items.i(Material.SPECKLED_MELON)).addIngredient(melon).addIngredient(gold).register();
        new ShapelessCraft(Items.i(Material.SPECKLED_MELON)).addIngredient(milk).addIngredient(milk).addIngredient(milk)
                .addIngredient(wheat).addIngredient(wheat).addIngredient(wheat).addIngredient(egg)
                .addIngredient(sugar).addIngredient(sugar).register();
        new ShapelessCraft(Items.i(Material.COOKED_BEEF)).addIngredient(bowl).addIngredient(brown_mushroom).addIngredient(red_mushroom).register();
        new ShapelessCraft(Items.i(Material.BLAZE_POWDER, 2)).addIngredient(rod).register();
        new ShapelessCraft(Items.i(Material.EYE_OF_ENDER)).addIngredient(pearl).addIngredient(blaze_powder).register();
        new ShapelessCraft(Items.i(Material.MAGMA_CREAM)).addIngredient(slime).addIngredient(blaze_powder).register();
        new ShapelessCraft(Items.i(Material.FERMENTED_SPIDER_EYE)).addIngredient(spider_eye).addIngredient(sugar).addIngredient(brown_mushroom).register();
        new ShapelessCraft(Items.i(Material.FERMENTED_SPIDER_EYE)).addIngredient(spider_eye).addIngredient(sugar).addIngredient(red_mushroom).register();


        /* -- ------------------ -- */
        /* -- REDSTONE MECHANISM -- */
        /* -- ------------------ -- */
        new ShapedCraft(Items.i(Material.RAILS, 16)).setIngredient(iron, 0, 1, 2, 6, 7, 8).setIngredient(stick, 4).register();
        new ShapedCraft(Items.i(Material.POWERED_RAIL, 6)).setIngredient(gold, 0, 1, 2, 6, 7, 8)
                .setIngredient(stick, 4).setIngredient(redstone, 5).register();
        new ShapedCraft(Items.i(Material.DETECTOR_RAIL, 6)).setIngredient(iron, 0, 1, 2, 6, 7, 8)
                .setIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.STONE_PLATE)), 4).setIngredient(redstone, 5).register();
        new ShapedCraft(Items.i(Material.PISTON_BASE)).setIngredient(wood, 1, 3, 6).setIngredient(cobble, 1, 2, 7, 8)
                .setIngredient(iron, 4).setIngredient(redstone, 5).register();
        new ShapelessCraft(Items.i(Material.PISTON_STICKY_BASE)).addIngredient(slime)
                .addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.PISTON_BASE))).register();
        new ShapelessCraft(Items.i(Material.REDSTONE_TORCH_ON)).addIngredient(redstone).addIngredient(stick).register();
        new ShapelessCraft(Items.i(Material.LEVER)).addIngredient(cobble).addIngredient(stick).register();
        new ShapedCraft(Items.i(Material.DIODE)).setIngredient(stone, 1, 4, 7).setIngredient(redstone, 3)
                .setIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.REDSTONE_TORCH_ON)), 0, 6).register();
        new ShapedCraft(Items.i(Material.DIODE)).setIngredient(stone, 2, 5, 8).setIngredient(redstone, 4)
                .setIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.REDSTONE_TORCH_ON)), 1, 7).register();
        new ShapedCraft(Items.i(Material.DROPPER)).setIngredient(cobble, 0, 1, 2, 3, 6, 7, 8).setIngredient(redstone, 5).register();
        new ShapedCraft(Items.i(Material.DISPENSER)).setIngredient(cobble, 0, 1, 2, 3, 6, 7, 8).setIngredient(redstone, 5)
                .setIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.BOW)), 4).register();
        new ShapedCraft(Items.i(Material.HOPPER)).setIngredient(iron, 0, 1, 5, 6, 7)
                .setIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.CHEST)), 4).register();
        registerSimpleUpgradeShape(Items.i(Material.NOTE_BLOCK), redstone, wood);
        registerSimpleUpgradeShape(Items.i(Material.JUKEBOX), diamond, wood);
        registerComplexUpgradeShape(Items.i(Material.REDSTONE_LAMP_OFF), new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.GLOWSTONE)), null, redstone);
        new ShapelessCraft(Items.i(Material.TRIPWIRE_HOOK)).addIngredient(iron).addIngredient(stick).addIngredient(wood).register();
        new ShapelessCraft(Items.i(Material.TRAPPED_CHEST)).addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.CHEST)))
                .addIngredient(new Ingredient(SeverityLevel.SIMILAR, Items.i(Material.TRIPWIRE_HOOK))).register();


        /* -- ----------------- -- */
        /* -- DECORATION BLOCKS -- */
        /* -- ----------------- -- */
        new ShapelessCraft(Items.i(Material.TORCH, 4)).addIngredient(stick).addIngredient(coal).register();
        registerSlabShape(Items.i(Material.WOOD_STEP, 6), wood);
        registerSlabShape(Items.i(Material.STEP, 6), stone);
        registerSlabShape(Items.i(Material.STEP, 6), cobble);
        registerSlabShape(Items.i(Material.STEP, 6), brick_block);
        registerSlabShape(Items.i(Material.STEP, 6), sandstone);
        registerSlabShape(Items.i(Material.STEP, 6), nether_brick);
        registerStairsShape(Items.i(Material.WOOD_STAIRS, 6), wood);
        registerStairsShape(Items.i(Material.COBBLESTONE_STAIRS, 6), stone);
        registerStairsShape(Items.i(Material.COBBLESTONE_STAIRS, 6), cobble);
        registerStairsShape(Items.i(Material.BRICK_STAIRS, 6), brick_block);
        registerStairsShape(Items.i(Material.SANDSTONE_STAIRS, 6), sandstone);
        registerStairsShape(Items.i(Material.NETHER_BRICK_STAIRS, 6), nether_brick);
        registerDoorShape(Items.i(Material.WOOD_DOOR, 3), wood);
        registerDoorShape(Items.i(Material.IRON_DOOR, 3), iron);
        registerBarsShape(Items.i(Material.TRAP_DOOR, 3), wood);
        registerBarsShape(Items.i(Material.IRON_BARDING, 16), iron);
        registerPlateShape(Items.i(Material.WOOD_PLATE), wood);
        registerPlateShape(Items.i(Material.STONE_PLATE), stone);
        new ShapelessCraft(Items.i(Material.WOOD_BUTTON)).addIngredient(wood).register();
        new ShapelessCraft(Items.i(Material.STONE_BUTTON)).addIngredient(stone).register();
        registerBarsShape(Items.i(Material.FENCE, 4), stick);
        new ShapedCraft(Items.i(Material.FENCE_GATE, 2)).setIngredient(stick, 0, 1, 6, 7).setIngredient(wood, 3, 4).register();
        new ShapedCraft(Items.i(Material.FENCE_GATE, 2)).setIngredient(stick, 1, 2, 7, 8).setIngredient(wood, 4, 5).register();
        registerBarsShape(Items.i(Material.THIN_GLASS, 16), glass);


        /* -- ------------- -- */
        /* -- CUSTOM CRAFTS -- */
        /* -- ------------- -- */
        registerComplexUpgradeShape(Items.i(Material.ENDER_PEARL), rod, lapis, obsi);
    }

    public static void registerSwordShape(ItemStack result, Ingredient component, Ingredient sleeve) {
        new ShapedCraft(result).setIngredient(component, 0, 1).setIngredient(sleeve, 2).register();
        new ShapedCraft(result).setIngredient(component, 3, 4).setIngredient(sleeve, 5).register();
        new ShapedCraft(result).setIngredient(component, 6, 7).setIngredient(sleeve, 8).register();
    }

    public static void registerPickaxeShape(ItemStack result, Ingredient component, Ingredient sleeve) {
        new ShapedCraft(result).setIngredient(component, 0, 3, 6).setIngredient(sleeve, 4, 5).register();
    }

    public static void registerShovelShape(ItemStack result, Ingredient component, Ingredient sleeve) {
        new ShapedCraft(result).setIngredient(component, 0).setIngredient(sleeve, 1, 2).register();
        new ShapedCraft(result).setIngredient(component, 3).setIngredient(sleeve, 4, 5).register();
        new ShapedCraft(result).setIngredient(component, 6).setIngredient(sleeve, 7, 8).register();
    }

    public static void registerAxeShape(ItemStack result, Ingredient component, Ingredient sleeve) {
        new ShapedCraft(result).setIngredient(component, 0, 1, 3).setIngredient(sleeve, 4, 5).register();
        new ShapedCraft(result).setIngredient(component, 3, 6, 7).setIngredient(sleeve, 4, 5).register();
        new ShapedCraft(result).setIngredient(component, 0, 3, 4).setIngredient(sleeve, 1, 2).register();
        new ShapedCraft(result).setIngredient(component, 3, 4, 6).setIngredient(sleeve, 7, 8).register();
    }

    public static void registerHoeShape(ItemStack result, Ingredient component, Ingredient sleeve) {
        new ShapedCraft(result).setIngredient(component, 0, 3).setIngredient(sleeve, 4, 5).register();
        new ShapedCraft(result).setIngredient(component, 3, 6).setIngredient(sleeve, 4, 5).register();
    }

    public static void registerBowShape(ItemStack result, Ingredient string, Ingredient sleeve) {
        new ShapedCraft(result).setIngredient(string, 0, 1, 2).setIngredient(sleeve, 3, 5, 7).register();
        new ShapedCraft(result).setIngredient(string, 6, 7, 8).setIngredient(sleeve, 1, 3, 5).register();
    }

    public static void registerFishingRodShape(ItemStack result, Ingredient string, Ingredient sleeve) {
        new ShapedCraft(result).setIngredient(string, 1, 2).setIngredient(sleeve, 0, 4, 8).register();
        new ShapedCraft(result).setIngredient(string, 7, 8).setIngredient(sleeve, 2, 4, 6).register();
    }

    public static void registerHelmetShape(ItemStack result, Ingredient ingredient) {
        new ShapedCraft(result).setIngredient(ingredient, 0, 1, 3, 6, 7).register();
        new ShapedCraft(result).setIngredient(ingredient, 1, 2, 4, 7, 8).register();
    }

    public static void registerChestPlateShape(ItemStack result, Ingredient ingredient) {
        new ShapedCraft(result).setIngredient(ingredient, 0, 1, 2, 4, 5, 6, 7, 8).register();
    }

    public static void registerLeggingsShape(ItemStack result, Ingredient ingredient) {
        new ShapedCraft(result).setIngredient(ingredient, 0, 1, 2, 3, 6, 7, 8).register();
    }

    public static void registerBootsShape(ItemStack result, Ingredient ingredient) {
        new ShapedCraft(result).setIngredient(ingredient, 0, 1, 6, 7).register();
        new ShapedCraft(result).setIngredient(ingredient, 1, 2, 7, 8).register();
    }

    public static void register2BlockShape(ItemStack result, Ingredient ingredient) {
        new ShapedCraft(result).setIngredient(ingredient, 0, 1, 3, 4).register();
        new ShapedCraft(result).setIngredient(ingredient, 1, 2, 4, 5).register();
        new ShapedCraft(result).setIngredient(ingredient, 3, 4, 6, 7).register();
        new ShapedCraft(result).setIngredient(ingredient, 4, 5, 7, 8).register();
    }

    public static void register9BlockShape(ItemStack result, Ingredient ingredient) {
        new ShapedCraft(result).setIngredient(ingredient, 0, 1, 2, 3, 4, 5, 6, 7, 8).register();
    }

    public static void registerSimpleUpgradeShape(ItemStack result, @Nullable Ingredient main, @Nullable Ingredient upgrader) {
        ShapedCraft c = new ShapedCraft(result);
        if (main != null)
            c.setIngredient(main, 4);
        if (upgrader != null)
            c.setIngredient(upgrader, 0, 1, 2, 3, 5, 6, 7, 8);
        if (main != null || upgrader != null)
            c.register();
    }

    public static void registerComplexUpgradeShape(ItemStack result, @Nullable Ingredient main, @Nullable Ingredient corner, @Nullable Ingredient edge) {
        ShapedCraft c = new ShapedCraft(result);
        if (main != null)
            c.setIngredient(main, 4);
        if (corner != null)
            c.setIngredient(corner, 0, 2, 6, 8);
        if (edge != null)
            c.setIngredient(edge, 1, 3, 5, 7);
        if (main != null || corner != null || edge != null)
            c.register();
    }

    public static void registerSlabShape(ItemStack result, Ingredient ingredient) {
        new ShapedCraft(result).setIngredient(ingredient, 0, 3, 6).register();
        new ShapedCraft(result).setIngredient(ingredient, 1, 4, 7).register();
        new ShapedCraft(result).setIngredient(ingredient, 2, 5, 8).register();
    }

    public static void registerStairsShape(ItemStack result, Ingredient ingredient) {
        new ShapedCraft(result).setIngredient(ingredient, 0, 1, 2, 4, 5, 8).register();
        new ShapedCraft(result).setIngredient(ingredient, 2, 4, 5, 6, 7, 8).register();
    }

    public static void registerDoorShape(ItemStack result, Ingredient ingredient) {
        new ShapedCraft(result).setIngredient(ingredient, 0, 1, 2, 3, 4, 5).register();
        new ShapedCraft(result).setIngredient(ingredient, 3, 4, 5, 6, 7, 8).register();
    }

    public static void registerBarsShape(ItemStack result, Ingredient ingredient) {
        new ShapedCraft(result).setIngredient(ingredient, 0, 1, 3, 4, 6, 7).register();
        new ShapedCraft(result).setIngredient(ingredient, 1, 2, 4, 5, 7, 8).register();
    }

    public static void registerPlateShape(ItemStack result, Ingredient ingredient) {
        new ShapedCraft(result).setIngredient(ingredient, 0, 3).register();
        new ShapedCraft(result).setIngredient(ingredient, 1, 4).register();
        new ShapedCraft(result).setIngredient(ingredient, 2, 5).register();
        new ShapedCraft(result).setIngredient(ingredient, 3, 6).register();
        new ShapedCraft(result).setIngredient(ingredient, 4, 7).register();
        new ShapedCraft(result).setIngredient(ingredient, 5, 8).register();
    }

    public static void registerBucketShape(ItemStack result, Ingredient ingredient) {
        new ShapedCraft(result).setIngredient(ingredient, 0, 4, 6).register();
        new ShapedCraft(result).setIngredient(ingredient, 1, 5, 7).register();
    }

    @EventHandler
    public static void onBlockInteract(PlayerInteractEvent e) {
        if (!Main.customCraftingTable || !e.hasBlock() || e.getClickedBlock().getType() != Material.WORKBENCH)
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                e.getPlayer().closeInventory();

                if (!e.getPlayer().isSneaking() && e.getAction() == Action.RIGHT_CLICK_BLOCK)
                    e.getPlayer().openInventory(getInv());
            }
        }.runTask(Main.instance);
    }

    @EventHandler
    public static void onClose(InventoryCloseEvent e) {
        if (!e.getInventory().getName().equals(NAME) || e.getInventory().getSize() != getInv().getSize())
            return;
        boolean hasMainItem = false;
        for(ItemStack is : e.getInventory().getContents())
            if(is != null && is.isSimilar(getItem()))
                hasMainItem = true;
        if(!hasMainItem)
            return;

        SLOTS.stream()
                .map(i -> e.getInventory().getItem(i))
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
                .forEach(is -> {
                    if(e.getPlayer().getInventory().firstEmpty() != -1)
                        e.getPlayer().getInventory().addItem(is);
                    else
                        e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), is);
                });
    }

    @EventHandler
    public static void onClick(InventoryClickEvent e) {
        if (!e.getInventory().getName().equals(NAME))
            return;

        boolean poursuite = e.getSlot() != -999 && e.getClickedInventory() != null
                && (e.getCurrentItem() == null || !new CustomNBT(e.getCurrentItem()).hasKey(Events.cantClickOnTag)
                || !new CustomNBT(e.getCurrentItem()).getBoolean(Events.cantClickOnTag));

        e.setCancelled(!poursuite);

        poursuite = poursuite && e.getClickedInventory().getType() == InventoryType.CHEST;

        boolean craft = false;

        if (poursuite && e.getSlot() == RESULT) {
            e.setCancelled(true);
            ItemStack[] ingredients = new ArrayList<ItemStack>() {{
                SLOTS.forEach(s -> add(e.getInventory().getItem(s)));
            }}.toArray(new ItemStack[9]);
            for (ShapedCraft r : shapedCrafts)
                if (r.compare(ingredients)) {
                    if (e.isShiftClick())
                        e.getWhoClicked().getInventory().addItem(r.getResult());
                    else if (e.getCursor().getType() == Material.AIR)
                        e.setCursor(r.getResult());
                    else {
                        ItemStack cursor = e.getCursor().clone();
                        cursor.setAmount(1);
                        ItemStack result = r.getResult().clone();
                        result.setAmount(1);
                        if (cursor.isSimilar(result) && e.getCursor().getAmount() + r.getResult().getAmount() <= result.getMaxStackSize()) {
                            result.setAmount(e.getCursor().getAmount() + r.getResult().getAmount());
                            e.setCursor(result);
                        } else {
                            e.setCancelled(true);
                            break;
                        }
                    }

                    ItemStack[] after = r.after(ingredients);
                    for (int i = 0; i < SLOTS.size(); i++)
                        e.getInventory().setItem(SLOTS.get(i), after[i]);
                    craft = true;
                    break;
                }

            if (!craft)
                for (ShapelessCraft r : shapelessCrafts)
                    if (r.compare(ingredients)) {
                        if (e.isShiftClick())
                            e.getWhoClicked().getInventory().addItem(r.getResult());
                        else if (e.getCursor().getType() == Material.AIR)
                            e.setCursor(r.getResult());
                        else {
                            ItemStack cursor = e.getCursor().clone();
                            cursor.setAmount(1);
                            ItemStack result = r.getResult().clone();
                            result.setAmount(1);
                            if (cursor.isSimilar(result)) {
                                result.setAmount(e.getCursor().getAmount() + r.getResult().getAmount());
                                e.setCursor(result);
                            } else
                                break;
                        }

                        ItemStack[] after = r.after(ingredients);
                        for (int i = 0; i < SLOTS.size(); i++)
                            e.getInventory().setItem(SLOTS.get(i), after[i]);
                        craft = true;
                        break;
                    }
        }


        boolean finalCraft = craft;
        new BukkitRunnable() {
            @Override
            public void run() {
                update(e.getInventory(), e.getWhoClicked());
                if (finalCraft)
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ItemStack cursor = e.getWhoClicked().getItemOnCursor();
                            e.getWhoClicked().setItemOnCursor(null);
                            if (e.getWhoClicked() instanceof Player)
                                ((Player) e.getWhoClicked()).updateInventory();
                            e.getWhoClicked().setItemOnCursor(cursor);
                        }
                    }.runTask(Main.instance);
            }
        }.runTask(Main.instance);
    }

    public static void update(Inventory inv, HumanEntity whoClicked) {
        boolean craft = false;
        GManager game = GManager.getCurrentGame() == null
                || GManager.getCurrentGame().getPlayer(whoClicked.getName(), false) == null ? null
                : GManager.getCurrentGame();
        Limits lim = game == null || game.getLimits() == null ? null : game.getLimits();
        ArrayList<Material> blacklist = lim == null || lim.getCraft() == null ? new ArrayList<>() : lim.getCraft();
        ItemStack[] ingredients = new ArrayList<ItemStack>() {{
            for (Integer slot : SLOTS) add(inv.getItem(slot) == null ? null : inv.getItem(slot).clone());
        }}.toArray(new ItemStack[9]);
        for (ShapedCraft r : shapedCrafts)
            if (r.compare(ingredients) && (r.getResult() == null || !blacklist.contains(r.getResult().getType()))) {
                inv.setItem(RESULT, r.getResult());
                craft = true;
                break;
            }
        for (ShapelessCraft r : shapelessCrafts)
            if (r.compare(ingredients) && (r.getResult() == null || !blacklist.contains(r.getResult().getType()))) {
                inv.setItem(RESULT, r.getResult());
                craft = true;
                break;
            }
        if (!craft)
            inv.setItem(RESULT, getNone());
    }

    public static enum SeverityLevel {TYPE, TYPE_AMOUNT, TYPE_NAME, TYPE_NAME_AMOUNT, NBT, SIMILAR, SIMILAR_AMOUNT}

    public static enum AfterLevel {NORMAL, SIMILAR, SIMILAR_AMOUNT}

    public static class Ingredient {
        private ItemStack item;
        private Integer amount;
        private Map<String, Object> nbt;
        private SeverityLevel severity;

        private ItemStack after;
        private AfterLevel afterLevel;

        public Ingredient(SeverityLevel severity, ItemStack item) {
            this.item = item;
            this.amount = severity == SeverityLevel.TYPE_AMOUNT || severity == SeverityLevel.TYPE_NAME_AMOUNT
                    || severity == SeverityLevel.SIMILAR_AMOUNT ? item.getAmount() : null;
            this.nbt = null;
            this.severity = severity;

            this.after = null;
            this.afterLevel = AfterLevel.NORMAL;
        }

        public Ingredient(SeverityLevel severity, Map<String, Object> nbt) {
            this.item = null;
            this.amount = null;
            this.nbt = nbt;
            this.severity = severity;

            this.after = null;
            this.afterLevel = AfterLevel.NORMAL;
        }

        public Ingredient(SeverityLevel severity, ItemStack item, @Nullable Integer amount) {
            this.item = item;
            this.amount = amount;
            this.nbt = null;
            this.severity = severity;

            this.after = null;
            this.afterLevel = AfterLevel.NORMAL;
        }

        public Ingredient(SeverityLevel severity, ItemStack item, @Nullable AfterLevel afterLevel, @Nullable ItemStack after) {
            this.item = item;
            this.amount = severity == SeverityLevel.TYPE_AMOUNT || severity == SeverityLevel.TYPE_NAME_AMOUNT
                    || severity == SeverityLevel.SIMILAR_AMOUNT ? item.getAmount() : null;
            this.nbt = null;
            this.severity = severity;

            this.after = after;
            this.afterLevel = afterLevel;
        }

        public Ingredient(SeverityLevel severity, @Nullable ItemStack item, @Nullable Integer amount, @Nullable Map<String, Object> nbt) {
            this.item = item;
            this.amount = amount;
            this.nbt = nbt;
            this.severity = severity;

            this.after = null;
            this.afterLevel = AfterLevel.NORMAL;
        }

        public Ingredient(SeverityLevel severity, ItemStack item, @Nullable Integer amount, @Nullable AfterLevel afterLevel, @Nullable ItemStack after) {
            this.item = item;
            this.amount = amount;
            this.nbt = null;
            this.severity = severity;

            this.after = after;
            this.afterLevel = afterLevel;
        }

        public Ingredient(SeverityLevel severity, @Nullable ItemStack item, @Nullable Integer amount, @Nullable Map<String, Object> nbt, @Nullable AfterLevel afterLevel, @Nullable ItemStack after) {
            this.item = item;
            this.amount = amount;
            this.nbt = nbt;
            this.severity = severity;

            this.after = after;
            this.afterLevel = afterLevel;
        }

        public Ingredient clone() {
            return new Ingredient(severity, item, amount, nbt, afterLevel, after);
        }

        public boolean compare(ItemStack item) {
            ItemStack is = item.clone();
            ItemStack compare;
            switch (getSeverity()) {
                case TYPE:
                    if (getItem().getType() != is.getType())
                        return false;
                    break;

                case TYPE_NAME:
                    if (getItem().getType() != is.getType())
                        return false;
                    else if (getItem().getItemMeta() == null || is.getItemMeta() == null)
                        return false;
                    else if (!getItem().getItemMeta().getDisplayName().equals(is.getItemMeta().getDisplayName()))
                        return false;
                    break;

                case TYPE_AMOUNT:
                    if (getItem().getType() != is.getType())
                        return false;
                    else if (getAmount() == null) {
                        if (getItem().getAmount() > is.getAmount())
                            return false;
                    } else if (getAmount() > is.getAmount())
                        return false;
                    break;

                case TYPE_NAME_AMOUNT:
                    if (getItem().getType() != is.getType())
                        return false;
                    else if (getItem().getItemMeta() == null || is.getItemMeta() == null)
                        return false;
                    else if (!getItem().getItemMeta().getDisplayName().equals(is.getItemMeta().getDisplayName()))
                        return false;
                    else if (getAmount() == null) {
                        if (getItem().getAmount() > is.getAmount())
                            return false;
                    } else if (getAmount() > is.getAmount())
                        return false;
                    break;

                case SIMILAR:
                    compare = getItem().clone();
                    compare.setAmount(1);
                    is.setAmount(1);
                    if (!is.isSimilar(compare))
                        return false;
                    break;

                case SIMILAR_AMOUNT:
                    compare = getItem().clone();
                    if (getAmount() != null)
                        compare.setAmount(getAmount());
                    if (!is.isSimilar(compare) || is.getAmount() < compare.getAmount())
                        return false;
                    break;

                case NBT:
                    CustomNBT nbt = new CustomNBT(is);
                    for (String key : getNbt().keySet())
                        if (!nbt.hasKey(key) || !getNbt().get(key).toString().equals(nbt.getString(key)))
                            return false;
                    break;
            }
            return true;
        }

        public ItemStack after(ItemStack ingredient) {
            ItemStack out = ingredient.clone();

            int minAmount = 1;
            if (getAfterLevel() == AfterLevel.NORMAL || getAfterLevel() == AfterLevel.SIMILAR)
                switch (getSeverity()) {
                    case TYPE:
                    case TYPE_NAME:
                    case SIMILAR:
                    case NBT:
                        break;

                    case TYPE_AMOUNT:
                    case TYPE_NAME_AMOUNT:
                    case SIMILAR_AMOUNT:
                        minAmount = getAmount() == null ? getItem().getAmount() : getAmount();
                        break;
                }

            int finalAmount = getAfterLevel() == AfterLevel.SIMILAR_AMOUNT ? getAfter().getAmount() : (out.getAmount() - minAmount);
            if (finalAmount <= 0)
                return Items.air();
            if (getAfterLevel() == AfterLevel.SIMILAR || getAfterLevel() == AfterLevel.SIMILAR_AMOUNT)
                out = getAfter();
            out.setAmount(finalAmount);
            return out;
        }

        public ItemStack getItem() {
            return item;
        }

        public Ingredient setItem(ItemStack item) {
            this.item = item;
            return this;
        }

        public Integer getAmount() {
            return amount;
        }

        public Ingredient setAmount(Integer amount) {
            this.amount = amount;
            return this;
        }

        public Map<String, Object> getNbt() {
            return nbt;
        }

        public Ingredient setNbt(Map<String, Object> nbt) {
            this.nbt = nbt;
            return this;
        }

        public SeverityLevel getSeverity() {
            return severity;
        }

        public Ingredient setSeverity(SeverityLevel severity) {
            this.severity = severity;
            return this;
        }

        public ItemStack getAfter() {
            return after;
        }

        public Ingredient setAfter(ItemStack after) {
            this.after = after;
            return this;
        }

        public AfterLevel getAfterLevel() {
            return afterLevel;
        }

        public Ingredient setAfterLevel(AfterLevel afterLevel) {
            this.afterLevel = afterLevel;
            return this;
        }
    }

    public static class ShapedCraft {
        private ItemStack result;
        private Ingredient[] ingredients;

        public ShapedCraft(ItemStack result) {
            this.result = result;
            ingredients = new Ingredient[9];
        }

        public boolean compare(List<ItemStack> ingredients) {
            return compare(ingredients.toArray(new ItemStack[9]));
        }

        public boolean compare(ItemStack[] ingredients) {
            for (int i = 0; i < ingredients.length; i++) {
                if (this.ingredients[i] == null) {
                    if (ingredients[i] != null)
                        return false;
                } else if (!this.ingredients[i].compare(ingredients[i] == null ? Items.air() : ingredients[i].clone()))
                    return false;
            }
            return true;
        }

        public ItemStack[] after(ItemStack[] ingredients) {
            ItemStack[] finalIngredients = ingredients.clone();
            for (int i = 0; i < finalIngredients.length; i++)
                if (finalIngredients[i] == null)
                    finalIngredients[i] = Items.air();
            for (int i = 0; i < finalIngredients.length; i++)
                finalIngredients[i] = this.ingredients[i] == null ? null : this.ingredients[i].after(finalIngredients[i]);
            return finalIngredients;
        }

        public void register() {
            if (!Crafting.shapedCrafts.contains(this))
                Crafting.shapedCrafts.add(this);
        }

        public void unregister() {
            Crafting.shapedCrafts.remove(this);
        }

        public ShapedCraft clearIngredients() {
            ingredients = new Ingredient[9];
            return this;
        }

        public Ingredient getIngredient(int slot) {
            return ingredients[slot];
        }

        public ShapedCraft setIngredient(Ingredient ingredient, int... slot) {
            for (int i : slot)
                ingredients[i] = ingredient;
            return this;
        }

        public ItemStack getResult() {
            return result;
        }

        public ShapedCraft setResult(ItemStack result) {
            this.result = result;
            return this;
        }

        public Ingredient[] getIngredients() {
            return ingredients;
        }

        public ShapedCraft setIngredients(Ingredient[] ingredients) {
            this.ingredients = ingredients;
            return this;
        }
    }

    public static class ShapelessCraft {
        private ItemStack result;
        private List<Ingredient> ingredients;

        public ShapelessCraft(ItemStack result) {
            this.result = result;
            ingredients = new ArrayList<>();
        }

        public boolean compare(ItemStack[] ingredients) {
            return compare(Arrays.asList(ingredients));
        }

        public boolean compare(List<ItemStack> ingredients) {
            List<Boolean> validations = new ArrayList<>();
            this.ingredients.forEach(i -> validations.add(false));

            for (ItemStack item : ingredients) {
                if (item == null)
                    continue;
                ItemStack is = item.clone();
                boolean valid = false;
                for (int i = 0; i < validations.size(); i++) {
                    if (validations.get(i))
                        continue;
                    if (this.ingredients.get(i).compare(is)) {
                        valid = true;
                        validations.set(i, true);
                        break;
                    }
                }
                if (!valid)
                    return false;
            }

            return !validations.contains(false);
        }

        public ItemStack[] after(ItemStack[] ingredients) {
            return after(Arrays.asList(ingredients)).toArray(new ItemStack[9]);
        }

        public List<ItemStack> after(List<ItemStack> ingredients) {
            List<ItemStack> arrangedIngredients = new ArrayList<>();
            ingredients.forEach(i -> arrangedIngredients.add(i == null ? Items.air() : i.clone()));

            List<ItemStack> finalIngredients = new ArrayList<>();
            for (ItemStack is : arrangedIngredients)
                for (Ingredient ingredient : this.ingredients)
                    if (ingredient.compare(is)) {
                        finalIngredients.add(ingredient.after(is));
                        break;
                    }

            return finalIngredients;
        }

        public void register() {
            if (!Crafting.shapelessCrafts.contains(this))
                Crafting.shapelessCrafts.add(this);
        }

        public void unregister() {
            Crafting.shapelessCrafts.remove(this);
        }

        public ShapelessCraft clearIngredients() {
            ingredients.clear();
            return this;
        }

        public Ingredient getIngredient(int index) {
            return ingredients.get(index);
        }

        public ShapelessCraft setIngredient(int index, Ingredient ingredient) {
            ingredients.set(index, ingredient);
            return this;
        }

        public ShapelessCraft addIngredient(Ingredient ingredient) {
            ingredients.add(ingredient);
            return this;
        }

        public ShapelessCraft addIngredient(int index, Ingredient ingredient) {
            ingredients.add(index, ingredient);
            return this;
        }

        public ShapelessCraft removeIngredient(Ingredient ingredient) {
            ingredients.remove(ingredient);
            return this;
        }

        public ShapelessCraft removeIngredient(int index) {
            ingredients.remove(index);
            return this;
        }

        public ItemStack getResult() {
            return result;
        }

        public ShapelessCraft setResult(ItemStack result) {
            this.result = result;
            return this;
        }

        public List<Ingredient> getIngredients() {
            return ingredients;
        }

        public ShapelessCraft setIngredients(List<Ingredient> ingredients) {
            this.ingredients = ingredients;
            return this;
        }
    }

}
