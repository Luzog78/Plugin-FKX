package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.commands.Admin.Vanish;
import fr.luzog.pl.fkx.utils.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public class FKPlayer {

    private UUID uuid;
    private String name;

    private String teamId;

    private PlayerStats stats;

    private FKAuth personalAuthorizations;

    public FKPlayer(@Nullable UUID uuid, @Nullable String name, @Nullable PlayerStats stats, @Nullable FKAuth personalAuthorizations) {
        this.uuid = uuid;
        this.name = name;

        this.stats = stats == null ? new PlayerStats() : stats;
        this.personalAuthorizations = personalAuthorizations == null ? new FKAuth(FKAuth.Definition.DEFAULT) : personalAuthorizations;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid) == null ? Bukkit.getPlayerExact(name) : Bukkit.getPlayer(uuid);
    }

    public boolean hasAuthorization(FKAuth.Type authorizationType, Location loc) {
        if (personalAuthorizations.getAuthorization(authorizationType) != FKAuth.Definition.DEFAULT)
            return personalAuthorizations.getAuthorization(authorizationType) == FKAuth.Definition.ON;
        if (getTeam().getAuthorizations().getAuthorization(authorizationType) != FKAuth.Definition.DEFAULT)
            return getTeam().getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.ON;
        if (getManager().getPriority().getAuthorization(authorizationType) != FKAuth.Definition.DEFAULT)
            return getManager().getPriority().getAuthorization(authorizationType) == FKAuth.Definition.ON;
        if (getZone(loc) != null)
            switch (getZone(loc).getType()) {
                case LOBBY:
                    if (getManager().getLobby().getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.DEFAULT)
                        break;
                    return getManager().getLobby().getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.ON;

                case SPAWN:
                    if (getManager().getSpawn().getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.DEFAULT)
                        break;
                    return getManager().getSpawn().getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.ON;

                case ZONE:
                    for (FKZone zone : getManager().getNormalZones())
                        if (zone.isInside(loc))
                            if (zone.getAuthorizations().getAuthorization(authorizationType) != FKAuth.Definition.DEFAULT)
                                return zone.getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.ON;
                    break;

                case FRIENDLY:
                    if (getManager().getFriendly().getAuthorization(authorizationType) == FKAuth.Definition.DEFAULT)
                        break;
                    return getManager().getFriendly().getAuthorization(authorizationType) == FKAuth.Definition.ON;

                case HOSTILE:
                    if (getManager().getHostile().getAuthorization(authorizationType) == FKAuth.Definition.DEFAULT)
                        break;
                    return getManager().getHostile().getAuthorization(authorizationType) == FKAuth.Definition.ON;

                case NEUTRAL:
                default:
                    break;
            }
        if (getManager().getNeutral().getAuthorization(authorizationType) != FKAuth.Definition.DEFAULT)
            return getManager().getNeutral().getAuthorization(authorizationType) == FKAuth.Definition.ON;
        return getManager().getGlobals().getAuthorization(authorizationType) == FKAuth.Definition.ON;
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

    /**
     * @deprecated You may not use this method.
     */
    @Deprecated
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

    public FKAuth getPersonalAuthorizations() {
        return personalAuthorizations;
    }

    public void setPersonalAuthorizations(FKAuth personalAuthorizations) {
        this.personalAuthorizations = personalAuthorizations;
    }
}
