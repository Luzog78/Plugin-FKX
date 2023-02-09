package fr.luzog.pl.fkx.utils;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.*;
import javafx.util.Pair;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Config {

    public static class Globals extends Config {
        public static final String VERSION = "version", LANG = "lang", SEASON = "season", IP = "ip", ORGA = "orga",
                SPAWN_PROTECTION_DURATION = "spawn-protection-duration",
                OVERWORLD = "worlds.over", NETHER = "worlds.nether", END = "worlds.end",
                CUSTOM_CRAFTS = "custom-options.vanilla-crafts",
                CUSTOM_CRAFTING_TABLE = "custom-options.crafting-table",
                CUSTOM_LOOTING_BLOCKS = "custom-options.block-looting-system",
                CUSTOM_LOOTING_MOBS = "custom-options.mob-looting-system",
                CUSTOM_CREEPER_ACTIVATED = "custom-options.creeper-handler.activated",
                CUSTOM_CREEPER_CHANCE = "custom-options.creeper-handler.supercharged-chance",
                CUSTOM_CREEPER_DROPS_NORMAL = "custom-options.creeper-handler.drop-chances.normal",
                CUSTOM_CREEPER_DROPS_CHARGED = "custom-options.creeper-handler.drop-chances.charged",
                ACTIVATION_AD = "activation.ad", ACTIVATION_COMPASS = "activation.compass",
                LAST_GAME = "last-game", VANISH_PRE_SUF_IX = "vanish.pre-suf-ix", VANISH_IS_PREFIX = "vanish.is-prefix",
                VANISH_PLAYERS = "vanish.players";

        public Globals(@Nonnull String path) {
            super(path, true);
        }

        @Override
        public Globals load() {
            super.load();
            return this;
        }

        @Override
        public Globals save() {
            super.save();
            return this;
        }

        public String getVersion() {
            return super.getStr(VERSION);
        }

        public String getLang() {
            return super.getStr(LANG);
        }

        public String getSeason() {
            return super.getStr(SEASON);
        }

        public String getIp() {
            return super.getStr(IP);
        }

        public List<String> getOrga() {
            return super.getStrList(ORGA);
        }

        public long getSpawnProtectionDuration() {
            return super.getLong(SPAWN_PROTECTION_DURATION);
        }

        public Globals setSpawnProtectionDuration(long spawnProtectionDuration, boolean force) {
            super.set(SPAWN_PROTECTION_DURATION, spawnProtectionDuration, force);
            return this;
        }

        public World getOverworld() {
            try {
                return Bukkit.getWorld(UUID.fromString(super.getStr(OVERWORLD)));
            } catch (IllegalArgumentException e) {
                return Bukkit.getWorld(super.getStr(OVERWORLD));
            }
        }

        public World getNether() {
            try {
                return Bukkit.getWorld(UUID.fromString(super.getStr(NETHER)));
            } catch (IllegalArgumentException e) {
                return Bukkit.getWorld(super.getStr(NETHER));
            }
        }

        public World getEnd() {
            try {
                return Bukkit.getWorld(UUID.fromString(super.getStr(END)));
            } catch (IllegalArgumentException e) {
                return Bukkit.getWorld(super.getStr(END));
            }
        }

        public boolean isCustomVanillaCraftsActivated() {
            return super.getBool(CUSTOM_CRAFTS);
        }

        public Globals setCustomVanillaCraftsActivated(boolean customCrafts, boolean force) {
            super.set(CUSTOM_CRAFTS, customCrafts, force);
            return this;
        }

        public boolean isCustomCraftingTableActivated() {
            return super.getBool(CUSTOM_CRAFTING_TABLE);
        }

        public Globals setCustomCraftingTableActivated(boolean customCraftingTable, boolean force) {
            super.set(CUSTOM_CRAFTING_TABLE, customCraftingTable, force);
            return this;
        }

        public boolean isCustomLootingBlocksSystemActivated() {
            return super.getBool(CUSTOM_LOOTING_BLOCKS);
        }

        public Globals setCustomLootingBlocksSystemActivated(boolean customLootingBlocks, boolean force) {
            super.set(CUSTOM_LOOTING_BLOCKS, customLootingBlocks, force);
            return this;
        }

        public boolean isCustomLootingMobsSystemActivated() {
            return super.getBool(CUSTOM_LOOTING_MOBS);
        }

        public Globals setCustomLootingMobsSystemActivated(boolean customLootingMobs, boolean force) {
            super.set(CUSTOM_LOOTING_MOBS, customLootingMobs, force);
            return this;
        }

        public boolean isCustomCreeperActivated() {
            return super.getBool(CUSTOM_CREEPER_ACTIVATED);
        }

        public Globals setCustomCreeperActivated(boolean customCreeper, boolean force) {
            super.set(CUSTOM_CREEPER_ACTIVATED, customCreeper, force);
            return this;
        }

        public double getCustomCreeperChance() {
            return super.getDouble(CUSTOM_CREEPER_CHANCE);
        }

        public Globals setCustomCreeperChance(double customCreeperChance, boolean force) {
            super.set(CUSTOM_CREEPER_CHANCE, customCreeperChance, force);
            return this;
        }

        public List<Utils.Pair<Material, Double>> getCustomNormalCreeperDrops() {
            List<Utils.Pair<Material, Double>> map = new ArrayList<>();
            super.getMapList(CUSTOM_CREEPER_DROPS_NORMAL).forEach(m -> {
                if(m.containsKey("type")) {
                    Material type = Material.getMaterial(m.get("type") + "");
                    if (type != null)
                        try {
                            map.add(new Utils.Pair<>(type, m.containsKey("chance") ?
                                    Double.parseDouble(m.get("chance") + "") : 1.0));
                        } catch (NumberFormatException ignored) {
                        }
                }
            });
            return map;
        }

        public Globals setCustomNormalCreeperDrops(List<Utils.Pair<Material, Double>> customNormalCreeperDrops, boolean force) {
            List<Map<String, ?>> map = new ArrayList<>();
            customNormalCreeperDrops.forEach(m -> map.add(new HashMap<String, Object>() {{
                put("type", m.getKey().name());
                put("chance", m.getValue());
            }}));
            super.set(CUSTOM_CREEPER_DROPS_NORMAL, map, force);
            return this;
        }

        public List<Utils.Pair<Material, Double>> getCustomChargedCreeperDrops() {
            List<Utils.Pair<Material, Double>> map = new ArrayList<>();
            super.getMapList(CUSTOM_CREEPER_DROPS_CHARGED).forEach(m -> {
                if(m.containsKey("type")) {
                    Material type = Material.getMaterial(m.get("type") + "");
                    if (type != null)
                        try {
                            map.add(new Utils.Pair<>(type, m.containsKey("chance") ?
                                    Double.parseDouble(m.get("chance") + "") : 1.0));
                        } catch (NumberFormatException ignored) {
                        }
                }
            });
            return map;
        }

        public Globals setCustomChargedCreeperDrops(List<Utils.Pair<Material, Double>> customChargedCreeperDrops, boolean force) {
            List<Map<String, ?>> map = new ArrayList<>();
            customChargedCreeperDrops.forEach(m -> map.add(new HashMap<String, Object>() {{
                put("type", m.getKey().name());
                put("chance", m.getValue());
            }}));
            super.set(CUSTOM_CREEPER_DROPS_CHARGED, map, force);
            return this;
        }

        public boolean isAdActivated() {
            return super.getBool(ACTIVATION_AD);
        }

        public Globals setAdActivated(boolean ad, boolean force) {
            super.set(ACTIVATION_AD, ad, force);
            return this;
        }

        public boolean isCompassActivated() {
            return super.getBool(ACTIVATION_COMPASS);
        }

        public Globals setCompassActivated(boolean compass, boolean force) {
            super.set(ACTIVATION_COMPASS, compass, force);
            return this;
        }

        public String getLastGame() {
            return super.getStr(LAST_GAME);
        }

        public String getVanishPreSufIx() {
            return super.getStr(VANISH_PRE_SUF_IX);
        }

        public boolean getVanishIsPrefix() {
            return super.getBool(VANISH_IS_PREFIX);
        }

        public HashSet<String> getVanishPlayers() {
            return new HashSet<>(super.getStrList(VANISH_PLAYERS));
        }

        public Globals setVersion(Object version, boolean force) {
            super.set(VERSION, version, force);
            return this;
        }

        public Globals setLang(String lang, boolean force) {
            super.set(LANG, lang, force);
            return this;
        }

        public Globals setSeason(String season, boolean force) {
            super.set(SEASON, season, force);
            return this;
        }

        public Globals setIp(String ip, boolean force) {
            super.set(IP, ip, force);
            return this;
        }

        public Globals setOrga(List<String> orga, boolean force) {
            super.set(ORGA, orga, force);
            return this;
        }

        public Globals setWorlds(String overworld, String nether, String end, boolean force) {
            super.set(OVERWORLD, overworld, force);
            super.set(NETHER, nether, force);
            super.set(END, end, force);
            return this;
        }

        public Globals setLastGame(String lastGame, boolean force) {
            super.set(LAST_GAME, lastGame, force);
            return this;
        }

        public Globals setVanish(String preSufIx, boolean isPrefix, Collection<String> players, boolean force) {
            super.set(VANISH_PRE_SUF_IX, preSufIx, force);
            super.set(VANISH_IS_PREFIX, isPrefix, force);
            super.set(VANISH_PLAYERS, new ArrayList<>(players), force);
            return this;
        }
    }

    public static class Kit extends Config {
        public static final String INFOS = "info", KITS = "kits";

        public Kit(@Nonnull String path) {
            super(path, true);
        }

        @Override
        public Kit load() {
            super.load();
            return this;
        }

        @Override
        public Kit save() {
            super.save();
            return this;
        }

        @SuppressWarnings("unchecked")
        public List<Map<Object, Object>> getInfos() {
            List<Map<Object, Object>> l = new ArrayList<>();
            for (Map<?, ?> map : super.getMapList(KITS))
                try {
                    l.add((Map<Object, Object>) map);
                } catch (Exception ignored) {
                }
            return l;
        }

        public Kit setInfos(List<Map<Object, Object>> infos, boolean force) {
            super.set(INFOS, infos, force);
            return this;
        }

        @SuppressWarnings("unchecked")
        public List<Utils.SavedInventory> getKits() {
            ArrayList<Utils.SavedInventory> l = new ArrayList<>();
            for (Map<?, ?> map : super.getMapList(KITS))
                try {
                    l.add(Utils.SavedInventory.fromMap((Map<String, Object>) map));
                } catch (Exception ignored) {
                }
            return l;
        }

        public Kit setKits(List<Utils.SavedInventory> kits, boolean force) {
            super.set(KITS, kits.stream().map(Utils.SavedInventory::toMap).collect(Collectors.toList()), force);
            return this;
        }
    }

    public static class Manager extends Config {
        public static final String STATE = "state", DAY = "day", WEATHER = "weather", TIME = "time",
                LINKED_TO_SUN = "linked-to-sun", OPTIONS = "options", OPTION_NAME = "options.%s.name",
                OPTION_ACTIVATION = "options.%s.day", OPTION_ACTIVATED = "options.%s.activated",
                LISTENER_NAME = "listener.objective-name", LISTENER_TIMEOUT = "listener.saving-timeout",
                PORTALS = "portals", PORTAL_NAME = "portals.%s.name", PORTAL_OPENED = "portals.%s.opened",
                PORTAL_COOLDOWN = "portals.%s.cooldown", PORTAL_OPENED_MATERIAL = "portals.%s.materials.opened",
                PORTAL_CLOSED_MATERIAL = "portals.%s.materials.closed",
                PORTAL_MATERIAL_DATA = "portals.%s.materials.data",
                PORTAL_SPAWN_OVER_LOCATION = "portals.%s.locations.over.spawn",
                PORTAL_POS1_OVER_LOCATION = "portals.%s.locations.over.pos1",
                PORTAL_POS2_OVER_LOCATION = "portals.%s.locations.over.pos2",
                PORTAL_SPAWN_DIM_LOCATION = "portals.%s.locations.dim.spawn",
                PORTAL_POS1_DIM_LOCATION = "portals.%s.locations.dim.pos1",
                PORTAL_POS2_DIM_LOCATION = "portals.%s.locations.dim.pos2",
                PORTAL_TELEPORTING = "portals.%s.teleporting",
                PERM_GLOBAL = "permissions.global", PERM_NEUTRAL = "permissions.neutral",
                PERM_FRIENDLY = "permissions.friendly", PERM_HOSTILE = "permissions.hostile",
                PERM_PRIORITY = "permissions.priority";

        public Manager(@Nonnull String path) {
            super(path, true);
        }

        @Override
        public Manager load() {
            super.load();
            return this;
        }

        @Override
        public Manager save() {
            super.save();
            return this;
        }

        public GManager.State getState() {
            return super.match(STATE, GManager.State.values());
        }

        public Manager setState(GManager.State state, boolean force) {
            super.set(STATE, state.name(), force);
            return this;
        }

        public int getDay() {
            return super.getInt(DAY);
        }

        public Manager setDay(int day, boolean force) {
            super.set(DAY, day, force);
            return this;
        }

        public GManager.Weather getCurrentWeather() {
            return super.match(WEATHER, GManager.Weather.values());
        }

        public Manager setCurrentWeather(GManager.Weather weather, boolean force) {
            super.set(WEATHER, weather.name(), force);
            return this;
        }

        public long getTime() {
            return super.getLong(TIME);
        }

        public Manager setTime(long time, boolean force) {
            super.set(TIME, time, force);
            return this;
        }

        public boolean isLinkedToSun() {
            return super.getBool(LINKED_TO_SUN);
        }

        public Manager setLinkedToSun(boolean linkedToSun, boolean force) {
            super.set(LINKED_TO_SUN, linkedToSun, force);
            return this;
        }

        public Set<String> getOptions() {
            return super.getKeys(OPTIONS, false);
        }

        public String getOptionName(String option) {
            return super.getStr(String.format(OPTION_NAME, option));
        }

        public Manager setOptionName(String option, String name, boolean force) {
            super.set(String.format(OPTION_NAME, option), name, force);
            return this;
        }

        public int getOptionActivation(String option) {
            return super.getInt(String.format(OPTION_ACTIVATION, option));
        }

        public Manager setOptionActivation(String option, int activation, boolean force) {
            super.set(String.format(OPTION_ACTIVATION, option), activation, force);
            return this;
        }

        public boolean isOptionActivated(String option) {
            return super.getBool(String.format(OPTION_ACTIVATED, option));
        }

        public Manager setOptionActivated(String option, boolean activated, boolean force) {
            super.set(String.format(OPTION_ACTIVATED, option), activated, force);
            return this;
        }

        public String getListenerName() {
            return super.getStr(LISTENER_NAME);
        }

        public Manager setListenerName(String name, boolean force) {
            super.set(LISTENER_NAME, name, force);
            return this;
        }

        public long getListenerSavingTimeout() {
            return super.getLong(LISTENER_TIMEOUT);
        }

        public Manager setListenerSavingTimeout(long timeout, boolean force) {
            super.set(LISTENER_TIMEOUT, timeout, force);
            return this;
        }

        public Set<String> getPortals() {
            return super.getKeys(PORTALS, false);
        }

        public String getPortalName(String portal) {
            return super.getStr(String.format(PORTAL_NAME, portal));
        }

        public Manager setPortalName(String portal, String name, boolean force) {
            super.set(String.format(PORTAL_NAME, portal), name, force);
            return this;
        }

        public boolean isPortalOpened(String portal) {
            return super.getBool(String.format(PORTAL_OPENED, portal));
        }

        public Manager setPortalOpened(String portal, boolean opened, boolean force) {
            super.set(String.format(PORTAL_OPENED, portal), opened, force);
            return this;
        }

        public long getPortalCooldown(String portal) {
            return super.getLong(String.format(PORTAL_COOLDOWN, portal));
        }

        public Manager setPortalCooldown(String portal, long cooldown, boolean force) {
            super.set(String.format(PORTAL_COOLDOWN, portal), cooldown, force);
            return this;
        }

        public Material getPortalOpenedMaterial(String portal) {
            return super.match(String.format(PORTAL_OPENED_MATERIAL, portal), Material.values());
        }

        public Manager setPortalOpenedMaterial(String portal, Material material, boolean force) {
            super.set(String.format(PORTAL_OPENED_MATERIAL, portal), material.name(), force);
            return this;
        }

        public Material getPortalClosedMaterial(String portal) {
            return super.match(String.format(PORTAL_CLOSED_MATERIAL, portal), Material.values());
        }

        public Manager setPortalClosedMaterial(String portal, Material material, boolean force) {
            super.set(String.format(PORTAL_CLOSED_MATERIAL, portal), material.name(), force);
            return this;
        }

        public byte getPortalMaterialData(String portal) {
            return super.getByte(String.format(PORTAL_MATERIAL_DATA, portal));
        }

        public Manager setPortalMaterialData(String portal, byte data, boolean force) {
            super.set(String.format(PORTAL_MATERIAL_DATA, portal), data, force);
            return this;
        }

        public Location getPortalSpawnOverLocation(String portal) {
            return super.getLoc(String.format(PORTAL_SPAWN_OVER_LOCATION, portal));
        }

        public Manager setPortalSpawnOverLocation(String portal, Location location, boolean force) {
            super.setLoc(String.format(PORTAL_SPAWN_OVER_LOCATION, portal), location, force);
            return this;
        }

        public Location getPortalPos1OverLocation(String portal) {
            return super.getLoc(String.format(PORTAL_POS1_OVER_LOCATION, portal));
        }

        public Manager setPortalPos1OverLocation(String portal, Location location, boolean force) {
            super.setLoc(String.format(PORTAL_POS1_OVER_LOCATION, portal), location, force);
            return this;
        }

        public Location getPortalPos2OverLocation(String portal) {
            return super.getLoc(String.format(PORTAL_POS2_OVER_LOCATION, portal));
        }

        public Manager setPortalPos2OverLocation(String portal, Location location, boolean force) {
            super.setLoc(String.format(PORTAL_POS2_OVER_LOCATION, portal), location, force);
            return this;
        }

        public Location getPortalSpawnDimLocation(String portal) {
            return super.getLoc(String.format(PORTAL_SPAWN_DIM_LOCATION, portal));
        }

        public Manager setPortalSpawnDimLocation(String portal, Location location, boolean force) {
            super.setLoc(String.format(PORTAL_SPAWN_DIM_LOCATION, portal), location, force);
            return this;
        }

        public Location getPortalPos1DimLocation(String portal) {
            return super.getLoc(String.format(PORTAL_POS1_DIM_LOCATION, portal));
        }

        public Manager setPortalPos1DimLocation(String portal, Location location, boolean force) {
            super.setLoc(String.format(PORTAL_POS1_DIM_LOCATION, portal), location, force);
            return this;
        }

        public Location getPortalPos2DimLocation(String portal) {
            return super.getLoc(String.format(PORTAL_POS2_DIM_LOCATION, portal));
        }

        public Manager setPortalPos2DimLocation(String portal, Location location, boolean force) {
            super.setLoc(String.format(PORTAL_POS2_DIM_LOCATION, portal), location, force);
            return this;
        }

        public List<UUID> getPortalTeleporting(String portal) {
            return super.getUUIDList(String.format(PORTAL_TELEPORTING, portal));
        }

        public Manager setPortalTeleporting(String portal, List<UUID> teleporting, boolean force) {
            super.set(String.format(PORTAL_TELEPORTING, portal), teleporting);
            return this;
        }

        public GPermissions getGlobalPermissions() {
            return super.getPermissions(PERM_GLOBAL);
        }

        public Manager setGlobalPermissions(GPermissions perm, boolean force) {
            super.setPermissions(PERM_GLOBAL, perm, force);
            return this;
        }

        public GPermissions getNeutralPermissions() {
            return super.getPermissions(PERM_NEUTRAL);
        }

        public Manager setNeutralPermissions(GPermissions perm, boolean force) {
            super.setPermissions(PERM_NEUTRAL, perm, force);
            return this;
        }

        public GPermissions getFriendlyPermissions() {
            return super.getPermissions(PERM_FRIENDLY);
        }

        public Manager setFriendlyPermissions(GPermissions perm, boolean force) {
            super.setPermissions(PERM_FRIENDLY, perm, force);
            return this;
        }

        public GPermissions getHostilePermissions() {
            return super.getPermissions(PERM_HOSTILE);
        }

        public Manager setHostilePermissions(GPermissions perm, boolean force) {
            super.setPermissions(PERM_HOSTILE, perm, force);
            return this;
        }

        public GPermissions getPriorityPermissions() {
            return super.getPermissions(PERM_PRIORITY);
        }

        public Manager setPriorityPermissions(GPermissions perm, boolean force) {
            super.setPermissions(PERM_PRIORITY, perm, force);
            return this;
        }
    }

    public static class Zone extends Config {
        public static final String TYPE = "type", SPAWN = "locations.spawn", POS1 = "locations.pos1",
                POS2 = "locations.pos2", PERMISSIONS = "permissions";

        public Zone(@Nonnull String path) {
            super(path, true);
        }

        @Override
        public Zone load() {
            super.load();
            return this;
        }

        @Override
        public Zone save() {
            super.save();
            return this;
        }

        public GZone.Type getType() {
            return super.match(TYPE, GZone.Type.values());
        }

        public Zone setType(String type, boolean force) {
            super.set(TYPE, type, force);
            return this;
        }

        public Location getSpawn() {
            return super.getLoc(SPAWN);
        }

        public Zone setSpawn(Location spawn, boolean force) {
            super.setLoc(SPAWN, spawn, force);
            return this;
        }

        public Location getPos1() {
            return super.getLoc(POS1);
        }

        public Zone setPos1(Location pos1, boolean force) {
            super.setLoc(POS1, pos1, force);
            return this;
        }

        public Location getPos2() {
            return super.getLoc(POS2);
        }

        public Zone setPos2(Location pos2, boolean force) {
            super.setLoc(POS2, pos2, force);
            return this;
        }

        public GPermissions getPermissions() {
            return super.getPermissions(PERMISSIONS);
        }

        public Zone setPermissions(GPermissions perm, boolean force) {
            super.setPermissions(PERMISSIONS, perm, force);
            return this;
        }
    }

    public static class Team extends Config {
        public static final String NAME = "name", PREFIX = "prefix", COLOR = "color", RADIUS = "radius",
                ELIMINATED = "elimination.eliminated", ELIMINATORS = "elimination.eliminators",
                TIMEOUT = "elimination.default-timeout", OLD_PLAYERS = "old-players",
                SPAWN = "spawn", PLUNDER_LOC = "plunder", PERMISSIONS = "permissions";

        public Team(@Nonnull String path) {
            super(path, true);
        }

        @Override
        public Team load() {
            super.load();
            return this;
        }

        @Override
        public Team save() {
            super.save();
            return this;
        }

        public String getName() {
            return super.getStr(NAME);
        }

        public Team setName(String name, boolean force) {
            super.set(NAME, name, force);
            return this;
        }

        public String getPrefix() {
            return super.getStr(PREFIX);
        }

        public Team setPrefix(String prefix, boolean force) {
            super.set(PREFIX, prefix, force);
            return this;
        }

        public boolean isEliminated() {
            return super.getBool(ELIMINATED);
        }

        public Team setEliminated(boolean eliminated, boolean force) {
            super.set(ELIMINATED, eliminated, force);
            return this;
        }

        public String getEliminators() {
            return super.getStr(ELIMINATORS);
        }

        public Team setEliminators(String eliminators, boolean force) {
            super.set(ELIMINATORS, eliminators, force);
            return this;
        }

        public long getTimeout() {
            return super.getLong(TIMEOUT);
        }

        public Team setTimeout(long timeout, boolean force) {
            super.set(TIMEOUT, timeout, force);
            return this;
        }

        public ChatColor getColor() {
            return super.match(COLOR, ChatColor.values());
        }

        public Team setColor(String color, boolean force) {
            super.set(COLOR, color, force);
            return this;
        }

        public double getRadius() {
            return super.getDouble(RADIUS);
        }

        public Team setRadius(double radius, boolean force) {
            super.set(RADIUS, radius, force);
            return this;
        }

        public ArrayList<String> getOldPlayers() {
            return new ArrayList<>(super.getStrList(OLD_PLAYERS));
        }

        public Team setOldPlayers(Collection<String> oldPlayers, boolean force) {
            super.set(OLD_PLAYERS, new ArrayList<>(oldPlayers), force);
            return this;
        }

        public Location getSpawn() {
            return super.getLoc(SPAWN);
        }

        public Team setSpawn(Location spawn, boolean force) {
            super.setLoc(SPAWN, spawn, force);
            return this;
        }

        public Location getPlunderLoc() {
            return super.getLoc(PLUNDER_LOC);
        }

        public Team setPlunderLoc(Location plunder, boolean force) {
            super.setLoc(PLUNDER_LOC, plunder, force);
            return this;
        }

        public GPermissions getPermissions() {
            return super.getPermissions(PERMISSIONS);
        }

        public Team setPermissions(GPermissions perm, boolean force) {
            super.setPermissions(PERMISSIONS, perm, force);
            return this;
        }
    }

    public static class Player extends Config {
        public static final String LAST_UUID = "last-uuid", TEAM = "team", COMPASS = "compass",
                STATS = "stats", PERMISSIONS = "permissions", INVENTORIES = "inventories";

        public Player(@Nonnull String path) {
            super(path, true);
        }

        @Override
        public Player load() {
            super.load();
            return this;
        }

        @Override
        public Player save() {
            super.save();
            return this;
        }

        public UUID getLastUuid() {
            try {
                return UUID.fromString(super.getStr(LAST_UUID));
            } catch (Exception e) {
                return null;
            }
        }

        public Player setLastUuid(UUID lastUuid, boolean force) {
            super.set(LAST_UUID, lastUuid == null ? null : lastUuid.toString(), force);
            return this;
        }

        public String getTeam() {
            return super.getStr(TEAM);
        }

        public Player setTeam(String team, boolean force) {
            super.set(TEAM, team, force);
            return this;
        }

        public GPlayer.Compass getCompass() {
            if (contains(COMPASS) && contains(COMPASS + ".location"))
                return new GPlayer.Compass(getStr(COMPASS + ".name"),
                        getDouble(COMPASS + ".radius"),
                        super.getLoc(COMPASS + ".location"));
            else
                return null;
        }

        public Player setCompass(GPlayer.Compass compass, boolean force) {
            if (compass == null)
                super.set(COMPASS, null, force);
            else {
                super.set(COMPASS + ".name", compass.getName(), force);
                super.set(COMPASS + ".radius", compass.getRadius(), force);
                super.setLoc(COMPASS + ".location", compass.getLocation(), force);
            }
            return this;
        }

        public PlayerStats getStats() {
            return super.getStats(STATS);
        }

        public Player setStats(PlayerStats stats, boolean force) {
            super.setStats(STATS, stats, force);
            return this;
        }

        public GPermissions getPermissions() {
            return super.getPermissions(PERMISSIONS);
        }

        public Player setPermissions(GPermissions perm, boolean force) {
            super.setPermissions(PERMISSIONS, perm, force);
            return this;
        }

        @SuppressWarnings("unchecked")
        public List<Utils.SavedInventory> getInventories() {
            ArrayList<Utils.SavedInventory> l = new ArrayList<>();
            for (Map<?, ?> map : super.getMapList(INVENTORIES))
                try {
                    l.add(Utils.SavedInventory.fromMap((Map<String, Object>) map));
                } catch (Exception ignored) {
                }
            return l;
        }

        public Player setInventories(List<Utils.SavedInventory> inventories, boolean force) {
            super.set(INVENTORIES, inventories.stream().map(Utils.SavedInventory::toMap).collect(Collectors.toList()), force);
            return this;
        }
    }

    public static class Limits extends Config {
        public static final String CRAFT = "craft", POTION = "potion", ENCHANT = "enchant.global", ENCHANT_SPE = "enchant.spe",
                HOLDING = "holding", WEARING_TIP = "wearing.tip", WEARING_MAX_DIAMOND_PIECES = "wearing.max-pieces-of.diamond",
                WEARING_MAX_GOLD_PIECES = "wearing.max-pieces-of.gold", WEARING_MAX_IRON_PIECES = "wearing.max-pieces-of.iron",
                WEARING_MAX_LEATHER_PIECES = "wearing.max-pieces-of.leather";

        public Limits(@Nonnull String path) {
            super(path, true);
        }

        @Override
        public Limits load() {
            super.load();
            set(CRAFT, new ArrayList<>(), false);
            set(POTION, new HashMap<>(), false);
            set(ENCHANT, new HashMap<>(), false);
            set(ENCHANT_SPE, new HashMap<>(), false);
            set(HOLDING, new HashMap<>(), false);
            set(WEARING_TIP, Arrays.asList("<Max pieces of> section includes armor pieces AND sword",
                    new LinkedHashMap<Object, Object>() {{
                        put("Example", "If max-diamond-pieces = 4, the player has 3 choices");
                        put(1, "Full diamond armor (but without sword)");
                        put(2, "3 pieces of diamond armor and it will be allowed to use the d sword");
                        put(3, "Or wearing leather, chainmail, iron or gold x)");
                    }}, "So it expect a number between 0 and 5 (included) ^^"), true);
            set(WEARING_MAX_DIAMOND_PIECES, 5, false);
            set(WEARING_MAX_GOLD_PIECES, 5, false);
            set(WEARING_MAX_IRON_PIECES, 5, false);
            set(WEARING_MAX_LEATHER_PIECES, 5, false);
            return this;
        }

        @Override
        public Limits save() {
            super.save();
            return this;
        }

        public ArrayList<Material> getCraft() {
            return matchList(CRAFT, Material.values());
        }

        public Limits setCraft(ArrayList<Material> craft, boolean force) {
            set(CRAFT, craft.stream().map(Enum::name).collect(Collectors.toList()), force);
            return this;
        }

        public HashMap<PotionEffectType, Integer> getPotion() {
            return new HashMap<PotionEffectType, Integer>() {{
                if (contains(POTION))
                    for (String k : getKeys(POTION, false))
                        if (k != null)
                            try {
                                if (PotionEffectType.getByName(k.toUpperCase()) != null)
                                    put(PotionEffectType.getByName(k.toUpperCase()), getInt(POTION + "." + k));
                            } catch (NumberFormatException | NullPointerException ignore) {
                            }
            }};
        }

        public Limits setPotion(HashMap<PotionEffectType, Integer> potion, boolean force) {
            potion.forEach((p, i) -> set(POTION + "." + p.getName(), i, force));
            return this;
        }

        public HashMap<Enchantment, Integer> getEnchantGlobal() {
            return new HashMap<Enchantment, Integer>() {{
                if (contains(ENCHANT))
                    for (String k : getKeys(ENCHANT, false))
                        if (k != null)
                            try {
                                if (Enchantment.getByName(k.toUpperCase()) != null)
                                    put(Enchantment.getByName(k.toUpperCase()), getInt(ENCHANT + "." + k));
                            } catch (NumberFormatException | NullPointerException ignore) {
                            }
            }};
        }

        public Limits setEnchantGlobal(HashMap<Enchantment, Integer> enchant, boolean force) {
            enchant.forEach((e, i) -> set(ENCHANT + "." + e.getName(), i, force));
            return this;
        }

        public HashMap<Enchantment, HashMap<Material, Integer>> getEnchantSpe() {
            return new HashMap<Enchantment, HashMap<Material, Integer>>() {{
                if (contains(ENCHANT_SPE))
                    for (String k : getKeys(ENCHANT_SPE, false))
                        if (k != null)
                            try {
                                if (Enchantment.getByName(k.toUpperCase()) != null) {
                                    HashMap<Material, Integer> v = new HashMap<>();
                                    for (String kk : getKeys(ENCHANT_SPE + "." + k, false))
                                        if (Material.getMaterial(kk) != null)
                                            try {
                                                v.put(Material.getMaterial(kk), getInt(ENCHANT_SPE + "." + k + "." + kk));
                                            } catch (NumberFormatException ignore) {
                                            }
                                    if (!v.isEmpty())
                                        put(Enchantment.getByName(k.toUpperCase()), v);
                                }
                            } catch (Exception ignore) {
                            }
            }};
        }

        public Limits setEnchantSpe(HashMap<Enchantment, HashMap<Material, Integer>> enchant, boolean force) {
            enchant.forEach((e, k) -> k.forEach((m, i) ->
                    set(ENCHANT_SPE + "." + e.getName() + "." + m.name(), i, force)));
            return this;
        }

        public HashMap<Material, Integer> getHolding() {
            return new HashMap<Material, Integer>() {{
                if (contains(HOLDING))
                    for (String k : getKeys(HOLDING, false))
                        if (k != null)
                            try {
                                if (matchKey(k, Material.values()) != null)
                                    put(matchKey(k, Material.values()), getInt(HOLDING + "." + k));
                            } catch (NumberFormatException | NullPointerException ignore) {
                            }
            }};
        }

        public Limits setHolding(HashMap<Material, Integer> holding, boolean force) {
            holding.forEach((h, i) -> set(HOLDING + "." + h.name(), i, force));
            return this;
        }

        public int getWearingMaxDiamondPieces() {
            return getInt(WEARING_MAX_DIAMOND_PIECES);
        }

        public Limits setWearingMaxDiamondPieces(int wearingMaxDiamondPieces, boolean force) {
            set(WEARING_MAX_DIAMOND_PIECES, wearingMaxDiamondPieces, force);
            return this;
        }

        public int getWearingMaxGoldPieces() {
            return getInt(WEARING_MAX_GOLD_PIECES);
        }

        public Limits setWearingMaxGoldPieces(int wearingMaxGoldPieces, boolean force) {
            set(WEARING_MAX_GOLD_PIECES, wearingMaxGoldPieces, force);
            return this;
        }

        public int getWearingMaxIronPieces() {
            return getInt(WEARING_MAX_IRON_PIECES);
        }

        public Limits setWearingMaxIronPieces(int wearingMaxIronPieces, boolean force) {
            set(WEARING_MAX_IRON_PIECES, wearingMaxIronPieces, force);
            return this;
        }

        public int getWearingMaxLeatherPieces() {
            return getInt(WEARING_MAX_LEATHER_PIECES);
        }

        public Limits setWearingMaxLeatherPieces(int wearingMaxLeatherPieces, boolean force) {
            set(WEARING_MAX_LEATHER_PIECES, wearingMaxLeatherPieces, force);
            return this;
        }

    }

    public static class PickableLocks extends Config {
        public static final String LOCKS = "locks", PICKING = "picking";

        public PickableLocks(@Nonnull String path) {
            super(path, true);
        }

        @Override
        public PickableLocks load() {
            super.load();
            return this;
        }

        @Override
        public PickableLocks save() {
            super.save();
            return this;
        }

        public static GPickableLocks.Lock mapToLock(Map<?, ?> map) {
            try {
                Map<?, ?> coolMap, locMap;
                if (map.containsKey("id") && map.containsKey("level")
                        && map.containsKey("pickable") && map.containsKey("picked") && map.containsKey("auto-bc")
                        && map.containsKey("cooldown") && (coolMap = (Map<?, ?>) map.get("cooldown")) != null
                        && coolMap.containsKey("original")
                        && (locMap = (Map<?, ?>) map.get("location")) != null
                        && locMap.containsKey("x") && locMap.containsKey("y") && locMap.containsKey("z")
                        && locMap.containsKey("w")) {

                    Location loc = new Location(Bukkit.getWorld((String) locMap.get("w")),
                            Double.parseDouble(locMap.get("x") + ""),
                            Double.parseDouble(locMap.get("y") + ""),
                            Double.parseDouble(locMap.get("z") + ""));

                    return new GPickableLocks.Lock(map.get("id") + "", Integer.parseInt(map.get("level") + ""),
                            Boolean.parseBoolean(map.get("pickable") + ""),
                            Boolean.parseBoolean(map.get("picked") + ""),
                            Boolean.parseBoolean(map.get("auto-bc") + ""),
                            Long.parseLong(coolMap.get("original") + ""),
                            Long.parseLong(coolMap.get(coolMap.containsKey("current") ? "current" : "original") + ""),
                            loc,
                            map.containsKey("picker") && map.get("picker") != null ? map.get("picker") + "" : null,
                            map.containsKey("armor-stand1") && map.get("armor-stand1") != null ? UUID.fromString(map.get("armor-stand1") + "") : null,
                            map.containsKey("armor-stand2") && map.get("armor-stand2") != null ? UUID.fromString(map.get("armor-stand2") + "") : null);
                }
            } catch (ClassCastException | NullPointerException | NumberFormatException ignored) {
            }
            return null;
        }

        public static Map<String, Object> lockToMap(GPickableLocks.Lock lock) {
            LinkedHashMap<String, Long> coolMap = new LinkedHashMap<String, Long>() {{
                put("original", lock.getOriginalCoolDown());
                put("current", lock.getCoolDown());
            }};
            LinkedHashMap<String, Object> locMap = new LinkedHashMap<String, Object>() {{
                put("x", lock.getLocation().getX());
                put("y", lock.getLocation().getY());
                put("z", lock.getLocation().getZ());
                put("w", lock.getLocation().getWorld().getName());
            }};
            return new LinkedHashMap<String, Object>() {{
                put("id", lock.getId());
                put("level", lock.getLevel());
                put("pickable", lock.isPickable());
                put("picked", lock.isPicked());
                put("auto-bc", lock.isAutoBC());
                put("cooldown", coolMap);
                put("location", locMap);
                put("picker", lock.getPicker());
                put("armor-stand1", lock.getArmorStand1() != null ? lock.getArmorStand1().toString() : null);
                put("armor-stand2", lock.getArmorStand2() != null ? lock.getArmorStand2().toString() : null);
            }};
        }

        public ArrayList<GPickableLocks.Lock> getLocks() {
            return getMapList(LOCKS).stream().map(PickableLocks::mapToLock).filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        public PickableLocks setLocks(ArrayList<GPickableLocks.Lock> locks, boolean force) {
            super.set(LOCKS, locks.stream().map(PickableLocks::lockToMap)
                    .collect(Collectors.toList()), force);
            return this;
        }

        public HashMap<String, String> getPicking() {
            return new HashMap<String, String>() {{
                if (contains(PICKING))
                    for (String k : getKeys(PICKING, false))
                        if (k != null && get(PICKING + "." + k) != null)
                            try {
                                put(k, get(PICKING + "." + k) + "");
                            } catch (NullPointerException ignore) {
                            }
            }};
        }

        public PickableLocks setPicking(HashMap<String, String> picking, boolean force) {
            super.set(PICKING, picking, force);
            return this;
        }
    }

    public static final String LAST_SAVE = "last-save";

    private File file;
    private FileConfiguration config;
    private boolean dated;

    public Config(@Nonnull String path, boolean dated) {
        config = null;
        setFile(path);
        setDated(dated);
    }

    public File getFile() {
        return file;
    }

    /**
     * Need an extra {@link Config#load()} call to get the actual config.
     */
    public Config setFile(@Nonnull String abstractPath) {
        this.file = new File(Main.instance.getDataFolder().getPath() + "/" + abstractPath);
        return this;
    }

    public void delete() {
        if (file.exists())
            file.delete();
        file = null;
        config = null;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public Config load() {
        if (!file.exists())
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        config = YamlConfiguration.loadConfiguration(file);
        if (dated)
            date();
        return this;
    }

    /*
     * public void reloadConfig() {
     *     if (file == null)
     *         file = new File(Main.instance.getDataFolder(), fileName);
     *
     *     config = YamlConfiguration.loadConfiguration(file);
     *
     *     InputStream defaultStream = Main.instance.getResource(fileName);
     *     if (defaultStream != null) {
     *         YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
     *         config.setDefaults(defaultConfig);
     *     }
     * }
     */

    public Config save() {
        if (loaded())
            try {
                if (dated)
                    date();
                config.save(file);
            } catch (IOException e) {
//                Main.instance.getLogger().log(Level.SEVERE, "Couldn't save config to " + file, e);
                throw new RuntimeException(e);
            }
        return this;
    }

    public boolean isDated() {
        return dated;
    }

    public Config setDated(boolean dated) {
        this.dated = dated;
        return this;
    }

    public Config date() {
        set(LAST_SAVE, DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL,
                Locale.ENGLISH).format(new Date()), true);
        return this;
    }

    public boolean exists() {
        return file.exists();
    }

    public boolean loaded() {
        return file != null && config != null;
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

    public boolean isNull(String path) {
        return config.get(path) == null || (config.isConfigurationSection(path) && (getKeys(path, false) == null || getKeys(path, false).isEmpty()));
    }

    public Object getObj(String path) {
        return config.get(path);
    }

    public String getStr(String path) {
        return config.getString(path);
    }

    public boolean getBool(String path) {
        if (isNull(path))
            throw new NumberFormatException("The path '" + path + "' is null");
        return config.getBoolean(path);
    }

    public byte getByte(String path) {
        if (isNull(path))
            throw new NumberFormatException("The path '" + path + "' is null");
        return Byte.parseByte(config.get(path) + "");
    }

    public short getShort(String path) {
        if (isNull(path))
            throw new NumberFormatException("The path '" + path + "' is null");
        return Short.parseShort(config.get(path) + "");
    }

    public char getChar(String path) {
        if (isNull(path))
            throw new NumberFormatException("The path '" + path + "' is null");
        if ((config.get(path) + "").length() != 1)
            throw new RuntimeException("Invalid char");
        return (config.get(path) + "").charAt(0);
    }

    public int getInt(String path) {
        if (isNull(path))
            throw new NumberFormatException("The path '" + path + "' is null");
        return config.getInt(path);
    }

    public long getLong(String path) {
        if (isNull(path))
            throw new NumberFormatException("The path '" + path + "' is null");
        return config.getLong(path);
    }

    public double getDouble(String path) {
        if (isNull(path))
            throw new NumberFormatException("The path '" + path + "' is null");
        return config.getDouble(path);
    }

    public float getFloat(String path) {
        if (isNull(path))
            throw new NumberFormatException("The path '" + path + "' is null");
        return Float.parseFloat(config.get(path) + "");
    }

    public Map<String, Object> getMap(String path) {
        if (isNull(path))
            return new HashMap<>();
        return config.getConfigurationSection(path).getValues(false);
    }

    public List<Boolean> getBoolList(String path) {
        return config.getBooleanList(path);
    }

    public List<Byte> getByteList(String path) {
        return config.getByteList(path);
    }

    public List<Short> getShortList(String path) {
        return config.getShortList(path);
    }

    public List<Character> getCharacterList(String path) {
        return config.getCharacterList(path);
    }

    public List<Integer> getIntList(String path) {
        return config.getIntegerList(path);
    }

    public List<Long> getLongList(String path) {
        return config.getLongList(path);
    }

    public List<Double> getDoubleList(String path) {
        return config.getDoubleList(path);
    }

    public List<Float> getFloatList(String path) {
        return config.getFloatList(path);
    }

    public List<String> getStrList(String path) {
        return config.getStringList(path);
    }

    public List<?> getList(String path) {
        return config.getList(path);
    }

    public List<Map<?, ?>> getMapList(String path) {
        return config.getMapList(path);
    }

    public Set<String> getKeys(String path, boolean keys) {
        return config.getConfigurationSection(path) == null ? new HashSet<>() : config.getConfigurationSection(path).getKeys(keys);
    }

    public Config set(String path, Object o, boolean force) {
        try {
            if (force || isNull(path))
                config.set(path, o);
        } catch (Exception e) {
            System.out.println(fr.luzog.pl.fkx.utils.Color.RED);
            e.printStackTrace();
            System.out.println(Color.RESET);
        }
        return this;
    }

    public Config set(String path, Object o) {
        set(path, o, false);
        return this;
    }

    public <T extends Config> T parse(Class<T> type) {
        return type.cast(this);
    }

    /*
     * ######################
     * ### Custom methods ###
     * ######################
     */

    public Location getLoc(String path) {
        if (config.get(path) == null || !getKeys(path, false).contains("x")
                || !getKeys(path, false).contains("y") || !getKeys(path, false).contains("z"))
            return null;
        World w;
        try {
            w = Bukkit.getWorld(UUID.fromString(getStr(path + ".w")));
        } catch (IllegalArgumentException e) {
            w = Bukkit.getWorld(getStr(path + ".w"));
        }
        double x = getDouble(path + ".x"), y = getDouble(path + ".y"), z = getDouble(path + ".z");
        float yw = getKeys(path, false).contains("yw") ? getFloat(path + ".yw") : 0,
                pi = getKeys(path, false).contains("pi") ? getFloat(path + ".pi") : 0;
        return getKeys(path, false).contains("yw") && getKeys(path, false).contains("pi") ?
                new Location(w, x, y, z) : new Location(w, x, y, z, yw, pi);
    }

    public Config setLoc(String path, Location loc, boolean force) {
        if (loc == null)
            set(path, new HashMap<>(), force);
        else
            set(path + ".x", loc.getX(), force)
                    .set(path + ".y", loc.getY(), force)
                    .set(path + ".z", loc.getZ(), force)
                    .set(path + ".yw", loc.getYaw(), force)
                    .set(path + ".pi", loc.getPitch(), force)
                    .set(path + ".w", loc.getWorld() == null ? null : loc.getWorld().getName(), force);
        return this;
    }

    public <T extends Enum<T>> T matchKey(String item, T[] values) {
        for (T value : values)
            if ((item + "").equalsIgnoreCase(value.name()))
                return value;
        return null;
    }

    public <T extends Enum<T>> T match(String path, T[] values) {
        return matchKey(config.get(path) + "", values);
    }

    public <T extends Enum<T>> ArrayList<T> matchList(String path, T[] values) {
        return new ArrayList<T>() {{
            for (String s : getStrList(path))
                for (T value : values)
                    if ((s + "").equalsIgnoreCase(value.name()))
                        add(value);
        }};
    }

    /**
     * This function returns a list of UUIDs from a path in the config.
     *
     * @param path The path to the list.
     *
     * @return A list of UUIDs
     *
     * @deprecated It will be removed in the future. (No UUID supported for offline mode)
     */
    @Deprecated
    public List<UUID> getUUIDList(String path) {
        return new ArrayList<UUID>() {{
            for (String s : getStrList(path))
                try {
                    add(UUID.fromString(s));
                } catch (IllegalArgumentException ignored) {
                }
        }};
    }

    public GPermissions getPermissions(String path) {
        if (config.get(path) == null || getKeys(path, false).isEmpty())
            return null;
        GPermissions perms = new GPermissions(GPermissions.Definition.DEFAULT);
        for (String s : getKeys(path, false))
            try {
                GPermissions.Type type = GPermissions.Type.valueOf(s.toUpperCase().replace("-", "_"));
                GPermissions.Definition def = config.contains(path + "." + s) ?
                        config.isBoolean(path + "." + s) ?
                                config.getBoolean(path + "." + s) ?
                                        GPermissions.Definition.ON
                                        : GPermissions.Definition.OFF
                                : match(path + "." + s, GPermissions.Definition.values())
                        : null;
                if (def != null)
                    perms.setPermission(type, def);
            } catch (NullPointerException ignored) {
            }
        return perms;
    }

    public Config setPermissions(String path, GPermissions perms, boolean force) {
        if (perms == null)
            set(path, new HashMap<>(), force);
        else
            perms.getPermissions().forEach((type, def) ->
                    set(path + "." + type.name().toLowerCase().replace("_", "-"),
                            def == GPermissions.Definition.ON ? true : def == GPermissions.Definition.OFF ? false : def.name().toLowerCase(), force));
        return this;
    }

    public PlayerStats getStats(String path) {
        if (config.get(path) == null || getKeys(path, false).isEmpty())
            return null;
        PlayerStats stats = new PlayerStats();
        for (String s : getKeys(path, false))
            stats.set(s, config.get(path + "." + s));
        return stats;
    }

    public Config setStats(String path, PlayerStats stats, boolean force) {
        if (stats == null)
            set(path, new HashMap<>(), force);
        else
            for (Field f : stats.getClass().getDeclaredFields())
                try {
                    boolean tempAccessible = f.isAccessible();
                    f.setAccessible(true);
                    Object o = f.get(stats);
                    f.setAccessible(tempAccessible);
                    set(path + "." + f.getName().toLowerCase().replace("_", "-"), o, force);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        return this;
    }

}