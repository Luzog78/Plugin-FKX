package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Portal;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FKCPortal {
    public static final String syntaxe = "/fk portal [help | (nether | end) [(over | dim) (spawn | pos1 | pos2) (get | set <x> <y> <z> [<yw> <pi>] [<world>])]]";

    public static boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        Portal p;

        if (args.length == 0)
            return false;

        else if (args.length == 1)
            if (sender instanceof Player)
                u.send("TODO -> Portal GUIs");
            else
                u.err(CmdUtils.err_not_player);

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if ((p = args[1].equalsIgnoreCase("nether") ? FKManager.getCurrentGame().getNether()
                : args[1].equalsIgnoreCase("end") ? FKManager.getCurrentGame().getEnd() : null) != null) {

            if (args.length == 2)
                if (sender instanceof Player)
                    u.send("TODO -> Portal GUI");
                else
                    u.err(CmdUtils.err_not_player);

            else if (args.length >= 5) {
                int w, t; // world (dim/ovr), type (sp, p1, p2)

                if ((w = args[2].equalsIgnoreCase("over") ? 0
                        : args[2].equalsIgnoreCase("dim") ? 1 : -1) != -1)

                    if ((t = args[3].equalsIgnoreCase("spawn") ? 0
                            : args[3].equalsIgnoreCase("pos1") ? 1
                            : args[3].equalsIgnoreCase("pos2") ? 2 : -1) != -1)

                        if (args[4].equalsIgnoreCase("get"))
                            u.succ("Coordonnées §6" + (w == 0 ? "Over" : "Dim") + " "
                                    + (t == 0 ? "Spawn" : t == 1 ? "Pos1" : "Pos2") + "§r du portail §d" + p.getName() + "§r :"
                                    + Utils.locToString(w == 0 ? t == 0 ? p.getOverSpawn() : t == 1 ? p.getOverPortal1() : p.getOverPortal2()
                                    : t == 0 ? p.getDimSpawn() : t == 1 ? p.getDimPortal1() : p.getDimPortal2(), true, true, true));

                        else if (args[4].equalsIgnoreCase("set")) {
                            Double x = null, y = null, z = null;
                            Float yw = null, pi = null;
                            World wo = null;
                            try {
                                x = Double.parseDouble(args[5]);
                                y = Double.parseDouble(args[6]);
                                z = Double.parseDouble(args[7]);
                                if (args.length > 8)
                                    if (args.length == 9) {
                                        if ((wo = Bukkit.getWorld(args[8])) == null) {
                                            u.err("Monde '" + args[8] + "' inconnu.");
                                            return false;
                                        }
                                    } else {
                                        yw = Float.parseFloat(args[8]);
                                        pi = Float.parseFloat(args[9]);
                                        if (args.length >= 11) {
                                            if ((wo = Bukkit.getWorld(args[10])) == null) {
                                                u.err("Monde '" + args[10] + "' inconnu.");
                                                return false;
                                            }
                                        }
                                    }

                                if (wo == null)
                                    wo = sender instanceof Player ? u.getPlayer().getLocation().getWorld() : Main.world;
                                Location loc = yw == null ? new Location(wo, x, y, z) : new Location(wo, x, y, z, yw, pi);

                                if (w == 0) {
                                    if (t == 0)
                                        p.setOverSpawn(loc);
                                    else if (t == 1)
                                        p.setOverPortal1(loc);
                                    else
                                        p.setOverPortal2(loc);
                                    FKManager.getCurrentGame().saveNether();
                                } else {
                                    if (t == 0)
                                        p.setDimSpawn(loc);
                                    else if (t == 1)
                                        p.setDimPortal1(loc);
                                    else
                                        p.setDimPortal2(loc);
                                    FKManager.getCurrentGame().saveEnd();
                                }

                                u.succ("Portail mis-à-jour !");
                            } catch (NumberFormatException e) {
                                u.err(CmdUtils.err_number_format + " ("
                                        + args[x == null ? 5 : y == null ? 6 : z == null ? 7
                                        : yw == null ? 8 : pi == null ? 9 : 10] + ")");
                            } catch (IndexOutOfBoundsException e) {
                                u.err(CmdUtils.err_missing_arg.replace("%ARG%",
                                        x == null ? "x" : y == null ? "y" : z == null ? "z"
                                                : yw == null ? "yaw" : pi == null ? "pitch" : "world"));
                            }
                        } else
                            u.err(CmdUtils.err_arg.replace("%ARG%", args[4]));
                    else
                        u.err(CmdUtils.err_arg.replace("%ARG%", args[3]));
                else
                    u.err(CmdUtils.err_arg.replace("%ARG%", args[2]));

            } else
                u.synt();

        } else
            u.err("Portail inconnu.");

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        return new ArrayList<String>() {{
            if (args.length == 2) {
                add("help");
                add("nether");
                add("end");
            } else if ((args[1].equalsIgnoreCase("nether") || args[1].equalsIgnoreCase("end"))) {
                if (args.length == 3) {
                    add("over");
                    add("dim");
                } else if (args[2].equalsIgnoreCase("over") || args[2].equalsIgnoreCase("dim")) {
                    if (args.length == 4) {
                        add("spawn");
                        add("pos1");
                        add("pos2");
                    } else if (args[3].equalsIgnoreCase("spawn") || args[3].equalsIgnoreCase("pos1") || args[3].equalsIgnoreCase("pos2")) {
                        if (args.length == 5) {
                            add("get");
                            add("set");
                        } else if (args[4].equalsIgnoreCase("set") && sender instanceof Player) {
                            Location l = ((Player) sender).getLocation();
                            DecimalFormat df = new DecimalFormat("#.00");
                            if (args.length == 6)
                                add(df.format(l.getX()));
                            else if (args.length == 7)
                                add(df.format(l.getY()));
                            else if (args.length == 8)
                                add(df.format(l.getZ()));
                            else if (args.length == 9) {
                                add(df.format(l.getYaw()));
                                addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
                            } else if (args.length == 10)
                                add(df.format(l.getPitch()));
                            else if (args.length == 11)
                                addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
                        }
                    }
                }
            }
        }};
    }
}
