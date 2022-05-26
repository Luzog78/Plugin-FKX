package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FKTeam {

    private FKManager manager;

    private String id, name, prefix;
    private ChatColor color;
    private Location spawn;
    private int radius;

    private List<FKPlayer> players;

    private Team scoreboardTeam;

    private FKAuth authorizations;

    public FKTeam(String id, String name, String prefix, ChatColor color, Location spawn, int radius, FKAuth authorizations, @Nullable List<FKPlayer> players) {
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

    public boolean isInside(Location loc){
        Location l1 = spawn.clone(), l2 = spawn.clone();
        l1.setY(0);
        l2.setY(255);
        return Utils.isInside(loc, l1.add(radius, 0, radius), l2.subtract(radius, 0, radius));
    }

    public FKPlayer getPlayer(UUID uuid) {
        for (FKPlayer player : players)
            if (player.getUuid().equals(uuid))
                return player;
        return null;
    }

    public void addPlayer(FKPlayer player) {
        if (getPlayer(player.getUuid()) == null)
            players.add(player);
    }

    public void removePlayer(FKPlayer player) {
        if (getPlayer(player.getUuid()) != null)
            players.remove(player);
    }

    public FKManager getManager() {
        return manager;
    }

    public void setManager(FKManager manager) {
        if (this.manager != null)
            if (manager.getMainScoreboard().getTeam(this.id) != null)
                manager.getMainScoreboard().getTeam(this.id).unregister();
        this.manager = manager;
        this.scoreboardTeam = this.manager.getMainScoreboard().registerNewTeam(this.id);
        this.scoreboardTeam.setDisplayName(this.name);
        this.scoreboardTeam.setPrefix(this.prefix);
        this.players.forEach(p -> this.scoreboardTeam.addEntry(p.getName()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return color + name.replace("§r", color.toString()) + color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return color + prefix.replace("§r", color.toString()) + color;
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
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public List<FKPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<FKPlayer> players) {
        this.players = players;
    }

    public Team getScoreboardTeam() {
        return scoreboardTeam;
    }

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
