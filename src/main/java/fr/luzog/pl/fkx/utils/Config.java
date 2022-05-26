package fr.luzog.pl.fkx.utils;

import fr.luzog.pl.fkx.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class Config {

    private static File file = null;
    private static final String fileName = "FKX - Config.yml";
    private static FileConfiguration config = null;

    public static enum Path {
        VANISH("users.states.vanish"),
        FREEZE("users.states.freeze"),
        GOD("users.states.god"),
        ;

        private String path;

        Path(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static void saveDefaultConfig() {
        if (file == null)
            file = new File(Main.instance.getDataFolder(), fileName);

        if (!file.exists())
            Main.instance.saveResource(fileName, false);
    }

    public static void reloadConfig() {
        if (file == null)
            file = new File(Main.instance.getDataFolder(), fileName);

        config = YamlConfiguration.loadConfiguration(file);

        InputStream defaultStream = Main.instance.getResource(fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            config.setDefaults(defaultConfig);
        }
    }

    @SuppressWarnings("deprecation")
    public static void saveConfig() {
        if (config == null || file == null)
            return;
        try {
            Date d = new Date();
            DecimalFormat df = new DecimalFormat("00");
            d.setYear(2022);
            getConfig().set("IMPORTANT.last-save",
                    df.format(d.getMonth()) + "." + df.format(d.getDay()) + "." + d.getYear() + ".."
                            + df.format(d.getHours()) + "." + df.format(d.getMinutes()) + "."
                            + df.format(d.getSeconds()));
            getConfig().save(file);
        } catch (IOException e) {
            Main.instance.getLogger().log(Level.SEVERE, "Couldn't save config to " + file, e);
        }
    }

    public static void saveAndReload() {
        saveConfig();
        reloadConfig();
    }

    public static File getFile() {
        if (file == null)
            reloadConfig();
        return file;
    }

    public static void destroy() {
        file.delete();
        file = null;
        config = null;
    }

    public static FileConfiguration getConfig() {
        if (config == null)
            reloadConfig();
        return config;
    }

    public static Object getObj(String root) {
        return getConfig().get(root);
    }

    public static String getRawStr(String root) {
        return getObj(root) == null ? null : getConfig().getString(root);
    }

    public static String getStr(String root) {
        return getConfig().getString(root) == null ? null : getConfig().getString(root).contains("&") ? (getObj(root) + "").replace("&", "§") : (getObj(root) + "");
    }

    public static int getInt(String root) {
        return getConfig().getInt(root);
    }

    public static double getDouble(String root) {
        return getConfig().getDouble(root);
    }

    public static float getFloat(String root) {
        return Float.parseFloat(getObj(root) + "");
    }

    public static boolean getBoolean(String root) {
        return getConfig().getBoolean(root);
    }

    public static List<String> getStrList(String root) {
        return getObj(root) == null ? null : getConfig().getStringList(root);
    }

    public static Set<String> getKeys(String root, boolean keys) {
        return getConfig().getConfigurationSection(root).getKeys(keys);
    }

    public static void force(String root, Object o) {
        getConfig().set(root, o);
    }

    public static void set(String root, Object o) {
        if (getObj(root) == null || getObj(root).equals(""))
            force(root, o);
    }

    public static void forceWithSave(String root, Object o) {
        force(root, o);
        saveConfig();
    }

    public static void setWithSave(String root, Object o) {
        set(root, o);
        saveConfig();
    }

    public static Location getLoc(String root) {
        return getObj(root) == null ? null : new Location(Bukkit.getWorld(getStr(root + ".world")), getDouble(root + ".x"), getDouble(root + ".y"),
                getDouble(root + ".z"), getFloat(root + ".yaw"), getFloat(root + ".pitch"));
    }

    public static void forceLoc(String root, Location loc) {
        force(root + ".x", loc.getX());
        force(root + ".y", loc.getY());
        force(root + ".z", loc.getZ());
        force(root + ".yaw", loc.getYaw());
        force(root + ".pitch", loc.getPitch());
        force(root + ".world", loc.getWorld().getName());
    }

    public static void setLoc(String root, Location loc) {
        if (getObj(root) == null || getObj(root).equals(""))
            forceLoc(root, loc);
    }

    public static void forceLocWithSave(String root, Location loc) {
        forceLoc(root, loc);
        saveConfig();
    }

    public static void setLocWithSave(String root, Location loc) {
        setLoc(root, loc);
        saveConfig();
    }

    public static List<String> getHomes(Player p) {
        List<String> l = new ArrayList<>();
        for (String s : getConfig().getConfigurationSection("loc.home." + p.getUniqueId()).getKeys(false))
            if (!s.equals("del"))
                l.add(s);
        return l;
    }

}