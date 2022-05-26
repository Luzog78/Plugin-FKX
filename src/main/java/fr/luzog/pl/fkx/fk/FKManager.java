package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.Main;
import org.bukkit.Bukkit;
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

    private int day = 1;
    private int weather = 0; // 0 >> Clear ; 1 >> Raining ; 2 >> Thundering

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


    public FKManager(String id, int day, int weather, FKOptions options, FKListener listener,
                     FKZone lobby, FKZone spawn, List<FKZone> zones, FKTeam gods, FKTeam specs, List<FKTeam> teams,
                     FKAuth globals, FKAuth neutral, FKAuth friendly, FKAuth hostile) {
        this.id = id;
        this.day = day;
        this.weather = weather;
        this.options = options;
        this.listener = listener;
        this.lobby = lobby;
        this.spawn = spawn;
        this.zones = zones;
        this.gods = gods;
        this.specs = specs;
        this.teams = teams;
        this.globals = globals;
        this.neutral = neutral;
        this.friendly = friendly;
        this.hostile = hostile;

        this.mainScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.options.setManager(this);
        this.listener.setManager(this);
        this.lobby.setManager(this);
        this.spawn.setManager(this);
        this.zones.forEach(z -> z.setManager(this));
        this.gods.setManager(this);
        this.specs.setManager(this);
        this.teams.forEach(t -> t.setManager(this));

        registered.add(this);
    }

    public static FKManager getCurrentGame() {
        return getGame(currentGameId);
    }

    public static FKManager getGame(String id) {
        for (FKManager game : registered)
            if (game.getId() == id || game.getId().equalsIgnoreCase(id))
                return game;
        return null;
    }

    public void refresh() {
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

    public FKTeam getTeam(String id) {
        for (FKTeam team : teams)
            if (team.getId().equalsIgnoreCase(id))
                return team;
        return null;
    }

    public void addTeam(FKTeam team) {
        teams.add(team);
    }

    public void removeTeam(FKTeam team) {
        teams.add(team);
    }

    public FKPlayer getPlayer(UUID uuid) {
        for (FKTeam team : teams)
            for (FKPlayer player : team.getPlayers())
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

    public long getTime() {
        return Main.world.getTime();
    }

    public void setTime(long time) {
        Main.world.setTime(time);
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

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    public FKOptions getOptions() {
        return options;
    }

    public void setOptions(FKOptions options) {
        this.options = options;
    }

    public FKListener getListener() {
        return listener;
    }

    public void setListener(FKListener listener) {
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
        this.lobby = lobby;
    }

    public FKZone getSpawn() {
        return spawn;
    }

    public void setSpawn(FKZone spawn) {
        this.spawn = spawn;
    }

    public List<FKZone> getZones() {
        return zones;
    }

    public void setZones(List<FKZone> zones) {
        this.zones = zones;
    }

    public FKTeam getGods() {
        return gods;
    }

    public void setGods(FKTeam gods) {
        this.gods = gods;
    }

    public FKTeam getSpecs() {
        return specs;
    }

    public void setSpecs(FKTeam specs) {
        this.specs = specs;
    }

    public List<FKTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<FKTeam> teams) {
        this.teams = teams;
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
