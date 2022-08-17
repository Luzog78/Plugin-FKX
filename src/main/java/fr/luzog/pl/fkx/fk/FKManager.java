package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.*;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FKManager {

    public static enum State {WAITING, RUNNING, PAUSED, ENDED}

    public static enum Weather {CLEAR, RAIN, THUNDER}

    public static final String CONFIG_FILE = "%s/Manager.yml";
    public static ArrayList<FKManager> registered = new ArrayList<>();
    public static String currentGameId = null;

    public static void initFromConfig(boolean printStackTrace) {
        for (File f : Objects.requireNonNull(Main.instance.getDataFolder().listFiles()))
            if (f.isDirectory() && f.getName().startsWith("game-")) {
                Config.Manager config = new Config.Manager(String.format(CONFIG_FILE, f.getName())).load();
                FKManager manager = new FKManager(f.getName().replaceFirst("game-", ""));
                Utils.tryTo(printStackTrace, () -> manager.setState(Objects.requireNonNull(config.getState()), false));
                Utils.tryTo(printStackTrace, () -> manager.setDay(config.getDay(), false));
                Utils.tryTo(printStackTrace, () -> manager.setWeather(Objects.requireNonNull(config.getCurrentWeather()), null, false));
                Utils.tryTo(printStackTrace, () -> manager.setLinkedToSun(config.isLinkedToSun(), false));
                Utils.tryTo(printStackTrace, () -> manager.setTime(config.getTime(), false));
                Utils.tryTo(printStackTrace, () -> {
                    for (String o : config.getOptions())
                        if (o != null) {
                            FKOptions.FKOption option = o.equalsIgnoreCase("pvp") ? manager.getOptions().getPvp()
                                    : o.equalsIgnoreCase("nether") ? manager.getOptions().getNether()
                                    : o.equalsIgnoreCase("assaults") ? manager.getOptions().getAssaults()
                                    : o.equalsIgnoreCase("end") ? manager.getOptions().getEnd() : null;
                            if (option != null) {
                                Utils.tryTo(printStackTrace, () -> option.setName(Objects.requireNonNull(config.getOptionName(o))));
                                Utils.tryTo(printStackTrace, () -> option.setActivationDay(config.getOptionActivation(o)));
                                Utils.tryTo(printStackTrace, () -> {
                                    if (config.isOptionActivated(o))
                                        option.activate(false);
                                    else
                                        option.deactivate(false);
                                });
                            }
                        }
                });
                Utils.tryTo(printStackTrace, () -> manager.getListener().setScoreName(Objects.requireNonNull(config.getListenerName())));
                Utils.tryTo(printStackTrace, () -> manager.getListener().setSavingTimeOut(config.getListenerSavingTimeout()));
                Utils.tryTo(printStackTrace, () -> {
                    for (String p : config.getPortals())
                        if (p != null) {
                            Portal portal = new Portal(config.getPortalName(p),
                                    config.getPortalSpawnOverLocation(p),
                                    config.getPortalPos1OverLocation(p),
                                    config.getPortalPos2OverLocation(p),
                                    config.getPortalSpawnDimLocation(p),
                                    config.getPortalPos1DimLocation(p),
                                    config.getPortalPos2DimLocation(p),
                                    config.getPortalOpenedMaterial(p),
                                    config.getPortalClosedMaterial(p),
                                    config.getPortalMaterialData(p),
                                    config.getPortalCooldown(p),
                                    config.isPortalOpened(p));
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    for (UUID uuid : config.getPortalTeleporting(p))
                                        if (uuid != null)
                                            for (World w : Bukkit.getWorlds())
                                                for (Entity e : w.getEntities())
                                                    if (e.getUniqueId().equals(uuid))
                                                        portal.tryToTeleport(e, manager, false);
                                }
                            }.runTaskLater(Main.instance, 1);

                            if (p.equalsIgnoreCase("nether")) {
                                manager.setNether(portal, false);
                            } else if (p.equalsIgnoreCase("end"))
                                manager.setEnd(portal, false);
                        }
                });
                Utils.tryTo(printStackTrace, () -> manager.setGlobal(Objects.requireNonNull(config.getGlobalPermissions()), false));
                Utils.tryTo(printStackTrace, () -> manager.setNeutral(Objects.requireNonNull(config.getNeutralPermissions()), false));
                Utils.tryTo(printStackTrace, () -> manager.setFriendly(Objects.requireNonNull(config.getFriendlyPermissions()), false));
                Utils.tryTo(printStackTrace, () -> manager.setHostile(Objects.requireNonNull(config.getHostilePermissions()), false));
                Utils.tryTo(printStackTrace, () -> manager.setPriority(Objects.requireNonNull(config.getPriorityPermissions()), false));


                for (File ff : Objects.requireNonNull(f.listFiles()))
                    if (ff.isFile() && ff.getName().toLowerCase().endsWith(".yml")) {
                        if (ff.getName().substring(0, ff.getName().length() - 4).equals("Limits")) {
                            Config.Limits c = new Config.Limits(f.getName() + "/" + ff.getName()).load();
                            Limits lim = new Limits();
                            Utils.tryTo(printStackTrace, () -> lim.setCraft(c.getCraft()));
                            Utils.tryTo(printStackTrace, () -> lim.setPotion(c.getPotion()));
                            Utils.tryTo(printStackTrace, () -> lim.setEnchantGlobal(c.getEnchantGlobal()));
                            Utils.tryTo(printStackTrace, () -> lim.setEnchantSpe(c.getEnchantSpe()));
                            Utils.tryTo(printStackTrace, () -> lim.setWearingMaxDiamondPieces(c.getWearingMaxDiamondPieces()));
                            Utils.tryTo(printStackTrace, () -> lim.setWearingMaxGoldPieces(c.getWearingMaxGoldPieces()));
                            Utils.tryTo(printStackTrace, () -> lim.setWearingMaxIronPieces(c.getWearingMaxIronPieces()));
                            Utils.tryTo(printStackTrace, () -> lim.setWearingMaxLeatherPieces(c.getWearingMaxLeatherPieces()));
                            manager.setLimits(lim, false);
                        } else if (ff.getName().substring(0, ff.getName().length() - 4).equals("PickableLocks")) {
                            Config.PickableLocks c = new Config.PickableLocks(f.getName() + "/" + ff.getName()).load();
                            FKPickableLocks pickableLocks = new FKPickableLocks();
                            Utils.tryTo(printStackTrace, () -> pickableLocks.setPickableLocks(c.getLocks()));
                            Utils.tryTo(printStackTrace, () -> pickableLocks.setInPicking(c.getPicking()));
                            manager.setPickableLocks(pickableLocks, false);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    manager.getPickableLocks().updateAll();
                                }
                            }.runTask(Main.instance);
                        }
                    } else if (ff.getName().equalsIgnoreCase("zones")) {
                        for (File fff : Objects.requireNonNull(ff.listFiles()))
                            if (fff.isFile() && fff.getName().toLowerCase().endsWith(".yml")) {
                                Config.Zone zc = new Config.Zone(String.format("%s/zones/%s", f.getName(), fff.getName())).load();
                                FKZone zone = new FKZone(fff.getName().substring(0, fff.getName().length() - 4), null,
                                        new Location(Main.world, 0, 0, 0), null, null, new FKPermissions(FKPermissions.Definition.DEFAULT));

                                Utils.tryTo(printStackTrace, () -> zone.setType(Objects.requireNonNull(zc.getType()), false));
                                Utils.tryTo(printStackTrace, () -> zone.setSpawn(Objects.requireNonNull(zc.getSpawn()), false));
                                Utils.tryTo(printStackTrace, () -> zone.setPos1(Objects.requireNonNull(zc.getPos1()), false));
                                Utils.tryTo(printStackTrace, () -> zone.setPos2(Objects.requireNonNull(zc.getPos2()), false));
                                Utils.tryTo(printStackTrace, () -> zone.setPermissions(Objects.requireNonNull(zc.getPermissions()), false));

                                if (fff.getName().equalsIgnoreCase(FKZone.LOBBY_FILE)) {
                                    zone.setType(FKZone.Type.LOBBY, false);
                                    manager.setLobby(zone, false);
                                } else if (fff.getName().equalsIgnoreCase(FKZone.SPAWN_FILE)) {
                                    zone.setType(FKZone.Type.SPAWN, false);
                                    manager.setSpawn(zone, false);
                                } else {
                                    if (zone.getType() == null)
                                        zone.setType(FKZone.Type.ZONE, false);
                                    manager.getNormalZones().add(zone);
                                }
                            }
                    } else if (ff.getName().equalsIgnoreCase("teams")) {
                        for (File fff : Objects.requireNonNull(ff.listFiles()))
                            if (fff.isFile() && fff.getName().toLowerCase().endsWith(".yml")) {
                                Config.Team tc = new Config.Team(String.format("%s/teams/%s", f.getName(), fff.getName())).load();
                                FKTeam team = new FKTeam(fff.getName().substring(0, fff.getName().length() - 4));

                                Utils.tryTo(printStackTrace, () -> team.setName(Objects.requireNonNull(tc.getName()), false));
                                Utils.tryTo(printStackTrace, () -> team.setColor(Objects.requireNonNull(tc.getColor()), false));
                                Utils.tryTo(printStackTrace, () -> team.setPrefix(Objects.requireNonNull(tc.getPrefix()), false));
                                Utils.tryTo(printStackTrace, () -> team.setEliminators(tc.getEliminators(), false));
                                Utils.tryTo(printStackTrace, () -> team.setSpawn(Objects.requireNonNull(tc.getSpawn()), false));
                                Utils.tryTo(printStackTrace, () -> team.setPlunderLoc(Objects.requireNonNull(tc.getPlunderLoc()), false));
                                Utils.tryTo(printStackTrace, () -> team.setRadius(tc.getRadius(), false));
                                Utils.tryTo(printStackTrace, () -> team.setOldPlayers(tc.getOldPlayers(), false));
                                Utils.tryTo(printStackTrace, () -> team.setEliminated(tc.isEliminated(), false));
                                Utils.tryTo(printStackTrace, () -> team.setPermissions(Objects.requireNonNull(tc.getPermissions()), false));

                                if (fff.getName().equalsIgnoreCase(FKTeam.GODS_FILE))
                                    manager.setGods(team, false);
                                else if (fff.getName().equalsIgnoreCase(FKTeam.SPECS_FILE))
                                    manager.setSpecs(team, false);
                                else
                                    manager.addTeam(team);
                            }
                    } else if (ff.getName().equalsIgnoreCase("players")) {
                        for (File fff : Objects.requireNonNull(ff.listFiles()))
                            if (fff.isFile() && fff.getName().toLowerCase().endsWith(".yml"))
                                try {
                                    Config.Player pc = new Config.Player(String.format("%s/players/%s", f.getName(), fff.getName())).load();
                                    FKPlayer player = new FKPlayer(fff.getName().substring(0, fff.getName().length() - 4), null, null);

                                    Utils.tryTo(printStackTrace, () -> player.setLastUuid(Objects.requireNonNull(pc.getLastUuid()), false));
                                    Utils.tryTo(printStackTrace, () -> player.setTeam(Objects.requireNonNull(pc.getTeam()), false));
                                    Utils.tryTo(printStackTrace, () -> player.setCompass(pc.getCompass(), false));
                                    Utils.tryTo(printStackTrace, () -> player.setStats(Objects.requireNonNull(pc.getStats()), false));
                                    Utils.tryTo(printStackTrace, () -> player.setPersonalPermissions(Objects.requireNonNull(pc.getPermissions()), false));

                                    if (!player.getName().equals(""))
                                        manager.getPlayers().add(player);
                                } catch (IllegalArgumentException ignored) {
                                }
                    }

                manager.register(false);
            }
    }

    public static void saveAll(boolean soft) {
        for (FKManager manager : registered)
            manager.saveGlobals(soft);
    }

    public void saveManager(boolean soft) {
        getConfig()
                .load()
                .setState(state, true)
                .setDay(day, !soft)
                .setCurrentWeather(weather, !soft)
                .setTime(time, true)
                .setLinkedToSun(linkedToSun, !soft)

                .setOptionName("pvp", options.getPvp().getName(), !soft)
                .setOptionActivation("pvp", options.getPvp().getActivationDay(), !soft)
                .setOptionActivated("pvp", options.getPvp().isActivated(), !soft)
                .setOptionName("nether", options.getNether().getName(), !soft)
                .setOptionActivation("nether", options.getNether().getActivationDay(), !soft)
                .setOptionActivated("nether", options.getNether().isActivated(), !soft)
                .setOptionName("assaults", options.getAssaults().getName(), !soft)
                .setOptionActivation("assaults", options.getAssaults().getActivationDay(), !soft)
                .setOptionActivated("assaults", options.getAssaults().isActivated(), !soft)
                .setOptionName("end", options.getEnd().getName(), !soft)
                .setOptionActivation("end", options.getEnd().getActivationDay(), !soft)
                .setOptionActivated("end", options.getEnd().isActivated(), !soft)

                .setListenerName(listener.getScoreName(), !soft)
                .setListenerSavingTimeout(listener.getSavingTimeOut(), !soft)

                .setPortalName("nether", nether.getName(), !soft)
                .setPortalOpened("nether", nether.isOpened(), !soft)
                .setPortalCooldown("nether", nether.getCoolDown(), !soft)
                .setPortalOpenedMaterial("nether", nether.getOpenedMat(), !soft)
                .setPortalClosedMaterial("nether", nether.getClosedMat(), !soft)
                .setPortalMaterialData("nether", nether.getData(), !soft)
                .setPortalSpawnOverLocation("nether", nether.getOverSpawn(), !soft)
                .setPortalPos1OverLocation("nether", nether.getOverPortal1(), !soft)
                .setPortalPos2OverLocation("nether", nether.getOverPortal2(), !soft)
                .setPortalSpawnDimLocation("nether", nether.getDimSpawn(), !soft)
                .setPortalPos1DimLocation("nether", nether.getDimPortal1(), !soft)
                .setPortalPos2DimLocation("nether", nether.getDimPortal2(), !soft)
                .setPortalTeleporting("nether", nether.getInTeleportation(), !soft)

                .setPortalName("end", end.getName(), !soft)
                .setPortalOpened("end", end.isOpened(), !soft)
                .setPortalCooldown("end", end.getCoolDown(), !soft)
                .setPortalOpenedMaterial("end", end.getOpenedMat(), !soft)
                .setPortalClosedMaterial("end", end.getClosedMat(), !soft)
                .setPortalMaterialData("end", end.getData(), !soft)
                .setPortalSpawnOverLocation("end", end.getOverSpawn(), !soft)
                .setPortalPos1OverLocation("end", end.getOverPortal1(), !soft)
                .setPortalPos2OverLocation("end", end.getOverPortal2(), !soft)
                .setPortalSpawnDimLocation("end", end.getDimSpawn(), !soft)
                .setPortalPos1DimLocation("end", end.getDimPortal1(), !soft)
                .setPortalPos2DimLocation("end", end.getDimPortal2(), !soft)
                .setPortalTeleporting("end", end.getInTeleportation(), !soft)

                .setGlobalPermissions(global, !soft)
                .setNeutralPermissions(neutral, !soft)
                .setFriendlyPermissions(friendly, !soft)
                .setHostilePermissions(hostile, !soft)
                .setPriorityPermissions(priority, !soft)

                .save();
    }

    public void saveGlobals(boolean soft) {
        saveManager(soft);

        limits.saveToConfig(id, soft);
        pickableLocks.saveToConfig(id, soft);

        lobby.saveToConfig(id, soft);
        spawn.saveToConfig(id, soft);
        if (!soft)
            for (FKZone z : zones)
                z.saveToConfig(id, false);

        gods.saveToConfig(id, soft);
        specs.saveToConfig(id, soft);
        if (!soft)
            for (FKTeam t : teams)
                t.saveToConfig(id, false);

        if (!soft)
            for (FKPlayer p : players)
                p.saveToConfig(id, false);
    }

    public Config.Manager getConfig() {
        return new Config.Manager(String.format(CONFIG_FILE, "game-" + id));
    }

    private int taskID;

    private String id;
    private State state;

    private int day;
    private Weather weather;
    private long time;
    private boolean linkedToSun;

    private FKOptions options;
    private FKListener listener;

    private Limits limits;

    private Portal nether, end;

    private FKZone lobby;
    private FKZone spawn;
    private ArrayList<FKZone> zones;

    private ArrayList<FKPlayer> players;

    private FKTeam gods;
    private FKTeam specs;
    private ArrayList<FKTeam> teams;

    private FKPermissions global;
    private FKPermissions neutral;
    private FKPermissions friendly;
    private FKPermissions hostile;
    private FKPermissions priority;

    private FKPickableLocks pickableLocks;


    public FKManager(String id) {
        Location loc = new Location(Main.world, 0, 0, 0);
        this.id = id;
        setDay(1, false);
        this.weather = Main.world == null ? Weather.CLEAR : Main.world.isThundering() ? Weather.THUNDER
                : Main.world.hasStorm() ? Weather.RAIN : Weather.CLEAR;
        setTime(0, false);
        setLinkedToSun(true, false);
        setOptions(FKOptions.getDefaultOptions(), false);
        setListener(new FKListener(60 * 5), false);
        setLimits(new Limits(), false);
        setNether(new Portal("§dNether", loc, null, null, loc, null, null, Material.PORTAL, Material.AIR, (byte) 0, 100, false), false);
        setEnd(new Portal("§5End", loc, null, null, loc, null, null, Material.ENDER_PORTAL, Material.AIR, (byte) 0, 10, false), false);
        setLobby(new FKZone(null, FKZone.Type.LOBBY,
                loc, loc, loc,
                new FKPermissions(FKPermissions.Definition.OFF)), false);
        setSpawn(new FKZone(null, FKZone.Type.SPAWN,
                loc, loc, loc,
                new FKPermissions(FKPermissions.Definition.DEFAULT,
                        new FKPermissions.Item(FKPermissions.Type.BREAK, FKPermissions.Definition.OFF),
                        new FKPermissions.Item(FKPermissions.Type.PLACE, FKPermissions.Definition.OFF),
                        new FKPermissions.Item(FKPermissions.Type.MOBS, FKPermissions.Definition.OFF))), false);
        setNormalZones(new ArrayList<FKZone>() {{
//            add(new FKZone("nether", FKZone.Type.ZONE,
//                    Bukkit.getWorld("world_nether").getSpawnLocation(),
//                    new Location(Bukkit.getWorld("world_nether"), Integer.MIN_VALUE, -1, Integer.MIN_VALUE),
//                    new Location(Bukkit.getWorld("world_nether"), Integer.MAX_VALUE, 256, Integer.MAX_VALUE),
//                    new FKPermissions(FKPermissions.Definition.ON)));
//            add(new FKZone("end", FKZone.Type.ZONE,
//                    Bukkit.getWorld("world_the_end").getSpawnLocation(),
//                    new Location(Bukkit.getWorld("world_the_end"), Integer.MIN_VALUE, -1, Integer.MIN_VALUE),
//                    new Location(Bukkit.getWorld("world_the_end"), Integer.MAX_VALUE, 256, Integer.MAX_VALUE),
//                    new FKPermissions(FKPermissions.Definition.ON)));
        }}, false);
        setPlayers(new ArrayList<>(), false);
        setGods(new FKTeam("gods", "Dieux", SpecialChars.STAR_5_6 + " Dieu ||  ", null, ChatColor.DARK_RED,
                loc, loc, 0, new ArrayList<>(), false, 0, new FKPermissions(FKPermissions.Definition.ON)), false);
        setSpecs(new FKTeam("specs", "Specs", SpecialChars.FLOWER_3 + " Spec ||  ", null, ChatColor.GRAY,
                loc, loc, 0, new ArrayList<>(), false, 0, new FKPermissions(FKPermissions.Definition.OFF)), false);
        setParticipantsTeams(new ArrayList<>(), false);
        setGlobal(new FKPermissions(FKPermissions.Definition.OFF,
                new FKPermissions.Item(FKPermissions.Type.BREAKSPE, FKPermissions.Definition.ON),
                new FKPermissions.Item(FKPermissions.Type.PLACESPE, FKPermissions.Definition.ON),
                new FKPermissions.Item(FKPermissions.Type.PVP, FKPermissions.Definition.ON),
                new FKPermissions.Item(FKPermissions.Type.MOBS, FKPermissions.Definition.ON)), false);
        setNeutral(new FKPermissions(FKPermissions.Definition.DEFAULT,
                new FKPermissions.Item(FKPermissions.Type.BREAK, FKPermissions.Definition.ON),
                new FKPermissions.Item(FKPermissions.Type.PLACE, FKPermissions.Definition.OFF)), false);
        setFriendly(new FKPermissions(FKPermissions.Definition.DEFAULT,
                new FKPermissions.Item(FKPermissions.Type.BREAK, FKPermissions.Definition.ON),
                new FKPermissions.Item(FKPermissions.Type.PLACE, FKPermissions.Definition.ON)), false);
        setHostile(new FKPermissions(FKPermissions.Definition.DEFAULT,
                new FKPermissions.Item(FKPermissions.Type.BREAK, FKPermissions.Definition.OFF),
                new FKPermissions.Item(FKPermissions.Type.PLACE, FKPermissions.Definition.OFF)), false);
        setPriority(new FKPermissions(FKPermissions.Definition.OFF), false);
        setPickableLocks(new FKPickableLocks(), false);

        setState(State.WAITING, false);
    }


    public FKManager(String id, State state, int day, Weather weather, long time, boolean linkedToSun, FKOptions options,
                     FKListener listener, Limits limits, Portal nether, Portal end, FKZone lobby, FKZone spawn,
                     ArrayList<FKZone> zones, ArrayList<FKPlayer> players, FKTeam gods, FKTeam specs,
                     ArrayList<FKTeam> teams, FKPermissions global, FKPermissions neutral, FKPermissions friendly,
                     FKPermissions hostile, FKPermissions priority, FKPickableLocks pickableLocks) {
        this.id = id;
        setState(state, false);
        setDay(day, false);
        this.weather = Main.world == null ? Weather.CLEAR : Main.world.isThundering() ? Weather.THUNDER : Main.world.hasStorm() ? Weather.RAIN : Weather.CLEAR;
        if (weather != this.weather)
            setWeather(weather, null, false);
        setTime(time, false);
        setLinkedToSun(linkedToSun, false);
        setOptions(options, false);
        setListener(listener, false);
        setLimits(limits, false);
        setNether(nether, false);
        setEnd(end, false);
        setLobby(lobby, false);
        setSpawn(spawn, false);
        setNormalZones(zones, false);
        setPlayers(players, false);
        setGods(gods, false);
        setSpecs(specs, false);
        setParticipantsTeams(teams, false);
        setGlobal(global, false);
        setNeutral(neutral, false);
        setFriendly(friendly, false);
        setHostile(hostile, false);
        setPriority(priority, false);
        setPickableLocks(pickableLocks, false);
    }

    public void register(boolean save) {
        registered.add(this);
        currentGameId = id;
        getTeams().forEach(FKTeam::updatePlayers);
        if (save) {
            saveManager(false);
            saveGlobals(true);
        }
    }

    public void unregister(boolean delete) {
        getListener().cancelTask();
        registered.remove(this);
        if (id.equals(currentGameId))
            currentGameId = registered.isEmpty() ? null : registered.get(0).getId();
        if (delete) {
            if (!getConfig().exists()) {
                saveManager(false);
                saveGlobals(true);
            }
            getConfig().getFile().getParentFile().renameTo(new File(getConfig().getFile().getParentFile().getParentFile().getPath() + "/old-" + id));
        }
    }

    public static ItemStack getBanner() {
        ItemStack banner = new ItemStack(Material.BANNER, 1, (short) 15);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.setDisplayName("§9§l-=[ §6Fallen Kingdoms " + Main.FK_SEASON + " §9§l]=-");
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.CURLY_BORDER));
        meta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.CIRCLE_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.CREEPER));
        meta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.TRIANGLE_TOP));
        meta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.TRIANGLES_TOP));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        banner.setItemMeta(meta);
        return banner;
    }

    public static ItemStack getBanner(ChatColor color) {
        ItemStack banner = new ItemStack(Material.BANNER, 1, (short) (15 - Utils.chatToDataColor(color)));
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.setDisplayName("§r" + color + "Bannière de couleur §l[ §k0§r" + color + " " + color.name().charAt(0)
                + color.name().toLowerCase().substring(1) + " §l§k0§r" + color + "§l ]");
        DyeColor dye = Utils.chatToDyeColor(color), white = DyeColor.WHITE;
        meta.addPattern(new Pattern(dye, PatternType.CURLY_BORDER));
        meta.addPattern(new Pattern(white, PatternType.CURLY_BORDER));
        meta.addPattern(new Pattern(dye, PatternType.STRIPE_CENTER));
        meta.addPattern(new Pattern(white, PatternType.STRIPE_CENTER));
        meta.addPattern(new Pattern(dye, PatternType.FLOWER));
        meta.addPattern(new Pattern(white, PatternType.FLOWER));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        banner.setItemMeta(meta);
        return banner;
    }

    public static FKManager getCurrentGame() {
// TODO -> Check if currentGameId is null
//        if(currentGameId == null)
//            currentGameId = Main.getInstance().getConfig().getString("current-game");
        return getGame(currentGameId);
    }

    public static FKManager setCurrentGame(String id, boolean save) {
        currentGameId = id;
// TODO -> Check if currentGameId is null
//        Main.getInstance().getConfig().set("current-game", id);
//        Main.getInstance().saveConfig();
        if (save)
            Main.globalConfig.load().setLastGame(id, true).save();
        return getGame(id);
    }

    public static FKManager getGame(String id) {
        for (FKManager game : registered)
            if (game.getId() == id || (game.getId() != null && game.getId().equalsIgnoreCase(id)))
                return game;
        return null;
    }

    public static ArrayList<FKPlayer> getGlobalPlayers() {
        return new ArrayList<FKPlayer>() {{
            for (FKManager g : registered)
                addAll(g.getPlayers());
        }};
    }

    /*
     * public static ArrayList<FKPlayer> getGlobalPlayer(UUID uuid) {
     *     return new ArrayList<FKPlayer>() {{
     *         for (FKPlayer player : getGlobalPlayers())
     *             if (player.getUuid().equals(uuid))
     *                 add(player);
     *     }};
     * }
     */

    public static ArrayList<FKPlayer> getGlobalPlayer(@Nonnull String name) {
        return new ArrayList<FKPlayer>() {{
            for (FKPlayer player : getGlobalPlayers())
                if (name.equalsIgnoreCase(player.getName()))
                    add(player);
        }};
    }

    /*
     * public static ArrayList<FKPlayer> getGlobalPlayer(@Nonnull UUID uuid, @Nonnull String name) {
     *     return new ArrayList<FKPlayer>() {{
     *         for (FKPlayer player : getGlobalPlayers())
     *             if (uuid.equals(player.getUuid()) || name.equalsIgnoreCase(player.getName()))
     *                 add(player);
     *     }};
     * }
     */

    public void start() {
        Utils.countDown(null, 15, false, true, true,
                "La partie commence dans §c%i%§rs...\n§7Préparez-vous à démarrer votre aventure !",
                "Bonne chance à tous !\n§7Prêt ?  Partez !", "§a", "§6", "§c§l",
                "§4§l", "§2§l", () -> {
                    getPlayers().forEach(p -> {
                        if (p.getPlayer() != null) {
                            p.getPlayer().setHealth(p.getPlayer().getMaxHealth());
                            p.getPlayer().setFoodLevel(20);
                            p.getPlayer().setSaturation(20);
                            p.getPlayer().setFireTicks(0);
                            p.getPlayer().setExp(0);
                            p.getPlayer().setTotalExperience(0);
                            if (p.getTeam() != null)
                                if (p.getTeam().getId().equals(FKTeam.GODS_ID)) {
                                    p.getPlayer().setGameMode(GameMode.CREATIVE);
                                    p.getPlayer().setFlying(true);
                                } else if (p.getTeam().getId().equals(FKTeam.SPECS_ID)) {
                                    p.getPlayer().setGameMode(GameMode.SPECTATOR);
                                } else {
                                    p.getPlayer().setGameMode(GameMode.SURVIVAL);
                                    p.getPlayer().getInventory().clear();
                                    p.getPlayer().getInventory().setArmorContents(null);
                                    p.getPlayer().getActivePotionEffects().forEach(e -> p.getPlayer().removePotionEffect(e.getType()));
                                    p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2400, 255, false, false), true);
                                    p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 2400, 255, false, false), true);
                                    p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2400, 1, false, false), true);
                                }
                            p.getPlayer().teleport(p.getTeam() == null ? getLobby().getSpawn()
                                    : Objects.equals(p.getTeamId(), FKTeam.GODS_ID) || Objects.equals(p.getTeamId(), FKTeam.SPECS_ID) ?
                                    getSpawn().getSpawn() : p.getTeam().getSpawn());
                        }
                    });
                    setPriority(new FKPermissions(FKPermissions.Definition.DEFAULT), true);
                    setState(State.RUNNING, true);
                    FKTeam.killAllArmorStands();
                });
    }

    public void pause(int countDown) {
        Utils.countDown(null, countDown, false, true, true,
                "Le jeu se suspend dans §c%i%§r secondes !\n§7Vous serez momentanément bloqués.",
                "Le jeu est en pause.\n§7Excusez-nous pour la gêne occasionnée...",
                "§e", "§6", "§c", "§4", "§4§l", new Runnable() {
                    @Override
                    public void run() {
                        setState(State.PAUSED, true);
                    }
                });
    }

    public void resume(int countDown) {
        Utils.countDown(null, countDown, false, true, true,
                "Le jeu reprend dans §c%i%§r secondes !\n§7Et la compétition continue.",
                "Et c'est reparti !\n§7Amusez-vous !",
                "§e", "§6", "§c", "§4", "§2§l", new Runnable() {
                    @Override
                    public void run() {
                        setState(State.RUNNING, true);
                    }
                });
    }

    public void end() {
        ArrayList<FKTeam> winnerTeams = teams.stream().filter(t ->
                !t.isEliminated()).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<FKPlayer> winners = teams.stream().map(FKTeam::getPlayers).flatMap(Collection::stream)
                .collect(Collectors.toCollection(ArrayList::new));
        String a1 = "§4§lFin de la partie", a2 = "§7RDV dans qq secondes pour les résultats !";
        String b1 = "§6§lVictoire " + (winnerTeams.size() == 0 ? "d'§cAucune Équipe§6§l..."
                : winnerTeams.size() == 1 ? "de l'Équipe §a" + winnerTeams.get(0).getColor() + winnerTeams.get(0).getName() + "§r !!"
                : "des §a" + winnerTeams.stream().map(t -> t.getColor() + t.getName())
                .collect(Collectors.joining("§6§l, §a")) + "§r !!"),
                b2 = "§aBravo§7 à tous et §aMerci§7 d'avoir participé !";
        getPlayers().forEach(p -> {
            if (p.getPlayer() != null) {
                p.getPlayer().setHealth(p.getPlayer().getMaxHealth());
                p.getPlayer().setFoodLevel(20);
                p.getPlayer().setSaturation(20);
                p.getPlayer().teleport(getLobby().getSpawn());
                if (winners.contains(p) || (p.getTeam() != null && p.getTeam().getId().equals(FKTeam.GODS_ID))) {
                    p.getPlayer().setGameMode(GameMode.CREATIVE);
                    p.getPlayer().setFlying(true);
                } else {
                    p.getPlayer().setGameMode(GameMode.SURVIVAL);
                }
                if (p.getTeam() == null || !p.getTeam().getId().equals(FKTeam.GODS_ID)) {
                    p.getPlayer().getInventory().clear();
                    p.getPlayer().getInventory().setArmorContents(null);
                }
            }
        });
        setPriority(new FKPermissions(FKPermissions.Definition.OFF), true);
        setState(State.ENDED, true);
        new BukkitRunnable() {
            boolean c = true;

            @Override
            public void run() {
                getPlayers().stream().map(FKPlayer::getPlayer)
                        .filter(Objects::nonNull).forEach(p -> {
                            p.sendTitle(c ? a1 : b1, c ? a2 : b2);
                            p.sendMessage(Main.PREFIX + (c ? a1 : b1));
                            p.sendMessage(Main.PREFIX + (c ? a2 : b2));
                        });
                if (!c)
                    cancel();
                c = false;
            }
        }.runTaskTimer(Main.instance, 0, 20 * 6);
    }

    public void reboot() {
        String m1 = "§8§lRéinitialisation de la partie...", m2 = "§7Patience, le début est proche !";
        getPlayers().forEach(p -> {
            if (p.getPlayer() != null) {
                p.getPlayer().setHealth(p.getPlayer().getMaxHealth());
                p.getPlayer().setFoodLevel(20);
                p.getPlayer().setSaturation(20);
                p.getPlayer().sendTitle(m1, m2);
                p.getPlayer().sendMessage(Main.PREFIX + m1);
                p.getPlayer().sendMessage(Main.PREFIX + m2);
                p.getPlayer().teleport(getLobby().getSpawn());
            }
        });
        setState(State.WAITING, false);
        setDay(1, false);
        setTime(0, false);
        getParticipantsTeams().forEach(t -> t.reintroduce(false, false));
        getNether().close();
        getEnd().close();
        getOptions().getOptions().forEach(opt -> opt.deactivate(true));
        setPriority(new FKPermissions(FKPermissions.Definition.OFF), false);
        saveManager(false);
    }

    public void checkActivations(boolean force) {
        for (FKOptions.FKOption opt : options.getOptions())
            if (force) {
                if (day >= opt.getActivationDay()) {
                    if (!opt.isActivated())
                        opt.activate(true);
                } else if (opt.isActivated())
                    opt.deactivate(true);
            } else if (day == opt.getActivationDay())
                opt.activate(true);
    }

    public FKZone getZone(Location loc) {
        if (lobby.isInside(loc))
            return lobby;
        if (spawn.isInside(loc))
            return spawn;
        for (FKTeam team : teams)
            if (team.isInside(loc))
                return team.getZone(false);
        for (FKZone zone : zones)
            if (zone.isInside(loc))
                return zone;
        return null;
    }

    public boolean hasPermission(FKPermissions.Type permissionType, Location loc, boolean teamNeutral) {
        if (priority.getPermission(permissionType) != FKPermissions.Definition.DEFAULT)
            return priority.getPermission(permissionType) == FKPermissions.Definition.ON;
        if (getZone(loc) != null)
            switch (getZone(loc).getType()) {
                case LOBBY:
                    if (lobby.getPermissions().getPermission(permissionType) == FKPermissions.Definition.DEFAULT)
                        break;
                    return lobby.getPermissions().getPermission(permissionType) == FKPermissions.Definition.ON;

                case SPAWN:
                    if (spawn.getPermissions().getPermission(permissionType) == FKPermissions.Definition.DEFAULT)
                        break;
                    return spawn.getPermissions().getPermission(permissionType) == FKPermissions.Definition.ON;

                case ZONE:
                    for (FKZone zone : zones)
                        if (zone.isInside(loc))
                            if (zone.getPermissions().getPermission(permissionType) != FKPermissions.Definition.DEFAULT)
                                return zone.getPermissions().getPermission(permissionType) == FKPermissions.Definition.ON;
                    break;

                case FRIENDLY:
                    if (teamNeutral || friendly.getPermission(permissionType) == FKPermissions.Definition.DEFAULT)
                        break;
                    return friendly.getPermission(permissionType) == FKPermissions.Definition.ON;

                case HOSTILE:
                    if (teamNeutral || hostile.getPermission(permissionType) == FKPermissions.Definition.DEFAULT)
                        break;
                    return hostile.getPermission(permissionType) == FKPermissions.Definition.ON;

                case NEUTRAL:
                default:
                    break;
            }
        if (neutral.getPermission(permissionType) != FKPermissions.Definition.DEFAULT)
            return neutral.getPermission(permissionType) == FKPermissions.Definition.ON;
        return global.getPermission(permissionType) == FKPermissions.Definition.ON;
    }

    public String getId() {
        return id;
    }

    public void setId(String id, boolean renameFile) {
        this.id = id;
        if (renameFile && getConfig() != null) {
            if (!getConfig().exists())
                saveManager(false);
            getConfig().getFile().getParentFile().renameTo(new File(getConfig().getFile().getParentFile().getParentFile().getPath(), "/game-" + id + "/Manager.yml"));
        }
    }

    public State getState() {
        return state;
    }

    public void setState(@Nonnull State state, boolean save) {
        this.state = state;
        if (save) {
            if (!getConfig().exists())
                saveManager(false);
            getConfig().load().setState(state, true).save();
        }
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day, boolean save) {
        this.day = day;
        if (save) {
            if (!getConfig().exists())
                saveManager(false);
            getConfig().load().setDay(day, true).save();
        }
    }

    public void increaseDay(boolean save) {
        setDay(day + 1, save);
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather, @Nullable Integer timeout, boolean save) {
        this.weather = weather;
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.world.setStorm(weather != Weather.CLEAR);
                Main.world.setThundering(weather == Weather.THUNDER);
                Main.world.setWeatherDuration(timeout == null ? 12000 + new Random().nextInt(weather == Weather.CLEAR ? 167999 : 11999) : timeout);
                Main.world.setThunderDuration(timeout == null ? (weather == Weather.CLEAR ? 12000 : 3600) + new Random().nextInt(weather == Weather.CLEAR ? 167999 : 12399) : timeout);
            }
        }.runTask(Main.instance);
        if (save) {
            if (!getConfig().exists())
                saveManager(false);
            getConfig().load().setCurrentWeather(weather, true).save();
        }
    }

    public long getTime() {
        return time;
    }

    public String getFormattedTime() {
        DecimalFormat df = new DecimalFormat("00");
        return df.format((int) (getTime() / 1200)) + ":" + df.format((int) ((getTime() % 1200) / 20));
    }

    public void setTime(long time, boolean save) {
        this.time = time;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isLinkedToSun())
                    Main.world.setTime(time);
            }
        }.runTask(Main.instance);
        if (save) {
            if (!getConfig().exists())
                saveManager(false);
            getConfig().load().setTime(time, true).save();
        }
    }

    public void increaseTime(long time, boolean save) {
        setTime(this.time + time, save);
    }

    public boolean isLinkedToSun() {
        return linkedToSun;
    }

    public void setLinkedToSun(boolean linkedToSun, boolean save) {
        this.linkedToSun = linkedToSun;
        if (save) {
            if (!getConfig().exists())
                saveManager(false);
            getConfig().load().setLinkedToSun(linkedToSun, true).save();
        }
    }

    public FKOptions getOptions() {
        return options;
    }

    public void setOptions(FKOptions options, boolean save) {
        options.setManager(this);
        this.options = options;
        if (save)
            saveOptions();
    }

    public void saveOptions() {
        if (!getConfig().exists())
            saveManager(false);
        getConfig()
                .load()

                .setOptionName("pvp", options.getPvp().getName(), true)
                .setOptionActivation("pvp", options.getPvp().getActivationDay(), true)
                .setOptionActivated("pvp", options.getPvp().isActivated(), true)
                .setOptionName("nether", options.getNether().getName(), true)
                .setOptionActivation("nether", options.getNether().getActivationDay(), true)
                .setOptionActivated("nether", options.getNether().isActivated(), true)
                .setOptionName("assaults", options.getAssaults().getName(), true)
                .setOptionActivation("assaults", options.getAssaults().getActivationDay(), true)
                .setOptionActivated("assaults", options.getAssaults().isActivated(), true)
                .setOptionName("end", options.getEnd().getName(), true)
                .setOptionActivation("end", options.getEnd().getActivationDay(), true)
                .setOptionActivated("end", options.getEnd().isActivated(), true)

                .save();
    }

    public FKListener getListener() {
        return listener;
    }

    public void setListener(FKListener listener, boolean save) {
        listener.setManager(this);
        this.listener = listener;
        if (save)
            saveListener();
    }

    public void saveListener() {
        if (!getConfig().exists())
            saveManager(false);
        getConfig()
                .load()

                .setListenerName(listener.getScoreName(), true)
                .setListenerSavingTimeout(listener.getSavingTimeOut(), true)

                .save();
    }

    public Limits getLimits() {
        return limits;
    }

    public void setLimits(Limits limits, boolean save) {
        this.limits = limits;
        if (save)
            saveLimits();
    }

    public void saveLimits() {
        if (limits != null)
            limits.saveToConfig(id, false);
    }

    public void savePortal(String id, Portal portal) {
        if (!getConfig().exists())
            saveManager(false);
        getConfig()
                .load()

                .setPortalName(id, portal.getName(), true)
                .setPortalOpened(id, portal.isOpened(), true)
                .setPortalCooldown(id, portal.getCoolDown(), true)
                .setPortalOpenedMaterial(id, portal.getOpenedMat(), true)
                .setPortalClosedMaterial(id, portal.getClosedMat(), true)
                .setPortalClosedMaterial(id, portal.getClosedMat(), true)
                .setPortalMaterialData(id, portal.getData(), true)
                .setPortalSpawnOverLocation(id, portal.getOverSpawn(), true)
                .setPortalPos1OverLocation(id, portal.getOverPortal1(), true)
                .setPortalPos2OverLocation(id, portal.getOverPortal2(), true)
                .setPortalSpawnDimLocation(id, portal.getDimSpawn(), true)
                .setPortalPos1DimLocation(id, portal.getDimPortal1(), true)
                .setPortalPos2DimLocation(id, portal.getDimPortal2(), true)
                .setPortalTeleporting(id, portal.getInTeleportation(), true)

                .save();
    }

    public Portal getNether() {
        return nether;
    }

    public void setNether(Portal nether, boolean save) {
        if (nether != null)
            nether.close();
        this.nether = nether;
        if (save)
            saveNether();
    }

    public void saveNether() {
        savePortal("nether", nether);
    }

    public Portal getEnd() {
        return end;
    }

    public void setEnd(Portal end, boolean save) {
        if (end != null)
            end.close();
        this.end = end;
        if (save)
            saveEnd();
    }

    public void saveEnd() {
        savePortal("end", end);
    }

    public FKZone getLobby() {
        return lobby;
    }

    public void setLobby(FKZone lobby, boolean save) {
        this.lobby = lobby;
        this.lobby.setId(FKZone.LOBBY_ID, false);
        if (save)
            this.lobby.saveToConfig(id, false);
    }

    public FKZone getSpawn() {
        return spawn;
    }

    public void setSpawn(FKZone spawn, boolean save) {
        this.spawn = spawn;
        this.spawn.setId(FKZone.SPAWN_ID, false);
        if (save)
            this.spawn.saveToConfig(id, false);
    }

    public ArrayList<FKZone> getNormalZones() {
        return zones;
    }

    public void setNormalZones(ArrayList<FKZone> zones, boolean save) {
        this.zones = zones;
        if (save)
            for (FKZone zone : zones)
                zone.saveToConfig(id, false);
    }

    public ArrayList<FKZone> getZones() {
        return new ArrayList<FKZone>() {{
            if (lobby != null)
                add(lobby);
            if (spawn != null)
                add(spawn);
            if (zones != null)
                addAll(zones);
        }};
    }

    public FKZone getZone(String id) {
        for (FKZone zone : getZones())
            if (zone.getId().equals(id))
                return zone;
        return null;
    }

    public ArrayList<FKPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<FKPlayer> players, boolean save) {
        this.players = players;
        if (save)
            for (FKPlayer player : this.players)
                player.saveToConfig(id, false);
    }

    /*
     * public FKPlayer getPlayer(@Nonnull UUID uuid, boolean create) {
     *     for (FKPlayer player : players)
     *         if (player.getUuid() != null && player.getUuid().equals(uuid))
     *             return player;
     *     if (create) {
     *         Utils.MojangProfile profile = Utils.getProfileFromMojangAPI(uuid);
     *         if (profile == null)
     *             throw new FKException.PlayerDoesNotExistException(uuid);
     *         FKPlayer player = new FKPlayer(profile.getUuid(), profile.getName(), null, null);
     *         players.add(player);
     *         return player;
     *     }
     *     return null;
     * }
     */

    /**
     * "Get a player by name, or create one if it doesn't exist."<br>
     * <br>
     * The first thing you'll notice is the `@Nonnull` annotation. This is a Java annotation that tells the compiler that
     * the parameter will never be null. This is important because the function will throw an exception if the parameter is
     * null.<br>
     *
     * @param name   The name of the player to get.
     * @param create If true, the player will be created if it doesn't exist.
     *
     * @return A {@link FKPlayer} object
     */
    public FKPlayer getPlayer(@Nonnull String name, boolean create) {
        for (FKPlayer player : players)
            if (player.getName() != null && player.getName().equalsIgnoreCase(name))
                return player;
        if (create) {
            /*
             * Old code:
             *
             *     Pair<String, UUID> i = Utils.getNameAndUUIDFromMojangAPI(name);
             *     if (i == null)
             *         throw new FKException.PlayerDoesNotExistException(name);
             */
            FKPlayer player = new FKPlayer(name, null, null);
            if (Bukkit.getPlayerExact(name) != null)
                player.setLastUuid(Bukkit.getPlayerExact(name).getUniqueId(), false);
            players.add(player);
            player.saveToConfig(id, false);
            return player;
        }
        return null;
    }

    /*
     * public FKPlayer getPlayer(@Nonnull UUID uuid, @Nonnull String name, boolean create) {
     *     for (FKPlayer player : players)
     *         if ((player.getUuid() != null && player.getUuid().equals(uuid)) || (player.getName() != null && player.getName().equalsIgnoreCase(name)))
     *             return player;
     *     if (create) {
     *         FKPlayer player = null;
     *         Utils.MojangProfile profile = Utils.getProfileFromMojangAPI(uuid);
     *         if (profile != null) {
     *             player = new FKPlayer(profile.getUuid(), profile.getName(), null, null);
     *         } else {
     *             Pair<String, UUID> i = Utils.getNameAndUUIDFromMojangAPI(name);
     *             if (i == null)
     *                 throw new FKException.PlayerDoesNotExistException(uuid, name);
     *             player = new FKPlayer(uuid, Bukkit.getOfflinePlayer(uuid).getName(), null, null);
     *         }
     *         players.add(player);
     *         return player;
     *     }
     *     return null;
     * }
     */

    public FKTeam getGods() {
        return gods;
    }

    public void setGods(FKTeam gods, boolean save) {
        this.gods = gods;
        this.gods.setId(FKTeam.GODS_ID, false);
        if (save)
            this.gods.saveToConfig(id, false);
    }

    public FKTeam getSpecs() {
        return specs;
    }

    public void setSpecs(FKTeam specs, boolean save) {
        this.specs = specs;
        this.specs.setId(FKTeam.SPECS_ID, false);
        if (save)
            this.specs.saveToConfig(id, false);
    }

    public ArrayList<FKTeam> getParticipantsTeams() {
        return teams;
    }

    public void setParticipantsTeams(ArrayList<FKTeam> teams, boolean save) {
        this.teams = teams;
        if (save)
            for (FKTeam team : teams)
                team.saveToConfig(id, false);
    }

    public ArrayList<FKTeam> getTeams() {
        return new ArrayList<FKTeam>() {{
            if (gods != null)
                add(gods);
            if (specs != null)
                add(specs);
            if (teams != null)
                addAll(teams);
        }};
    }

    public FKTeam getTeam(String id) {
        for (FKTeam team : getTeams())
            if (team.getId().equalsIgnoreCase(id))
                return team;
        return null;
    }

    public void addTeam(FKTeam team) {
        teams.add(team);
    }


    public void removeTeam(FKTeam team) {
        teams.remove(team);
        team.getConfig(id).delete();
        getPlayers().stream().filter(p -> p.getTeam() != null && p.getTeam().getId().equals(team.getId()))
                .forEach(p -> p.leaveTeam(true));
    }

    public void removeTeam(String id) {
        removeTeam(getTeam(id));
    }

    public FKPermissions getGlobal() {
        return global;
    }

    public void setGlobal(FKPermissions global, boolean save) {
        this.global = global;
        if (save)
            getConfig().load().setGlobalPermissions(global, true).save();
    }

    public FKPermissions getNeutral() {
        return neutral;
    }

    public void setNeutral(FKPermissions neutral, boolean save) {
        this.neutral = neutral;
        if (save)
            getConfig().load().setNeutralPermissions(neutral, true).save();
    }

    public FKPermissions getFriendly() {
        return friendly;
    }

    public void setFriendly(FKPermissions friendly, boolean save) {
        this.friendly = friendly;
        if (save)
            getConfig().load().setFriendlyPermissions(friendly, true).save();
    }

    public FKPermissions getHostile() {
        return hostile;
    }

    public void setHostile(FKPermissions hostile, boolean save) {
        this.hostile = hostile;
        if (save)
            getConfig().load().setHostilePermissions(hostile, true).save();
    }

    public FKPermissions getPriority() {
        return priority;
    }

    public void setPriority(FKPermissions priority, boolean save) {
        this.priority = priority;
        if (save)
            getConfig().load().setPriorityPermissions(priority, true).save();
    }

    public FKPickableLocks getPickableLocks() {
        return pickableLocks;
    }

    public void setPickableLocks(FKPickableLocks pickableLocks, boolean save) {
        this.pickableLocks = pickableLocks;
        if (save)
            savePickableLocks();
    }

    public void savePickableLocks() {
        this.pickableLocks.saveToConfig(id, false);
    }
}
