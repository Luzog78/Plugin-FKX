package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.commands.Admin.Vanish;
import fr.luzog.pl.fkx.utils.Config;
import fr.luzog.pl.fkx.utils.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public class FKPlayer {

    public void saveToConfig(String gameId, boolean soft) {
        new Config.Player("game-" + gameId + "/players/" + (uuid == null ? "null-" + UUID.randomUUID().toString().substring(0, 8) + ".yml" : uuid + ".yml"))
                .load()

                .setName(name, !soft)
                .setTeam(teamId, !soft)
                .setStats(stats, !soft)
                .setPermissions(personalPermissions, !soft)

                .save();
    }

    private UUID uuid;
    private String name;

    private String teamId;

    private PlayerStats stats;

    private FKPermissions personalPermissions;

    public FKPlayer(@Nullable UUID uuid, @Nullable String name, @Nullable PlayerStats stats, @Nullable FKPermissions personalPermissions) {
        this.uuid = uuid;
        this.name = name;

        this.stats = stats == null ? new PlayerStats() : stats;
        this.personalPermissions = personalPermissions == null ? new FKPermissions(FKPermissions.Definition.DEFAULT) : personalPermissions;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid) == null ? Bukkit.getPlayerExact(name) : Bukkit.getPlayer(uuid);
    }

    public boolean hasPermission(FKPermissions.Type permissionType, Location loc) {
        if (personalPermissions.getPermission(permissionType) != FKPermissions.Definition.DEFAULT)
            return personalPermissions.getPermission(permissionType) == FKPermissions.Definition.ON;
        if (getTeam().getPermissions().getPermission(permissionType) != FKPermissions.Definition.DEFAULT)
            return getTeam().getPermissions().getPermission(permissionType) == FKPermissions.Definition.ON;
        if (getManager().getPriority().getPermission(permissionType) != FKPermissions.Definition.DEFAULT)
            return getManager().getPriority().getPermission(permissionType) == FKPermissions.Definition.ON;
        if (getZone(loc) != null)
            switch (getZone(loc).getType()) {
                case LOBBY:
                    if (getManager().getLobby().getPermissions().getPermission(permissionType) == FKPermissions.Definition.DEFAULT)
                        break;
                    return getManager().getLobby().getPermissions().getPermission(permissionType) == FKPermissions.Definition.ON;

                case SPAWN:
                    if (getManager().getSpawn().getPermissions().getPermission(permissionType) == FKPermissions.Definition.DEFAULT)
                        break;
                    return getManager().getSpawn().getPermissions().getPermission(permissionType) == FKPermissions.Definition.ON;

                case ZONE:
                    for (FKZone zone : getManager().getNormalZones())
                        if (zone.isInside(loc))
                            if (zone.getPermissions().getPermission(permissionType) != FKPermissions.Definition.DEFAULT)
                                return zone.getPermissions().getPermission(permissionType) == FKPermissions.Definition.ON;
                    break;

                case FRIENDLY:
                    if (getManager().getFriendly().getPermission(permissionType) == FKPermissions.Definition.DEFAULT)
                        break;
                    return getManager().getFriendly().getPermission(permissionType) == FKPermissions.Definition.ON;

                case HOSTILE:
                    if (getManager().getHostile().getPermission(permissionType) == FKPermissions.Definition.DEFAULT)
                        break;
                    return getManager().getHostile().getPermission(permissionType) == FKPermissions.Definition.ON;

                case NEUTRAL:
                default:
                    break;
            }
        if (getManager().getNeutral().getPermission(permissionType) != FKPermissions.Definition.DEFAULT)
            return getManager().getNeutral().getPermission(permissionType) == FKPermissions.Definition.ON;
        return getManager().getGlobal().getPermission(permissionType) == FKPermissions.Definition.ON;
    }

    public FKZone getZone() {
        if (!Bukkit.getOfflinePlayer(uuid).isOnline())
            return null;
        return getZone(Bukkit.getPlayer(uuid).getLocation());
    }

    public FKZone getZone(Location loc) {
        if (getManager().getLobby().isInside(loc))
            return getManager().getLobby();
        if (getManager().getSpawn().isInside(loc))
            return getManager().getSpawn();
        if (getTeam().isInside(loc))
            return getTeam().getZone(true);
        for (FKTeam team : getManager().getTeams())
            if (team.isInside(loc))
                return team.getZone(false);
        for (FKZone zone : getManager().getNormalZones())
            if (zone.isInside(loc))
                return zone;
        return null;
    }

    public FKManager getManager() {
        for (FKManager manager : FKManager.registered)
            if (manager.getPlayers().contains(this))
                return manager;
        return null;
    }

    public String getTeamId() {
        return teamId;
    }

    public FKTeam getTeam() {
        return getManager() == null ? null : getManager().getTeam(teamId);
    }

    public void setTeam(String teamId) {
        if (getTeam() != null) {
            leaveTeam();
            return;
        }
        this.teamId = teamId;
        getTeam().updatePlayers();
    }

    public void leaveTeam() {
        FKTeam tempTeam = getTeam();
        teamId = null;
        tempTeam.updatePlayers();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return (Vanish.vanished.contains(uuid) && Vanish.isPrefix ? Vanish.pre_suf_ix : "")
                + (getTeam() != null ? getTeam().getPrefix() : "")
                + name
                + (Vanish.vanished.contains(uuid) && !Vanish.isPrefix ? Vanish.pre_suf_ix : "")
                + "§r";
    }

    public PlayerStats getStats() {
        return stats;
    }

    public void setStats(PlayerStats stats) {
        this.stats = stats;
    }

    public FKPermissions getPersonalPermissions() {
        return personalPermissions;
    }

    public void setPersonalPermissions(FKPermissions personalPermissions) {
        this.personalPermissions = personalPermissions;
    }
}
