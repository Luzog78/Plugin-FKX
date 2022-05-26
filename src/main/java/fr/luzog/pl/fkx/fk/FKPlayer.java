package fr.luzog.pl.fkx.fk;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.UUID;

public class FKPlayer {

    private FKTeam team;

    private UUID uuid;
    private String name, customName;

    private FKAuth personalAuthorizations;

    public FKPlayer(UUID uuid, String name, String customName, @Nullable FKAuth personalAuthorizations) {
        this.uuid = uuid;
        this.name = name;
        this.customName = customName;

        if (personalAuthorizations != null)
            this.personalAuthorizations = personalAuthorizations;
        else
            this.personalAuthorizations = new FKAuth(FKAuth.Definition.DEFAULT);
    }

    public void register(FKTeam team) {
        this.team = team;
        this.team.addPlayer(this);
    }

    public void leave() {
        team.removePlayer(this);
    }

    public boolean hasAuthorization(FKAuth.Type authorizationType, Location loc) {
        if (personalAuthorizations.getAuthorization(authorizationType) != FKAuth.Definition.DEFAULT)
            return personalAuthorizations.getAuthorization(authorizationType) == FKAuth.Definition.ON;
        if (team.getAuthorizations().getAuthorization(authorizationType) != FKAuth.Definition.DEFAULT)
            return team.getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.ON;
        switch (getZone(loc)) {
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
                    if (zone.isInside(Bukkit.getPlayer(uuid).getLocation()))
                        if (zone.getAuthorizations().getAuthorization(authorizationType) == FKAuth.Definition.DEFAULT)
                            continue;
                        else
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
                if (getManager().getNeutral().getAuthorization(authorizationType) == FKAuth.Definition.DEFAULT)
                    break;
                return getManager().getNeutral().getAuthorization(authorizationType) == FKAuth.Definition.ON;

            default:
                break;
        }
        return getManager().getGlobals().getAuthorization(authorizationType) == FKAuth.Definition.ON;
    }

    public FKZone.Type getZone(){
        if (!Bukkit.getOfflinePlayer(uuid).isOnline())
            return null;
        return getZone(Bukkit.getPlayer(uuid).getLocation());
    }

    public FKZone.Type getZone(Location loc) {
        if (getManager().getLobby().isInside(loc))
            return getManager().getLobby().getType();
        if (getManager().getSpawn().isInside(loc))
            return getManager().getSpawn().getType();
        for (FKZone zone : getManager().getZones())
            if (zone.isInside(loc))
                return zone.getType();
        if (team.isInside(loc))
            return FKZone.Type.FRIENDLY;
        for (FKTeam team : getManager().getTeams())
            if (team.isInside(loc))
                return FKZone.Type.HOSTILE;
        return FKZone.Type.NEUTRAL;
    }

    public FKManager getManager() {
        return team.getManager();
    }

    public FKTeam getTeam() {
        return team;
    }

    public void setTeam(FKTeam team) {
        this.team = team;
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

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public FKAuth getPersonalAuthorizations() {
        return personalAuthorizations;
    }

    public void setPersonalAuthorizations(FKAuth personalAuthorizations) {
        this.personalAuthorizations = personalAuthorizations;
    }
}
