package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.utils.Config;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Location;

import java.io.File;
import java.util.Objects;

public class FKZone {

    public static enum Type {NEUTRAL, FRIENDLY, HOSTILE, LOBBY, SPAWN, ZONE}

    public static final String LOBBY_ID = "lobby", LOBBY_FILE = "Lobby.yml",
            SPAWN_ID = "spawn", SPAWN_FILE = "Spawn.yml";
    public static double LOBBY_RADIUS = 15, SPAWN_RADIUS = 1, ZONE_RADIUS = 5;

    public void saveToConfig(String gameId, boolean soft) {
        if(soft)
            return;

        getConfig(gameId)
                .load()

                .setType(type == null ? null : type.name(), true)
                .setSpawn(spawn, true)
                .setPos1(pos1, true)
                .setPos2(pos2, true)
                .setPermissions(permissions, true)

                .save();
    }

    public void delete(String gameId) {
        Objects.requireNonNull(FKManager.getGame(gameId)).getNormalZones().removeIf(z -> z.getId().equalsIgnoreCase(id));
        getConfig(gameId).delete();
    }

    public Config.Zone getConfig(String gameId) {
        return new Config.Zone("game-" + gameId + "/zones/" + (id.equalsIgnoreCase(LOBBY_ID) ? LOBBY_FILE : id.equalsIgnoreCase(SPAWN_ID) ? SPAWN_FILE : id + ".yml"));
    }

    private String id;
    private Type type;
    private Location spawn, pos1, pos2;

    private FKPermissions permissions;

    public FKZone(String id, Type type, Location spawn, Location pos1, Location pos2, FKPermissions permissions) {
        this.id = id;
        this.type = type;
        this.spawn = spawn;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "FKZone{" +
                "id='" + id + "'" +
                ", type=" + type +
                ", spawn=" + spawn +
                ", pos1=" + pos1 +
                ", pos2=" + pos2 +
                ", permissions=" + permissions +
                "}";
    }

    public FKManager getManager() {
        for (FKManager manager : FKManager.registered)
            for (FKZone zone : manager.getZones())
                if (zone.equals(this))
                    return manager;
        return null;
    }

    public boolean isInside(Location loc) {
        return Utils.isInside(loc, pos1, pos2);
    }

    public String getId() {
        return id;
    }

    public void setId(String id, boolean save) {
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).getFile().renameTo(new File(getConfig(getManager().getId()).getFile().getParentFile().getPath() + "/" + id + ".yml"));
        }
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type, boolean save) {
        this.type = type;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setType(type.name(), true).save();
        }
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn, boolean save) {
        this.spawn = spawn;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setSpawn(spawn, true).save();
        }
    }

    public Location getPos1() {
        return pos1;
    }

    public void setPos1(Location pos1, boolean save) {
        this.pos1 = pos1;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setPos1(pos1, true).save();
        }
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos2(Location pos2, boolean save) {
        this.pos2 = pos2;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setPos2(pos2, true).save();
        }
    }

    public FKPermissions getPermissions() {
        return permissions;
    }

    public void setPermissions(FKPermissions permissions, boolean save) {
        this.permissions = permissions;
        if (save)
            savePermissions();
    }

    public void savePermissions() {
        if (getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setPermissions(permissions, true).save();
        }
    }
}
