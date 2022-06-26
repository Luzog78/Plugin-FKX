package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.Config;
import fr.luzog.pl.fkx.utils.Portal;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

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
                Utils.tryTo(printStackTrace, () -> manager.setState(Objects.requireNonNull(config.getState())));
                Utils.tryTo(printStackTrace, () -> manager.setDay(config.getDay()));
                Utils.tryTo(printStackTrace, () -> manager.setWeather(Objects.requireNonNull(config.getCurrentWeather()), null));
                Utils.tryTo(printStackTrace, () -> manager.setLinkedToSun(config.isLinkedToSun()));
                Utils.tryTo(printStackTrace, () -> manager.setTime(config.getTime()));
                Utils.tryTo(printStackTrace, () -> {
                    for (String o : config.getOptions())
                        if (o != null) {
                            FKOptions.FKOption option = new FKOptions.FKOption(config.getOptionName(o), config.getOptionActivation(o), config.isOptionActivated(o));
                            if (o.equalsIgnoreCase("pvp"))
                                Utils.tryTo(printStackTrace, () -> manager.getOptions().setPvp(option));
                            else if (o.equalsIgnoreCase("nether"))
                                Utils.tryTo(printStackTrace, () -> manager.getOptions().setNether(option));
                            else if (o.equalsIgnoreCase("assaults"))
                                Utils.tryTo(printStackTrace, () -> manager.getOptions().setAssaults(option));
                            else if (o.equalsIgnoreCase("end"))
                                Utils.tryTo(printStackTrace, () -> manager.getOptions().setEnd(option));
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
                                                        portal.tryToTeleport(e);
                                }
                            }.runTaskLater(Main.instance, 1);

                            if (p.equalsIgnoreCase("nether")) {
                                manager.setNether(portal);
                            } else if (p.equalsIgnoreCase("end"))
                                manager.setEnd(portal);
                        }
                });
                Utils.tryTo(printStackTrace, () -> manager.setGlobal(Objects.requireNonNull(config.getGlobalPermissions())));
                Utils.tryTo(printStackTrace, () -> manager.setNeutral(Objects.requireNonNull(config.getNeutralPermissions())));
                Utils.tryTo(printStackTrace, () -> manager.setFriendly(Objects.requireNonNull(config.getFriendlyPermissions())));
                Utils.tryTo(printStackTrace, () -> manager.setHostile(Objects.requireNonNull(config.getHostilePermissions())));
                Utils.tryTo(printStackTrace, () -> manager.setPriority(Objects.requireNonNull(config.getPriorityPermissions())));


                for (File ff : Objects.requireNonNull(f.listFiles()))
                    if (ff.isDirectory())
                        if (ff.getName().equalsIgnoreCase("zones")) {
                            for (File fff : Objects.requireNonNull(ff.listFiles()))
                                if (fff.isFile() && fff.getName().toLowerCase().endsWith(".yml")) {
                                    Config.Zone zc = new Config.Zone(String.format("%s/zones/%s", f.getName(), fff.getName())).load();
                                    FKZone zone = new FKZone(fff.getName().substring(0, fff.getName().length() - 4), null, null, null, null, null);

                                    Utils.tryTo(printStackTrace, () -> zone.setType(Objects.requireNonNull(zc.getType())));
                                    Utils.tryTo(printStackTrace, () -> zone.setSpawn(Objects.requireNonNull(zc.getSpawn())));
                                    Utils.tryTo(printStackTrace, () -> zone.setPos1(Objects.requireNonNull(zc.getPos1())));
                                    Utils.tryTo(printStackTrace, () -> zone.setPos2(Objects.requireNonNull(zc.getPos2())));
                                    Utils.tryTo(printStackTrace, () -> zone.setPermissions(Objects.requireNonNull(zc.getPermissions())));

                                    if (fff.getName().equalsIgnoreCase(FKZone.LOBBY_FILE))
                                        manager.setLobby(zone);
                                    else if (fff.getName().equalsIgnoreCase(FKZone.SPAWN_FILE))
                                        manager.setSpawn(zone);
                                    else
                                        manager.getZones().add(zone);
                                }
                        } else if (ff.getName().equalsIgnoreCase("teams")) {
                            for (File fff : Objects.requireNonNull(ff.listFiles()))
                                if (fff.isFile() && fff.getName().toLowerCase().endsWith(".yml")) {
                                    Config.Team tc = new Config.Team(String.format("%s/teams/%s", f.getName(), fff.getName())).load();
                                    FKTeam team = new FKTeam(fff.getName().substring(0, fff.getName().length() - 4));

                                    Utils.tryTo(printStackTrace, () -> team.setName(Objects.requireNonNull(tc.getName())));
                                    Utils.tryTo(printStackTrace, () -> team.setColor(Objects.requireNonNull(tc.getColor())));
                                    Utils.tryTo(printStackTrace, () -> team.setPrefix(Objects.requireNonNull(tc.getPrefix())));
                                    Utils.tryTo(printStackTrace, () -> team.setRadius(Objects.requireNonNull(tc.getRadius())));
                                    Utils.tryTo(printStackTrace, () -> team.setSpawn(Objects.requireNonNull(tc.getSpawn())));
                                    Utils.tryTo(printStackTrace, () -> team.setPermissions(Objects.requireNonNull(tc.getPermissions())));

                                    if (fff.getName().equalsIgnoreCase(FKTeam.GODS_FILE))
                                        manager.setGods(team);
                                    else if (fff.getName().equalsIgnoreCase(FKTeam.SPECS_FILE))
                                        manager.setSpecs(team);
                                    else
                                        manager.addTeam(team);
                                }
                        } else if (ff.getName().equalsIgnoreCase("players")) {
                            for (File fff : Objects.requireNonNull(ff.listFiles()))
                                if (fff.isFile() && fff.getName().toLowerCase().endsWith(".yml"))
                                    try {
                                        Config.Player pc = new Config.Player(String.format("%s/players/%s", f.getName(), fff.getName())).load();
                                        FKPlayer player = new FKPlayer(fff.getName().toLowerCase().startsWith("null") ? null
                                                : UUID.fromString(fff.getName().substring(0, fff.getName().length() - 4)), null, null, null);

                                        Utils.tryTo(printStackTrace, () -> player.setName(Objects.requireNonNull(pc.getName())));
                                        Utils.tryTo(printStackTrace, () -> player.setTeam(Objects.requireNonNull(pc.getTeam())));
                                        Utils.tryTo(printStackTrace, () -> player.setStats(Objects.requireNonNull(pc.getStats())));
                                        Utils.tryTo(printStackTrace, () -> player.setPersonalPermissions(Objects.requireNonNull(pc.getPermissions())));

                                        if (player.getUuid() != null || player.getName() != null)
                                            manager.getPlayers().add(player);
                                    } catch (IllegalArgumentException ignored) {
                                    }
                        }

                manager.register();
                manager.getListener().scheduleTask();
            }
    }

    public static void saveAll(boolean soft) {
        for (FKManager manager : registered)
            manager.save(soft);
    }

    public void save(boolean soft) {
        new Config.Manager(String.format(CONFIG_FILE, "game-" + id))
                .load()
                .setState(state, !soft)
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

        lobby.saveToConfig(id, soft);
        spawn.saveToConfig(id, soft);
        for (FKZone z : zones)
            z.saveToConfig(id, soft);

        gods.saveToConfig(id, soft);
        specs.saveToConfig(id, soft);
        for (FKTeam t : teams)
            t.saveToConfig(id, soft);

        for (FKPlayer p : players)
            p.saveToConfig(id, soft);
    }

    private String id;
    private State state;

    private int day;
    private Weather weather;
    private long time;
    private boolean linkedToSun;

    private FKOptions options;
    private FKListener listener;

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


    public FKManager(String id) {
        this.id = id;
        setDay(1);
        this.weather = Main.world == null ? Weather.CLEAR : Main.world.isThundering() ? Weather.THUNDER : Main.world.hasStorm() ? Weather.RAIN : Weather.CLEAR;
        setTime(0);
        setLinkedToSun(true);
        setOptions(FKOptions.getDefaultOptions());
        setListener(new FKListener("fko-sb", "FALLEN KINGDOM X", 60 * 5));
        setNether(null);
        setEnd(null);
        setLobby(new FKZone(null, FKZone.Type.LOBBY,
                new Location(Main.world, 0, 0, 0),
                null, null,
                new FKPermissions(FKPermissions.Definition.OFF)));
        setSpawn(new FKZone(null, FKZone.Type.SPAWN,
                new Location(Main.world, 0, 0, 0),
                null, null,
                new FKPermissions(FKPermissions.Definition.DEFAULT,
                        new FKPermissions.Item(FKPermissions.Type.BREAK, FKPermissions.Definition.OFF),
                        new FKPermissions.Item(FKPermissions.Type.PLACE, FKPermissions.Definition.OFF),
                        new FKPermissions.Item(FKPermissions.Type.MOBS, FKPermissions.Definition.OFF))));
        setNormalZones(new ArrayList<FKZone>() {{
            add(new FKZone("nether", FKZone.Type.ZONE,
                    Bukkit.getWorld("world_nether").getSpawnLocation(),
                    new Location(Bukkit.getWorld("world_nether"), Integer.MIN_VALUE, -1, Integer.MIN_VALUE),
                    new Location(Bukkit.getWorld("world_nether"), Integer.MAX_VALUE, 256, Integer.MAX_VALUE),
                    new FKPermissions(FKPermissions.Definition.ON)));
            add(new FKZone("end", FKZone.Type.ZONE,
                    Bukkit.getWorld("world_the_end").getSpawnLocation(),
                    new Location(Bukkit.getWorld("world_the_end"), Integer.MIN_VALUE, -1, Integer.MIN_VALUE),
                    new Location(Bukkit.getWorld("world_the_end"), Integer.MAX_VALUE, 256, Integer.MAX_VALUE),
                    new FKPermissions(FKPermissions.Definition.ON)));
        }});
        setPlayers(new ArrayList<FKPlayer>());
        setGods(new FKTeam("gods", "Dieux", SpecialChars.STAR_5_6 + " Dieu ||  ", ChatColor.DARK_RED, null, 0, new FKPermissions(FKPermissions.Definition.ON)));
        setSpecs(new FKTeam("specs", "Specs", SpecialChars.FLOWER_3 + " Spec ||  ", ChatColor.GRAY, null, 0, new FKPermissions(FKPermissions.Definition.OFF)));
        setParticipantsTeams(new ArrayList<>());
        setGlobal(new FKPermissions(FKPermissions.Definition.OFF,
                new FKPermissions.Item(FKPermissions.Type.BREAKSPE, FKPermissions.Definition.ON),
                new FKPermissions.Item(FKPermissions.Type.PLACESPE, FKPermissions.Definition.ON),
                new FKPermissions.Item(FKPermissions.Type.PVP, FKPermissions.Definition.ON),
                new FKPermissions.Item(FKPermissions.Type.MOBS, FKPermissions.Definition.ON)));
        setNeutral(new FKPermissions(FKPermissions.Definition.DEFAULT,
                new FKPermissions.Item(FKPermissions.Type.BREAK, FKPermissions.Definition.ON),
                new FKPermissions.Item(FKPermissions.Type.PLACE, FKPermissions.Definition.OFF)));
        setFriendly(new FKPermissions(FKPermissions.Definition.DEFAULT,
                new FKPermissions.Item(FKPermissions.Type.BREAK, FKPermissions.Definition.ON),
                new FKPermissions.Item(FKPermissions.Type.PLACE, FKPermissions.Definition.ON)));
        setHostile(new FKPermissions(FKPermissions.Definition.DEFAULT,
                new FKPermissions.Item(FKPermissions.Type.BREAK, FKPermissions.Definition.OFF),
                new FKPermissions.Item(FKPermissions.Type.PLACE, FKPermissions.Definition.OFF)));
        setPriority(new FKPermissions(FKPermissions.Definition.OFF));

        setState(State.WAITING);
    }


    public FKManager(String id, State state, int day, Weather weather, long time, boolean linkedToSun, FKOptions options,
                     FKListener listener, Portal nether, Portal end, FKZone lobby, FKZone spawn, ArrayList<FKZone> zones,
                     ArrayList<FKPlayer> players, FKTeam gods, FKTeam specs, ArrayList<FKTeam> teams, FKPermissions global,
                     FKPermissions neutral, FKPermissions friendly, FKPermissions hostile, FKPermissions priority) {
        this.id = id;
        setState(state);
        setDay(day);
        this.weather = Main.world == null ? Weather.CLEAR : Main.world.isThundering() ? Weather.THUNDER : Main.world.hasStorm() ? Weather.RAIN : Weather.CLEAR;
        if (weather != this.weather)
            setWeather(weather, null);
        setTime(time);
        setLinkedToSun(linkedToSun);
        setOptions(options);
        setListener(listener);
        setNether(nether);
        setEnd(end);
        setLobby(lobby);
        setSpawn(spawn);
        setNormalZones(zones);
        setPlayers(players);
        setGods(gods);
        setSpecs(specs);
        setParticipantsTeams(teams);
        setGlobal(global);
        setNeutral(neutral);
        setFriendly(friendly);
        setHostile(hostile);
        setPriority(priority);
    }

    public void register() {
        registered.add(this);
        currentGameId = id;
        getTeams().forEach(FKTeam::updatePlayers);
    }

    public void unregister() {
        registered.remove(this);
        currentGameId = registered.isEmpty() ? null : registered.get(0).getId();
    }

    public static FKManager getCurrentGame() {
// TODO -> Check if currentGameId is null
//        if(currentGameId == null)
//            currentGameId = Main.getInstance().getConfig().getString("current-game");
        return getGame(currentGameId);
    }

    public static FKManager setCurrentGame(String id) {
        currentGameId = id;
// TODO -> Check if currentGameId is null
//        Main.getInstance().getConfig().set("current-game", id);
//        Main.getInstance().saveConfig();
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

    public static ArrayList<FKPlayer> getGlobalPlayer(UUID uuid) {
        return new ArrayList<FKPlayer>() {{
            for (FKPlayer player : getGlobalPlayers())
                if (player.getUuid().equals(uuid))
                    add(player);
        }};
    }

    public static ArrayList<FKPlayer> getGlobalPlayer(@Nonnull String name) {
        return new ArrayList<FKPlayer>() {{
            for (FKPlayer player : getGlobalPlayers())
                if (name.equalsIgnoreCase(player.getName()))
                    add(player);
        }};
    }

    public static ArrayList<FKPlayer> getGlobalPlayer(@Nonnull UUID uuid, @Nonnull String name) {
        return new ArrayList<FKPlayer>() {{
            for (FKPlayer player : getGlobalPlayers())
                if (uuid.equals(player.getUuid()) || name.equalsIgnoreCase(player.getName()))
                    add(player);
        }};
    }

    public void start() {
        Utils.countDown(null, 20, false, true, true,
                "La partie commence dans §c%i%§rs...\n§7Préparez-vous à démarrer votre aventure !",
                "Bonne chance à tous !\n§7Prêt ?  Partez !", "§a", "§6", "§c§l",
                "§4§l", "§2§l", new Runnable() {
                    @Override
                    public void run() {
                        getPlayers().forEach(p -> {
                            if (p.getPlayer() != null)
                                p.getPlayer().teleport(p.getTeam().getSpawn());
                        });
                        setPriority(new FKPermissions(FKPermissions.Definition.DEFAULT));
                        setState(State.RUNNING);
                    }
                });
    }

    public void pause(int countDown) {
        Utils.countDown(null, countDown, false, true, true, "Le jeu se suspend dans §c%i%§r secondes !\n§7Vous serez momentanément bloqués.", "Le jeu est en pause.\n§7Excusez-nous pour la gêne occasionnée...", "§e", "§6", "§c", "§4", "§4§l", new Runnable() {
            @Override
            public void run() {
                setState(State.PAUSED);
            }
        });
    }

    public void resume(int countDown) {
        Utils.countDown(null, countDown, false, true, true, "Le jeu reprend dans §c%i%§r secondes !\n§7Et la compétition continue.", "Et c'est reparti !\n§7Amusez-vous !", "§e", "§6", "§c", "§4", "§2§l", new Runnable() {
            @Override
            public void run() {
                setState(State.RUNNING);
            }
        });
    }

    public void end() {
        String m1 = "§4§lFin de la partie", m2 = "§7RDV dans qq minutes pour les résultats !";
        getPlayers().forEach(p -> {
            if (p.getPlayer() != null) {
                p.getPlayer().sendTitle(m1, m2);
                p.getPlayer().sendMessage(Main.PREFIX + m1);
                p.getPlayer().sendMessage(Main.PREFIX + m2);
                p.getPlayer().teleport(getLobby().getSpawn());
            }
        });
        setPriority(new FKPermissions(FKPermissions.Definition.OFF));
        setState(State.ENDED);
    }

    public void checkActivations(boolean force) {
        for (FKOptions.FKOption opt : options.getOptions())
            if (force) {
                if (day >= opt.getActivationDay()) {
                    if (!opt.isActivated())
                        opt.activate();
                } else if (opt.isActivated())
                    opt.deactivate();
            } else if (day == opt.getActivationDay())
                opt.activate();
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

    public boolean hasPermission(FKPermissions.Type permissionType, Location loc) {
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
                    if (friendly.getPermission(permissionType) == FKPermissions.Definition.DEFAULT)
                        break;
                    return friendly.getPermission(permissionType) == FKPermissions.Definition.ON;

                case HOSTILE:
                    if (hostile.getPermission(permissionType) == FKPermissions.Definition.DEFAULT)
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

    public void setId(String id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(@Nonnull State state) {
        this.state = state;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void increaseDay() {
        day++;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather, @Nullable Integer timeout) {
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
    }

    public long getTime() {
        return time;
    }

    public String getFormattedTime() {
        DecimalFormat df = new DecimalFormat("00");
        return df.format((int) (getTime() / 1200)) + ":" + df.format((int) ((getTime() % 1200) / 20));
    }

    public void setTime(long time) {
        this.time = time;
        long tempTime = this.time;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isLinkedToSun())
                    Main.world.setTime(tempTime);
            }
        }.runTask(Main.instance);
    }

    public void increaseTime(long time) {
        this.time += time;
        long tempTime = this.time;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isLinkedToSun())
                    Main.world.setTime(tempTime);
            }
        }.runTask(Main.instance);
    }

    public boolean isLinkedToSun() {
        return linkedToSun;
    }

    public void setLinkedToSun(boolean linkedToSun) {
        this.linkedToSun = linkedToSun;
    }

    public FKOptions getOptions() {
        return options;
    }

    public void setOptions(FKOptions options) {
        options.setManager(this);
        this.options = options;
    }

    public FKListener getListener() {
        return listener;
    }

    public void setListener(FKListener listener) {
        listener.setManager(this);
        this.listener = listener;
    }

    public Portal getNether() {
        return nether;
    }

    public void setNether(Portal nether) {
        if (nether != null)
            nether.close();
        this.nether = nether;
    }

    public Portal getEnd() {
        return end;
    }

    public void setEnd(Portal end) {
        if (end != null)
            end.close();
        this.end = end;
    }

    public FKZone getLobby() {
        return lobby;
    }

    public void setLobby(FKZone lobby) {
        this.lobby = lobby;
        this.lobby.setId(FKZone.LOBBY_ID);
    }

    public FKZone getSpawn() {
        return spawn;
    }

    public void setSpawn(FKZone spawn) {
        this.spawn = spawn;
        this.spawn.setId(FKZone.SPAWN_ID);
    }

    public ArrayList<FKZone> getNormalZones() {
        return zones;
    }

    public void setNormalZones(ArrayList<FKZone> zones) {
        this.zones = zones;
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

    public void setPlayers(ArrayList<FKPlayer> players) {
        this.players = players;
    }

    public FKPlayer getPlayer(@Nonnull UUID uuid, boolean create) {
        for (FKPlayer player : players)
            if (player.getUuid() != null && player.getUuid().equals(uuid))
                return player;
        if (create) {
            Utils.MojangProfile profile = Utils.getMojangProfileFromAPI(uuid);
            if (profile == null)
                throw new FKException.PlayerDoesNotExistException(uuid);
            FKPlayer player = new FKPlayer(profile.getUuid(), profile.getName(), null, null);
            players.add(player);
            return player;
        }
        return null;
    }

    public FKPlayer getPlayer(@Nonnull String name, boolean create) {
        for (FKPlayer player : players)
            if (player.getName() != null && player.getName().equals(name))
                return player;
        if (create) {
            Pair<String, UUID> i = Utils.getNameAndUUIDFromAPI(name);
            if (i == null)
                throw new FKException.PlayerDoesNotExistException(name);
            FKPlayer player = new FKPlayer(i.getValue(), i.getKey(), null, null);
            players.add(player);
            return player;
        }
        return null;
    }

    public FKPlayer getPlayer(@Nonnull UUID uuid, @Nonnull String name, boolean create) {
        for (FKPlayer player : players)
            if ((player.getUuid() != null && player.getUuid().equals(uuid)) || (player.getName() != null && player.getName().equals(name)))
                return player;
        if (create) {
            FKPlayer player = null;
            Utils.MojangProfile profile = Utils.getMojangProfileFromAPI(uuid);
            if (profile != null) {
                player = new FKPlayer(profile.getUuid(), profile.getName(), null, null);
            } else {
                Pair<String, UUID> i = Utils.getNameAndUUIDFromAPI(name);
                if (i == null)
                    throw new FKException.PlayerDoesNotExistException(uuid, name);
                player = new FKPlayer(uuid, Bukkit.getOfflinePlayer(uuid).getName(), null, null);
            }
            players.add(player);
            return player;
        }
        return null;
    }

    public FKTeam getGods() {
        return gods;
    }

    public void setGods(FKTeam gods) {
        this.gods = gods;
        this.gods.setId(FKTeam.GODS_ID);
    }

    public FKTeam getSpecs() {
        return specs;
    }

    public void setSpecs(FKTeam specs) {
        this.specs = specs;
        this.specs.setId(FKTeam.SPECS_ID);
    }

    public ArrayList<FKTeam> getParticipantsTeams() {
        return teams;
    }

    public void setParticipantsTeams(ArrayList<FKTeam> teams) {
        this.teams = teams;
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
    }

    public void removeTeam(String id) {
        removeTeam(getTeam(id));
    }

    public FKPermissions getGlobal() {
        return global;
    }

    public void setGlobal(FKPermissions global) {
        this.global = global;
    }

    public FKPermissions getNeutral() {
        return neutral;
    }

    public void setNeutral(FKPermissions neutral) {
        this.neutral = neutral;
    }

    public FKPermissions getFriendly() {
        return friendly;
    }

    public void setFriendly(FKPermissions friendly) {
        this.friendly = friendly;
    }

    public FKPermissions getHostile() {
        return hostile;
    }

    public void setHostile(FKPermissions hostile) {
        this.hostile = hostile;
    }

    public FKPermissions getPriority() {
        return priority;
    }

    public void setPriority(FKPermissions priority) {
        this.priority = priority;
    }
}
