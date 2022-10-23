package fr.luzog.pl.fkx.utils;

import fr.luzog.pl.fkx.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class Broadcast {

    /**
     * <strong>Normal BroadCast</strong>
     * <br>
     * <br><strong>&nbsp; - Base Color :</strong> WHITE
     * <br><strong>&nbsp; - Strong Color :</strong> BOLD
     * <br><strong>&nbsp; - Line Style :</strong> ###
     */
    public static void mess(String mess) {
        Bukkit.broadcastMessage(form(mess, ChatColor.WHITE, ChatColor.BOLD));
    }

    /**
     * <strong>System Success (SYS_PREFIX)</strong>
     * <br>
     * <br><strong>&nbsp; - Base Color :</strong> GREEN
     * <br><strong>&nbsp; - Strong Color :</strong> RED
     * <br><strong>&nbsp; - Line Style :</strong> ###
     */
    public static void sys_succ(String mess) {
        Bukkit.broadcastMessage(Main.SYS_PREFIX + form(mess, ChatColor.GREEN, ChatColor.RED));
    }

    /**
     * <strong>System Error (SYS_PREFIX)</strong>
     * <br>
     * <br><strong>&nbsp; - Base Color :</strong> RED
     * <br><strong>&nbsp; - Strong Color :</strong> DARK_RED
     * <br><strong>&nbsp; - Line Style :</strong> ###
     */
    public static void sys_err(String mess) {
        Bukkit.broadcastMessage(Main.SYS_PREFIX + form(mess, ChatColor.RED, ChatColor.DARK_RED));
    }

    /**
     * <strong>Success (GAME_PREFIX)</strong>
     * <br>
     * <br><strong>&nbsp; - Base Color :</strong> GREEN
     * <br><strong>&nbsp; - Strong Color :</strong> RED
     * <br><strong>&nbsp; - Line Style :</strong> ###
     */
    public static void succ(String mess) {
        Bukkit.broadcastMessage(Main.PREFIX + form(mess, ChatColor.GREEN, ChatColor.RED));
    }

    /**
     * <strong>Error (GAME_PREFIX)</strong>
     * <br>
     * <br><strong>&nbsp; - Base Color :</strong> RED
     * <br><strong>&nbsp; - Strong Color :</strong> DARK_RED
     * <br><strong>&nbsp; - Line Style :</strong> ###
     */
    public static void err(String mess) {
        Bukkit.broadcastMessage(Main.PREFIX + form(mess, ChatColor.RED, ChatColor.DARK_RED));
    }

    /**
     * <strong>Logment (GAME_PREFIX)</strong>
     * <br>
     * <br><strong>&nbsp; - Base Color :</strong> GOLD
     * <br><strong>&nbsp; - Strong Color :</strong> RED
     * <br><strong>&nbsp; - Line Color :</strong> ###
     */
    public static void log(String mess) {
        Bukkit.broadcastMessage(Main.PREFIX + form(mess, ChatColor.GOLD, ChatColor.RED));
    }

    /**
     * <strong>Infos BroadCast (WITH LINES)</strong>
     * <br>
     * <br><strong>&nbsp; - Base Color :</strong> DARK_PURPLE
     * <br><strong>&nbsp; - Strong Color :</strong> LIGHT_PURPLE
     * <br><strong>&nbsp; - Line Color :</strong> RED
     */
    public static void info(String mess) {
        Bukkit.broadcastMessage(bd(mess, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, ChatColor.RED));
    }

    /**
     * <strong>Events BroadCast (WITH LINES)</strong>
     * <br>
     * <br><strong>&nbsp; - Base Color :</strong> BLUE
     * <br><strong>&nbsp; - Strong Color :</strong> DARK_BLUE
     * <br><strong>&nbsp; - Line Color :</strong> DARK_PURPLE
     */
    public static void event(String mess) {
        Bukkit.broadcastMessage(bd(mess, ChatColor.BLUE, ChatColor.DARK_BLUE, ChatColor.DARK_PURPLE));
    }

    /**
     * <strong>Announcements BroadCast (WITH LINES)</strong>
     * <br>
     * <br><strong>&nbsp; - Base Color :</strong> GREEN
     * <br><strong>&nbsp; - Strong Color :</strong> RED
     * <br><strong>&nbsp; - Line Color :</strong> DARK_GREEN
     */
    public static void announcement(String mess) {
        Bukkit.broadcastMessage(bd(mess, ChatColor.GREEN, ChatColor.RED, ChatColor.DARK_GREEN));
    }

    /**
     * <strong>Warns BroadCast (WITH LINES)</strong>
     * <br>
     * <br><strong>&nbsp; - Base Color :</strong> RED
     * <br><strong>&nbsp; - Strong Color :</strong> DARK_RED
     * <br><strong>&nbsp; - Line Color :</strong> GOLD
     */
    public static void warn(String mess) {
        Bukkit.broadcastMessage(bd(mess, ChatColor.RED, ChatColor.DARK_RED, ChatColor.GOLD));
    }


    /**
     * <strong>Complete Custom BroadCast (WITH OR WITHOUT LINES)</strong>
     * <br>
     * <br><strong>&nbsp; - Base Color :</strong> Custom
     * <br><strong>&nbsp; - Strong Color :</strong> Custom
     * <br><strong>&nbsp; - Line Color :</strong> Custom
     */
    public static void custom(String mess, ChatColor base, ChatColor bold, @Nullable ChatColor lines) {
        Bukkit.broadcastMessage(bd(mess, base, bold, lines));
    }

    /**
     * <strong>Raw Custom BroadCast (WITH OR WITHOUT LINES)</strong>
     * <br>
     * <br><strong>&nbsp; - Base Color :</strong> Custom
     * <br><strong>&nbsp; - Strong Color :</strong> Custom
     * <br><strong>&nbsp; - Line Color :</strong> Custom
     */
    public static String bd(String mess, ChatColor base, ChatColor bold, @Nullable ChatColor lines) {
        return (lines != null ? "\n§" + lines.getChar() + "-------------------------§r\n \n  " : "\n")
                + form(mess, base, bold) + "\n"
                + (lines != null ? " \n§" + lines.getChar() + "-------------------------" : "");
    }

    private static String form(String mess, ChatColor base, ChatColor bold) {
        return ("§r" + Strings.join(new ArrayList<String>(){{
            for (String part : mess.replace("&", "§").replace("§§", "&").split(" ")) {
                if (part.equals("!!") || part.equals("!"))
                    add("!");
                else if (part.startsWith("!!"))
                    add(part.replaceFirst("!!", "!"));
                else if (part.startsWith("!"))
                    add(part.replaceFirst("!", "§" + bold.getChar()) + "§r");
                else
                    add(part);
            }
        }}, " ")).replace("\n", "\n§r  ").replace("§r", "§" + base.getChar());
    }

}
