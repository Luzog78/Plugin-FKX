package fr.luzog.pl.fkx;

import fr.luzog.pl.fkx.commands.Admin.Vanish;
import fr.luzog.pl.fkx.commands.CommandManager;
import fr.luzog.pl.fkx.commands.Other.Ad;
import fr.luzog.pl.fkx.events.Events;
import fr.luzog.pl.fkx.fk.*;
import fr.luzog.pl.fkx.utils.*;
import fr.luzog.pl.fkx.utils.Color;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DateFormat;
import java.util.*;


public class Main extends JavaPlugin implements Listener {

    public static final Object VERSION = "Beta 1.1";

    public static final String SYS_PREFIX = "§8[§l§4SYSTEM§r§8] §r";
    public static final String PREFIX = "§8§l[§6FKX§8§l] >> §7";
    public static final String HEADER = "§9--------------------------- §8[ §6FKX §8] §9---------------------------§r";
    public static final String FOOTER = "§9---------------------------------------------------------------§r";
    public static final String REBOOT_KICK_MESSAGE = Main.HEADER + "\n\n§cRedémarrage du serveur.\nReconnectez vous dans moins d'une minute !\n\n" + Main.FOOTER;
    public static Main instance = null;
    public static World world = null, nether = null, end = null;

    public static Config.Globals globalConfig;

    public static boolean customCrafts, customCraftingTable, customLootingBlocksSystem, customLootingMobsSystem;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                System.out.println(" ");
                Color.sout(HEADER);
                souf("         §fInitialisation des differentes composantes");
                souf("           §fdu plugin de §bFallen Kingdom X§f...");
                souf("");
                souf("");

                soufInstruction("§6Initialisation du module : §eConfigurations§6...");
                globalConfig = new Config.Globals("Globals.yml").load()
                        .setVersion(VERSION, true)
                        .setLang("fr-FR", false)
                        .setWorlds("world", "world_nether", "world_the_end", false)
                        .setCustomVanillaCraftsActivated(false, false)
                        .setCustomCraftingTableActivated(true, false)
                        .setCustomLootingBlocksSystemActivated(true, false)
                        .setCustomLootingMobsSystemActivated(true, false)
                        .save()
                        .load(); // Reload the config for the next lines

                customCrafts = globalConfig.isCustomVanillaCraftsActivated();
                customCraftingTable = globalConfig.isCustomCraftingTableActivated();
                customLootingBlocksSystem = globalConfig.isCustomLootingBlocksSystemActivated();
                customLootingMobsSystem = globalConfig.isCustomLootingMobsSystemActivated();

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
                FKListener.scheduleMainTask();

                soufInstruction("§6Initialisation du module : §eFKManager§6...");
                FKManager.initFromConfig(false);
                if (FKManager.getCurrentGame() != null)
                    FKManager.getCurrentGame().getListener().scheduleTask();

                soufInstruction("§6Initialisation du module : §eListeners§6...");
                getServer().getPluginManager().registerEvents(Main.instance, Main.instance);
                Events.events.forEach(e -> getServer().getPluginManager().registerEvents(e, Main.instance));

                soufInstruction("§6Initialisation du module : §eCrafts§6...");
                Crafting.initCrafts();

                String last = globalConfig.getLastGame() == null ? FKManager.registered.isEmpty() ? null : FKManager.registered.get(0).getId() : globalConfig.getLastGame();
                soufInstruction("§6Set de la derniere partie : " + (last != null ? "§a" + last : "§cAucune"));
                FKManager.currentGameId = last;

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
                souf("       §fTous les modules ont etes initialise, le plugin");
                souf("         §fest maintenant pret. §aBon jeu a §2tous §a!");
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
        souf("       §4Nettoyage des donnees caches du plugin...");
        souf("");
        souf("");

        soufInstruction("§6Nettoyage du §eScoreboard §6principal :");
        soufInstruction("  > §6Suppression des teams temporaires...");
        Bukkit.getScoreboardManager().getMainScoreboard().getTeams().forEach(t -> {
            if (t.getName().startsWith("fkt"))
                t.unregister();
        });
        soufInstruction("  > §6Suppression des objectifs caches...");
        Bukkit.getScoreboardManager().getMainScoreboard().getObjectives().forEach(o -> {
            if (o.getName().startsWith("fko"))
                o.unregister();
        });

        soufInstruction("§6Sauvegarde legere de toutes les §eFKGames§6...");
        FKManager.saveAll(true);

        soufInstruction("§6Desactivation de tous les §eRunnable§6...");
        FKListener.cancelMainTask();
        if (FKManager.getCurrentGame() != null)
            FKManager.getCurrentGame().getListener().cancelTask();

        souf("");
        soufInstruction("§cFin du processus de cloture.");

        souf("");
        souf("");
        souf("             §4Dechargement du plugin termine.");
        souf("                §4Le plugin peut s'arreter.");
        soufDate();
        Color.sout(FOOTER);
        System.out.println(" ");
    }

    public static void saveAll(boolean headerAndFooter) {
        if (headerAndFooter) {
            System.out.println(" ");
            Color.sout(HEADER);
            souf("       §dSauvegarde generale de toutes les donnees du");
            souf("          §dserveur, des joueurs, et des jeux...");
            souf("");
            souf("");
        }

        soufInstruction("§6Sauvegarde de §eGlobalsConfig§6...");
        globalConfig.load()
                .setVersion(VERSION, true)
                .setLastGame(FKManager.currentGameId, true);

        if (world != null && nether != null && end != null)
            globalConfig.setWorlds(world.getName(), nether.getName(), end.getName(), true);

        globalConfig.save();

        soufInstruction("§6Sauvegarde de §eVanish§6...");
        Vanish.saveToConfig();

        soufInstruction("§6Sauvegarde de §eAd§6...");
        Ad.saveToConfig();

        soufInstruction("§6Sauvegarde de toutes les donnees des §eFKGames§6...");
        FKManager.saveAll(false);

        if (headerAndFooter) {
            souf("");
            soufInstruction("§5Sauvegardes mises-a-jour.");

            souf("");
            souf("");
            souf("            §dDonnees sauvegardees avec succes.");
            soufDate();
            Color.sout(FOOTER);
            System.out.println(" ");
        }
    }

    public static void souf(String msg) {
        System.out.println(String.format(Color.convert("§9|%-" + (61 + Color.convert(msg).length() - ChatColor.stripColor(msg).length()) + "s§9|"), Color.convert(msg)));
    }

    public static void soufInstruction(String msg) {
        souf("   " + PREFIX + " " + msg);
    }

    public static void soufDate() {
        souf("");
        String msg = "§7- " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.FRENCH).format(new Date()) + " | " + DateFormat.getTimeInstance(DateFormat.FULL, Locale.ENGLISH).format(new Date()) + "  ";
        System.out.println(String.format(Color.convert("§9|%" + (61 + Color.convert(msg).length() - ChatColor.stripColor(msg).length()) + "s§9|"), Color.convert(msg)));
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
