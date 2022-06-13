package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FKTeam {

    private FKManager manager;

    private String id, name, prefix;
    private ChatColor color;
    private Location spawn;
    private double radius;

    private List<FKPlayer> players;

    private Team scoreboardTeam;

    private FKAuth authorizations;

    public FKTeam(String id, String name, String prefix, ChatColor color, Location spawn, double radius, FKAuth authorizations, @Nullable List<FKPlayer> players) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        this.spawn = spawn;
        this.radius = radius;
        this.authorizations = authorizations;
        if (players == null)
            this.players = new ArrayList<>();
        else
            this.players = players;
        this.players.forEach(p -> p.setTeam(this));
    }

    public boolean isInside(Location loc) {
        Location l1 = getSpawn().clone(), l2 = getSpawn().clone();
        l1.setY(-1);
        l2.setY(256);
        return Utils.isInside(loc, l1.add(radius, 0, radius), l2.subtract(radius, 0, radius));
    }

    public FKZone getZone(boolean friendly) {
        return new FKZone(getId(), friendly ? FKZone.Type.FRIENDLY : FKZone.Type.HOSTILE, getSpawn().clone(),
                getSpawn().clone().subtract(radius, radius, radius), getSpawn().clone().add(radius, radius, radius), getAuthorizations());
    }

    public FKManager getManager() {
        return manager;
    }

    public void setManager(FKManager manager) {
        setManager(manager, true);
    }

    public void setManager(FKManager manager, boolean registerToTeamList) {
        if (this.manager != null) {
            leaveManager();
            return;
        }
        this.manager = manager;
        if (registerToTeamList && manager.getTeam(getId()) == null)
            manager.getTeams().add(this);
        scoreboardTeam = this.manager.getMainScoreboard().registerNewTeam(id);
        updateParams();
        updatePlayers();
    }

    public void updatePlayers() {
        scoreboardTeam.getPlayers().forEach(p -> scoreboardTeam.removePlayer(p));
        players.forEach(p -> scoreboardTeam.addPlayer(Bukkit.getOfflinePlayer(p.getUuid())));
    }

    public void updateParams() {
        scoreboardTeam.setDisplayName(getName());
        scoreboardTeam.setPrefix(getPrefix());
        scoreboardTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
        scoreboardTeam.setCanSeeFriendlyInvisibles(false);
        scoreboardTeam.setAllowFriendlyFire(true);
    }

    public void leaveManager() {
        if (scoreboardTeam != null)
            scoreboardTeam.unregister();
        if (this.manager != null)
            manager.getTeams().remove(this);
        this.manager = null;
        this.scoreboardTeam = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return color + name.replace("§r", color.toString()) + color + "§r";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        String p = color + prefix.replace("§r", color.toString());
        if(prefix.contains("§"))
            for (String s : prefix.toLowerCase().split("§"))
                if (!s.startsWith("r") && !s.startsWith((color.getChar() + "").toLowerCase())) {
                    p += color;
                    break;
                }
        while (p.contains(color + "" + color))
            p = p.replace(color + "" + color, color.toString());
        return p;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public Location getSpawn() {
        return spawn == null ? manager == null ? null : manager.getSpawn().getSpawn() : spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public List<FKPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<FKPlayer> players) {
        this.players = players;
    }

    public FKPlayer getPlayer(UUID uuid) {
        if (players != null)
            for (FKPlayer player : players)
                if (player.getUuid().equals(uuid))
                    return player;
        return null;
    }

    public FKPlayer getPlayer(@Nonnull String name) {
        if (players != null)
            for (FKPlayer player : players)
                if (name.equalsIgnoreCase(player.getName()))
                    return player;
        return null;
    }

    /**
     * @deprecated This method is deprecated. Use {@link FKPlayer#setTeam(FKTeam)}  instead.
     */
    @Deprecated
    public void addPlayer(FKPlayer player) {
        player.setTeam(this);
    }

    /**
     * @deprecated This method is deprecated. Use {@link FKPlayer#leaveTeam()} instead.
     */
    @Deprecated
    public void removePlayer(FKPlayer player) {
        player.leaveTeam();
    }

    public Team getScoreboardTeam() {
        return scoreboardTeam;
    }

    /**
     * @deprecated You may not use this method.
     */
    @Deprecated
    public void setScoreboardTeam(Team scoreboardTeam) {
        this.scoreboardTeam = scoreboardTeam;
    }

    public FKAuth getAuthorizations() {
        return authorizations;
    }

    public void setAuthorizations(FKAuth authorizations) {
        this.authorizations = authorizations;
    }
}
