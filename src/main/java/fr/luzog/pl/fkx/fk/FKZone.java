package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Location;

public class FKZone {

    public static enum Type {NEUTRAL, FRIENDLY, HOSTILE, LOBBY, SPAWN, ZONE}

    private FKManager manager;

    private String id;
    private Type type;
    private Location spawn, pos1, pos2;

    private FKAuth authorizations;

    public FKZone(String id, Type type, Location spawn, Location pos1, Location pos2, FKAuth authorizations) {
        this.id = id;
        this.type = type;
        this.spawn = spawn;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.authorizations = authorizations;
    }

    public boolean isInside(Location loc){
        return Utils.isInside(loc, pos1, pos2);
    }

    public FKManager getManager() {
        return manager;
    }

    public void setManager(FKManager manager) {
        this.manager = manager;
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

    public FKAuth getAuthorizations() {
        return authorizations;
    }

    public void setAuthorizations(FKAuth authorizations) {
        this.authorizations = authorizations;
    }
}
