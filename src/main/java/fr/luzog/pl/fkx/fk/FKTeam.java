package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.UUID;

public class FKTeam {

    public static final String GODS_ID = "gods", SPECS_ID = "specs";

    private String id, name, prefix;
    private ChatColor color;
    private Location spawn;
    private double radius;

    private Team scoreboardTeam;

    private FKPermissions permissions;

    public FKTeam(String id) {
        this.id = id;
        this.name = id;
        this.prefix = id + " ";
        this.color = ChatColor.WHITE;
        this.spawn = null;
        this.radius = 0;
        this.permissions = new FKPermissions(FKPermissions.Definition.DEFAULT);
        scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("fkt" + UUID.randomUUID().toString().substring(0, 4) + ":" + id);
        updateParams();
    }

    public FKTeam(String id, String name, String prefix, ChatColor color, Location spawn, double radius, FKPermissions permissions) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        this.spawn = spawn;
        this.radius = radius;
        this.permissions = permissions;
        scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("fkt" + UUID.randomUUID().toString().substring(0, 4) + ":" + id);
        updateParams();
    }

    public boolean isInside(Location loc) {
        Location l1 = getSpawn().clone(), l2 = getSpawn().clone();
        l1.setY(-1);
        l2.setY(256);
        return Utils.isInside(loc, l1.add(radius, 0, radius), l2.subtract(radius, 0, radius));
    }

    public FKZone getZone(boolean friendly) {
        return new FKZone(getId(), friendly ? FKZone.Type.FRIENDLY : FKZone.Type.HOSTILE, getSpawn().clone(),
                getSpawn().clone().subtract(radius, radius, radius), getSpawn().clone().add(radius, radius, radius), getPermissions());
    }

    public FKManager getManager() {
        for (FKManager manager : FKManager.registered)
            for (FKTeam team : manager.getTeams())
                if (team.equals(this))
                    return manager;
        return null;
    }

    public void updatePlayers() {
        scoreboardTeam.getPlayers().forEach(p -> scoreboardTeam.removePlayer(p));
        scoreboardTeam.getEntries().forEach(e -> scoreboardTeam.addEntry(e));
        getPlayers().forEach(p -> {
            if (p.getName() != null)
                scoreboardTeam.addPlayer(Bukkit.getOfflinePlayer(p.getName()));
        });
    }

    public void updateParams() {
        scoreboardTeam.setDisplayName(getName());
        scoreboardTeam.setPrefix(getPrefix());
        scoreboardTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
        scoreboardTeam.setCanSeeFriendlyInvisibles(false);
        scoreboardTeam.setAllowFriendlyFire(true);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (getManager() != null)
            if (getManager().getTeam(id) != null && !getManager().getTeam(id).equals(this))
                throw new FKException.DuplicateTeamIdException(id, getManager().getId(), getManager().getId());
        this.id = id;
    }

    public String getName() {
        return color + name.replace("§r", color.toString()) + color + "§r";
    }

    public void setName(String name) {
        this.name = name;
        updateParams();
    }

    public String getPrefix() {
        String p = color + prefix.replace("§r", color.toString());
        if (prefix.contains("§"))
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
        updateParams();
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
        updateParams();
    }

    public Location getSpawn() {
        return spawn == null ? getManager() == null ? null : getManager().getSpawn().getSpawn() : spawn;
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

    public ArrayList<FKPlayer> getPlayers() {
        ArrayList<FKPlayer> ps = new ArrayList<>();
        if(getManager() != null)
            ps.addAll(getManager().getPlayers());
        ps.removeIf(p -> p.getTeam() == null || !p.getTeamId().equals(id));
        return ps;
    }

    public FKPlayer getPlayer(UUID uuid) {
        for (FKPlayer player : getPlayers())
            if (player.getUuid().equals(uuid))
                return player;
        return null;
    }

    public FKPlayer getPlayer(@Nonnull String name) {
        for (FKPlayer player : getPlayers())
            if (name.equalsIgnoreCase(player.getName()))
                return player;
        return null;
    }

    /**
     * @throws FKException.PlayerAlreadyInTeamException
     * @deprecated This method is deprecated. Use {@link FKPlayer#setTeam(String)} instead.
     */
    @Deprecated
    public void addPlayer(FKPlayer player) {
        if (player.getTeam() == null)
            player.setTeam(id);
        else
            throw new FKException.PlayerAlreadyInTeamException(id, player.getUuid(), player.getName());
    }

    /**
     * @throws FKException.PlayerNotInTeamException
     * @deprecated This method is deprecated. Use {@link FKPlayer#leaveTeam()} instead.
     */
    @Deprecated
    public void removePlayer(FKPlayer player) {
        if (player.getTeam() == this)
            player.leaveTeam();
        else
            throw new FKException.PlayerNotInTeamException(id, player.getUuid(), player.getName());
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

    public FKPermissions getPermissions() {
        return permissions;
    }

    public void setPermissions(FKPermissions permissions) {
        this.permissions = permissions;
    }
}
