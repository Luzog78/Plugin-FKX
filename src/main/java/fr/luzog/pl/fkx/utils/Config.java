package fr.luzog.pl.fkx.utils;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPermissions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.security.Permissions;
import java.text.DateFormat;
import java.util.*;

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

        public List<UUID> getVanishPlayers() {
            return super.getUUIDList(VANISH_PLAYERS);
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

        public Globals setVanish(String preSufIx, boolean isPrefix, List<UUID> players, boolean force) {
            super.set(VANISH_PRE_SUF_IX, preSufIx, force);
            super.set(VANISH_IS_PREFIX, isPrefix, force);
            super.set(VANISH_PLAYERS, players, force);
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
            if (force || config.get(path) == null || config.get(path).equals("") || getKeys(path, false) == null || getKeys(path, false).isEmpty())
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
                    .set(path + ".w", loc.getWorld().getName(), force);
        return this;
    }

    public <T extends Enum<T>> T match(String path, T[] values) {
        for (T value : values)
            if ((config.get(path) + "").equalsIgnoreCase(value.name()))
                return value;
        return null;
    }

    public <T extends Enum<T>> ArrayList<T> matchList(String path, T[] values) {
        return new ArrayList<T>() {{
            for (String s : getStrList(path))
                for (T value : values)
                    if ((s + "").equalsIgnoreCase(value.name()))
                        add(value);
        }};
    }

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

}