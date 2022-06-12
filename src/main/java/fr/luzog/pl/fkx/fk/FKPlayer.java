package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.commands.Admin.Vanish;
import fr.luzog.pl.fkx.utils.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.UUID;

public class FKPlayer {

    private FKTeam team;

    private UUID uuid;
    private String name;

    private PlayerStats stats;

    private FKAuth personalAuthorizations;

    public FKPlayer(UUID uuid, String name, @Nullable PlayerStats stats, @Nullable FKAuth personalAuthorizations) {
        this.uuid = uuid;
        this.name = name;

        this.stats = stats == null ? new PlayerStats() : stats;
        this.personalAuthorizations = personalAuthorizations == null ? new FKAuth(FKAuth.Definition.DEFAULT) : personalAuthorizations;
    }

    public boolean hasAuthorization(FKAuth.Type authorizationType, Location loc) {
        if (personalAuthorizations.getAuthorization(authorizationType) != FKAuth.Definition.DEFAULT)
            return personalAuthorizations.getAuthorization(authorizationType) == FKAuth.Definition.ON;
        if (team.getAuthorizations().getAuthorization(authorizationType) != FKAuth.Definition.DEFAULT)
            return team.getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.ON;
        if (getZone(loc) != null)
            switch (getZone(loc).getType()) {
                case LOBBY:
                    if (getManager().getLobby().getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.DEFAULT)
                        break;
                    return getManager().getLobby().getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.ON;

                case SPAWN:
                    if (getManager().getLobby().getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.DEFAULT)
                        break;
                    return getManager().getSpawn().getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.ON;

                case ZONE:
                    for (FKZone zone : getManager().getZones())
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
        for (FKZone zone : getManager().getZones())
            if (zone.isInside(loc))
                return zone;
        if (team.isInside(loc))
            return team.getZone(true);
        for (FKTeam team : getManager().getTeams())
            if (team.isInside(loc))
                return team.getZone(false);
        return null;
    }

    public FKManager getManager() {
        return team.getManager();
    }

    public FKTeam getTeam() {
        return team;
    }

    public void setTeam(FKTeam team) {
        if (team == null) {
            leaveTeam();
            return;
        }
        this.team = team;
        if (team.getPlayer(uuid) == null)
            team.getPlayers().add(this);
        team.updatePlayers();
    }

    public void leaveTeam() {
        if (team != null)
            team.getPlayers().remove(this);
        team = null;
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
                + (team != null ? team.getPrefix() : "")
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
