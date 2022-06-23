package fr.luzog.pl.fkx;

import fr.luzog.pl.fkx.commands.Admin.Vanish;
import fr.luzog.pl.fkx.commands.CommandManager;
import fr.luzog.pl.fkx.events.Events;
import fr.luzog.pl.fkx.fk.*;
import fr.luzog.pl.fkx.utils.*;
import fr.luzog.pl.fkx.utils.Color;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;


public class Main extends JavaPlugin implements Listener {

    public static final Object VERSION = "1.0.0";

    public static final String SYS_PREFIX = "§8[§l§4SYSTEM§r§8] §r";
    public static final String PREFIX = "§8§l[§6FKX§8§l] >> §7";
    public static final String HEADER = "§9------------------- §8§l[ §6FKX §8§l] §9-------------------§r";
    public static final String FOOTER = "§9--------------------------------------------------§r";
    public static final String REBOOT_KICK_MESSAGE = Main.HEADER + "\n\n§cRedémarrage du serveur.\nReconnectez vous dans moins d'une minute !\n\n" + Main.FOOTER;
    public static Main instance = null;
    public static World world = null, nether = null, end = null;

    public static Config.Globals globalConfig;

    public static boolean dontSave = false;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        System.out.println();
        Color.sout("§9--------------------------- §8[ §6FKX §8] §9---------------------------");
        souf("         §fInitialisation des differentes composantes");
        souf("           §fdu plugin de §bFallen Kingdom X§f...");
        souf("");
        souf("");

        soufInstruction("§6Initialisation du module : §eConfigurations§6...");
        globalConfig = new Config.Globals("Globals.yml").load()
                .forceVersion(VERSION)
                .setLang("fr-FR")
                .setWorlds("world", "world_nether", "world_the_end")
                .save();

        FKManager.setCurrentGame(globalConfig.getLastGame());

        new BukkitRunnable() {
            @Override
            public void run() {
                world = globalConfig.getOverworld();
                nether = globalConfig.getNether();
                end = globalConfig.getEnd();
            }
        }.runTaskLaterAsynchronously(this, 1);

        soufInstruction("§6Initialisation du module : §eVanish§6...");
        Vanish.initFromConfig();

        soufInstruction("§6Initialisation du module : §eListeners§6...");
        getServer().getPluginManager().registerEvents(this, this);
        Events.events.forEach(e -> getServer().getPluginManager().registerEvents(e, this));

        soufInstruction("§6Initialisation du module : §eCommandes§6...");
        CommandManager.init();

        soufInstruction("§6Initialisation du module : §eCrafts§6...");
        Crafting.initCrafts();

        souf("");
        soufInstruction("§aInitialisations terminees !");

        new BukkitRunnable() {
            @Override
            public void run() {
                FKManager man = new FKManager("null", FKManager.State.WAITING, 1, 0, 0, true,
                        new FKOptions(
                                new FKOptions.FKOption("PvP", 2, false),
                                new FKOptions.FKOption("Nether", 4, false),
                                new FKOptions.FKOption("Assauts", 6, false),
                                new FKOptions.FKOption("End", 6, true)
                        ),
                        new FKListener("FALLEN KINGDOM X"),
                        new Portal("Nether",
                                null, new Location(world, 52, 226, -28), new Location(world, 51, 228, -28),
                                Bukkit.getWorld("world_nether").getSpawnLocation(), null, null,
                                Material.PORTAL, Material.AIR, (byte) 0, 60L, false),
                        new Portal("End", null, null, null, null, null, null, Material.ENDER_PORTAL, Material.AIR, (byte) 0, 200L, false),
                        new FKZone("Lobby", FKZone.Type.LOBBY,
                                new Location(Main.world, 51, 225, -26),
                                new Location(Main.world, 56, 224, -25),
                                new Location(Main.world, 46, 226, -27),
                                new FKAuth(FKAuth.Definition.OFF)
                        ),
                        new FKZone("Spawn", FKZone.Type.SPAWN,
                                new Location(Main.world, 51, 225, -31),
                                new Location(Main.world, 56, 224, -30),
                                new Location(Main.world, 46, 226, -32),
                                new FKAuth(FKAuth.Definition.DEFAULT,
                                        new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.OFF),
                                        new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.OFF),
                                        new FKAuth.Item(FKAuth.Type.MOBS, FKAuth.Definition.OFF))
                        ),
                        new ArrayList<FKZone>() {{
                            add(new FKZone("nether", FKZone.Type.ZONE,
                                    Bukkit.getWorld("world_nether").getSpawnLocation(),
                                    new Location(Bukkit.getWorld("world_nether"), Integer.MIN_VALUE, -1, Integer.MIN_VALUE),
                                    new Location(Bukkit.getWorld("world_nether"), Integer.MAX_VALUE, 256, Integer.MAX_VALUE),
                                    new FKAuth(FKAuth.Definition.ON)));
                            add(new FKZone("end", FKZone.Type.ZONE,
                                    Bukkit.getWorld("world_the_end").getSpawnLocation(),
                                    new Location(Bukkit.getWorld("world_the_end"), Integer.MIN_VALUE, -1, Integer.MIN_VALUE),
                                    new Location(Bukkit.getWorld("world_the_end"), Integer.MAX_VALUE, 256, Integer.MAX_VALUE),
                                    new FKAuth(FKAuth.Definition.ON)));
                        }},
                        new ArrayList<>(),
                        new FKTeam("gods", "Dieux", SpecialChars.STAR_5_6 + " Dieu ||  ", ChatColor.DARK_RED, null, 0, new FKAuth(FKAuth.Definition.ON)),
                        new FKTeam("specs", "Specs", SpecialChars.FLOWER_3 + " Spec ||  ", ChatColor.GRAY, null, 0, new FKAuth(FKAuth.Definition.OFF)),
                        new ArrayList<>(Arrays.asList(new FKTeam("red", "§l[§rRouge§l]", "§lR§r ||  ", ChatColor.RED, new Location(Main.world, 52.5, 225, -28.5), 1.5, new FKAuth(FKAuth.Definition.DEFAULT)),
                                new FKTeam("blue", "§l[§rBleue§l]", "§lB§r ||  ", ChatColor.BLUE, new Location(Main.world, 49.5, 225, -28.5), 1.5, new FKAuth(FKAuth.Definition.DEFAULT)))),
                        new FKAuth(FKAuth.Definition.OFF,
                                new FKAuth.Item(FKAuth.Type.BREAKSPE, FKAuth.Definition.ON),
                                new FKAuth.Item(FKAuth.Type.PLACESPE, FKAuth.Definition.ON),
                                new FKAuth.Item(FKAuth.Type.PVP, FKAuth.Definition.ON),
                                new FKAuth.Item(FKAuth.Type.MOBS, FKAuth.Definition.ON)),
                        new FKAuth(FKAuth.Definition.DEFAULT,
                                new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.ON),
                                new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.OFF)),
                        new FKAuth(FKAuth.Definition.DEFAULT,
                                new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.ON),
                                new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.ON)),
                        new FKAuth(FKAuth.Definition.DEFAULT,
                                new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.OFF),
                                new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.OFF)),
                        new FKAuth(FKAuth.Definition.OFF)
                );
                man.register();
                man.getListener().scheduleTask();

                try {
                    man.getPlayer("Luzog78", true).setTeam("specs");
                } catch (NullPointerException ignored) {
                }
                try {
                    new FKPlayer(Bukkit.getPlayer("Jigoku_san").getUniqueId(), "Jigoku_san", new PlayerStats(), null)
                            .setTeam("blue");
                } catch (NullPointerException ignored) {
                }
            }
        }.runTaskLater(this, 20);

        souf("");
        souf("");
        souf("       §fTous les modules ont etes initialise, le plugin");
        souf("         §fest maintenant pret. §aBon jeu a §2tous §a!");
        Color.sout("§9---------------------------------------------------------------");
        System.out.println();
    }

    public static void souf(String msg) {
        System.out.println(String.format(Color.convert("§9|%-" + (61 + Color.convert(msg).length() - ChatColor.stripColor(msg).length()) + "s§9|"), Color.convert(msg)));
    }

    public static void soufInstruction(String msg) {
        souf("   " + PREFIX + " " + msg);
    }

    @Override
    public void onDisable() {
        System.out.println();
        Color.sout("§9--------------------------- §8[ §6FKX §8] §9---------------------------");
        souf("    §4Sauvegarde des donnees des joueurs ainsi que celles ");
        souf("       §4des parties en cours... Et arret du plugin.");
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

        soufInstruction("§6Sauvegarde des donnees de : §eGlobalsConfig§6...");
        globalConfig.load()
                .forceVersion(VERSION)
                .forceWorlds(world.getUID().toString(), nether.getUID().toString(), end.getUID().toString())
                .forceLastGame(FKManager.currentGameId)
                .save();

        soufInstruction("§6Sauvegarde des donnees de : §eVanish§6...");
        Vanish.saveToConfig();

        souf("");
        soufInstruction("§cFin du processus de cloture.");

        souf("");
        souf("");
        souf("            §4Donnees sauvegardees avec succes.");
        souf("                §4Le plugin peut s'arreter.");
        Color.sout("§9---------------------------------------------------------------");
        System.out.println();
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
