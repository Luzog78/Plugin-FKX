package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scoreboard.Scoreboard;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class FKManager {

    public static List<FKManager> registered = new ArrayList<FKManager>() {{
    }};
    public static String currentGameId = null;

    private String id;

    private int day;
    private int weather; // 0 >> Clear ; 1 >> Raining ; 2 >> Thundering
    private int time;
    private boolean linkedToSun;

    private FKOptions options;
    private FKListener listener;
    private Scoreboard mainScoreboard;

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


    public FKManager(String id) {
        this.mainScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.id = id;
        setDay(1);
        setWeather(0);
        setTime(0);
        setLinkedToSun(true);
        setOptions(FKOptions.getDefaultOptions());
        setListener(new FKListener("FALLEN KINGDOM X"));
        setLobby(new FKZone(null, FKZone.Type.LOBBY,
                new Location(Main.world, 0, 0, 0),
                new Location(Main.world, 0, 0, 0),
                new Location(Main.world, 0, 0, 0),
                new FKAuth(FKAuth.Definition.OFF)));
        setSpawn(new FKZone(null, FKZone.Type.SPAWN,
                new Location(Main.world, 0, 0, 0),
                new Location(Main.world, 0, 0, 0),
                new Location(Main.world, 0, 0, 0),
                new FKAuth(FKAuth.Definition.DEFAULT,
                        new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.OFF),
                        new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.OFF))));
        setZones(new ArrayList<>());
        setGods(new FKTeam("gods", "Dieux", "D", ChatColor.DARK_RED, new Location(Main.world, 0, 0, 0), 0, new FKAuth(FKAuth.Definition.ON), null));
        setSpecs(new FKTeam("specs", "Specs", "S", ChatColor.GRAY, new Location(Main.world, 0, 0, 0), 0, new FKAuth(FKAuth.Definition.OFF), null));
        setTeams(new ArrayList<>());
        setGlobals(new FKAuth(FKAuth.Definition.OFF,
                new FKAuth.Item(FKAuth.Type.BREAKSPE, FKAuth.Definition.ON),
                new FKAuth.Item(FKAuth.Type.PLACESPE, FKAuth.Definition.ON),
                new FKAuth.Item(FKAuth.Type.PVP, FKAuth.Definition.ON)));
        setNeutral(new FKAuth(FKAuth.Definition.DEFAULT,
                new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.ON),
                new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.OFF)));
        setFriendly(new FKAuth(FKAuth.Definition.DEFAULT,
                new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.ON),
                new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.ON)));
        setHostile(new FKAuth(FKAuth.Definition.DEFAULT,
                new FKAuth.Item(FKAuth.Type.BREAK, FKAuth.Definition.OFF),
                new FKAuth.Item(FKAuth.Type.PLACE, FKAuth.Definition.OFF)));
    }


    public FKManager(String id, int day, int weather, int time, boolean linkedToSun, FKOptions options, FKListener listener,
                     FKZone lobby, FKZone spawn, List<FKZone> zones, FKTeam gods, FKTeam specs, List<FKTeam> teams,
                     FKAuth globals, FKAuth neutral, FKAuth friendly, FKAuth hostile) {
        this.mainScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.id = id;
        setDay(day);
        setWeather(weather);
        setTime(time);
        setLinkedToSun(linkedToSun);
        setOptions(options);
        setListener(listener);
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
        for (FKManager game : registered)
            for (FKTeam team : game.getTeams())
                for (FKPlayer player : team.getPlayers())
                    if (player.getUuid().equals(uuid))
                        return player;
        return null;
    }

    public static List<FKPlayer> getGlobalPlayers() {
        return new ArrayList<>(new HashSet<FKPlayer>() {{
            for (FKManager game : registered)
                for (FKTeam team : game.getTeams())
                    this.addAll(team.getPlayers());
        }});
    }

    public void refresh() {
// TODO -> refresh all
//        final int dayDuration = 1200;
//        if (time + 1 >= dayDuration) {
//            day += (int) ((time + 1) / dayDuration);
//            time -= (int) (((time + 1) / dayDuration)) * dayDuration;
//            Broadcast.log("Passage au Jour !" + day + " !");
//        } else
//            time++;

//        dayExactVerif();
//
//        switch (getTime()) {
//            case dayDuration - 60:
//                Broadcast.log("Jour !" + (day + 1) + " dans !1 minute !");
//                break;
//            case dayDuration - 30:
//                Broadcast.log("Jour !" + (day + 1) + " dans !30 secondes !");
//                break;
//            case dayDuration - 10:
//                Broadcast.log("Jour !" + (day + 1) + " dans !10 secondes !");
//                break;
//            case dayDuration - 5:
//                Broadcast.log("Jour !" + (day + 1) + " dans !5 secondes !");
//                break;
//            case dayDuration - 3:
//                Broadcast.log("Jour !" + (day + 1) + " dans !3 secondes !");
//                break;
//            case dayDuration - 2:
//                Broadcast.log("Jour !" + (day + 1) + " dans !2 secondes !");
//                break;
//            case dayDuration - 1:
//                Broadcast.log("Jour !" + (day + 1) + " dans !1 seconde !");
//                break;
//            default:
//                break;
//        }

//        refreshEntries();
    }

    public void start() {
        // TODO -> start game
    }

    public void pause() {
        // TODO -> pause game
    }

    public void resume() {
        // TODO -> resume game
    }

    public void end() {
        // TODO -> end game
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

    public List<FKPlayer> getPlayers() {
        return new ArrayList<>(new HashSet<FKPlayer>() {{
            teams.forEach(t -> this.addAll(t.getPlayers()));
        }});
    }

    public String getFormattedTime() {
        DecimalFormat df = new DecimalFormat("00");
        return df.format((int) (getTime() / 1200)) + ":" + df.format((int) ((getTime() % 1200) / 20));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setWeather(int weather) {
        this.weather = weather;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
        if (isLinkedToSun())
            Main.world.setTime(this.time);
    }

    public void increaseTime(int time) {
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

    public FKZone getLobby() {
        return lobby;
    }

    public void setLobby(FKZone lobby) {
        lobby.setManager(this);
        this.lobby = lobby;
    }

    public FKZone getSpawn() {
        return spawn;
    }

    public void setSpawn(FKZone spawn) {
        spawn.setManager(this);
        this.spawn = spawn;
    }

    public List<FKZone> getZones() {
        return zones;
    }

    public void setZones(List<FKZone> zones) {
        zones.forEach(z -> z.setManager(this));
        this.zones = zones;
    }

    public FKTeam getGods() {
        return gods;
    }

    public void setGods(FKTeam gods) {
        this.gods = gods;
        gods.setManager(this, false);
    }

    public FKTeam getSpecs() {
        return specs;
    }

    public void setSpecs(FKTeam specs) {
        this.specs = specs;
        specs.setManager(this, false);
    }

    public List<FKTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<FKTeam> teams) {
        this.teams = teams;
        teams.forEach(t -> t.setManager(this));
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
}
