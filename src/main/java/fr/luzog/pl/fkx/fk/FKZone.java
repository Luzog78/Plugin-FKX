package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.utils.Config;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Location;

public class FKZone {

    public static enum Type {NEUTRAL, FRIENDLY, HOSTILE, LOBBY, SPAWN, ZONE}

    public static final String LOBBY_ID = "lobby", LOBBY_FILE = "Lobby.yml",
            SPAWN_ID = "spawn", SPAWN_FILE = "Spawn.yml";

    public void saveToConfig(String gameId, boolean soft) {
        new Config.Zone("game-" + gameId + "/zones/" + (id.equalsIgnoreCase(LOBBY_ID) ? LOBBY_FILE : id.equalsIgnoreCase(SPAWN_ID) ? SPAWN_FILE : id + ".yml"))
                .load()

                .setType(type == null ? null : type.name(), !soft)
                .setSpawn(spawn, !soft)
                .setPos1(pos1, !soft)
                .setPos2(pos2, !soft)
                .setPermissions(permissions, !soft)

                .save();
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

    public boolean isInside(Location loc){
        return Utils.isInside(loc, pos1, pos2);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public Location getPos1() {
        return pos1;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public FKPermissions getPermissions() {
        return permissions;
    }

    public void setPermissions(FKPermissions permissions) {
        this.permissions = permissions;
    }
}
