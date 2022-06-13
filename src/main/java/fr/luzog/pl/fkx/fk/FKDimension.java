package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.*;

public class FKDimension {

    private String name;
    private boolean opened;
    private long coolDown;

    private Location overSpawn, overPortal1, overPortal2, dimSpawn, dimPortal1, dimPortal2;

    private Material openedMat, closedMat;
    private byte data;

    private Map<UUID, Integer> inTeleportation;

    public FKDimension(String name, Location overSpawn, Location overPortal1, Location overPortal2,
                       Location dimSpawn, Location dimPortal1, Location dimPortal2, Material openedMat,
                       Material closedMat, byte data, long coolDown, boolean opened) {
        this.name = name;
        this.coolDown = coolDown;
        this.overSpawn = overSpawn;
        this.overPortal1 = overPortal1;
        this.overPortal2 = overPortal2;
        this.dimSpawn = dimSpawn;
        this.dimPortal1 = dimPortal1;
        this.dimPortal2 = dimPortal2;
        this.openedMat = openedMat;
        this.closedMat = closedMat;
        this.data = data;

        inTeleportation = new HashMap<>();

        setOpened(opened);
    }

    public void open() {
        opened = true;
        if(openedMat == null)
            return;
        Utils.getBlocksIn(overPortal1, overPortal2).forEach(block -> block.setTypeIdAndData(openedMat.getId(), data, false));
        Utils.getBlocksIn(dimPortal1, dimPortal2).forEach(block -> block.setTypeIdAndData(openedMat.getId(), data, false));
    }

    public void close() {
        opened = false;
        if(openedMat == null)
            return;
        Utils.getBlocksIn(overPortal1, overPortal2).forEach(block -> block.setTypeIdAndData(closedMat.getId(), data, false));
        Utils.getBlocksIn(dimPortal1, dimPortal2).forEach(block -> block.setTypeIdAndData(closedMat.getId(), data, false));
    }

    public Location nextSpawn(Location loc) {
        if (loc == null)
            return null;
        if(overPortal1 != null && overPortal2 != null && loc.getWorld().getUID().equals(overPortal1.getWorld().getUID()) && loc.getWorld().getUID().equals(overPortal2.getWorld().getUID()))
            for(Block b : Utils.getBlocksIn(overPortal1, overPortal2))
                if(loc.distance(Utils.normalize(b.getLocation(), false)) < 0.95)
                    return dimSpawn;
        if(dimPortal1 != null && dimPortal2 != null && loc.getWorld().getUID().equals(dimPortal1.getWorld().getUID()) && loc.getWorld().getUID().equals(dimPortal2.getWorld().getUID()))
            for(Block b : Utils.getBlocksIn(dimPortal1, dimPortal2))
                if(loc.distance(Utils.normalize(b.getLocation(), false)) < 0.95)
                    return overSpawn;
        return null;
    }

    public boolean tryToTeleport(Entity e) {
        if(!isOpened() || nextSpawn(e.getLocation()) == null || isInTeleportation(e))
            return false;
        inTeleportation.put(e.getUniqueId(), Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
            if (nextSpawn(e.getLocation()) != null)
                e.teleport(nextSpawn(e.getLocation()));
            inTeleportation.remove(e.getUniqueId());
        }, coolDown));
        return true;
    }

    public boolean isInTeleportation(UUID uuid) {
        return inTeleportation.containsKey(uuid);
    }

    public boolean isInTeleportation(Entity entity) {
        return inTeleportation.containsKey(entity.getUniqueId());
    }

    public void resetTeleportation() {
        inTeleportation.forEach((uuid, taskID) -> Bukkit.getScheduler().cancelTask(taskID));
        inTeleportation.clear();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        if(opened && !this.opened)
            open();
        else if(!opened && this.opened)
            close();
    }

    public long getCoolDown() {
        return coolDown;
    }

    public void setCoolDown(long coolDown) {
        this.coolDown = coolDown;
    }

    public Location getOverSpawn() {
        return overSpawn;
    }

    public void setOverSpawn(Location overSpawn) {
        this.overSpawn = overSpawn;
    }

    public Location getOverPortal1() {
        return overPortal1;
    }

    public void setOverPortal1(Location overPortal1) {
        this.overPortal1 = overPortal1;
    }

    public Location getOverPortal2() {
        return overPortal2;
    }

    public void setOverPortal2(Location overPortal2) {
        this.overPortal2 = overPortal2;
    }

    public Location getDimSpawn() {
        return dimSpawn;
    }

    public void setDimSpawn(Location dimSpawn) {
        this.dimSpawn = dimSpawn;
    }

    public Location getDimPortal1() {
        return dimPortal1;
    }

    public void setDimPortal1(Location dimPortal1) {
        this.dimPortal1 = dimPortal1;
    }

    public Location getDimPortal2() {
        return dimPortal2;
    }

    public void setDimPortal2(Location dimPortal2) {
        this.dimPortal2 = dimPortal2;
    }

    public Material getOpenedMat() {
        return openedMat;
    }

    public void setOpenedMat(Material openedMat) {
        this.openedMat = openedMat;
    }

    public Material getClosedMat() {
        return closedMat;
    }

    public void setClosedMat(Material closedMat) {
        this.closedMat = closedMat;
    }

    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }

    public Map<UUID, Integer> getInTeleportationMap() {
        return inTeleportation;
    }

    public void setInTeleportationMap(Map<UUID, Integer> inTeleportation) {
        this.inTeleportation = inTeleportation;
    }

    public List<UUID> getInTeleportation() {
        return new ArrayList<>(inTeleportation.keySet());
    }

}
