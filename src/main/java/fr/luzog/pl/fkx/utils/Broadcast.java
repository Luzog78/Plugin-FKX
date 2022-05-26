package fr.luzog.pl.fkx.utils;

import fr.luzog.pl.fkx.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import javax.annotation.Nullable;

public class Broadcast {

    /**
     * @info Normal BroadCast
     * @baseColor WHITE
     * @boldColor BOLD
     * @linesColor ###
     */
    public static void mess(String mess) {
        Bukkit.broadcastMessage(form(mess, ChatColor.WHITE, ChatColor.BOLD));
    }

    /**
     * @info System Success (SYS_PREFIX)
     * @baseColor GREEN
     * @boldColor RED
     * @linesColor ###
     */
    public static void succ(String mess) {
        Bukkit.broadcastMessage(Main.SYS_PREFIX + form(mess, ChatColor.GREEN, ChatColor.RED));
    }

    /**
     * @info System Error (SYS_PREFIX)
     * @baseColor RED
     * @boldColor DARK_RED
     * @linesColor ###
     */
    public static void err(String mess) {
        Bukkit.broadcastMessage(Main.SYS_PREFIX + form(mess, ChatColor.RED, ChatColor.DARK_RED));
    }

    /**
     * @info FK Logement (FK_PREFIX)
     * @baseColor GOLD
     * @boldColor RED
     * @linesColor ###
     */
    public static void log(String mess) {
        Bukkit.broadcastMessage(Main.PREFIX + form(mess, ChatColor.GOLD, ChatColor.RED));
    }

    /**
     * @info Infos BroadCast (WITH LINES)
     * @baseColor DARK_PURPLE
     * @boldColor LIGHT_PURPLE
     * @linesColor RED
     */
    public static void info(String mess) {
        Bukkit.broadcastMessage(bd(mess, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, ChatColor.RED));
    }

    /**
     * @info Events BroadCast (WITH LINES)
     * @baseColor BLUE
     * @boldColor DARK_BLUE
     * @linesColor DARK_PURPLE
     */
    public static void event(String mess) {
        Bukkit.broadcastMessage(bd(mess, ChatColor.BLUE, ChatColor.DARK_BLUE, ChatColor.DARK_PURPLE));
    }

    /**
     * @info Warns BroadCast (WITH LINES)
     * @baseColor RED
     * @boldColor DARK_RED
     * @linesColor GOLD
     */
    public static void warn(String mess) {
        Bukkit.broadcastMessage(bd(mess, ChatColor.RED, ChatColor.DARK_RED, ChatColor.GOLD));
    }

    /**
     * @info Complete Custom BroadCast (WITH OR WITHOUT LINES)
     * @baseColor Custom
     * @boldColor Custom
     * @linesColor Custom
     */
    private static void custom(String mess, ChatColor base, ChatColor bold, @Nullable ChatColor lines) {
        Bukkit.broadcastMessage(bd(mess, base, bold, lines));
    }

    /**
     * @info Raw Custom BroadCast (WITH OR WITHOUT LINES)
     * @baseColor Custom
     * @boldColor Custom
     * @linesColor Custom
     */
    private static String bd(String mess, ChatColor base, ChatColor bold, @Nullable ChatColor lines) {
        return (lines != null ? "\n§" + lines.getChar() + "-------------------------§r\n \n  " : "\n")
                + form(mess, base, bold) + "\n"
                + (lines != null ? " \n§" + lines.getChar() + "-------------------------" : "");
    }

    private static String form(String mess, ChatColor base, ChatColor bold) {
        String ff = "§" + base.getChar(); // ff like finalFormat
        if (mess.contains("&"))
            ff += mess.replace('&', '§');
        else
            ff += mess;
        if (mess.contains(" ")) {
            String[] parts = ff.split(" ");
            ff = "§" + base.getChar();
            for (String s : parts) {
                if (s.startsWith("!") && s.length() > 1)
                    ff += s.replaceFirst("!", "§" + bold.getChar()) + " §r§" + base.getChar();
                else
                    ff += s + " ";
            }
        }
        return ff;
    }

}
