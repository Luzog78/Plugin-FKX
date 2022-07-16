package fr.luzog.pl.fkx.commands.Location;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;

public class Tp implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/tp [<selector>] (<player> | <x> <y> <z> [<yaw> <pitch>] [<world>])";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length == 0 || args[0].equals("?") || args[0].equalsIgnoreCase("help"))
            u.synt();

        else if (args.length > 3)
            try {
                Double.parseDouble(args[args.length - 1]);
                Bukkit.dispatchCommand(sender, "minecraft:tp " + String.join(" ", args));
            } catch (NumberFormatException e) {
                for (World w : Bukkit.getWorlds())
                    if (w.getName().equalsIgnoreCase(args[args.length - 1])
                            || w.getUID().toString().equalsIgnoreCase(args[args.length - 1])) {
                        try {
                            Double.parseDouble(args[0]);
                            if (sender instanceof Player) {
                                Bukkit.dispatchCommand(sender, "minecraft:tp "
                                        + String.join(" ", Arrays.copyOfRange(args, 0, args.length - 1)));
                                tp((Player) sender, w);
                            } else
                                u.err(CmdUtils.err_not_player);
                        } catch (NumberFormatException e1) {
                            if (Bukkit.getOfflinePlayer(args[0]).isOnline()) {
                                Bukkit.dispatchCommand(sender, "minecraft:tp "
                                        + String.join(" ", Arrays.copyOfRange(args, 0, args.length - 1)));
                                tp(Bukkit.getPlayerExact(args[0]), w);
                            } else
                                u.err(CmdUtils.err_player_not_found + " (" + args[0] + ")");
                        }
                        break;
                    }
            }

        else
            Bukkit.dispatchCommand(sender, "minecraft:tp " + String.join(" ", args));

        return false;
    }

    public static void tp(Player p, World w) {
        new BukkitRunnable() {
            @Override
            public void run() {
                p.teleport(new Location(w, p.getLocation().getX(), p.getLocation().getY(),
                        p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
            }
        }.runTaskLater(Main.instance, 1);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        Block b = sender instanceof Player ? ((Player) sender).getTargetBlock(new HashSet<>(Collections.singletonList(Material.AIR)), 7) : null;
        Location loc1 = sender instanceof Player ? ((Player) sender).getLocation() : null,
                loc2 = b != null && b.getType() != Material.AIR ? b.getLocation() : null;
        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            if (args.length == 1) {
                addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
                if (loc1 != null)
                    add(df.format(loc1.getX()));
                if (loc2 != null)
                    add("" + loc2.getBlockX());
            } else
                try {
                    Double.parseDouble(args[0]);
                    if (args.length == 2) {
                        if (loc1 != null)
                            add(df.format(loc1.getY()));
                        if (loc2 != null)
                            add("" + loc2.getBlockY());
                    } else if (args.length == 3) {
                        if (loc1 != null)
                            add(df.format(loc1.getZ()));
                        if (loc2 != null)
                            add("" + loc2.getBlockZ());
                    } else if (args.length == 4) {
                        if (loc1 != null)
                            add(df.format(loc1.getYaw()));
                        addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
                    } else if (args.length == 5) {
                        if (loc1 != null)
                            add(df.format(loc1.getPitch()));
                    } else if (args.length == 6) {
                        addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
                    }
                } catch (NumberFormatException e) {
                    if (args.length == 2) {
                        addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
                        if (loc1 != null)
                            add(df.format(loc1.getX()));
                        if (loc2 != null)
                            add("" + loc2.getBlockX());
                    } else
                        try {
                            Double.parseDouble(args[1]);
                            if (args.length == 3) {
                                if (loc1 != null)
                                    add(df.format(loc1.getY()));
                                if (loc2 != null)
                                    add("" + loc2.getBlockY());
                            } else if (args.length == 4) {
                                if (loc1 != null)
                                    add(df.format(loc1.getZ()));
                                if (loc2 != null)
                                    add("" + loc2.getBlockZ());
                            } else if (args.length == 5) {
                                if (loc1 != null)
                                    add(df.format(loc1.getYaw()));
                                addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
                            } else if (args.length == 6) {
                                if (loc1 != null)
                                    add(df.format(loc1.getPitch()));
                            } else if (args.length == 7) {
                                addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
                            }
                        } catch (NumberFormatException ignored) {
                        }
                }
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}
