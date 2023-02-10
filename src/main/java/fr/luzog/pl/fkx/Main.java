package fr.luzog.pl.fkx;

import fr.luzog.pl.fkx.commands.Admin.Vanish;
import fr.luzog.pl.fkx.commands.CommandManager;
import fr.luzog.pl.fkx.commands.Other.Ad;
import fr.luzog.pl.fkx.events.Events;
import fr.luzog.pl.fkx.game.GListener;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.utils.Color;
import fr.luzog.pl.fkx.utils.Config;
import fr.luzog.pl.fkx.utils.Crafting;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DateFormat;
import java.util.*;


public class Main extends JavaPlugin implements Listener {

    public static final Object VERSION = "Version 2.11";
    public static final String CMD = "fk";

    private static int sideLength = 27, centerLength;

    public static String SEASON, IP, SYS_PREFIX, PREFIX, HEADER, FOOTER, REBOOT_KICK_MESSAGE;
    public static ArrayList<String> ORGA;
    public static Main instance = null;
    public static World world = null, nether = null, end = null;

    public static Config.Globals globalConfig;
    public static Config.Kit kitConfig;

    public static boolean customCrafts, customCraftingTable, customLootingBlocksSystem, customLootingMobsSystem;

    public static boolean isCreeperActivated;
    public static double creeperSuperchargedChance;
    public static List<Utils.Pair<Material, Double>> normalCreeperLoots, chargedCreeperLoots;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        globalConfig = new Config.Globals("Globals.yml").load()
                .setVersion(VERSION, true)
                .setLang("fr-FR", true)
                .setSeason("Fallen Kingdom X", false)
                .setIp("play.azion.fr", false)
                .setOrga(Arrays.asList("Mathis_Bruel", "Luzog78"), false)
                .setSpawnProtectionDuration(60, false)
                .setWorlds("world", "world_nether", "world_the_end", false)
                .setCustomVanillaCraftsActivated(false, false)
                .setCustomCraftingTableActivated(true, false)
                .setCustomLootingBlocksSystemActivated(true, false)
                .setCustomLootingMobsSystemActivated(true, false)
                .setCustomCreeperActivated(true, false)
                .setCustomCreeperChance(0.1, false)
                .setCustomNormalCreeperDrops(new ArrayList<Utils.Pair<Material, Double>>() {{
                    add(new Utils.Pair<>(Material.SULPHUR, 1.0));
                    add(new Utils.Pair<>(Material.SULPHUR, 0.2));
                    add(new Utils.Pair<>(Material.SULPHUR, 0.2));
                }}, false)
                .setCustomChargedCreeperDrops(new ArrayList<Utils.Pair<Material, Double>>() {{
                    add(new Utils.Pair<>(Material.SULPHUR, 1.0));
                    add(new Utils.Pair<>(Material.SULPHUR, 0.8));
                    add(new Utils.Pair<>(Material.SULPHUR, 0.5));
                    add(new Utils.Pair<>(Material.SULPHUR, 0.2));
                    add(new Utils.Pair<>(Material.SULPHUR, 0.1));
                    add(new Utils.Pair<>(Material.TNT, 0.1));
                }}, false)
                .setAdActivated(true, false)
                .setCompassActivated(true, false)
                .save()
                .load(); // Reload the config for the next lines

        kitConfig = new Config.Kit("Kit.yml").load()
                .setInfos(Collections.singletonList(new HashMap<Object, Object>() {{
                    put("id", "Identifier string (must be unique)");
                    put("name", "Display name of the kit (can be colored or null)");
                    put("creation", "Creation timestamp (in milliseconds)");
                    put("creator", "Creator name (can be null)");
                    put("op-only", "Bolean value of the kit being only for OPs");
                    put("content", "Base64 encoded ItemStack array of the kit content (max length must be 36)");
                    put("armor", "Base64 encoded ItemStack array of the kit armor (length must be 4)");
                }}), true)
                .setKits(new ArrayList<>(), false)
                .save()
                .load(); // Reload the config for the next lines

        SEASON = globalConfig.getSeason().replace(" ", "").toUpperCase();
        IP = globalConfig.getIp();
        ORGA = new ArrayList<>(globalConfig.getOrga());

        centerLength = 9 + SEASON.length();

        SYS_PREFIX = "§8[§l§4SYSTEM§r§8] §r";
        PREFIX = "§8§l[§6" + SEASON + "§8§l] >> §7";

        String side = String.format("%" + sideLength + "s", "").replace(" ", "-");
        HEADER = "§9" + side + "-- §8[ §6" + SEASON + " §8] §9-" + side + "§r";
        FOOTER = String.format("§9%" + (63 + SEASON.length()) + "s§r", "").replace(" ", "-");

        REBOOT_KICK_MESSAGE = Main.HEADER + "\n\n§cRedémarrage du serveur.\n§6Reconnectez vous dans moins d'une minute !\n\n" + Main.FOOTER;

        System.out.println(" ");
        Color.sout(HEADER);
        souf("          §fInitialisation des differentes composantes");
        souf("              §fdu plugin §b" + SEASON + "§f...");
        souf("");
        souf("");

        InCaseThereIsAProblem.init();

        soufInstruction("§6Initialisation du module : §eListeners§6...");
        getServer().getPluginManager().registerEvents(Main.instance, Main.instance);
        Events.events.forEach(e -> getServer().getPluginManager().registerEvents(e, Main.instance));

        new BukkitRunnable() {
            @Override
            public void run() {
                soufInstruction("§6Initialisation du module : §eConfigurations§6...");

                customCrafts = globalConfig.isCustomVanillaCraftsActivated();
                customCraftingTable = globalConfig.isCustomCraftingTableActivated();
                customLootingBlocksSystem = globalConfig.isCustomLootingBlocksSystemActivated();
                customLootingMobsSystem = globalConfig.isCustomLootingMobsSystemActivated();

                isCreeperActivated = globalConfig.isCustomCreeperActivated();
                creeperSuperchargedChance = globalConfig.getCustomCreeperChance();
                normalCreeperLoots = globalConfig.getCustomNormalCreeperDrops();
                chargedCreeperLoots = globalConfig.getCustomChargedCreeperDrops();

                world = globalConfig.getOverworld();
                nether = globalConfig.getNether();
                end = globalConfig.getEnd();

                soufInstruction("§6Initialisation du module : §eVanish§6...");
                Vanish.initFromConfig();

                soufInstruction("§6Initialisation du module : §eCommandes§6...");
                CommandManager.init();

                soufInstruction("§6Initialisation du module : §eAd§6...");
                Ad.initFromConfig();

                soufInstruction("§6Initialisation du module : §eRunnable§6...");
                GListener.scheduleMainTask();

                soufInstruction("§6Initialisation du module : §eGame Manager§6...");
                GManager.initFromConfig(false);
                if (GManager.getCurrentGame() != null)
                    GManager.getCurrentGame().getListener().scheduleTask();

                soufInstruction("§6Initialisation du module : §eCrafts§6...");
                Crafting.initCrafts();

                String last = globalConfig.getLastGame() == null ? GManager.registered.isEmpty() ? null : GManager.registered.get(0).getId() : globalConfig.getLastGame();
                soufInstruction("§6Set de la derniere partie : " + (last != null ? "§a" + last : "§cAucune"));
                GManager.currentGameId = last;

                souf("");
                soufInstruction("§aInitialisations terminees !");

                new BukkitRunnable() {
                    @Override
                    public void run() {
//                FKManager man = new FKManager("null", FKManager.State.WAITING, 1, FKManager.Weather.CLEAR, 0, true,
//                        new FKOptions(
//                                new FKOptions.FKOption("PvP", 2, false),
//                                new FKOptions.FKOption("Nether", 4, false),
//                                new FKOptions.FKOption("Assauts", 6, false),
//                                new FKOptions.FKOption("End", 6, true)
//                        ),
//                        new FKListener("fko-kkk", "FALLEN KINGDOM X", 60 * 5),
//                        new Portal("Nether",
//                                null, new Location(world, 52, 226, -28), new Location(world, 51, 228, -28),
//                                Bukkit.getWorld("world_nether").getSpawnLocation(), null, null,
//                                Material.PORTAL, Material.AIR, (byte) 0, 60L, false),
//                        new Portal("End", null, null, null, null, null, null, Material.ENDER_PORTAL, Material.AIR, (byte) 0, 200L, false),
//                        new FKZone("Lobby", FKZone.Type.LOBBY,
//                                new Location(Main.world, 51, 225, -26),
//                                new Location(Main.world, 56, 224, -25),
//                                new Location(Main.world, 46, 226, -27),
//                                new FKPermissions(FKPermissions.Definition.OFF)
//                        ),
//                        new FKZone("Spawn", FKZone.Type.SPAWN,
//                                new Location(Main.world, 51, 225, -31),
//                                new Location(Main.world, 56, 224, -30),
//                                new Location(Main.world, 46, 226, -32),
//                                new FKPermissions(FKPermissions.Definition.DEFAULT,
//                                        new FKPermissions.Item(FKPermissions.Type.BREAK, FKPermissions.Definition.OFF),
//                                        new FKPermissions.Item(FKPermissions.Type.PLACE, FKPermissions.Definition.OFF),
//                                        new FKPermissions.Item(FKPermissions.Type.MOBS, FKPermissions.Definition.OFF))
//                        ),
//                        new ArrayList<FKZone>() {{
//                            add(new FKZone("nether", FKZone.Type.ZONE,
//                                    Bukkit.getWorld("world_nether").getSpawnLocation(),
//                                    new Location(Bukkit.getWorld("world_nether"), Integer.MIN_VALUE, -1, Integer.MIN_VALUE),
//                                    new Location(Bukkit.getWorld("world_nether"), Integer.MAX_VALUE, 256, Integer.MAX_VALUE),
//                                    new FKPermissions(FKPermissions.Definition.ON)));
//                            add(new FKZone("end", FKZone.Type.ZONE,
//                                    Bukkit.getWorld("world_the_end").getSpawnLocation(),
//                                    new Location(Bukkit.getWorld("world_the_end"), Integer.MIN_VALUE, -1, Integer.MIN_VALUE),
//                                    new Location(Bukkit.getWorld("world_the_end"), Integer.MAX_VALUE, 256, Integer.MAX_VALUE),
//                                    new FKPermissions(FKPermissions.Definition.ON)));
//                        }},
//                        new ArrayList<>(),
//                        new FKTeam("gods", "Dieux", SpecialChars.STAR_5_6 + " Dieu ||  ", ChatColor.DARK_RED, null, 0, new FKPermissions(FKPermissions.Definition.ON)),
//                        new FKTeam("specs", "Specs", SpecialChars.FLOWER_3 + " Spec ||  ", ChatColor.GRAY, null, 0, new FKPermissions(FKPermissions.Definition.OFF)),
//                        new ArrayList<>(Arrays.asList(new FKTeam("red", "§l[§rRouge§l]", "§lR§r ||  ", ChatColor.RED, new Location(Main.world, 52.5, 225, -28.5), 1.5, new FKPermissions(FKPermissions.Definition.DEFAULT)),
//                                new FKTeam("blue", "§l[§rBleue§l]", "§lB§r ||  ", ChatColor.BLUE, new Location(Main.world, 49.5, 225, -28.5), 1.5, new FKPermissions(FKPermissions.Definition.DEFAULT)))),
//                        new FKPermissions(FKPermissions.Definition.OFF,
//                                new FKPermissions.Item(FKPermissions.Type.BREAKSPE, FKPermissions.Definition.ON),
//                                new FKPermissions.Item(FKPermissions.Type.PLACESPE, FKPermissions.Definition.ON),
//                                new FKPermissions.Item(FKPermissions.Type.PVP, FKPermissions.Definition.ON),
//                                new FKPermissions.Item(FKPermissions.Type.MOBS, FKPermissions.Definition.ON)),
//                        new FKPermissions(FKPermissions.Definition.DEFAULT,
//                                new FKPermissions.Item(FKPermissions.Type.BREAK, FKPermissions.Definition.ON),
//                                new FKPermissions.Item(FKPermissions.Type.PLACE, FKPermissions.Definition.OFF)),
//                        new FKPermissions(FKPermissions.Definition.DEFAULT,
//                                new FKPermissions.Item(FKPermissions.Type.BREAK, FKPermissions.Definition.ON),
//                                new FKPermissions.Item(FKPermissions.Type.PLACE, FKPermissions.Definition.ON)),
//                        new FKPermissions(FKPermissions.Definition.DEFAULT,
//                                new FKPermissions.Item(FKPermissions.Type.BREAK, FKPermissions.Definition.OFF),
//                                new FKPermissions.Item(FKPermissions.Type.PLACE, FKPermissions.Definition.OFF)),
//                        new FKPermissions(FKPermissions.Definition.OFF)
//                );
//                man.register();
//                man.getListener().scheduleTask();
//
//                try {
//                    man.getPlayer("Luzog78", true).setTeam("specs");
//                } catch (NullPointerException ignored) {
//                }
//                try {
//                    new FKPlayer(Bukkit.getPlayer("Jigoku_san").getUniqueId(), "Jigoku_san", new PlayerStats(), null)
//                            .setTeam("blue");
//                } catch (NullPointerException ignored) {
//                }

                        saveAll(true);
                    }
                }.runTaskLater(Main.instance, 10);

                souf("");
                souf("");
                souf("        §fTous les modules ont etes initialise, le plugin");
                souf("          §fest maintenant pret. §aBon jeu a §2tous §a!");
                soufDate();
                Color.sout(FOOTER);
                System.out.println(" ");
            }
        }.runTask(this);
    }

    @Override
    public void onDisable() {
        System.out.println(" ");
        Color.sout(HEADER);
        souf("        §4Nettoyage des donnees caches du plugin...");
        souf("");
        souf("");

        soufInstruction("§6Nettoyage du §eScoreboard §6principal :");
        soufInstruction("  > §6Suppression des teams temporaires...");
        Bukkit.getScoreboardManager().getMainScoreboard().getTeams().forEach(t -> {
            if (t.getName().startsWith("gt"))
                t.unregister();
        });
        soufInstruction("  > §6Suppression des objectifs caches...");
        Bukkit.getScoreboardManager().getMainScoreboard().getObjectives().forEach(o -> {
            if (o.getName().startsWith("go"))
                o.unregister();
        });

        soufInstruction("§6Sauvegarde legere de toutes les §eGames§6...");
        GManager.saveAll(true);

        soufInstruction("§6Desactivation de tous les §eRunnable§6...");
        GListener.cancelMainTask();
        if (GManager.getCurrentGame() != null)
            GManager.getCurrentGame().getListener().cancelTask();

        souf("");
        soufInstruction("§cFin du processus de cloture.");

        souf("");
        souf("");
        souf("              §4Dechargement du plugin termine.");
        souf("                 §4Le plugin peut s'arreter.");
        soufDate();
        Color.sout(FOOTER);
        System.out.println(" ");
    }

    public static void saveAll(boolean headerAndFooter) {
        if (headerAndFooter) {
            System.out.println(" ");
            Color.sout(HEADER);
            souf("        §dSauvegarde generale de toutes les donnees du");
            souf("           §dserveur, des joueurs, et des jeux...");
            souf("");
            souf("");
        }

        soufInstruction("§6Sauvegarde de §eGlobalsConfig§6...");
        globalConfig.load()
                .setVersion(VERSION, true)
                .setLastGame(GManager.currentGameId, true);

        if (world != null && nether != null && end != null)
            globalConfig.setWorlds(world.getName(), nether.getName(), end.getName(), true);

        globalConfig.save();

        soufInstruction("§6Sauvegarde de §eVanish§6...");
        Vanish.saveToConfig();

        soufInstruction("§6Sauvegarde de §eAd§6...");
        Ad.saveToConfig();

        soufInstruction("§6Sauvegarde de toutes les donnees des §eGames§6...");
        GManager.saveAll(false);

        if (headerAndFooter) {
            souf("");
            soufInstruction("§5Sauvegardes mises-a-jour.");

            souf("");
            souf("");
            souf("             §dDonnees sauvegardees avec succes.");
            soufDate();
            Color.sout(FOOTER);
            System.out.println(" ");
        }
    }

    public static void souf(String msg) {
        System.out.println(String.format(Color.convert("§9|%-" + (centerLength + sideLength * 2 - 2
                        + Color.convert(msg).length() - ChatColor.stripColor(msg).length()) + "s§9|"),
                Color.convert(msg)));
    }

    public static void soufInstruction(String msg) {
        souf("   " + PREFIX + " " + msg);
    }

    public static void soufDate() {
        souf("");
        String msg = "§7- " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.FRENCH)
                .format(new Date()) + " | " + DateFormat.getTimeInstance(DateFormat.FULL,
                Locale.ENGLISH).format(new Date()) + "  ";
        System.out.println(String.format(Color.convert("§9|%" + (centerLength + sideLength * 2 - 2
                        + Color.convert(msg).length() - ChatColor.stripColor(msg).length()) + "s§9|"),
                Color.convert(msg)));
    }

    public static void clearLag(boolean soft) {
        new BukkitRunnable() {
            int time = 5;
            int entities = 0;

            public void increase() {
                entities++;
            }

            @Override
            public void run() {
                if (time <= 0) {
                    if (soft) {
                        List<EntityType> blacklist = Arrays.asList(EntityType.ITEM_FRAME, EntityType.DROPPED_ITEM, EntityType.FIREBALL, EntityType.VILLAGER,
                                EntityType.ARROW, EntityType.EXPERIENCE_ORB, EntityType.SPLASH_POTION, EntityType.THROWN_EXP_BOTTLE, EntityType.FIREWORK,
                                EntityType.SMALL_FIREBALL, EntityType.FALLING_BLOCK, EntityType.SILVERFISH, EntityType.RABBIT, EntityType.SLIME,
                                EntityType.MAGMA_CUBE, EntityType.PIG_ZOMBIE, EntityType.WITHER_SKULL, EntityType.BOAT, EntityType.LIGHTNING);

                        world.getEntities().forEach(e -> {
                            if (blacklist.contains(e.getType())) {
                                e.remove();
                                increase();
                            }
                        });
                    } else
                        world.getEntities().forEach(e -> {
                            if (!e.getType().equals(EntityType.PLAYER) && !e.getType().equals(EntityType.ARMOR_STAND)) {
                                e.remove();
                                increase();
                            }
                        });
                    Bukkit.broadcastMessage(Main.PREFIX + "§c§nClearLag§c effectué ! (§8§o" + entities + " §centités supprimées)");
                    cancel();
                } else {
                    Bukkit.broadcastMessage(Main.PREFIX + "§cClearLag§c dans §4" + time + " §cseconde" + (time == 1 ? "" : "s") + "...");
                    time--;
                }
            }
        }.runTaskTimer(Main.instance, 0, 20);
    }

}
