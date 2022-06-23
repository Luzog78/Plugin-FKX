package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.Portal;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class FKManager {

    public static enum State {WAITING, RUNNING, PAUSED, ENDED}

    public static ArrayList<FKManager> registered = new ArrayList<>();
    public static String currentGameId = null;

    private String id;
    private State state;

    private int day, weather; // 0 >> Clear ; 1 >> Raining ; 2 >> Thundering
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

    private FKAuth globals;
    private FKAuth neutral;
    private FKAuth friendly;
    private FKAuth hostile;
    private FKAuth priority;


    public FKManager(String id) {
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
        setNormalZones(new ArrayList<FKZone>() {{
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
        setPlayers(new ArrayList<FKPlayer>());
        setGods(new FKTeam("gods", "Dieux", SpecialChars.STAR_5_6 + " Dieu ||  ", ChatColor.DARK_RED, null, 0, new FKAuth(FKAuth.Definition.ON)));
        setSpecs(new FKTeam("specs", "Specs", SpecialChars.FLOWER_3 + " Spec ||  ", ChatColor.GRAY, null, 0, new FKAuth(FKAuth.Definition.OFF)));
        setParticipantsTeams(new ArrayList<>());
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
                     FKListener listener, Portal nether, Portal end, FKZone lobby, FKZone spawn, ArrayList<FKZone> zones,
                     ArrayList<FKPlayer> players, FKTeam gods, FKTeam specs, ArrayList<FKTeam> teams, FKAuth globals,
                     FKAuth neutral, FKAuth friendly, FKAuth hostile, FKAuth priority) {
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
        setNormalZones(zones);
        setPlayers(players);
        setGods(gods);
        setSpecs(specs);
        setParticipantsTeams(teams);
        setGlobals(globals);
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

    public String getFormattedTime() {
        DecimalFormat df = new DecimalFormat("00");
        return df.format((int) (getTime() / 1200)) + ":" + df.format((int) ((getTime() % 1200) / 20));
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
            if(i == null)
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
            if (profile != null){
                player = new FKPlayer(profile.getUuid(), profile.getName(), null, null);
            } else {
                Pair<String, UUID> i = Utils.getNameAndUUIDFromAPI(name);
                if(i == null)
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
