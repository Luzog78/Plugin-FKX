package fr.luzog.pl.fkx.utils;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.*;
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
        public static final String VERSION = "version", LANG = "lang", OVERWORLD = "worlds.over",
                NETHER = "worlds.nether", END = "worlds.end", LAST_GAME = "last-game",
                VANISH_PRE_SUF_IX = "vanish.pre-suf-ix", VANISH_IS_PREFIX = "vanish.is-prefix",
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

        public FKManager.State getState() {
            return super.match(STATE, FKManager.State.values());
        }

        public Manager setState(FKManager.State state, boolean force) {
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

        public FKManager.Weather getCurrentWeather() {
            return super.match(WEATHER, FKManager.Weather.values());
        }

        public Manager setCurrentWeather(FKManager.Weather weather, boolean force) {
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

//        public FKOptions.FKOption getOption(String option) {
//            try {
//                return option == null ? null : new FKOptions.FKOption(getOptionName(option), getOptionActivation(option), isOptionActivated(option));
//            } catch (Exception e) {
//                return null;
//            }
//        }
//
//        public Manager setOption(String option, FKOptions.FKOption fkOption, boolean force) {
//            if (fkOption == null) {
//                super.set(String.format(OPTION_NAME, option), null, force);
//                super.set(String.format(OPTION_ACTIVATION, option), null, force);
//                super.set(String.format(OPTION_ACTIVATED, option), null, force);
//            } else {
//                super.set(String.format(OPTION_NAME, option), fkOption.getName(), force);
//                super.set(String.format(OPTION_ACTIVATION, option), fkOption.getActivationDay(), force);
//                super.set(String.format(OPTION_ACTIVATED, option), fkOption.isActivated(), force);
//            }
//            return this;
//        }
//
//        public FKOptions getOptions(String path) {
//            if (path == null || isNull(path) || super.getKeys(path, false) == null)
//                return null;
//            FKOptions options = new FKOptions(
//                    new FKOptions.FKOption("öµñàù", -1, false),
//                    new FKOptions.FKOption("àöñùµ", -1, false),
//                    new FKOptions.FKOption("ñöàµù", -1, false),
//                    new FKOptions.FKOption("ùàµöñ", -1, false)
//            );
//            boolean isOK = false;
//            for (String option : super.getKeys(path, false))
//                if (getOption(path + "." + option) != null)
//                    if (option.equalsIgnoreCase("pvp")) {
//                        options.setPvp(getOption(option));
//                        isOK = true;
//                    } else if (option.equalsIgnoreCase("nether")) {
//                        options.setNether(getOption(option));
//                        isOK = true;
//                    } else if (option.equalsIgnoreCase("assaults")) {
//                        options.setAssaults(getOption(option));
//                        isOK = true;
//                    } else if (option.equalsIgnoreCase("end")) {
//                        options.setEnd(getOption(option));
//                        isOK = true;
//                    }
//            return isOK ? options : null;
//        }
//
//        public Manager setOptions(FKOptions options, boolean force) {
//            if (options == null) {
//                super.set(OPTIONS, new HashMap<>(), force);
//                return this;
//            }
//            setOption(OPTIONS + ".pvp", options.getPvp(), force);
//            setOption(OPTIONS + ".nether", options.getNether(), force);
//            setOption(OPTIONS + ".assaults", options.getAssaults(), force);
//            setOption(OPTIONS + ".end", options.getEnd(), force);
//            return this;
//        }

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

        public FKPermissions getGlobalPermissions() {
            return super.getPermissions(PERM_GLOBAL);
        }

        public Manager setGlobalPermissions(FKPermissions perm, boolean force) {
            super.setPermissions(PERM_GLOBAL, perm, force);
            return this;
        }

        public FKPermissions getNeutralPermissions() {
            return super.getPermissions(PERM_NEUTRAL);
        }

        public Manager setNeutralPermissions(FKPermissions perm, boolean force) {
            super.setPermissions(PERM_NEUTRAL, perm, force);
            return this;
        }

        public FKPermissions getFriendlyPermissions() {
            return super.getPermissions(PERM_FRIENDLY);
        }

        public Manager setFriendlyPermissions(FKPermissions perm, boolean force) {
            super.setPermissions(PERM_FRIENDLY, perm, force);
            return this;
        }

        public FKPermissions getHostilePermissions() {
            return super.getPermissions(PERM_HOSTILE);
        }

        public Manager setHostilePermissions(FKPermissions perm, boolean force) {
            super.setPermissions(PERM_HOSTILE, perm, force);
            return this;
        }

        public FKPermissions getPriorityPermissions() {
            return super.getPermissions(PERM_PRIORITY);
        }

        public Manager setPriorityPermissions(FKPermissions perm, boolean force) {
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

        public FKZone.Type getType() {
            return super.match(TYPE, FKZone.Type.values());
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

        public FKPermissions getPermissions() {
            return super.getPermissions(PERMISSIONS);
        }

        public Zone setPermissions(FKPermissions perm, boolean force) {
            super.setPermissions(PERMISSIONS, perm, force);
            return this;
        }
    }

    public static class Team extends Config {
        public static final String NAME = "name", PREFIX = "prefix", COLOR = "color", CHESTS_ROOM = "chests-room.loc",
                GUARDIAN = "chests-room.guardian-uuid", RADIUS = "radius", SPAWN = "spawn", PERMISSIONS = "permissions";

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

        public ChatColor getColor() {
            return super.match(COLOR, ChatColor.values());
        }

        public Team setColor(String color, boolean force) {
            super.set(COLOR, color, force);
            return this;
        }

        public Location getChestsRoom() {
            return super.getLoc(CHESTS_ROOM);
        }

        public Team setChestsRoom(Location chestsRoom, boolean force) {
            super.setLoc(CHESTS_ROOM, chestsRoom, force);
            return this;
        }

        public UUID getGuardian() {
            try {
                return UUID.fromString(getStr(GUARDIAN));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        public Team setGuardian(UUID guardian, boolean force) {
            super.set(GUARDIAN, guardian + "", force);
            return this;
        }

        public double getRadius() {
            return super.getDouble(RADIUS);
        }

        public Team setRadius(double radius, boolean force) {
            super.set(RADIUS, radius, force);
            return this;
        }

        public Location getSpawn() {
            return super.getLoc(SPAWN);
        }

        public Team setSpawn(Location spawn, boolean force) {
            super.setLoc(SPAWN, spawn, force);
            return this;
        }

        public FKPermissions getPermissions() {
            return super.getPermissions(PERMISSIONS);
        }

        public Team setPermissions(FKPermissions perm, boolean force) {
            super.setPermissions(PERMISSIONS, perm, force);
            return this;
        }
    }

    public static class Player extends Config {
        public static final String NAME = "name", TEAM = "team", STATS = "stats", PERMISSIONS = "permissions";

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
                return UUID.fromString(super.getStr(NAME));
            } catch (Exception e) {
                return null;
            }
        }

        public Player setLastUuid(UUID lastUuid, boolean force) {
            super.set(NAME, lastUuid == null ? null : lastUuid.toString(), force);
            return this;
        }

        public String getTeam() {
            return super.getStr(TEAM);
        }

        public Player setTeam(String team, boolean force) {
            super.set(TEAM, team, force);
            return this;
        }

        public PlayerStats getStats() {
            return super.getStats(STATS);
        }

        public Player setStats(PlayerStats stats, boolean force) {
            super.setStats(STATS, stats, force);
            return this;
        }

        public FKPermissions getPermissions() {
            return super.getPermissions(PERMISSIONS);
        }

        public Player setPermissions(FKPermissions perm, boolean force) {
            super.setPermissions(PERMISSIONS, perm, force);
            return this;
        }
    }

    public static class Limits extends Config {
        public static final String CRAFT = "craft", POTION = "potion", ENCHANT = "enchant", HOLDING = "holding", WEARING_TIP = "wearing.tip",
                WEARING_MAX_DIAMOND_PIECES = "wearing.max-pieces-of.diamond", WEARING_MAX_GOLD_PIECES = "wearing.max-pieces-of.gold",
                WEARING_MAX_IRON_PIECES = "wearing.max-pieces-of.iron", WEARING_MAX_LEATHER_PIECES = "wearing.max-pieces-of.leather";

        public Limits(@Nonnull String path) {
            super(path, true);
        }

        @Override
        public Limits load() {
            super.load();
            set(CRAFT, new ArrayList<>(), false);
            set(POTION, new HashMap<>(), false);
            set(ENCHANT, new HashMap<>(), false);
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

        public HashMap<Enchantment, Integer> getEnchant() {
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

        public Limits setEnchant(HashMap<Enchantment, Integer> enchant, boolean force) {
            enchant.forEach((e, i) -> set(ENCHANT + "." + e.getName(), i, force));
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

        public static FKPickableLocks.Lock mapToLock(Map<?, ?> map) {
            try {
                Map<?, ?> coolMap, locMap;
                if (map.containsKey("id") && map.containsKey("rarity")
                        && map.containsKey("pickable") && map.containsKey("picked")
                        && map.containsKey("cooldown") && (coolMap = (Map<?, ?>) map.get("cooldown")) != null
                        && coolMap.containsKey("original")
                        && (locMap = (Map<?, ?>) map.get("location")) != null
                        && locMap.containsKey("x") && locMap.containsKey("y") && locMap.containsKey("z")
                        && locMap.containsKey("w")) {

                    Location loc = new Location(Bukkit.getWorld((String) locMap.get("w")),
                            Double.parseDouble(locMap.get("x") + ""),
                            Double.parseDouble(locMap.get("y") + ""),
                            Double.parseDouble(locMap.get("z") + ""));

                    return new FKPickableLocks.Lock(map.get("id") + "", FKPickableLocks.Rarity.valueOf((map.get("rarity") + "").toUpperCase()),
                            Boolean.parseBoolean(map.get("pickable") + ""),
                            Boolean.parseBoolean(map.get("picked") + ""),
                            Long.parseLong(coolMap.get("original") + ""),
                            Long.parseLong(coolMap.get(coolMap.containsKey("current") ? "current" : "original") + ""),
                            loc,
                            map.containsKey("picker") && map.get("picker") != null ? map.get("picker") + "" : null,
                            map.containsKey("key-type") ? FKPickableLocks.KeyType.valueOf(map.get("key-type") + "") : FKPickableLocks.KeyType.NONE,
                            map.containsKey("armor-stand1") && map.get("armor-stand1") != null ? UUID.fromString(map.get("armor-stand1") + "") : null,
                            map.containsKey("armor-stand2") && map.get("armor-stand2") != null ? UUID.fromString(map.get("armor-stand2") + "") : null);
                }
            } catch (ClassCastException | NullPointerException | NumberFormatException ignored) {
            }
            return null;
        }

        public static Map<String, Object> lockToMap(FKPickableLocks.Lock lock) {
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
                put("rarity", lock.getRarity().name());
                put("pickable", lock.isPickable());
                put("picked", lock.isPicked());
                put("cooldown", coolMap);
                put("location", locMap);
                put("picker", lock.getPicker());
                put("key-type", lock.getKeyType().name());
                put("armor-stand1", lock.getArmorStand1() != null ? lock.getArmorStand1().toString() : null);
                put("armor-stand2", lock.getArmorStand2() != null ? lock.getArmorStand2().toString() : null);
            }};
        }

        public ArrayList<FKPickableLocks.Lock> getLocks() {
            return getMapList(LOCKS).stream().map(PickableLocks::mapToLock).filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        public PickableLocks setLocks(ArrayList<FKPickableLocks.Lock> locks, boolean force) {
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
        return config.getBoolean(path);
    }

    public byte getByte(String path) {
        return Byte.parseByte(config.get(path) + "");
    }

    public short getShort(String path) {
        return Short.parseShort(config.get(path) + "");
    }

    public char getChar(String path) {
        if ((config.get(path) + "").length() != 1)
            throw new RuntimeException("Invalid char");
        return (config.get(path) + "").charAt(0);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public long getLong(String path) {
        return config.getLong(path);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public float getFloat(String path) {
        return Float.parseFloat(config.get(path) + "");
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
            System.out.println(Color.RED);
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

    public FKPermissions getPermissions(String path) {
        if (config.get(path) == null || getKeys(path, false).isEmpty())
            return null;
        FKPermissions perms = new FKPermissions(FKPermissions.Definition.DEFAULT);
        for (String s : getKeys(path, false))
            try {
                FKPermissions.Type type = FKPermissions.Type.valueOf(s.toUpperCase().replace("-", "_"));
                FKPermissions.Definition def = config.contains(path + "." + s) ?
                        config.isBoolean(path + "." + s) ?
                                config.getBoolean(path + "." + s) ?
                                        FKPermissions.Definition.ON
                                        : FKPermissions.Definition.OFF
                                : match(path + "." + s, FKPermissions.Definition.values())
                        : null;
                if (def != null)
                    perms.setPermission(type, def);
            } catch (NullPointerException ignored) {
            }
        return perms;
    }

    public Config setPermissions(String path, FKPermissions perms, boolean force) {
        if (perms == null)
            set(path, new HashMap<>(), force);
        else
            perms.getPermissions().forEach((type, def) ->
                    set(path + "." + type.name().toLowerCase().replace("_", "-"),
                            def == FKPermissions.Definition.ON ? true : def == FKPermissions.Definition.OFF ? false : def.name().toLowerCase(), force));
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