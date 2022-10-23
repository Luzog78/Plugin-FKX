package fr.luzog.pl.fkx.commands.Game;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.guis.GuiDate;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.DayMoment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GCDate {
    public static final String syntaxe = "/" + Main.CMD + " date [help | info | day | time | weather]";
    public static final String syntaxe_day = "/" + Main.CMD + " date day [get | set <day>]";
    public static final String syntaxe_time = "/" + Main.CMD + " date time [get | set (<time> | <moment> | <hh>:<mm>)]";
    public static final String syntaxe_weather = "/" + Main.CMD + " date weather [get | set (sun | rain | thunder) [<timeout>]]";
    public static final String update_success = "Date mise à jour avec succès !";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        else if (args.length == 1)
            if (sender instanceof Player)
                u.getPlayer().openInventory(GuiDate.getMainInventory(Main.CMD));
            else
                u.err(CmdUtils.err_not_player);

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if (args[1].equalsIgnoreCase("info")) {
            u.succ("Date :");
            u.succ(" - Jour §f: " + GManager.getCurrentGame().getDay());
            u.succ(" - Heure §f: " + GManager.getCurrentGame().getFormattedTime());
            u.succ(" - Météo §f: " + (GManager.getCurrentGame().getWeather() == GManager.Weather.CLEAR ? "§eEnsoleillé"
                    : GManager.getCurrentGame().getWeather() == GManager.Weather.RAIN ? "§9Pluvieux" : "§7Tempête"));
        } else if (args[1].equalsIgnoreCase("day")) {
            u.setSyntaxe(syntaxe_day);
            if (args.length == 2)
                if (sender instanceof Player)
                    u.getPlayer().openInventory(GuiDate.getDayInventory(Main.CMD + " date"));
                else
                    u.err(CmdUtils.err_not_player);
            else if (args[2].equalsIgnoreCase("get"))
                u.succ("Date :\n - Jour : §f" + GManager.getCurrentGame().getDay());
            else if (args[2].equalsIgnoreCase("set") && args.length >= 4)
                try {
                    GManager.getCurrentGame().setDay(Integer.parseInt(args[3]), true);
                    u.succ(update_success);
                } catch (NumberFormatException e) {
                    u.err(CmdUtils.err_number_format);
                }
            else
                u.synt();
        } else if (args[1].equalsIgnoreCase("time")) {
            u.setSyntaxe(syntaxe_time);
            if (args.length == 2)
                if (sender instanceof Player)
                    u.getPlayer().openInventory(GuiDate.getHourInventory(Main.CMD + " date"));
                else
                    u.err(CmdUtils.err_not_player);
            else if (args[2].equalsIgnoreCase("get"))
                u.succ("Date :\n - Heure : §f" + GManager.getCurrentGame().getFormattedTime());
            else if (args[2].equalsIgnoreCase("set") && args.length >= 4)
                if (args[3].contains(":"))
                    try {
                        GManager.getCurrentGame().setTime(Long.parseLong(args[3].split(":")[0]) * 1200 + Long.parseLong(args[3].split(":")[1]) * 20, true);
                        u.succ(update_success);
                    } catch (NumberFormatException e) {
                        u.err(CmdUtils.err_number_format + " (for numbers : '" + args[3].split(":")[0] + "' and '" + args[3].split(":")[1] + "')");
                    }
                else if (DayMoment.match(args[3]) != null) {
                    GManager.getCurrentGame().setTime(DayMoment.match(args[3]).getHour(), true);
                    u.succ(update_success);
                } else
                    try {
                        GManager.getCurrentGame().setTime(Long.parseLong(args[3]), true);
                        u.succ(update_success);
                    } catch (NumberFormatException e) {
                        u.err(CmdUtils.err_arg.replace("%ARG%", args[3]));
                    }
            else
                u.synt();
        } else if (args[1].equalsIgnoreCase("weather")) {
            u.setSyntaxe(syntaxe_weather);
            if (args.length == 2)
                if (sender instanceof Player)
                    u.getPlayer().openInventory(GuiDate.getWeatherInventory(Main.CMD + " date"));
                else
                    u.err(CmdUtils.err_not_player);
            else if (args[2].equalsIgnoreCase("get"))
                u.succ("Date :\n - Météo : §f" + (GManager.getCurrentGame().getWeather() == GManager.Weather.CLEAR ? "§eEnsoleillé"
                        : GManager.getCurrentGame().getWeather() == GManager.Weather.RAIN ? "§9Pluvieux" : "§7Tempête"));
            else if (args[2].equalsIgnoreCase("set") && args.length >= 4)
                try {
                    Integer timeout = args.length >= 5 ? Integer.parseInt(args[4]) : null;
                    if (args[3].equalsIgnoreCase("sun")) {
                        GManager.getCurrentGame().setWeather(GManager.Weather.CLEAR, timeout, true);
                        u.succ(update_success);
                    } else if (args[3].equalsIgnoreCase("rain")) {
                        GManager.getCurrentGame().setWeather(GManager.Weather.RAIN, timeout, true);
                        u.succ(update_success);
                    } else if (args[3].equalsIgnoreCase("thunder")) {
                        GManager.getCurrentGame().setWeather(GManager.Weather.THUNDER, timeout, true);
                        u.succ(update_success);
                    } else
                        u.synt();
                } catch (NumberFormatException e) {
                    u.err(CmdUtils.err_number_format + " (for timeout : '" + args[4] + "')");
                }
            else
                u.synt();
        } else
            u.synt();

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return new ArrayList<String>() {{
            if (args.length == 2) {
                add("help");
                add("info");
                add("day");
                add("time");
                add("weather");
            } else if (args[1].equalsIgnoreCase("day")) {
                if (args.length == 3) {
                    add("get");
                    add("set");
                } else if (args.length == 4 && args[2].equalsIgnoreCase("set"))
                    for (int i = 1; i <= 12; i++)
                        add(i + "");
            } else if (args[1].equalsIgnoreCase("time")) {
                if (args.length == 3) {
                    add("get");
                    add("set");
                } else if (args.length == 4 && args[2].equalsIgnoreCase("set")) {
                    for (DayMoment dm : DayMoment.values())
                        add(dm.toString());
                    DecimalFormat df = new DecimalFormat("00");
                    for (int i = 0; i < 20; i += 4)
                        add(df.format(i) + ":00");
                    for (int i = 0; i < 24000; i += 6000)
                        add(i + "");
                }
            } else if (args[1].equalsIgnoreCase("weather")) {
                if (args.length == 3) {
                    add("get");
                    add("set");
                } else if (args.length == 4 && args[2].equalsIgnoreCase("set")) {
                    add("sun");
                    add("rain");
                    add("thunder");
                } else if (args.length == 5 && args[2].equalsIgnoreCase("set"))
                    for (int i = 3600; i <= 12000; i += 1200) // 3 to 10 minutes
                        add(i + "");
            }
        }};
    }
}
