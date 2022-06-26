package fr.luzog.pl.fkx.utils;

import java.lang.reflect.Field;

public class PlayerStats {

    private int kills, deaths, blocks_broken, blocks_placed, ores_broken, arrows_shot, arrows_hit, picked_items,
            dropped_items, enchanted_items, jumps, chats, inventories_opened, clicks_on_inventory, interactions, connections;
    private double damage_dealt, damage_taken, traveled_distance, regained_food;

    public PlayerStats() {
        this.kills = 0;
        this.deaths = 0;
        this.blocks_broken = 0;
        this.blocks_placed = 0;
        this.ores_broken = 0;
        this.arrows_shot = 0;
        this.arrows_hit = 0;
        this.picked_items = 0;
        this.dropped_items = 0;
        this.enchanted_items = 0;
        this.jumps = 0;
        this.chats = 0;
        this.inventories_opened = 0;
        this.clicks_on_inventory = 0;
        this.interactions = 0;
        this.connections = 0;
        this.damage_dealt = 0;
        this.damage_taken = 0;
        this.traveled_distance = 0;
        this.regained_food = 0;
    }

    public PlayerStats(int kills, int deaths, int blocks_broken, int blocks_placed, int ores_broken, int arrows_shot,
                       int arrows_hit, int picked_items, int dropped_items, int enchanted_items, int jumps, int chats,
                       int inventories_opened, int clicks_on_inventory, int interactions, int connections,
                       double damage_dealt, double damage_taken, double traveled_distance, double regained_food) {
        this.kills = kills;
        this.deaths = deaths;
        this.blocks_broken = blocks_broken;
        this.blocks_placed = blocks_placed;
        this.ores_broken = ores_broken;
        this.arrows_shot = arrows_shot;
        this.arrows_hit = arrows_hit;
        this.picked_items = picked_items;
        this.dropped_items = dropped_items;
        this.enchanted_items = enchanted_items;
        this.jumps = jumps;
        this.chats = chats;
        this.inventories_opened = inventories_opened;
        this.clicks_on_inventory = clicks_on_inventory;
        this.interactions = interactions;
        this.connections = connections;
        this.damage_dealt = damage_dealt;
        this.damage_taken = damage_taken;
        this.traveled_distance = traveled_distance;
        this.regained_food = regained_food;
    }

    public <T> Object get(String namespace, Class<T> clazz) {
        return clazz.cast(get(namespace));
    }

    public Object get(String namespace) {
        if(namespace == null)
            return null;
        for (Field f : this.getClass().getDeclaredFields())
            if (f.isAccessible() && f.getName().equalsIgnoreCase(namespace.replace(" ", "")
                    .replace("_", "").replace("-", "").replace(".", "_")))
                try {
                    return f.get(this);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
        return null;
    }

    public void set(String namespace, Object value) {
        if(namespace == null)
            return;
        for (Field f : this.getClass().getDeclaredFields())
            if (f.isAccessible() && f.getName().equalsIgnoreCase(namespace.replace(" ", "")
                    .replace("_", "").replace("-", "").replace(".", "_")))
                try {
                    f.set(this, f.getClass().cast(value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void increaseKills() {
        this.kills++;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void increaseDeaths() {
        this.deaths++;
    }

    public int getBlocksBroken() {
        return blocks_broken;
    }

    public void setBlocksBroken(int blocks_broken) {
        this.blocks_broken = blocks_broken;
    }

    public void increaseBlocksBroken() {
        this.blocks_broken++;
    }

    public int getBlocksPlaced() {
        return blocks_placed;
    }

    public void setBlocksPlaced(int blocks_placed) {
        this.blocks_placed = blocks_placed;
    }

    public void increaseBlocksPlaced() {
        this.blocks_placed++;
    }

    public int getOres_broken() {
        return ores_broken;
    }

    public void setOresBroken(int oresBroken) {
        this.ores_broken = oresBroken;
    }

    public void increaseOresBroken() {
        this.ores_broken++;
    }

    public int getArrowsShot() {
        return arrows_shot;
    }

    public void setArrowsShot(int arrows_shot) {
        this.arrows_shot = arrows_shot;
    }

    public void increaseArrowsShot() {
        this.arrows_shot++;
    }

    public int getArrowsHit() {
        return arrows_hit;
    }

    public void setArrowsHit(int arrows_hit) {
        this.arrows_hit = arrows_hit;
    }

    public void increaseArrowsHit() {
        this.arrows_hit++;
    }

    public int getPicked_items() {
        return picked_items;
    }

    public void setPickedItems(int pickedItems) {
        this.picked_items = pickedItems;
    }

    public void increasePickedItems() {
        this.picked_items++;
    }

    public int getDroppedItems() {
        return dropped_items;
    }

    public void setDroppedItems(int dropped_items) {
        this.dropped_items = dropped_items;
    }

    public void increaseDroppedItems() {
        this.dropped_items++;
    }

    public int getEnchantedItems() {
        return enchanted_items;
    }

    public void setEnchantedItems(int enchanted_items) {
        this.enchanted_items = enchanted_items;
    }

    public void increaseEnchantedItems() {
        this.enchanted_items++;
    }

    public int getJumps() {
        return jumps;
    }

    public void setJumps(int jumps) {
        this.jumps = jumps;
    }

    public void increaseJumps() {
        this.jumps++;
    }

    public int getChats() {
        return chats;
    }

    public void setChats(int chats) {
        this.chats = chats;
    }

    public void increaseChats() {
        this.chats++;
    }

    public int getInventories_opened() {
        return inventories_opened;
    }

    public void setInventoriesOpened(int inventoriesOpened) {
        this.inventories_opened = inventoriesOpened;
    }

    public void increaseInventoriesOpened() {
        this.inventories_opened++;
    }

    public int getClicksOnInventory() {
        return clicks_on_inventory;
    }

    public void setClicksOnInventory(int clicks_on_inventory) {
        this.clicks_on_inventory = clicks_on_inventory;
    }

    public void increaseClicksOnInventory() {
        this.clicks_on_inventory++;
    }

    public int getInteractions() {
        return interactions;
    }

    public void setInteractions(int interactions) {
        this.interactions = interactions;
    }

    public void increaseInteractions() {
        this.interactions++;
    }

    public int getConnections() {
        return connections;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

    public void increaseConnections() {
        this.connections++;
    }

    public double getDamageDealt() {
        return damage_dealt;
    }

    public void setDamageDealt(double damage_dealt) {
        this.damage_dealt = damage_dealt;
    }

    public void increaseDamageDealt(double damageDealt) {
        this.damage_dealt += damageDealt;
    }

    public double getDamageTaken() {
        return damage_taken;
    }

    public void setDamageTaken(double damage_taken) {
        this.damage_taken = damage_taken;
    }

    public void increaseDamageTaken(double damageTaken) {
        this.damage_taken += damageTaken;
    }

    public double getTraveled_distance() {
        return traveled_distance;
    }

    public void setTraveledDistance(double traveledDistance) {
        this.traveled_distance = traveledDistance;
    }

    public void increaseTraveledDistance(double traveledDistance) {
        this.traveled_distance += traveledDistance;
    }

    public double getRegained_food() {
        return regained_food;
    }

    public void setRegainedFood(double regainedFood) {
        this.regained_food = regainedFood;
    }

    public void increaseRegainedFood(double regainedFood) {
        this.regained_food += regainedFood;
    }

}
