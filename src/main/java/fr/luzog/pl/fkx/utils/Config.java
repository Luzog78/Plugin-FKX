package fr.luzog.pl.fkx.utils;

import fr.luzog.pl.fkx.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
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

        public Globals setVersion(Object version) {
            super.set(VERSION, version);
            return this;
        }

        public Globals forceVersion(Object version) {
            super.force(VERSION, version);
            return this;
        }

        public Globals setLang(String lang) {
            super.set(LANG, lang);
            return this;
        }

        public Globals forceLang(String lang) {
            super.force(LANG, lang);
            return this;
        }

        public Globals setWorlds(String overworld, String nether, String end) {
            super.set(OVERWORLD, overworld);
            super.set(NETHER, nether);
            super.set(END, end);
            return this;
        }

        public Globals forceWorlds(String overworld, String nether, String end) {
            super.force(OVERWORLD, overworld);
            super.force(NETHER, nether);
            super.force(END, end);
            return this;
        }

        public Globals setLastGame(String lastGame) {
            super.set(LAST_GAME, lastGame);
            return this;
        }

        public Globals forceLastGame(String lastGame) {
            super.force(LAST_GAME, lastGame);
            return this;
        }

        public Globals setVanish(String preSufIx, boolean isPrefix, List<UUID> players) {
            super.set(VANISH_PRE_SUF_IX, preSufIx);
            super.set(VANISH_IS_PREFIX, isPrefix);
            super.set(VANISH_PLAYERS, players);
            return this;
        }

        public Globals forceVanish(String preSufIx, boolean isPrefix, List<UUID> players) {
            super.force(VANISH_PRE_SUF_IX, preSufIx);
            super.force(VANISH_IS_PREFIX, isPrefix);
            super.force(VANISH_PLAYERS, players);
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
        config.set(LAST_SAVE, DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL,
                Locale.ENGLISH).format(new Date()));
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

    public List<Map<?, ?>> getMap(String path) {
        return config.getMapList(path);
    }

    public Set<String> getKeys(String path, boolean keys) {
        return config.getConfigurationSection(path).getKeys(keys);
    }

    public Config force(String path, Object o) {
        config.set(path, o);
        return this;
    }

    public Config set(String path, Object o) {
        if (config.get(path) == null || config.get(path).equals(""))
            force(path, o);
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
        return new Location(Bukkit.getWorld(getStr(path + ".world")), getDouble(path + ".x"), getDouble(path + ".y"),
                getDouble(path + ".z"), getFloat(path + ".yaw"), getFloat(path + ".pitch"));
    }

    public Config forceLoc(String path, Location loc) {
        force(path + ".x", loc.getX())
                .force(path + ".y", loc.getY())
                .force(path + ".z", loc.getZ())
                .force(path + ".yaw", loc.getYaw())
                .force(path + ".pitch", loc.getPitch())
                .force(path + ".world", loc.getWorld().getName());
        return this;
    }

    public Config setLoc(String path, Location loc) {
        if (config.get(path) == null || config.get(path).equals(""))
            forceLoc(path, loc);
        return this;
    }

    public <T extends Enum<T>> T match(String path, Iterable<T> values) {
        for (T value : values)
            if ((config.get(path) + "").equalsIgnoreCase(value.name()))
                return value;
        return null;
    }

    public <T extends Enum<T>> ArrayList<T> matchList(String path, Iterable<T> values) {
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

}