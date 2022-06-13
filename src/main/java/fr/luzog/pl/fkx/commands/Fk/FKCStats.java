package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.PlayerStats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FKCStats {
    public static final String syntaxe = "/fk stats (help | <player> [(get | reset) (<stat> | all) | (set | increase | decrease) <stat> <value>])";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        if (args.length == 1)
            u.succ("TODO -> Stats GUIs");

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else {
            FKPlayer p = FKManager.getCurrentGame().getPlayer(args[1]);
            if (p == null) {
                u.err(CmdUtils.err_player_not_found);
                return false;
            }
            if (args.length == 2)
                u.succ("TODO -> Stats GUI");
            else if (args.length >= 4)
                try {
                    if (args[2].equalsIgnoreCase("get") && args[3].equalsIgnoreCase("all")) {
                        u.succ("Stats :");
                        for (Field field : p.getStats().getClass().getDeclaredFields())
                            try {
                                field.setAccessible(true);
                                u.succ(" - §6" + field.getName() + "§r : §f" + field.get(p.getStats()));
                                field.setAccessible(false);
                            } catch (IllegalAccessException ignored) {
                                u.err(CmdUtils.err_unknown);
                            }
                        return false;
                    }

                    Field f = p.getStats().getClass().getDeclaredField(args[3]);

                    if (args[2].equalsIgnoreCase("get"))
                        try {
                            f.setAccessible(true);
                            u.succ("Stat :");
                            u.succ(" - §6" + f.getName() + "§r : §f" + f.get(p.getStats()));
                            f.setAccessible(false);
                        } catch (IllegalAccessException ignored) {
                            u.err(CmdUtils.err_unknown);
                        }
                    else if (args[2].equalsIgnoreCase("reset"))
                        try {
                            f.setAccessible(true);
                            f.set(p.getStats(), 0);
                            f.setAccessible(false);
                            u.succ("La statistique a été réinitialisée.");
                        } catch (IllegalAccessException ignored) {
                            u.err(CmdUtils.err_unknown);
                        }
                    else if (args.length >= 5)
                        if (args[2].equalsIgnoreCase("set"))
                            try {
                                f.setAccessible(true);
                                if (f.getType() == int.class)
                                    f.set(p.getStats(), Integer.parseInt(args[4]));
                                else if (f.getType() == double.class)
                                    f.set(p.getStats(), Double.parseDouble(args[4]));
                                else if (f.getType() == float.class)
                                    f.set(p.getStats(), Float.parseFloat(args[4]));
                                else if (f.getType() == long.class)
                                    f.set(p.getStats(), Long.parseLong(args[4]));
                                else if (f.getType() == short.class)
                                    f.set(p.getStats(), Short.parseShort(args[4]));
                                else if (f.getType() == byte.class)
                                    f.set(p.getStats(), Byte.parseByte(args[4]));
                                else if (f.getType() == boolean.class)
                                    f.set(p.getStats(), Boolean.parseBoolean(args[4]));
                                else if (f.getType() == String.class)
                                    f.set(p.getStats(), args[4]);
                                f.setAccessible(false);
                                u.succ("La statistique a été mise à jour !");
                            } catch (IllegalAccessException ignored) {
                                u.err(CmdUtils.err_unknown);
                            } catch (NumberFormatException e) {
                                u.err(CmdUtils.err_number_format);
                            }
                        else if (args[2].equalsIgnoreCase("increase"))
                            try {
                                f.setAccessible(true);
                                if (f.getType() == int.class)
                                    f.set(p.getStats(), (int) f.get(p.getStats()) + Integer.parseInt(args[4]));
                                else if (f.getType() == double.class)
                                    f.set(p.getStats(), (double) f.get(p.getStats()) + Double.parseDouble(args[4]));
                                else if (f.getType() == float.class)
                                    f.set(p.getStats(), (float) f.get(p.getStats()) + Float.parseFloat(args[4]));
                                else if (f.getType() == long.class)
                                    f.set(p.getStats(), (long) f.get(p.getStats()) + Long.parseLong(args[4]));
                                else if (f.getType() == short.class)
                                    f.set(p.getStats(), (short) f.get(p.getStats()) + Short.parseShort(args[4]));
                                else if (f.getType() == byte.class)
                                    f.set(p.getStats(), (byte) f.get(p.getStats()) + Byte.parseByte(args[4]));
                                else if (f.getType() == boolean.class)
                                    f.set(p.getStats(), (boolean) f.get(p.getStats()) || Boolean.parseBoolean(args[4]));
                                else if (f.getType() == String.class)
                                    f.set(p.getStats(), f.get(p.getStats()) + args[4]);
                                f.setAccessible(false);
                                u.succ("La statistique a été mise à jour !");
                            } catch (IllegalAccessException ignored) {
                                u.err(CmdUtils.err_unknown);
                            } catch (NumberFormatException e) {
                                u.err(CmdUtils.err_number_format);
                            }
                        else if (args[2].equalsIgnoreCase("decrease"))
                            try {
                                f.setAccessible(true);
                                if (f.getType() == int.class)
                                    f.set(p.getStats(), (int) f.get(p.getStats()) - Integer.parseInt(args[4]));
                                else if (f.getType() == double.class)
                                    f.set(p.getStats(), (double) f.get(p.getStats()) - Double.parseDouble(args[4]));
                                else if (f.getType() == float.class)
                                    f.set(p.getStats(), (float) f.get(p.getStats()) - Float.parseFloat(args[4]));
                                else if (f.getType() == long.class)
                                    f.set(p.getStats(), (long) f.get(p.getStats()) - Long.parseLong(args[4]));
                                else if (f.getType() == short.class)
                                    f.set(p.getStats(), (short) f.get(p.getStats()) - Short.parseShort(args[4]));
                                else if (f.getType() == byte.class)
                                    f.set(p.getStats(), (byte) f.get(p.getStats()) - Byte.parseByte(args[4]));
                                else if (f.getType() == boolean.class)
                                    f.set(p.getStats(), (boolean) f.get(p.getStats()) && Boolean.parseBoolean(args[4]));
                                else if (f.getType() == String.class)
                                    f.set(p.getStats(), args[4] + f.get(p.getStats()));
                                f.setAccessible(false);
                                u.succ("La statistique a été mise à jour !");
                            } catch (IllegalAccessException ignored) {
                                u.err(CmdUtils.err_unknown);
                            } catch (NumberFormatException e) {
                                u.err(CmdUtils.err_number_format);
                            }
                        else
                            u.synt();
                    else
                        u.synt();
                } catch (NoSuchFieldException e) {
                    u.err("Cette statistique n'existe pas.");
                    return false;
                }
            else
                u.synt();
        }

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return new ArrayList<String>() {{
            if (args.length == 2) {
                add("help");
                addAll(FKManager.getGlobalPlayers().stream().map(FKPlayer::getName).collect(Collectors.toList()));
            } else if (args.length == 3 && FKManager.getGlobalPlayer(args[1]) != null) {
                add("get");
                add("set");
                add("increase");
                add("decrease");
                add("reset");
            } else if (args.length == 4 && FKManager.getGlobalPlayer(args[1]) != null)
                for (Field field : PlayerStats.class.getDeclaredFields())
                    add(field.getName());
        }};
    }
}
