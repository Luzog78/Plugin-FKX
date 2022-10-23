package fr.luzog.pl.fkx.game;

import fr.luzog.pl.fkx.commands.Admin.Vanish;
import fr.luzog.pl.fkx.utils.Config;
import fr.luzog.pl.fkx.utils.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Objects;
import java.util.UUID;

public class GPlayer {

    public void saveToConfig(String gameId, boolean soft) {
        if (soft)
            return;

        getConfig(gameId)
                .load()

                .setLastUuid(lastUuid, true)
                .setTeam(teamId, true)
                .setCompass(compass, true)
                .setStats(stats, true)
                .setPermissions(personalPermissions, true)

                .save();
    }

    public Config.Player getConfig(String gameId) {
        return new Config.Player("game-" + Objects.requireNonNull(gameId) + "/players/" + name + ".yml");
    }

    public static class Compass {
        private String name;
        private double radius;
        private Location location;

        public Compass(String name, double radius, Location location) {
            this.name = name;
            this.radius = radius;
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getRadius() {
            return radius;
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }

    private String name;
    private UUID lastUuid;

    private String teamId;
    private Compass compass;

    private PlayerStats stats;

    private GPermissions personalPermissions;

    public GPlayer(@Nullable String name, @Nullable PlayerStats stats, @Nullable GPermissions personalPermissions) {
        this.name = name;
        this.lastUuid = null;

        this.teamId = null;
        this.compass = null;

        this.stats = stats == null ? new PlayerStats() : stats;
        this.personalPermissions = personalPermissions == null ? new GPermissions(GPermissions.Definition.DEFAULT) : personalPermissions;
    }

    public GPlayer(String name, UUID lastUuid, String teamId, Compass compass, PlayerStats stats, GPermissions personalPermissions) {
        this.name = name;
        this.lastUuid = lastUuid;

        this.teamId = teamId;
        this.compass = compass;

        this.stats = stats == null ? new PlayerStats() : stats;
        this.personalPermissions = personalPermissions == null ? new GPermissions(GPermissions.Definition.DEFAULT) : personalPermissions;
    }

    public Player getPlayer() {
        return Bukkit.getPlayerExact(name);
    }

    public boolean hasPermission(GPermissions.Type permissionType, Location loc) {
        return hasPermission(permissionType, loc, 0);
    }

    public boolean hasPermission(GPermissions.Type permissionType, Location loc, int incrementTeamRadius) {
        if (personalPermissions.getPermission(permissionType) != GPermissions.Definition.DEFAULT)
            return personalPermissions.getPermission(permissionType) == GPermissions.Definition.ON;
        if (getTeam() != null && getTeam().getPermissions().getPermission(permissionType) != GPermissions.Definition.DEFAULT)
            return getTeam().getPermissions().getPermission(permissionType) == GPermissions.Definition.ON;
        if (getManager().getPriority().getPermission(permissionType) != GPermissions.Definition.DEFAULT)
            return getManager().getPriority().getPermission(permissionType) == GPermissions.Definition.ON;
        if (getZone(loc, incrementTeamRadius) != null)
            switch (getZone(loc, incrementTeamRadius).getType()) {
                case LOBBY:
                    if (getManager().getLobby().getPermissions().getPermission(permissionType) == GPermissions.Definition.DEFAULT)
                        break;
                    return getManager().getLobby().getPermissions().getPermission(permissionType) == GPermissions.Definition.ON;

                case SPAWN:
                    if (getManager().getSpawn().getPermissions().getPermission(permissionType) == GPermissions.Definition.DEFAULT)
                        break;
                    return getManager().getSpawn().getPermissions().getPermission(permissionType) == GPermissions.Definition.ON;

                case ZONE:
                    for (GZone zone : getManager().getNormalZones())
                        if (zone.isInside(loc))
                            if (zone.getPermissions().getPermission(permissionType) != GPermissions.Definition.DEFAULT)
                                return zone.getPermissions().getPermission(permissionType) == GPermissions.Definition.ON;
                    break;

                case FRIENDLY:
                    if (getManager().getFriendly().getPermission(permissionType) == GPermissions.Definition.DEFAULT)
                        break;
                    return getManager().getFriendly().getPermission(permissionType) == GPermissions.Definition.ON;

                case HOSTILE:
                    if (getManager().getHostile().getPermission(permissionType) == GPermissions.Definition.DEFAULT)
                        break;
                    return getManager().getHostile().getPermission(permissionType) == GPermissions.Definition.ON;

                case NEUTRAL:
                default:
                    break;
            }
        if (getManager().getNeutral().getPermission(permissionType) != GPermissions.Definition.DEFAULT)
            return getManager().getNeutral().getPermission(permissionType) == GPermissions.Definition.ON;
        return getManager().getGlobal().getPermission(permissionType) == GPermissions.Definition.ON;
    }

    public GZone getZone() {
        if (!Bukkit.getOfflinePlayer(name).isOnline())
            return null;
        return getZone(Bukkit.getPlayerExact(name).getLocation());
    }

    public GZone getZone(Location loc) {
        return getZone(loc, 0);
    }

    public GZone getZone(Location loc, int incrementTeamRadius) {
        if (getManager().getLobby().isInside(loc))
            return getManager().getLobby();
        if (getManager().getSpawn().isInside(loc))
            return getManager().getSpawn();
        if (getTeam() != null && getTeam().isInside(loc, incrementTeamRadius))
            return getTeam().getZone(true, incrementTeamRadius);
        for (GTeam team : getManager().getTeams())
            if (team.isInside(loc, incrementTeamRadius))
                return team.getZone(false, incrementTeamRadius);
        for (GZone zone : getManager().getNormalZones())
            if (zone.isInside(loc))
                return zone;
        return null;
    }

    public GManager getManager() {
        for (GManager manager : GManager.registered)
            if (manager.getPlayers().contains(this))
                return manager;
        return null;
    }

    public String getTeamId() {
        return teamId;
    }

    public GTeam getTeam() {
        return getManager() == null ? null : getManager().getTeam(teamId);
    }

    public void setTeam(String teamId, boolean save) {
        if (this.teamId == null && teamId == null)
            return;
        if (getTeam() != null) {
            leaveTeam(false);
            return;
        }
        this.teamId = teamId;
        getTeam().updatePlayers();
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setTeam(teamId, true).save();
        }
    }

    public void leaveTeam(boolean save) {
        GTeam tempTeam = getTeam();
        teamId = null;
        if (tempTeam != null)
            tempTeam.updatePlayers();
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setTeam(null, true).save();
        }
    }

    public Compass getCompass() {
        return compass;
    }

    public void setCompass(Compass compass, boolean save) {
        this.compass = compass;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setCompass(compass, true).save();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name, boolean renameFile) {
        if (renameFile && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).getFile().renameTo(new File(getConfig(getManager().getId()).getFile().getParentFile().getPath() + "/" + name + ".yml"));
        }
        this.name = name;
    }

    public UUID getLastUuid() {
        return lastUuid;
    }

    public void setLastUuid(UUID lastUuid, boolean save) {
        this.lastUuid = lastUuid;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setLastUuid(lastUuid, true).save();
        }
    }

    public String getDisplayName() {
        return (Vanish.isVanished(name) && Vanish.isPrefix ? Vanish.pre_suf_ix : "")
                + (getTeam() != null ? getTeam().getPrefix() : "")
                + name
                + (Vanish.isVanished(name) && !Vanish.isPrefix ? Vanish.pre_suf_ix : "")
                + "§r";
    }

    public PlayerStats getStats() {
        return stats;
    }

    public void setStats(PlayerStats stats, boolean save) {
        this.stats = stats;
        if (save)
            saveStats();
    }

    public void saveStats() {
        if (getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setStats(stats, true).save();
        }
    }

    public GPermissions getPersonalPermissions() {
        return personalPermissions;
    }

    public void setPersonalPermissions(GPermissions personalPermissions, boolean save) {
        this.personalPermissions = personalPermissions;
        if (save)
            savePersonalPermissions();
    }

    public void savePersonalPermissions() {
        if (getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setPermissions(personalPermissions, true).save();
        }
    }
}
