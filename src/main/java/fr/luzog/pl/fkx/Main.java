package fr.luzog.pl.fkx;

import fr.luzog.pl.fkx.commands.CommandManager;
import fr.luzog.pl.fkx.events.Events;
import fr.luzog.pl.fkx.fk.*;
import fr.luzog.pl.fkx.utils.Config;
import fr.luzog.pl.fkx.utils.Crafting;
import fr.luzog.pl.fkx.utils.PlayerStats;
import fr.luzog.pl.fkx.utils.SpecialChars;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;


public class Main extends JavaPlugin implements Listener {

    public static final String SYS_PREFIX = "§8[§l§4SYSTEM§r§8] §r";
    public static final String PREFIX = "§8§l[§6FKX§8§l] >> §7";
    public static final String HEADER = "§9-------------- §8§l[ §6FKX §8§l] §9--------------§r";
    public static final String FOOTER = "§9----------------------------------------§r";
    public static final String REBOOT_KICK_MESSAGE = Main.HEADER + "\n\n§cRedémarrage du serveur.\nReconnectez vous dans moins d'une minute !\n\n" + Main.FOOTER;
    public static Main instance = null;
    public static World world = null;

    public static boolean dontSave = false;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                world = Bukkit.getWorld("world");
            }
        }.runTaskLaterAsynchronously(this, 1);

        Bukkit.getLogger().log(Level.INFO, PREFIX + "Initialisation des Configurations");
        Config.saveDefaultConfig();
        Config.saveConfig();

        Bukkit.getLogger().log(Level.INFO, PREFIX + "Initialisation des Listeners.");
        getServer().getPluginManager().registerEvents(this, this);
        Events.events.forEach(e -> getServer().getPluginManager().registerEvents(e, this));

        Bukkit.getLogger().log(Level.INFO, PREFIX + "Initialisation des commandes.");
        CommandManager.init();

        Bukkit.getLogger().log(Level.INFO, PREFIX + "Initialisation des crafts.");
        Crafting.initCrafts();

        Bukkit.getLogger().log(Level.INFO, PREFIX + "Plugin Actif !");

        new BukkitRunnable() {
            @Override
            public void run() {
                FKManager man = new FKManager("null", 1, 0, 0, true,
                        new FKOptions(
                                new FKOptions.FKOption("PvP", 2, false),
                                new FKOptions.FKOption("Nether", 4, false),
                                new FKOptions.FKOption("Assauts", 6, false),
                                new FKOptions.FKOption("End", 6, true)
                        ),
                        new FKListener("FALLEN KINGDOM X"),
                        new FKDimension("Nether",
                                null, new Location(world, 52, 226, -28), new Location(world, 51, 228, -28),
                                Bukkit.getWorld("world_nether").getSpawnLocation(), null, null,
                                Material.PORTAL, Material.AIR, (byte) 0, 60L, false),
                        new FKDimension("End", null, null, null, null, null, null, Material.ENDER_PORTAL, Material.AIR, (byte) 0, 200L, false),
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
                                        new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.OFF))
                        ),
                        new ArrayList<>(),
                        new FKTeam("gods", "Dieux", SpecialChars.STAR_5_6 + " Dieu || ", ChatColor.DARK_RED, null, 0, new FKAuth(FKAuth.Definition.ON), null),
                        new FKTeam("specs", "Specs", SpecialChars.FLOWER_3 + " Spec || ", ChatColor.GRAY, null, 0, new FKAuth(FKAuth.Definition.OFF), null),
                        Arrays.asList(new FKTeam("red", "§l[§rRouge§l]", "§lR§r || ", ChatColor.RED, new Location(Main.world, 52.5, 225, -28.5), 1.5, new FKAuth(FKAuth.Definition.DEFAULT), null),
                                new FKTeam("blue", "§l[§rBleue§l]", "§lB§r || ", ChatColor.BLUE, new Location(Main.world, 49.5, 225, -28.5), 1.5, new FKAuth(FKAuth.Definition.DEFAULT), null)),
                        new FKAuth(FKAuth.Definition.OFF,
                                new FKAuth.Item(FKAuth.Type.BREAKSPE, FKAuth.Definition.ON),
                                new FKAuth.Item(FKAuth.Type.PLACESPE, FKAuth.Definition.ON),
                                new FKAuth.Item(FKAuth.Type.PVP, FKAuth.Definition.ON)),
                        new FKAuth(FKAuth.Definition.DEFAULT,
                                new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.ON),
                                new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.OFF)),
                        new FKAuth(FKAuth.Definition.DEFAULT,
                                new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.ON),
                                new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.ON)),
                        new FKAuth(FKAuth.Definition.DEFAULT,
                                new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.OFF),
                                new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.OFF))
                );
                man.register();
                man.getListener().scheduleTask();

                try {
                    new FKPlayer(Bukkit.getPlayer("Luzog78").getUniqueId(), "Luzog78", new PlayerStats(), null)
                            .setTeam(FKManager.getCurrentGame().getTeam("red"));
                } catch (NullPointerException ignored) {
                }
                try {
                    new FKPlayer(Bukkit.getPlayer("Jigoku_san").getUniqueId(), "Jigoku_san", new PlayerStats(), null)
                            .setTeam(FKManager.getCurrentGame().getTeam("blue"));
                } catch (NullPointerException ignored) {
                }
            }
        }.runTaskLater(this, 20);
    }

    @Override
    public void onDisable() {
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
