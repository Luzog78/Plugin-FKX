package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPermissions;
import fr.luzog.pl.fkx.fk.FKTeam;
import fr.luzog.pl.fkx.fk.FKZone;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Portal;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FKCZone {
    public static final String syntaxe = "/fk zone [help | list | (info | gui | remove) <id> | (create | options) <id> [<args...>]]";
    public static final String opts = "Arguments:" +
            "\n§r > --s <x> <y> <z> [<yw> <pi>] [<world>]" +
            "\n§r > --pos1 <x> <y> <z> [<world>]" +
            "\n§r > --pos2 <x> <y> <z> [<world>]";
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
        } else if (args[1].equalsIgnoreCase("create")) {
            if (args.length >= 3) {
                for (String id : illegalIds)
                    if (args[2].equalsIgnoreCase(id)) {
                        u.err("Il est impossible de créer une zone avec un id correspondant à " +
                                "celui d'une team ou compris dans : " + Arrays.stream(illegalIds).collect(Collectors.toList()));
                        return false;
                    }
                if (FKManager.getCurrentGame().getZone(args[2]) == null) {
                    Location base = new Location(Main.world, 0, 0, 0);
                    FKZone z = new FKZone(args[2], FKZone.Type.ZONE, base, base, base, new FKPermissions(FKPermissions.Definition.DEFAULT));
                    FKManager.getCurrentGame().getNormalZones().add(z);
                    u.succ("Zone §6" + z.getId() + "§r créée et ajoutée à §f" + FKManager.getCurrentGame().getId() + "§r !");
                    if (args.length > 3)
                        handleOptions(u, z, args, 3);
                } else
                    u.err("La zone " + args[2] + " existe déjà.");
            } else
                u.synt();
        } else if (args.length >= 3) {
            FKZone z = FKManager.getCurrentGame().getZone(args[2]);
            if (z == null) {
                u.err("Zone non trouvée.");
                return false;
            }
            if (args[1].equalsIgnoreCase("info")) {
                u.succ("Zone :");
                u.succ(" > ID : §6" + z.getId());
                u.succ(" > Type : §7" + z.getType());
                u.succ(" > Localisations :");
                u.succ("   - Spawn : §f" + Utils.locToString(z.getSpawn(), true, true, true));
                u.succ("   - Position 1 : §f" + Utils.locToString(z.getPos1(), true, false, true));
                u.succ("   - Position 2 : §f" + Utils.locToString(z.getPos2(), true, false, true));
            } else if (args[1].equalsIgnoreCase("gui")) {
                if (sender instanceof Player)
                    u.succ("TODO -> Zone GUI");
                else
                    u.err(CmdUtils.err_not_player);
            } else if (args[1].equalsIgnoreCase("remove")) {
                z.delete(FKManager.getCurrentGame().getId());
                u.succ("Zone " + z.getId() + " supprimée.");
            } else if (args[1].equalsIgnoreCase("options")) {
                handleOptions(u, z, args, 3);
            } else
                u.synt();
        } else
            u.synt();

        return false;
    }

    public static String handleString(String base, int substring) {
        String s = base.substring(substring);
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))
            s = s.substring(2, s.length() - 1);
        return s.replace("\\\"", "\"").replace("\\'", "'").replace("\\ ", " ").replace("\\\\", "\\");
    }

    public static void handleOptions(CmdUtils u, FKZone zone, String[] arguments, int substring) {
        String[] args = (" " + String.join(" ", Arrays.copyOfRange(arguments, substring, arguments.length))).split(" --");
        for (int i = 0; i < args.length; i++)
            args[i] = args[i].replace("\\--", "--").replace("\\\\", "\\");
        u.succ("Options de la zone §6" + zone.getId() + "§r :");
        boolean hasAnyOption = false;
        for (String arg : args) {
            if (arg.replace(" ", "").length() == 0)
                continue;
            hasAnyOption = true;
            boolean isEmpty = arg.length() < 3 || arg.replace(" ", "").length() == 0;
            if (arg.equalsIgnoreCase("s") || arg.toLowerCase().startsWith("s ")) {
                String handled = handleString(arg, 2);

                if (isEmpty)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "<x> <y> <z>"));
                else if (handled.split(" ").length <= 1)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "<y> <z>"));
                else if (handled.split(" ").length <= 2)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "<z>"));
                else {
                    Double x = null, y = null, z = null;
                    Float yw = null, pi = null;
                    World w = Main.world;
                    boolean orientation = handled.split(" ").length >= 5;

                    try {
                        x = Double.parseDouble(handled.split(" ")[0]);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        y = Double.parseDouble(handled.split(" ")[1]);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        z = Double.parseDouble(handled.split(" ")[2]);
                    } catch (NumberFormatException ignored) {
                    }
                    if (orientation) {
                        try {
                            yw = Float.parseFloat(handled.split(" ")[3]);
                        } catch (NumberFormatException ignored) {
                        }
                        try {
                            pi = Float.parseFloat(handled.split(" ")[4]);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    if (handled.split(" ").length >= (orientation ? 6 : 4)) {
                        w = Bukkit.getWorld(handled.split(" ")[orientation ? 5 : 3]);
                    }

                    List<String> err = new ArrayList<>();
                    if (x == null)
                        err.add("X");
                    if (y == null)
                        err.add("Y");
                    if (z == null)
                        err.add("Z");
                    if (orientation && yw == null)
                        err.add("Yaw");
                    if (orientation && pi == null)
                        err.add("Pitch");
                    if (w == null)
                        err.add("World");

                    if (err.size() > 0) {
                        u.err(" - Spawn // Erreur avec le(s) paramètre(s) : " + String.join(", ", err) + ".");
                    } else {
                        Location loc = orientation ? new Location(w, x, y, z, yw, pi) : new Location(w, x, y, z);
                        zone.setSpawn(loc, true);
                        u.succ(" - Spawn : §f" + Utils.locToString(loc, true, orientation, true));
                    }
                }
            } else if (arg.equalsIgnoreCase("pos1") || arg.toLowerCase().startsWith("pos1 ")) {
                String handled = handleString(arg, 5);

                if (isEmpty)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "<x> <y> <z>"));
                else if (handled.split(" ").length <= 1)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "<y> <z>"));
                else if (handled.split(" ").length <= 2)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "<z>"));
                else {
                    Double x = null, y = null, z = null;
                    Float yw = null, pi = null;
                    World w = Main.world;
                    boolean orientation = handled.split(" ").length >= 5;

                    try {
                        x = Double.parseDouble(handled.split(" ")[0]);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        y = Double.parseDouble(handled.split(" ")[1]);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        z = Double.parseDouble(handled.split(" ")[2]);
                    } catch (NumberFormatException ignored) {
                    }
                    if (orientation) {
                        try {
                            yw = Float.parseFloat(handled.split(" ")[3]);
                        } catch (NumberFormatException ignored) {
                        }
                        try {
                            pi = Float.parseFloat(handled.split(" ")[4]);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    if (handled.split(" ").length >= (orientation ? 6 : 4)) {
                        w = Bukkit.getWorld(handled.split(" ")[orientation ? 5 : 3]);
                    }

                    List<String> err = new ArrayList<>();
                    if (x == null)
                        err.add("X");
                    if (y == null)
                        err.add("Y");
                    if (z == null)
                        err.add("Z");
                    if (orientation && yw == null)
                        err.add("Yaw");
                    if (orientation && pi == null)
                        err.add("Pitch");
                    if (w == null)
                        err.add("World");

                    if (err.size() > 0) {
                        u.err(" - Position 1 // Erreur avec le(s) paramètre(s) : " + String.join(", ", err) + ".");
                    } else {
                        Location loc = orientation ? new Location(w, x, y, z, yw, pi) : new Location(w, x, y, z);
                        zone.setPos1(loc, true);
                        u.succ(" - Position 1 : §f" + Utils.locToString(loc, true, orientation, true));
                    }
                }
            } else if (arg.equalsIgnoreCase("pos2") || arg.toLowerCase().startsWith("pos2 ")) {
                String handled = handleString(arg, 5);

                if (isEmpty)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "<x> <y> <z>"));
                else if (handled.split(" ").length <= 1)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "<y> <z>"));
                else if (handled.split(" ").length <= 2)
                    u.err(" - " + CmdUtils.err_missing_arg.replace("%ARG%", "<z>"));
                else {
                    Double x = null, y = null, z = null;
                    Float yw = null, pi = null;
                    World w = Main.world;
                    boolean orientation = handled.split(" ").length >= 5;

                    try {
                        x = Double.parseDouble(handled.split(" ")[0]);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        y = Double.parseDouble(handled.split(" ")[1]);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        z = Double.parseDouble(handled.split(" ")[2]);
                    } catch (NumberFormatException ignored) {
                    }
                    if (orientation) {
                        try {
                            yw = Float.parseFloat(handled.split(" ")[3]);
                        } catch (NumberFormatException ignored) {
                        }
                        try {
                            pi = Float.parseFloat(handled.split(" ")[4]);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    if (handled.split(" ").length >= (orientation ? 6 : 4)) {
                        w = Bukkit.getWorld(handled.split(" ")[orientation ? 5 : 3]);
                    }

                    List<String> err = new ArrayList<>();
                    if (x == null)
                        err.add("X");
                    if (y == null)
                        err.add("Y");
                    if (z == null)
                        err.add("Z");
                    if (orientation && yw == null)
                        err.add("Yaw");
                    if (orientation && pi == null)
                        err.add("Pitch");
                    if (w == null)
                        err.add("World");

                    if (err.size() > 0) {
                        u.err(" - Position 2 // Erreur avec le(s) paramètre(s) : " + String.join(", ", err) + ".");
                    } else {
                        Location loc = orientation ? new Location(w, x, y, z, yw, pi) : new Location(w, x, y, z);
                        zone.setPos2(loc, true);
                        u.succ(" - Position 2 : §f" + Utils.locToString(loc, true, orientation, true));
                    }
                }
            } else
                u.err(" - " + CmdUtils.err_arg.replace("%ARG%", arg));
        }
        if (!hasAnyOption)
            u.err(" - " + CmdUtils.err_unknown);
    }

    public static ArrayList<String> completeOptions(CommandSender sender, String[] args) {
        try {
            if ((args[args.length - 2].equalsIgnoreCase("--s")
                    || args[args.length - 2].equalsIgnoreCase("--pos1")
                    || args[args.length - 2].equalsIgnoreCase("--pos2")) && sender instanceof Player) {
                Block block = ((Player) sender).getTargetBlock(new HashSet<>(Collections.singletonList(Material.AIR)), 7);
                Location loc = block.getType() == Material.AIR ? ((Player) sender).getLocation() : block.getLocation();
                return new ArrayList<>(Collections.singletonList(loc.getBlockX() + ""));
            } else if ((args[args.length - 3].equalsIgnoreCase("--s")
                    || args[args.length - 3].equalsIgnoreCase("--pos1")
                    || args[args.length - 3].equalsIgnoreCase("--pos2")) && sender instanceof Player) {
                Block block = ((Player) sender).getTargetBlock(new HashSet<>(Collections.singletonList(Material.AIR)), 7);
                Location loc = block == null || block.getType() == Material.AIR ? ((Player) sender).getLocation() : block.getLocation();
                return new ArrayList<>(Collections.singletonList(loc.getBlockY() + ""));
            } else if ((args[args.length - 4].equalsIgnoreCase("--s")
                    || args[args.length - 4].equalsIgnoreCase("--pos1")
                    || args[args.length - 4].equalsIgnoreCase("--pos2")) && sender instanceof Player) {
                Block block = ((Player) sender).getTargetBlock(new HashSet<>(Collections.singletonList(Material.AIR)), 7);
                Location loc = block.getType() == Material.AIR ? ((Player) sender).getLocation() : block.getLocation();
                return new ArrayList<>(Collections.singletonList(loc.getBlockZ() + ""));
            } else if (args[args.length - 5].equalsIgnoreCase("--s")
                    || args[args.length - 5].equalsIgnoreCase("--pos1")
                    || args[args.length - 5].equalsIgnoreCase("--pos2")) {
                ArrayList<String> list = new ArrayList<>();
                if (sender instanceof Player) {
                    Block block = ((Player) sender).getTargetBlock(new HashSet<>(Collections.singletonList(Material.AIR)), 7);
                    Location loc = block.getType() == Material.AIR ? ((Player) sender).getLocation() : block.getLocation();
                    list.add(loc.getYaw() + "");
                }
                list.addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
                return list;
            } else if ((args[args.length - 6].equalsIgnoreCase("--s")
                    || args[args.length - 6].equalsIgnoreCase("--pos1")
                    || args[args.length - 6].equalsIgnoreCase("--pos2")) && sender instanceof Player) {
                Block block = ((Player) sender).getTargetBlock(new HashSet<>(Collections.singletonList(Material.AIR)), 7);
                Location loc = block.getType() == Material.AIR ? ((Player) sender).getLocation() : block.getLocation();
                return new ArrayList<>(Collections.singletonList(loc.getPitch() + ""));
            } else if (args[args.length - 7].equalsIgnoreCase("--s")
                    || args[args.length - 7].equalsIgnoreCase("--pos1")
                    || args[args.length - 7].equalsIgnoreCase("--pos2")) {
                return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toCollection(ArrayList::new));
            } else if (args[args.length - 1].startsWith("--") || !args[args.length - 2].startsWith("--"))
                return new ArrayList<>(Arrays.asList("--s", "--pos1", "--pos2"));
            else
                return new ArrayList<>();
        } catch (IndexOutOfBoundsException e) {
            if (args[args.length - 1].startsWith("--") || !args[args.length - 2].startsWith("--"))
                return new ArrayList<>(Arrays.asList("--s", "--pos1", "--pos2"));
            else
                return new ArrayList<>();
        }
    }

    public static List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        return new ArrayList<String>() {{
            if (args.length == 2) {
                add("help");
                add("list");
                add("info");
                add("gui");
                add("remove");
                add("create");
                add("options");
            } else if (args[1].equalsIgnoreCase("info") || args[1].equalsIgnoreCase("gui") || args[1].equalsIgnoreCase("remove")) {
                addAll(FKManager.getCurrentGame().getZones().stream().map(FKZone::getId).collect(Collectors.toList()));
            } else if (args[1].equalsIgnoreCase("create") && args.length >= 5) {
                addAll(completeOptions(sender, args));
            } else if (args[1].equalsIgnoreCase("options")) {
                if (args.length == 4)
                    addAll(FKManager.getCurrentGame().getZones().stream().map(FKZone::getId).collect(Collectors.toList()));
                else
                    addAll(completeOptions(sender, args));
            }
        }};
    }
}
