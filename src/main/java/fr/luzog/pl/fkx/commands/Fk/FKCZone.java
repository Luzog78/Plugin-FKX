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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FKCZone {
    public static final String syntaxe = "/fk zone [help | list | (info | gui | remove) <id> | (add | options) <id> [<args...>] | perm <id> <permission> (get | set (on | off | default))]";
    public static final String opts = "Arguments:" +
            "\n§r > -s <x> <y> <z> [<yw> <pi>] [<world>]" +
            "\n§r > -pos1 <x> <y> <z> [<world>]" +
            "\n§r > -pos2 <x> <y> <z> [<world>]";
    public static final String[] illegalIds = {"spawn", "lobby", "gods", "specs",
            "id", "null", "list", "help", "add", "remove", "options"};
    public static final String compl = "Note : Il est impossible de créer une zone avec un id correspondant à " +
            "celui d'une team ou compris dans : " + Arrays.stream(illegalIds).collect(Collectors.toList());

    public static boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe + "\n§r" + opts + "\n§r" + compl);

        if (args.length == 0)
            return false;

        else if (args.length == 1)
            if (sender instanceof Player)
                u.succ("TODO -> Zones GUIs");
            else
                u.err(CmdUtils.err_not_player);

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if (args[1].equalsIgnoreCase("list")) {
            u.succ("Zones :");
            FKManager.getCurrentGame().getZones().forEach(z ->
                    u.succ(" - §6" + z.getId() + " §7(" + z.getType() + ")§r : §f" + (z.getSpawn() == null ?
                            "§cnull" : Utils.locToString(z.getSpawn(), true, true, true))));
        } else if (args[1].equalsIgnoreCase())

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
