package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.Portal;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scoreboard.Scoreboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.*;

public class FKManager {

    public static enum State {WAITING, RUNNING, PAUSED, ENDED}

    public static List<FKManager> registered = new ArrayList<>();
    public static String currentGameId = null;

    private String id;
    private State state;

    private int day, weather; // 0 >> Clear ; 1 >> Raining ; 2 >> Thundering
    private long time;
    private boolean linkedToSun;

    private FKOptions options;
    private FKListener listener;
    private Scoreboard mainScoreboard;

    private Portal nether, end;

    private FKZone lobby;
    private FKZone spawn;
    private List<FKZone> zones;

    private FKTeam gods;
    private FKTeam specs;
    private List<FKTeam> teams;

    private FKAuth globals;
    private FKAuth neutral;
    private FKAuth friendly;
    private FKAuth hostile;
    private FKAuth priority;


    public FKManager(String id) {
        this.mainScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.id = id;
        setDay(1);
        this.weather = Main.world == null ? 0 : Main.world.isThundering() ? 2 : Main.world.hasStorm() ? 1 : 0;
        setTime(0);
        setLinkedToSun(true);
        setOptions(FKOptions.getDefaultOptions());
        setListener(new FKListener("FALLEN KINGDOM X"));
        setNether(null);
        setEnd(null);
        setLobby(new FKZone(null, FKZone.Type.LOBBY,
                new Location(Main.world, 0, 0, 0),
                null, null,
                new FKAuth(FKAuth.Definition.OFF)));
        setSpawn(new FKZone(null, FKZone.Type.SPAWN,
                new Location(Main.world, 0, 0, 0),
                null, null,
                new FKAuth(FKAuth.Definition.DEFAULT,
                        new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.OFF),
                        new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.OFF),
                        new FKAuth.Item(FKAuth.Type.MOBS, FKAuth.Definition.OFF))));
        setZones(new ArrayList<FKZone>() {{
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
        }});
        setGods(new FKTeam("gods", "Dieux", SpecialChars.STAR_5_6 + " Dieu ||  ", ChatColor.DARK_RED, null, 0, new FKAuth(FKAuth.Definition.ON), null));
        setSpecs(new FKTeam("specs", "Specs", SpecialChars.FLOWER_3 + " Spec ||  ", ChatColor.GRAY, null, 0, new FKAuth(FKAuth.Definition.OFF), null));
        setTeams(new ArrayList<>());
        setGlobals(new FKAuth(FKAuth.Definition.OFF,
                new FKAuth.Item(FKAuth.Type.BREAKSPE, FKAuth.Definition.ON),
                new FKAuth.Item(FKAuth.Type.PLACESPE, FKAuth.Definition.ON),
                new FKAuth.Item(FKAuth.Type.PVP, FKAuth.Definition.ON),
                new FKAuth.Item(FKAuth.Type.MOBS, FKAuth.Definition.ON)));
        setNeutral(new FKAuth(FKAuth.Definition.DEFAULT,
                new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.ON),
                new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.OFF)));
        setFriendly(new FKAuth(FKAuth.Definition.DEFAULT,
                new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.ON),
                new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.ON)));
        setHostile(new FKAuth(FKAuth.Definition.DEFAULT,
                new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.OFF),
                new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.OFF)));
        setPriority(new FKAuth(FKAuth.Definition.OFF));

        setState(State.WAITING);
    }


    public FKManager(String id, State state, int day, int weather, long time, boolean linkedToSun, FKOptions options,
                     FKListener listener, Portal nether, Portal end, FKZone lobby, FKZone spawn, List<FKZone> zones,
                     FKTeam gods, FKTeam specs, List<FKTeam> teams, FKAuth globals, FKAuth neutral, FKAuth friendly,
                     FKAuth hostile, FKAuth priority) {
        this.mainScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.id = id;
        setState(state);
        setDay(day);
        this.weather = Main.world == null ? 0 : Main.world.isThundering() ? 2 : Main.world.hasStorm() ? 1 : 0;
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
        setZones(zones);
        setGods(gods);
        setSpecs(specs);
        setTeams(teams);
        setGlobals(globals);
        setNeutral(neutral);
        setFriendly(friendly);
        setHostile(hostile);
        setPriority(priority);
    }

    public void register() {
        registered.add(this);
        currentGameId = id;
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

    public static FKPlayer getGlobalPlayer(UUID uuid) {
        for (FKManager game : registered) {
            if (game.getTeams() != null)
                for (FKTeam team : game.getTeams())
                    for (FKPlayer player : team.getPlayers())
                        if (player.getUuid().equals(uuid))
                            return player;
            if (game.getGods() != null)
                for (FKPlayer player : game.getGods().getPlayers())
                    if (player.getUuid().equals(uuid))
                        return player;
            if (game.getSpecs() != null)
                for (FKPlayer player : game.getSpecs().getPlayers())
                    if (player.getUuid().equals(uuid))
                        return player;
        }
        return null;
    }

    public static FKPlayer getGlobalPlayer(@Nonnull String name) {
        for (FKManager game : registered) {
            if (game.getTeams() != null)
                for (FKTeam team : game.getTeams())
                    for (FKPlayer player : team.getPlayers())
                        if (name.equalsIgnoreCase(player.getName()))
                            return player;
            if (game.getGods() != null)
                for (FKPlayer player : game.getGods().getPlayers())
                    if (name.equalsIgnoreCase(player.getName()))
                        return player;
            if (game.getSpecs() != null)
                for (FKPlayer player : game.getSpecs().getPlayers())
                    if (name.equalsIgnoreCase(player.getName()))
                        return player;
        }
        return null;
    }

    public static List<FKPlayer> getGlobalPlayers() {
        return new ArrayList<>(new HashSet<FKPlayer>() {{
            for (FKManager game : registered) {
                if (game.getTeams() != null)
                    for (FKTeam team : game.getTeams())
                        addAll(team.getPlayers());
                if (game.getGods() != null)
                    addAll(game.getGods().getPlayers());
                if (game.getSpecs() != null)
                    addAll(game.getSpecs().getPlayers());
            }
        }});
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
                        setPriority(new FKAuth(FKAuth.Definition.DEFAULT));
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
        setPriority(new FKAuth(FKAuth.Definition.OFF));
        setState(State.ENDED);
    }

    public FKTeam getTeam(String id) {
        if (teams != null)
            for (FKTeam team : teams)
                if (team.getId().equalsIgnoreCase(id))
                    return team;
        if (gods != null)
            if (gods.getId().equalsIgnoreCase(id))
                return gods;
        if (specs != null)
            if (specs.getId().equalsIgnoreCase(id))
                return specs;
        return null;
    }

    /**
     * @deprecated This method is deprecated. Use {@link FKTeam#setManager(FKManager)} instead.
     */
    @Deprecated
    public void addTeam(FKTeam team) {
        team.setManager(this);
    }

    /**
     * @deprecated This method is deprecated. Use {@link FKTeam#leaveManager()} instead.
     */
    @Deprecated
    public void removeTeam(FKTeam team) {
        team.leaveManager();
    }

    public FKPlayer getPlayer(UUID uuid) {
        if (teams != null)
            for (FKTeam team : teams)
                for (FKPlayer player : team.getPlayers())
                    if (player.getUuid().equals(uuid))
                        return player;
        if (gods != null)
            for (FKPlayer player : gods.getPlayers())
                if (player.getUuid().equals(uuid))
                    return player;
        if (specs != null)
            for (FKPlayer player : specs.getPlayers())
                if (player.getUuid().equals(uuid))
                    return player;
        return null;
    }

    public FKPlayer getPlayer(@Nonnull String name) {
        if (teams != null)
            for (FKTeam team : teams)
                for (FKPlayer player : team.getPlayers())
                    if (name.equalsIgnoreCase(player.getName()))
                        return player;
        if (gods != null)
            for (FKPlayer player : gods.getPlayers())
                if (name.equalsIgnoreCase(player.getName()))
                    return player;
        if (specs != null)
            for (FKPlayer player : specs.getPlayers())
                if (name.equalsIgnoreCase(player.getName()))
                    return player;
        return null;
    }

    public List<FKPlayer> getPlayers() {
        return new ArrayList<>(new HashSet<FKPlayer>() {{
            teams.forEach(t -> this.addAll(t.getPlayers()));
            addAll(gods.getPlayers());
            addAll(specs.getPlayers());
        }});
    }

    public String getFormattedTime() {
        DecimalFormat df = new DecimalFormat("00");
        return df.format((int) (getTime() / 1200)) + ":" + df.format((int) ((getTime() % 1200) / 20));
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

    public boolean hasAuthorization(FKAuth.Type authorizationType, Location loc) {
        if (priority.getAuthorization(authorizationType) != FKAuth.Definition.DEFAULT)
            return priority.getAuthorization(authorizationType) == FKAuth.Definition.ON;
        if (getZone(loc) != null)
            switch (getZone(loc).getType()) {
                case LOBBY:
                    if (lobby.getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.DEFAULT)
                        break;
                    return lobby.getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.ON;

                case SPAWN:
                    if (spawn.getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.DEFAULT)
                        break;
                    return spawn.getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.ON;

                case ZONE:
                    for (FKZone zone : zones)
                        if (zone.isInside(loc))
                            if (zone.getAuthorizations().getAuthorization(authorizationType) != FKAuth.Definition.DEFAULT)
                                return zone.getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.ON;
                    break;

                case FRIENDLY:
                    if (friendly.getAuthorization(authorizationType) == FKAuth.Definition.DEFAULT)
                        break;
                    return friendly.getAuthorization(authorizationType) == FKAuth.Definition.ON;

                case HOSTILE:
                    if (hostile.getAuthorization(authorizationType) == FKAuth.Definition.DEFAULT)
                        break;
                    return hostile.getAuthorization(authorizationType) == FKAuth.Definition.ON;

                case NEUTRAL:
                default:
                    break;
            }
        if (neutral.getAuthorization(authorizationType) != FKAuth.Definition.DEFAULT)
            return neutral.getAuthorization(authorizationType) == FKAuth.Definition.ON;
        return globals.getAuthorization(authorizationType) == FKAuth.Definition.ON;
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

    public void setState(State state) {
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

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather, @Nullable Integer timeout) {
        this.weather = weather;
        Main.world.setStorm(weather != 0);
        Main.world.setThundering(weather == 2);
        Main.world.setWeatherDuration(timeout == null ? 12000 + new Random().nextInt(weather == 0 ? 167999 : 11999) : timeout);
        Main.world.setThunderDuration(timeout == null ? (weather == 0 ? 12000 : 3600) + new Random().nextInt(weather == 0 ? 167999 : 12399) : timeout);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
        if (isLinkedToSun())
            Main.world.setTime(this.time);
    }

    public void increaseTime(long time) {
        this.time += time;
        if (isLinkedToSun())
            Main.world.setTime(this.time);
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

    public Scoreboard getMainScoreboard() {
        return mainScoreboard;
    }

    public void setMainScoreboard(Scoreboard mainScoreboard) {
        this.mainScoreboard = mainScoreboard;
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
        if (this.lobby != null)
            this.lobby.setManager(null);
        this.lobby = lobby;
        this.lobby.setManager(this);
    }

    public FKZone getSpawn() {
        return spawn;
    }

    public void setSpawn(FKZone spawn) {
        if (this.spawn != null)
            this.spawn.setManager(null);
        this.spawn = spawn;
        this.spawn.setManager(this);
    }

    public List<FKZone> getZones() {
        return zones;
    }

    public void setZones(List<FKZone> zones) {
        if(this.zones != null)
            this.zones.forEach(zone -> zone.setManager(null));
        this.zones = zones;
        this.zones.forEach(z -> z.setManager(this));
    }

    public FKTeam getGods() {
        return gods;
    }

    public void setGods(FKTeam gods) {
        if (this.gods != null)
            this.gods.leaveManager();
        this.gods = gods;
        this.gods.setManager(this, false);
        this.gods.setId(FKTeam.GODS_ID);
    }

    public FKTeam getSpecs() {
        return specs;
    }

    public void setSpecs(FKTeam specs) {
        if (this.specs != null)
            this.specs.leaveManager();
        this.specs = specs;
        this.specs.setManager(this, false);
        this.specs.setId(FKTeam.SPECS_ID);
    }

    public List<FKTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<FKTeam> teams) {
        if(this.teams != null)
            this.teams.forEach(FKTeam::leaveManager);
        this.teams = teams;
        this.teams.forEach(t -> t.setManager(this));
    }

    public FKAuth getGlobals() {
        return globals;
    }

    public void setGlobals(FKAuth globals) {
        this.globals = globals;
    }

    public FKAuth getNeutral() {
        return neutral;
    }

    public void setNeutral(FKAuth neutral) {
        this.neutral = neutral;
    }

    public FKAuth getFriendly() {
        return friendly;
    }

    public void setFriendly(FKAuth friendly) {
        this.friendly = friendly;
    }

    public FKAuth getHostile() {
        return hostile;
    }

    public void setHostile(FKAuth hostile) {
        this.hostile = hostile;
    }

    public FKAuth getPriority() {
        return priority;
    }

    public void setPriority(FKAuth priority) {
        this.priority = priority;
    }
}
